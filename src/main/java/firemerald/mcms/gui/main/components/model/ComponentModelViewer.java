package firemerald.mcms.gui.main.components.model;

import static org.lwjgl.opengl.GL33.*;

import java.util.Map;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.math.MathUtils;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.IRaytraceTarget;
import firemerald.mcms.api.model.Skeleton;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.model.ComponentBox;
import firemerald.mcms.model.IComponentParent;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.shader.FrameBuffer;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.ApplicationState;
import firemerald.mcms.util.EditorMode;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.IEditable;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.TextureRaytraceResult;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;
import firemerald.mcms.window.api.MouseButtons;

public class ComponentModelViewer extends Component
{
	public final GuiMain gui;
	private final FrameBuffer fb;
	private int sizeW, sizeH;
	double fov = 70 * Math.PI / 180;
	public final Matrix4d modelm = new Matrix4d(), originm = new Matrix4d(), view = new Matrix4d(), projection = new Matrix4d(), inverse = new Matrix4d();
	public Quaterniond rotationq = new Quaterniond();
	public final Mesh renderFB = new Mesh();
	public ThemeElement rect;

	public float orbitX = 0, orbitY = 0, orbitZ = 0;
	public float lookX = 0, lookY = 0, lookZ = 0;
	public float orbitYaw = 0, orbitPitch = 0;
	public float orbitZoom = 5;
	public float orbitMX, orbitMY, lookVX, lookVY, lookVZ, panVX, panVY, panVZ;
	public int orbit = -1, look = -1, pan = -1;
	
	public ComponentModelViewer(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2);
		this.gui = gui;
		fb = new FrameBuffer(sizeW = x2 - x1, sizeH = y2 - y1);
		renderFB.setMesh(x1, y1, x2, y2, 0, 0, 1, 1, 0);
		updateMatricies();
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		fb.setSize(sizeW = x2 - x1, sizeH = y2 - y1);
		renderFB.setMesh(x1, y1, x2, y2, 0, 0, 1, 1, 0);
		updateMatricies();
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(x2 - x1, y2 - y1, 1);
		}
		else if (reason == GuiUpdate.PROJECT)
		{
			this.updateMatricies();
		}
	}
	
	public void updateMatricies()
	{
		modelm.identity();
		originm.identity();
		view.identity();
		projection.identity();
		/*
		view.translate(0, 0, 0.01f * (float) (-this.sizeH / (2 * Math.tan(fov * 0.5))));
		*/
		view.translate(0, 0, -orbitZoom);
		originm.translate(orbitX, orbitY, orbitZ).mul((rotationq = new Quaterniond().rotateX(orbitPitch).rotateY(orbitYaw)).get(new Matrix4d())).scale(.03125f * orbitZoom);
		modelm.translate(orbitX, orbitY, orbitZ).mul((rotationq = new Quaterniond().rotateX(orbitPitch).rotateY(orbitYaw)).get(new Matrix4d())).translate(lookX, lookY, lookZ);
		modelm.scale(Main.instance.project.getScale());
		projection.perspective((float) fov, (float) this.sizeW / this.sizeH, .1f, 1000);
		inverse.identity().mul(projection).mul(view).mul(modelm).invert();
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		Main main = Main.instance;
    	if (this.contains(mx, my))
    	{
    		Project project = main.project;
        	IModel<?, ?> model = project.getModel();
    		if (model != null || !project.lockedModels.isEmpty())
    		{
    	    	float rmx = ((mx - x1) * 2) / sizeW - 1;
    	    	float rmy = 1 - ((my - y1) * 2) / sizeH;
    	    	Vector4f p1 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, -1, 1)));
            	p1.mul(1 / p1.w());
            	Vector4f p2 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, 1, 1)));
            	p2.mul(1 / p2.w());
            	ExtendedAnimationState[] states = project.getStates();
            	float dx = p2.x() - p1.x(), dy = p2.y() - p1.y(), dz = p2.z() - p1.z();
            	RaytraceResult trace = model == null ? null : model.rayTrace(p1.x(), p1.y(), p1.z(), dx, dy, dz, model.getPose(states));
            	for (IModel<?, ?> lockedModel : project.lockedModels.keySet()) if (lockedModel != model)
            	{
            		RaytraceResult res = lockedModel.rayTrace(p1.x(), p1.y(), p1.z(), dx, dy, dz, lockedModel.getPose(states));
            		if (res != null && (trace == null || trace.m > res.m)) trace = res;
            	}
            	main.trace = trace;
            	if (main.getEditorMode() == EditorMode.TEXTURE && trace instanceof TextureRaytraceResult)
            	{
            		TextureRaytraceResult texRes = (TextureRaytraceResult) trace;
            		if (texRes.tex != null && texRes.tex == project.getTexture()) //TODO overlay on other textures
            		{
                		if (main.getOverlay().w != texRes.tex.w || main.getOverlay().h != texRes.tex.h) main.getOverlay().setSize(texRes.tex.w, texRes.tex.h);
                		Main.instance.tool.drawOnOverlay(main.getOverlay(), texRes.u, texRes.v);
            		}
            	}
    		}
        	else main.trace = null;
    	}
	}
	
	public void renderModel(IModel<?, ?> model, AnimationState[] states, Runnable setTex)
	{
    	Map<String, Matrix4d> map = model.getPose(states);
    	model.render(map, setTex);
	}
	
	public void renderBones(IModel<?, ?> model, AnimationState[] states)
	{
    	ApplicationState state = Main.instance.state;
    	Map<String, Matrix4d> map = model.getPose(states);
		for (Bone<?> bone : model.getRootBones()) RenderUtil.renderSkeleton(bone, map, state.showNodes(), state.showBones());
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		GL11.glDisable(GL11.GL_STENCIL_TEST);
    	Main.listGLErrors("pre-pre-render");
		Main main = Main.instance;
		Shader shader = main.shader;
		Project project = main.project;
    	ApplicationState state = main.state;
		rect.bind();
		renderFB.render();
		Shader.MODEL.push();
		Shader.VIEW.push();
		Shader.PROJECTION.push();
		glBindFramebuffer(GL_FRAMEBUFFER, fb.frameBuffer);
		shader.bind();
		glViewport(0, 0, sizeW, sizeH);
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDepthFunc(GL_LESS);

        glEnable(GL_CULL_FACE);
		modelm.get(Shader.MODEL.matrix());
		view.get(Shader.VIEW.matrix());
		projection.get(Shader.PROJECTION.matrix());
		shader.updateModel();
		shader.updateView();
		shader.updateProjection();
    	Main.listGLErrors("pre-render");
    	if (!state.showNodes() && !state.showBones() && (pan >= 0 || look >= 0 || Modifier.CONTROL.isDown(Main.instance.window) || Modifier.SHIFT.isDown(Main.instance.window)))
    	{
    		Shader.MODEL.push();
    		originm.get(Shader.MODEL.matrix());
    		shader.updateModel();
    		main.textureManager.unbindTexture();
    		RenderUtil.SPHERE_MESH.render();
    		Shader.MODEL.pop();
    		shader.updateModel();
    	}
    	IModel<?, ?> model = project.getModel();
    	ExtendedAnimationState[] states = project.getStates();
    	if (model != null)
    	{
        	Map<String, Matrix4d> map = model.getPose(states);
        	project.bindTex();
    		if (main.getEditorMode() == EditorMode.TEXTURE) main.shader.setOverlayTexture(main.getOverlay());
        	model.render(map, () -> project.bindTex());
    		if (main.getEditorMode() == EditorMode.TEXTURE) main.shader.setOverlayTexture(null);
        	project.unbindTex();
    	}
    	project.lockedModels.forEach((m, tex) -> {
    		if (m != model)
    		{
        		if (tex != null) tex.bind();
        		else Main.instance.textureManager.unbindTexture();
        		renderModel(m, states, tex == null ? Main.instance.textureManager::unbindTexture : tex::bind);
    		}
    	});
		if (state.showNodes() || state.showBones())
		{
			glClear(GL_DEPTH_BUFFER_BIT);
	    	if (pan >= 0 || look >= 0 || Modifier.CONTROL.isDown(Main.instance.window) || Modifier.SHIFT.isDown(Main.instance.window))
	    	{
	    		Shader.MODEL.push();
	    		originm.get(Shader.MODEL.matrix());
	    		shader.updateModel();
	    		main.textureManager.unbindTexture();
	    		RenderUtil.SPHERE_MESH.render();
	    		Shader.MODEL.pop();
	    		shader.updateModel();
	    	}
	    	if (model != null) renderBones(model, states);
	    	else
	    	{
	    		Skeleton skeleton = Main.instance.project.getSkeleton();
	    		if (skeleton != null)
	    		{
	            	Map<String, Matrix4d> map = skeleton.getPose(project.getStates());
	    			for (Bone.Actual bone : skeleton.getRootBones()) RenderUtil.renderSkeleton(bone, map, state.showNodes(), state.showBones());
	    		}
	    	}
	    	project.lockedModels.keySet().forEach(m -> {
	    		if (m != model) renderBones(m, states);
	    	});
		}
    	if (pan >= 0 || look >= 0 || Modifier.CONTROL.isDown(Main.instance.window) || Modifier.SHIFT.isDown(Main.instance.window))
    	{
    		Shader.MODEL.push();
    		originm.get(Shader.MODEL.matrix());
    		shader.updateModel();
    		main.textureManager.unbindTexture();
    		RenderUtil.SPHERE_MESH.render();
    		shader.setColor(0, 0, 0, .5f);
    		GL11.glDepthFunc(GL11.GL_GREATER);
    		RenderUtil.SPHERE_MESH.render();
    		shader.setColor(1, 1, 1, 1);
    		GL11.glDepthFunc(GL11.GL_LEQUAL);
    		Shader.MODEL.pop();
    		shader.updateModel();
    	}
        glDisable(GL_CULL_FACE);
    	Main.listGLErrors("post-render");
		
		//TODO rendering
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		Shader.MODEL.pop();
		Shader.VIEW.pop();
		Shader.PROJECTION.pop();
		shader.bind();
		shader.updateModel();
		shader.updateView();
		shader.updateProjection();
		glViewport(0, 0, main.sizeW, main.sizeH);
		glDepthMask(false);
		glDisable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glBindTexture(GL_TEXTURE_2D, fb.frameBufferTex);
		renderFB.render();
    	Main.listGLErrors("post-post-render");
	}
	
	private double prevU, prevV;
	private IRaytraceTarget prevHit = null;

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (orbit < 0 && pan < 0 && look < 0 && (button == MouseButtons.MIDDLE || (Main.instance.getEditorMode() != EditorMode.TEXTURE && button == MouseButtons.RIGHT)))
		{
			if ((mods & Modifier.SHIFT.flag) != 0)
			{
				pan = button;
		    	float rmx = ((mx - x1) * 2) / sizeW - 1;
		    	float rmy = 1 - ((my - y1) * 2) / sizeH;
		    	Vector3f p1 = MathUtils.toVector3f(rotationq.conjugate(new Quaterniond()).transform(new Vector3d(rmx, rmy, 0)));
		    	panVX = p1.x();
		    	panVY = p1.y();
		    	panVZ = p1.z();
			}
			else if ((mods & Modifier.CONTROL.flag) != 0)
			{
				look = button;
		    	float rmx = ((mx - x1) * 2) / sizeW - 1;
		    	float rmy = 1 - ((my - y1) * 2) / sizeH;
		    	Vector3f p1 = MathUtils.toVector3f(rotationq.conjugate(new Quaterniond()).transform(new Vector3d(rmx, rmy, 0)));
		    	lookVX = p1.x();
		    	lookVY = p1.y();
		    	lookVZ = p1.z();
			}
			else
			{
				orbit = button;
				orbitMX = mx;
				orbitMY = my;
			}
		}
		else
		{
			Main main = Main.instance;
			Project project = main.project;
			IModel<?, ? extends RenderObjectComponents<?>> model = project.getModel();
			if (model != null || !project.lockedModels.isEmpty())
			{
				if (main.getEditorMode() == EditorMode.TEXTURE || button == MouseButtons.LEFT)
				{
			    	float rmx = ((mx - x1) * 2) / sizeW - 1;
			    	float rmy = 1 - ((my - y1) * 2) / sizeH;
			    	Vector4f p1 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, -1, 1)));
			    	p1.mul(1 / p1.w());
			    	Vector4f p2 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, 1, 1)));
			    	p2.mul(1 / p2.w());
	            	float dx = p2.x() - p1.x(), dy = p2.y() - p1.y(), dz = p2.z() - p1.z();
	            	ExtendedAnimationState[] states = project.getStates();
	            	IModel<?, ? extends RenderObjectComponents<?>> clicked = null;
			    	RaytraceResult trace = null;
			    	if (model != null)
			    	{
			    		trace = model.rayTrace(p1.x(), p1.y(), p1.z(), dx, dy, dz, model.getPose(states));
				    	if (trace != null) clicked = model;
			    	}
	            	for (IModel<?, ? extends RenderObjectComponents<?>> lockedModel : project.lockedModels.keySet()) if (lockedModel != model)
	            	{
				    	RaytraceResult res = lockedModel.rayTrace(p1.x(), p1.y(), p1.z(), dx, dy, dz, lockedModel.getPose(states));
	            		if (res != null && (trace == null || trace.m > res.m))
	            		{
	            			trace = res;
				    		clicked = lockedModel;
	            		}
	            	}
			    	IEditable editable = main.getEditing();
			    	if (trace != null)
			    	{
			    		if (clicked != model) //clicked on a locked model
			    		{
			    			project.lockedModels.put(model, project.getTexture());
			    			project.setModel(clicked);
			    			Texture tex = project.lockedModels.get(clicked);
			    			if (tex != project.getTexture()) project.setTexture(tex);
			    		}
			    		if (main.getEditorMode() == EditorMode.TEXTURE && trace instanceof TextureRaytraceResult)
			    		{
			    			TextureRaytraceResult texRes = (TextureRaytraceResult) trace;
			    			if (texRes.tex != null) Main.instance.tool.onMouseClick(texRes.tex, prevU = texRes.u, prevV = texRes.v, button);
				    		this.prevHit = trace.hit;
			    		}
			    		if (trace.hit != main.getEditing())
			    		{
				    		if (trace.hit instanceof IEditable)
				    		{
				    			main.setEditing((IEditable) trace.hit);
				    		}
				    		else main.setEditing(null);
			    		}
			    	}
			    	else if (editable != null)
			    	{
			    		main.setEditing(null);
			    	}
				}
			}
		}
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (button == orbit)
		{
			final float scaleY = MathUtils.DEG_TO_RAD_F * .25f, scaleP = MathUtils.DEG_TO_RAD_F * .25f;
			orbitYaw += scaleY * (mx - orbitMX);
			orbitPitch += scaleP * (my - orbitMY);
			orbitMX = mx;
			orbitMY = my;
			updateMatricies();
		}
		else if (button == look)
		{
	    	float rmx = ((mx - x1) * 2) / sizeW - 1;
	    	float rmy = 1 - ((my - y1) * 2) / sizeH;
	    	Vector3d p1 = rotationq.conjugate(new Quaterniond()).transform(new Vector3d(rmx, rmy, 0));
	    	p1.sub(lookVX, lookVY, lookVZ);
	    	lookVX += p1.x();
	    	lookVY += p1.y();
	    	lookVZ += p1.z();
			this.lookX += p1.x() * this.orbitZoom;
			this.lookY += p1.y() * this.orbitZoom;
			this.lookZ += p1.z() * this.orbitZoom;
			updateMatricies();
		}
		else if (button == pan)
		{
	    	float rmx = ((mx - x1) * 2) / sizeW - 1;
	    	float rmy = 1 - ((my - y1) * 2) / sizeH;
	    	Vector3d p1 = rotationq.conjugate(new Quaterniond()).transform(new Vector3d(rmx, rmy, 0));
	    	p1.sub(panVX, panVY, panVZ);
	    	panVX += p1.x();
	    	panVY += p1.y();
	    	panVZ += p1.z();
	    	p1 = rotationq.transform(p1);
			this.orbitX += p1.x() * this.orbitZoom;
			this.orbitY += p1.y() * this.orbitZoom;
			this.orbitZ += p1.z() * this.orbitZoom;
			updateMatricies();
		}
		else
		{
			Project project = Main.instance.project;
			IModel<?, ? extends RenderObjectComponents<?>> model = project.getModel();
			if (model != null || !project.lockedModels.isEmpty())
			{
				if (Main.instance.getEditorMode() == EditorMode.TEXTURE)
				{
			    	float rmx = ((mx - x1) * 2) / sizeW - 1;
			    	float rmy = 1 - ((my - y1) * 2) / sizeH;
			    	Vector4f p1 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, -1, 1)));
			    	p1.mul(1 / p1.w());
			    	Vector4f p2 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, 1, 1)));
			    	p2.mul(1 / p2.w());
	            	float dx = p2.x() - p1.x(), dy = p2.y() - p1.y(), dz = p2.z() - p1.z();
	            	ExtendedAnimationState[] states = project.getStates();
	            	IModel<?, ? extends RenderObjectComponents<?>> clicked = null;
			    	RaytraceResult trace = null;
			    	if (model != null)
			    	{
			    		trace = model.rayTrace(p1.x(), p1.y(), p1.z(), dx, dy, dz, model.getPose(states));
				    	if (trace != null) clicked = model;
			    	}
	            	for (IModel<?, ? extends RenderObjectComponents<?>> lockedModel : project.lockedModels.keySet()) if (lockedModel != model)
	            	{
				    	RaytraceResult t = lockedModel.rayTrace(p1.x(), p1.y(), p1.z(), dx, dy, dz, model.getPose(states));
				    	if (t != null && (trace == null || trace.m > t.m))
				    	{
				    		trace = t;
				    		clicked = lockedModel;
				    	}
	            	}
			    	if (trace != null && clicked == model)
			    	{
			    		if (trace instanceof TextureRaytraceResult)
			    		{
			    			TextureRaytraceResult texRes = (TextureRaytraceResult) trace;
			    			if (texRes.tex != null) Main.instance.tool.onMouseDrag(texRes.tex, prevU, prevV, prevU = texRes.u, prevV = texRes.v, button, trace.hit != this.prevHit);
			    		}
			    		this.prevHit = trace.hit;
			    	}
			    	else this.prevHit = null;
				}
				
			}
		}
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		this.prevHit = null;
		if (button == orbit) orbit = -1;
		else if (button == pan) pan = -1;
		else if (button == look) look = -1;
		else
		{
			Project project = Main.instance.project;
			IModel<?, ?> model = project.getModel();
			if (model != null)
			{
				if (Main.instance.getEditorMode() == EditorMode.TEXTURE)
				{
			    	float rmx = ((mx - x1) * 2) / sizeW - 1;
			    	float rmy = 1 - ((my - y1) * 2) / sizeH;
			    	Vector4f p1 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, -1, 1)));
			    	p1.mul(1 / p1.w());
			    	Vector4f p2 = MathUtils.toVector4f(inverse.transform(new Vector4d(rmx, rmy, 1, 1)));
			    	p2.mul(1 / p2.w());
			    	RaytraceResult trace = model.rayTrace(p1.x(), p1.y(), p1.z(), p2.x() - p1.x(), p2.y() - p1.y(), p2.z() - p1.z(), model.getPose(project.getStates()));
			    	if (trace != null)
			    	{
			    		if (trace instanceof TextureRaytraceResult)
			    		{
			    			TextureRaytraceResult texRes = (TextureRaytraceResult) trace;
			    			if (texRes.tex != null) Main.instance.tool.onMouseRelease(texRes.tex, prevU = texRes.u, prevV = texRes.v, button);
			    		}
			    	}
				}
			}
		}
	}

	@Override
	public boolean canScrollV(float mx, float my)
	{
		return true;
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY)
	{
		orbitZoom *= Math.pow(1.1f, -scrollY);
		updateMatricies();
	}

	@Override
	public boolean onKeyPressed(Key key, int scancode, int mods) //TODO invert?
	{
		if (mods == 0)
		{
			switch (key)
			{
			case R:
			{
				lookX = lookY = lookZ = orbitX = orbitY = orbitZ = orbitPitch = orbitYaw = 0;
				updateMatricies();
				break;
			}
			//case F:
			//{
			//	this.model.getChildren().forEach(editable -> {
			//		if (editable instanceof IComponentParent) fix((IComponentParent) editable);
			//		if (editable instanceof Bone) fix((Bone) editable);
			//	});
			//}
			default:
				return false;
			}
			return true;
		}
		return false;
	}
	
	public void fix(Bone<?> parent)
	{
		parent.children.forEach(bone -> {
			Vector3f trans = bone.defaultTransform.translation;
			trans.y = -trans.y();
			if (bone instanceof IComponentParent) fix((IComponentParent) bone);
			fix(bone);
		});
	}
	
	public void fix(IComponentParent parent)
	{
		parent.getChildrenComponents().forEach(component -> {
			if (component instanceof ComponentBox)
			{
				ComponentBox box = (ComponentBox) component;
				box.posY(-(box.posY() + box.lengthY()));
			}
			if (component instanceof IComponentParent) fix(component);
		});
	}

	@Override
	public boolean onKeyRepeat(Key key, int scancode, int mods)
	{
		if (mods == 0)
		{
			final float trans = 0.0625f;
			switch (key)
			{
			case D:
			{
				Vector4d t = rotationq.invert(new Quaterniond()).get(new Matrix4d()).transform(new Vector4d(trans, 0, 0, 0));
				orbitX += t.x();
				orbitY += t.y();
				orbitZ += t.z();
				updateMatricies();
				break;
			}
			case A:
			{
				Vector4d t = rotationq.invert(new Quaterniond()).get(new Matrix4d()).transform(new Vector4d(-trans, 0, 0, 0));
				orbitX += t.x();
				orbitY += t.y();
				orbitZ += t.z();
				updateMatricies();
				break;
			}
			case W:
			{
				Vector4d t = rotationq.invert(new Quaterniond()).get(new Matrix4d()).transform(new Vector4d(0, trans, 0, 0));
				orbitX += t.x();
				orbitY += t.y();
				orbitZ += t.z();
				updateMatricies();
				break;
			}
			case S:
			{
				Vector4d t = rotationq.invert(new Quaterniond()).get(new Matrix4d()).transform(new Vector4d(0, -trans, 0, 0));
				orbitX += t.x();
				orbitY += t.y();
				orbitZ += t.z();
				updateMatricies();
				break;
			}
			case E:
			{
				Vector4d t = rotationq.invert(new Quaterniond()).get(new Matrix4d()).transform(new Vector4d(0, 0, trans, 0));
				orbitX += t.x();
				orbitY += t.y();
				orbitZ += t.z();
				updateMatricies();
				break;
			}
			case Q:
			{
				Vector4d t = rotationq.invert(new Quaterniond()).get(new Matrix4d()).transform(new Vector4d(0, 0, -trans, 0));
				orbitX += t.x();
				orbitY += t.y();
				orbitZ += t.z();
				updateMatricies();
				break;
			}
			case R:
			{
				orbitX = orbitY = orbitZ = orbitPitch = orbitYaw = 0;
				updateMatricies();
				break;
			}
			default:
				return false;
			}
			return true;
		}
		return false;
	}
}