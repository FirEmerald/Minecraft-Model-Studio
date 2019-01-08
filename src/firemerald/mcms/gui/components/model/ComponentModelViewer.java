package firemerald.mcms.gui.components.model;

import static org.lwjgl.opengl.GL11.*;

import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Vec4;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.FrameBuffer;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.theme.RoundedBoxFormat;

public class ComponentModelViewer extends Component
{
	public IModel model;
	private final FrameBuffer fb;
	private int sizeW, sizeH;
	double fov = 70 * Math.PI / 180;
	public final Matrix4 modelm = new Matrix4(), view = new Matrix4(), projection = new Matrix4(), inverse = new Matrix4();
	public final Mesh renderFB = new Mesh();
	public final EditorPanes editorPanes;
	public Texture tex;
	public RoundedBoxFormat rect;
	
	public ComponentModelViewer(float x1, float y1, float x2, float y2, EditorPanes editorPanes, Texture tex)
	{
		super(x1, y1, x2, y2);
		this.tex = tex;
		this.editorPanes = editorPanes;
		fb = new FrameBuffer(sizeW = (int) (x2 - x1), sizeH = (int) (y2 - y1));
		renderFB.setMesh(x1, y1, x2, y2, 0, 0, 1, 1, 0);
		updateMatricies();
		rect = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1));
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		fb.setSize(sizeW = (int) (x2 - x1), sizeH = (int) (y2 - y1));
		renderFB.setMesh(x1, y1, x2, y2, 0, 0, 1, 1, 0);
		updateMatricies();
		rect = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1));
	}
	
	public void updateMatricies()
	{
		modelm.identity();
		view.identity();
		projection.identity();
		view.translate(0, 0, 0.01f * (float) (-this.sizeH / (2 * Math.tan(fov * 0.5))));
		projection.perspective((float) fov, (float) this.sizeW / this.sizeH, .1f, 1000);
		inverse.identity().mul(projection).mul(view).mul(modelm).invert();
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		Shader shader = main.shader;
		main.theme.bindRoundedBox(rect);
		renderFB.render();
		Shader.MODEL.push();
		Shader.VIEW.push();
		Shader.PROJECTION.push();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fb.frameBuffer);
		shader.bind();
		glViewport(0, 0, sizeW, sizeH);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glDepthFunc(GL_LESS);

		modelm.copy(Shader.MODEL.matrix());
		view.copy(Shader.VIEW.matrix());
		projection.copy(Shader.PROJECTION.matrix());
		shader.updateModel();
		shader.updateView();
		shader.updateProjection();
    	Main.listGLErrors("pre-render");
    	
    	tex.bind();

    	Map<String, Matrix4> map = model.getPose();
    	if (canHover && this.contains(mx, my))
    	{
	    	float rmx = ((mx - x1) * 2) / sizeW - 1;
	    	float rmy = 1 - ((my - y1) * 2) / sizeH;
        	Vec4 p1 = inverse.mul(new Vec4(rmx, rmy, -1, 1));
        	p1.scale(1 / p1.w());
        	Vec4 p2 = inverse.mul(new Vec4(rmx, rmy, 1, 1));
        	p2.scale(1 / p2.w());
        	main.trace = model.rayTrace(p1.x(), p1.y(), p1.z(), p2.x() - p1.x(), p2.y() - p1.y(), p2.z() - p1.z(), map);
    	}
    	model.render(map);
    	
		/*
		Bone bone = model.getBase();
		glClear(GL_DEPTH_BUFFER_BIT);
		RenderUtil.renderSkeleton(bone, map);
		*/
		
		//TODO rendering
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		Shader.MODEL.pop();
		Shader.VIEW.pop();
		Shader.PROJECTION.pop();
		shader.bind();
		shader.updateModel();
		shader.updateView();
		shader.updateProjection();
		glViewport(0, 0, main.sizeW, main.sizeH);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.frameBufferTex);
		renderFB.render();
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			Main main = Main.instance;
	    	float rmx = ((mx - x1) * 2) / sizeW - 1;
	    	float rmy = 1 - ((my - y1) * 2) / sizeH;
	    	Vec4 p1 = inverse.mul(new Vec4(rmx, rmy, -1, 1));
	    	p1.scale(1 / p1.w());
	    	Vec4 p2 = inverse.mul(new Vec4(rmx, rmy, 1, 1));
	    	p2.scale(1 / p2.w());
	    	RaytraceResult trace = model.rayTrace(p1.x(), p1.y(), p1.z(), p2.x() - p1.x(), p2.y() - p1.y(), p2.z() - p1.z(), model.getPose());
	    	if (trace != null)
	    	{
	    		if (trace.hit != main.editing)
	    		{
		    		if (main.editing != null) main.editing.onDeselect(editorPanes);
		    		if (trace.hit instanceof IEditable) (main.editing = (IEditable) trace.hit).onSelect(editorPanes);
		    		else main.editing = null;
	    		}
	    	}
	    	else if (main.editing != null)
	    	{
	    		main.editing.onDeselect(editorPanes);
	    		main.editing = null;
	    	}
		}
	}
}