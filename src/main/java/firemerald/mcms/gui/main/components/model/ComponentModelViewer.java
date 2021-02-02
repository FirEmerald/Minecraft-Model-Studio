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
import firemerald.mcms.shader.DepthBuffer;
import firemerald.mcms.shader.FrameBuffer;
import firemerald.mcms.shader.ModelShader;
import firemerald.mcms.shader.ModelShaderBase;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.ApplicationState;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.IEditable;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.TextureRaytraceResult;
import firemerald.mcms.util.mesh.DrawMode;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;

public class ComponentModelViewer extends Component
{
	public static final int SHADOW_SIZE = 4096;
	public final GuiMain gui;
	private final FrameBuffer fb, widget;
	private final DepthBuffer shadowMap;
	private int sizeW, sizeH;
	double fov = 70 * Math.PI / 180;
	public final Matrix4d modelm = new Matrix4d(), originm = new Matrix4d(), view = new Matrix4d(), projection = new Matrix4d(), inverse = new Matrix4d(), lightMatrix = new Matrix4d();
	public Quaterniond rotationq = new Quaterniond();
	public final GuiMesh renderFB = new GuiMesh();
	public final GuiMesh renderWidget = new GuiMesh();
	public ThemeElement rect;

	public float orbitX = 0, orbitY = 0, orbitZ = 0;
	public float lookX = 0, lookY = 0, lookZ = 0;
	public float orbitYaw = 0, orbitPitch = 0;
	public float orbitZoom = 5;
	public float orbitMX, orbitMY, lookVX, lookVY, lookVZ, panVX, panVY, panVZ, lightMX, lightMY;
	public int orbit = -1, look = -1, pan = -1, light = -1;
	private final Vector3f lightVec = new Vector3f(0, 0, -1);
	public float lightYaw, lightPitch;
	
	public ComponentModelViewer(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2);
		this.gui = gui;
		fb = new FrameBuffer(sizeW = x2 - x1, sizeH = y2 - y1);
		shadowMap = new DepthBuffer(SHADOW_SIZE, SHADOW_SIZE);
		widget = new FrameBuffer(128, 128);
		renderFB.setMesh(x1, y1, x2, y2, 0, 1, 1, 0);
		renderWidget.setMesh(x2 - 128, y1, x2, y1 + 128, 0, 1, 1, 0);
		updateMatricies();
		updateLight();
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	public boolean processModelViewClick(float mx, float my, int button, int mods)
	{
		if (orbit < 0 && pan < 0 && look < 0 && light < 0)
		{
			if ((mods & Modifier.SHIFT.flag) != 0)
			{
				pan = button;
		    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
		    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
		    	Vector3f p1 = MathUtils.toVector3f(rotationq.conjugate(new Quaterniond()).transform(new Vector3d(rmx, rmy, 0)));
		    	panVX = p1.x();
		    	panVY = p1.y();
		    	panVZ = p1.z();
			}
			else if ((mods & Modifier.CONTROL.flag) != 0)
			{
				look = button;
		    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
		    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
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
			return true;
		}
		else return false;
	}
	
	public boolean processLightViewClick(float mx, float my, int button, int mods)
	{
		if (orbit < 0 && pan < 0 && look < 0 && light < 0)
		{
			light = button;
			lightMX = mx;
			lightMY = my;
			return true;
		}
		else return false;
	}
	
	public boolean processToolDrag(float mx, float my, int button)
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
	    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
	    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
	    	Vector3d p1 = rotationq.conjugate(new Quaterniond()).transform(new Vector3d(rmx, rmy, 0));
	    	p1.sub(lookVX, lookVY, lookVZ);
	    	lookVX += p1.x();
	    	lookVY += p1.y();
	    	lookVZ += p1.z();
			lookX += p1.x() * orbitZoom;
			lookY += p1.y() * orbitZoom;
			lookZ += p1.z() * orbitZoom;
			updateMatricies();
		}
		else if (button == pan)
		{
	    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
	    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
	    	Vector3d p1 = rotationq.conjugate(new Quaterniond()).transform(new Vector3d(rmx, rmy, 0));
	    	p1.sub(panVX, panVY, panVZ);
	    	panVX += p1.x();
	    	panVY += p1.y();
	    	panVZ += p1.z();
	    	p1 = rotationq.transform(p1);
			orbitX += p1.x() * orbitZoom;
			orbitY += p1.y() * orbitZoom;
			orbitZ += p1.z() * orbitZoom;
			updateMatricies();
		}
		else if (button == light)
		{
			final float scaleY = MathUtils.DEG_TO_RAD_F * .25f, scaleP = MathUtils.DEG_TO_RAD_F * .25f;
			lightYaw += scaleY * (mx - lightMX);
			lightPitch += scaleP * (my - lightMY);
			lightMX = mx;
			lightMY = my;
			updateLight();
		}
		else return false;
		return true;
	}
	
	public boolean processToolRelease(float mx, float my, int button)
	{
		if (button == orbit) orbit = -1;
		else if (button == pan) pan = -1;
		else if (button == look) look = -1;
		else if (button == light) light = -1;
		else return false;
		return true;
	}
	
	public void updateLight() //TODO fit to objects
	{
		Quaterniond lightRot = new Quaterniond().mul(rotationq).rotateY(lightYaw).rotateX(lightPitch);
		lightRot.transform(lightVec.set(0, 0, -1));
		//lightRot.conjugate();
		/*
		Vector3d[] points = new Vector3d[] {
				new Vector3d(-1, -1, -1),
				new Vector3d(1, -1, -1),
				new Vector3d(-1, 1, -1),
				new Vector3d(1, 1, -1),
				new Vector3d(-1, -1, 1),
				new Vector3d(1, -1, 1),
				new Vector3d(-1, 1, 1),
				new Vector3d(1, 1, 1)
		};
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
		for (Vector3d point : points)
		{
			inverse.transformPosition(point);
			if (point.x < minX) minX = point.x;
			if (point.x > maxX) maxX = point.x;
			if (point.y < minY) minY = point.y;
			if (point.y > maxY) maxY = point.y;
			if (point.z < minZ) minZ = point.z;
			if (point.z > maxZ) maxZ = point.z;
		}
		lightMatrix.setOrtho(minX, maxX, minY, maxY, minZ, maxZ).rotate(lightRot);
		*/
		lightMatrix.setOrtho(-10, 10, -10, 10, -10, 10).rotate(lightRot.conjugate());
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		fb.setSize(sizeW = x2 - x1, sizeH = y2 - y1);
		renderFB.setMesh(x1, y1, x2, y2, 0, 1, 1, 0);
		renderWidget.setMesh(x2 - 128, y1, x2, y1 + 128, 0, 1, 1, 0);
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
		modelm.translate(orbitX, orbitY, orbitZ).mul(rotationq.get(new Matrix4d())).translate(lookX, lookY, lookZ);
		modelm.scale(Main.instance.project.getScale());
		projection.perspective((float) fov, (float) this.getSizeW() / this.getSizeH(), .1f, 1000);
		inverse.identity().mul(projection).mul(view).mul(modelm).invert();
		updateLight();
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
    	    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
    	    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
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
            	if (trace instanceof TextureRaytraceResult)
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
    	model.render(null, map, setTex);
	}
	
	public void renderBones(IModel<?, ?> model, AnimationState[] states)
	{
    	ApplicationState state = Main.instance.state;
    	Map<String, Matrix4d> map = model.getPose(states);
		for (Bone<?> bone : model.getRootBones()) RenderUtil.renderSkeleton(bone, map, state.showNodes(), state.showBones());
	}

	private void renderPass(Main main, Project project)
	{
    	IModel<?, ?> model = project.getModel();
    	ExtendedAnimationState[] states = project.getStates();
    	if (model != null)
    	{
        	Map<String, Matrix4d> map = model.getPose(states);
        	project.bindTex();
        	main.currentModelShader.setOverlayTexture(main.getOverlay());
        	model.render(null, map, () -> project.bindTex());
        	main.currentModelShader.setOverlayTexture(null);
        	project.unbindTex();
    	}
    	project.lockedModels.forEach((m, tex) -> {
    		if (m != model)
    		{
        		if (tex != null) tex.bind();
        		else main.textureManager.unbindTexture();
        		renderModel(m, states, tex == null ? main.textureManager::unbindTexture : tex::bind);
    		}
    	});
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		RenderUtil.disableScissor();
    	Main.listGLErrors("pre-pre-render");
		Main main = Main.instance;
		Project project = main.project;
    	ApplicationState state = main.state;
		rect.bind();
		renderFB.render();
    	main.guiShader.unbind();
    	
    	boolean enableShadows = main.state.enableShadows();
    	
    	if (enableShadows)
    	{
        	Main.listGLErrors("pre-shadow");
        	main.currentModelShader = main.shadowShader;
    		glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.frameBuffer);
    		glDrawBuffer(GL_NONE);
    		glReadBuffer(GL_NONE);
        	main.shadowShader.reset();
        	main.shadowShader.bind();
    		glViewport(0, 0, SHADOW_SIZE, SHADOW_SIZE);
    		glDepthMask(true);
    		glEnable(GL_DEPTH_TEST);
    		glClear(GL_DEPTH_BUFFER_BIT);
            glDepthFunc(GL_LESS);
            glEnable(GL_CULL_FACE);
    		modelm.get(ModelShaderBase.MODEL.matrix());
    		ModelShaderBase.VIEW.matrix().identity();
    		lightMatrix.get(ModelShaderBase.PROJECTION.matrix());
    		ModelShaderBase.TEXTURE.matrix().identity();
    		main.currentModelShader.updateModelViewProjection();
    		main.currentModelShader.updateTexture();
        	this.renderPass(main, project);
        	main.currentModelShader.unbind();
        	/**/
        	Main.listGLErrors("post-shadow");
    	}
    	/**/
    	main.currentModelShader = main.modelShader;

		glBindFramebuffer(GL_FRAMEBUFFER, fb.frameBuffer);
		main.currentModelShader.reset();
		main.currentModelShader.bind();
		main.currentModelShader.setLight(lightVec);
		glViewport(0, 0, getSizeW(), getSizeH());
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);

        //Main.saveFloatTexture(shadowMap.depth_texture);
        
		modelm.get(ModelShaderBase.MODEL.matrix().identity());
		view.get(ModelShaderBase.VIEW.matrix().identity());
		projection.get(ModelShaderBase.PROJECTION.matrix().identity());
		ModelShaderBase.TEXTURE.matrix().identity();
		lightMatrix.get(ModelShader.LIGHT.matrix());
		main.currentModelShader.updateModelViewProjection();
		main.currentModelShader.updateTexture();
		main.modelShader.updateLightSpace();
    	Main.listGLErrors("pre-render");
    	if (!state.showNodes() && !state.showBones() && (pan >= 0 || look >= 0 || Modifier.CONTROL.isDown(main.window) || Modifier.SHIFT.isDown(main.window)))
    	{
    		ModelShaderBase.MODEL.push();
    		originm.get(ModelShaderBase.MODEL.matrix());
    		main.currentModelShader.updateModel();
    		main.textureManager.unbindTexture();
    		RenderUtil.SPHERE_MESH.render();
    		ModelShaderBase.MODEL.pop();
    		main.currentModelShader.updateModel();
    	}
    	if (enableShadows) main.modelShader.setShadowTexture(shadowMap.depth_texture);
    	renderPass(main, project);
		if (enableShadows) main.modelShader.setShadowTexture(0);
    	IModel<?, ?> model = project.getModel();
    	ExtendedAnimationState[] states = project.getStates();
		if (state.showNodes() || state.showBones())
		{
			glClear(GL_DEPTH_BUFFER_BIT);
	    	if (pan >= 0 || look >= 0 || Modifier.CONTROL.isDown(main.window) || Modifier.SHIFT.isDown(main.window))
	    	{
	    		ModelShaderBase.MODEL.push();
	    		originm.get(ModelShaderBase.MODEL.matrix());
	    		main.currentModelShader.updateModel();
	    		main.textureManager.unbindTexture();
	    		RenderUtil.SPHERE_MESH.render();
	    		ModelShaderBase.MODEL.pop();
	    		main.currentModelShader.updateModel();
	    	}
	    	if (model != null) renderBones(model, states);
	    	else
	    	{
	    		Skeleton skeleton = main.project.getSkeleton();
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
    	if (pan >= 0 || look >= 0 || Modifier.CONTROL.isDown(main.window) || Modifier.SHIFT.isDown(main.window))
    	{
    		ModelShaderBase.MODEL.push();
    		originm.get(ModelShaderBase.MODEL.matrix());
    		main.currentModelShader.updateModel();
    		main.textureManager.unbindTexture();
    		RenderUtil.SPHERE_MESH.render();
    		main.currentModelShader.setColor(0, 0, 0, .5f);
    		GL11.glDepthFunc(GL11.GL_GREATER);
    		RenderUtil.SPHERE_MESH.render();
    		main.currentModelShader.setColor(1, 1, 1, 1);
    		GL11.glDepthFunc(GL11.GL_LEQUAL);
    		ModelShaderBase.MODEL.pop();
    		main.currentModelShader.updateModel();
    	}
        glDisable(GL_CULL_FACE);
        
        //render widget
		glBindFramebuffer(GL_FRAMEBUFFER, widget.frameBuffer);
		main.currentModelShader.reset();
		main.currentModelShader.bind();
		main.currentModelShader.setLight(lightVec);
		glViewport(0, 0, 128, 128);
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDepthFunc(GL_LESS);
		ModelShaderBase.MODEL.matrix().set(rotationq);
		ModelShaderBase.VIEW.matrix().identity().translate(0, 0, -2.5);
		ModelShaderBase.PROJECTION.matrix().setPerspective(fov, 1, 1, 4);
		ModelShaderBase.TEXTURE.matrix().identity();
		main.currentModelShader.updateModelViewProjection();
		main.currentModelShader.updateTexture();
		main.currentModelShader.setIgnoreLighting(true);
		main.textureManager.unbindTexture();
		GL11.glPointSize(9);
		Main.modelTempMesh.drawMode = DrawMode.LINES;
		main.currentModelShader.setColor(1, 0, 0, 1);
		Main.modelTempMesh.setMesh(new float[] {0, 0, 0, 1, 0, 0}, new float[4], new float[6], new int[] {0, 1});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.POINTS;
		Main.modelTempMesh.setMesh(new float[] {-1, 0, 0, 1, 0, 0}, new float[4], new float[6], new int[] {0, 1});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.LINES;
		main.currentModelShader.setColor(0, 1, 0, 1);
		Main.modelTempMesh.setMesh(new float[] {0, 0, 0, 0, 1, 0}, new float[4], new float[6], new int[] {0, 1});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.POINTS;
		Main.modelTempMesh.setMesh(new float[] {0, -1, 0, 0, 1, 0}, new float[4], new float[6], new int[] {0, 1});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.LINES;
		main.currentModelShader.setColor(0, 0, 1, 1);
		Main.modelTempMesh.setMesh(new float[] {0, 0, 0, 0, 0, 1}, new float[4], new float[6], new int[] {0, 1});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.POINTS;
		Main.modelTempMesh.setMesh(new float[] {0, 0, -1, 0, 0, 1}, new float[4], new float[6], new int[] {0, 1});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.LINES;
		main.currentModelShader.setColor(1, 1, 1, 1);
		ModelShaderBase.MODEL.matrix().identity();
		main.currentModelShader.updateModelViewProjection();
		GL11.glPointSize(5);
		float lX = lightVec.x * -1.25f, lY = lightVec.y * -1.25f, lZ = lightVec.z * -1.25f;
		Main.modelTempMesh.setMesh(new float[] {0, 0, 0, lX, lY, lZ}, new float[4], new float[6], new int[] {0, 1});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.POINTS;
		Main.modelTempMesh.setMesh(new float[] {lX, lY, lZ}, new float[2], new float[3], new int[] {0});
		Main.modelTempMesh.render();
		Main.modelTempMesh.drawMode = DrawMode.TRIANGLES;
		GL11.glPointSize(1);
		
    	Main.listGLErrors("post-render");
    	main.currentModelShader.unbind();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
    	main.guiShader.bind();
		main.guiShader.updateModelViewProjection();
		main.guiShader.updateTexture();
		RenderUtil.enableScissor();
		glViewport(0, 0, main.sizeW, main.sizeH);
		glDepthMask(false);
		glDisable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glBindTexture(GL_TEXTURE_2D, fb.frameBufferTex);
		renderFB.render();
        glBindTexture(GL_TEXTURE_2D, widget.frameBufferTex);
		renderWidget.render();
    	Main.listGLErrors("post-post-render");
	}
	
	private double prevU, prevV;
	private IRaytraceTarget prevHit = null;

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (!Main.instance.tool.onModelViewClick(this, mx, my, button, mods))
		{
			Main main = Main.instance;
			Project project = main.project;
			IModel<?, ? extends RenderObjectComponents<?>> model = project.getModel();
			if (model != null || !project.lockedModels.isEmpty())
			{
		    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
		    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
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
		    		if (trace instanceof TextureRaytraceResult)
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

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (!processToolDrag(mx, my, button) && !Main.instance.tool.onModelViewDrag(this, mx, my, button))
		{
			Project project = Main.instance.project;
			IModel<?, ? extends RenderObjectComponents<?>> model = project.getModel();
			if (model != null || !project.lockedModels.isEmpty())
			{
		    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
		    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
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
	

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		this.prevHit = null;
		if (!this.processToolRelease(mx, my, button) && !Main.instance.tool.onModelViewRelease(this, mx, my, button))
		{
			Project project = Main.instance.project;
			IModel<?, ?> model = project.getModel();
			if (model != null)
			{
		    	float rmx = ((mx - x1) * 2) / getSizeW() - 1;
		    	float rmy = 1 - ((my - y1) * 2) / getSizeH();
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
				lookX = lookY = lookZ = orbitX = orbitY = orbitZ = orbitPitch = orbitYaw = lightPitch = lightYaw = 0;
				updateMatricies();
				updateLight();
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
			default:
				return false;
			}
			return true;
		}
		return false;
	}

	public int getSizeW()
	{
		return sizeW;
	}

	public int getSizeH()
	{
		return sizeH;
	}
}