package firemerald.mcms.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.TextureRaytraceResult;
import firemerald.mcms.util.mesh.DrawMode;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.util.mesh.ModelMesh;

public abstract class ComponentMesh extends ModelComponent
{
	protected final ModelMesh mesh;
	protected final GuiMesh texMesh = new GuiMesh(DrawMode.LINES);
	
	public ComponentMesh(ModelMesh mesh, String name)
	{
		super(name);
		this.mesh = mesh;
	}
	
	public ComponentMesh(ModelMesh mesh, IComponentParent parent, String name)
	{
		super(parent, name);
		this.mesh = mesh;
	}
	
	public ComponentMesh(IComponentParent parent, ComponentMesh from)
	{
		super(parent, from);
		this.mesh = from.mesh.copy();
	}

	@Override
	public void doRender(Object holder, Matrix4d currentTransform, Runnable defaultTexture)
	{
		boolean traced = (Main.instance.trace != null) && (Main.instance.trace.hit == this);
		if (traced) Main.instance.guiShader.setColor(.5f, .5f, 1, 1);
		mesh.render();
		if (traced) Main.instance.guiShader.setColor(1, 1, 1, 1);
	}

	@Override
	public void doTick(Object holder, Matrix4d currentTransform, float deltaTime) {}
	
	public void setTexMesh()
	{
		float[] meshUV = this.mesh.getTexs();
		if (mesh.drawMode.stride < 3) texMesh.drawMode = mesh.drawMode;
		else texMesh.drawMode = DrawMode.LINES;
		int[] meshInds = mesh().getIndicies();
		List<Float> verts = new ArrayList<>();
		List<Integer> inds = new ArrayList<>();
		switch (mesh().drawMode)
		{
		case POINTS:
		case LINES:
		case LINE_LOOP:
		case LINE_STRIP:
		{
			for (int ind : meshInds) inds.add(ind);
			for (float uv : meshUV) verts.add(uv);
			break;
		}
		case TRIANGLES:
		{
			for (int i = 0; i < meshInds.length; i += 3)
			{
				int ind0 = meshInds[i] * 2;
				int ind1 = meshInds[i + 1] * 2;
				int ind2 = meshInds[i + 2] * 2;
				inds.add(i);
				inds.add(i + 1);
				inds.add(i + 1);
				inds.add(i + 2);
				inds.add(i + 2);
				inds.add(i);
				verts.add(meshUV[ind0]);
				verts.add(meshUV[ind0 + 1]);
				verts.add(meshUV[ind1]);
				verts.add(meshUV[ind1 + 1]);
				verts.add(meshUV[ind2]);
				verts.add(meshUV[ind2 + 1]);
			}
			break;
		}
		}
		int[] indsArray = new int[inds.size()];
		for (int i = 0; i < indsArray.length; i++) indsArray[i] = inds.get(i);
		float[] vertsArray = new float[verts.size()];
		float[] texsArray = new float[verts.size()];
		for (int i = 0; i < vertsArray.length; i ++) vertsArray[i] = texsArray[i] = verts.get(i);
		texMesh.setMesh(vertsArray, texsArray, indsArray);
	}

	@Override
	public void doCleanUp()
	{
		mesh.cleanUp();
		texMesh.cleanUp();
	}

	@Override
	public float[][][] generateMesh()
	{
		int length = mesh.drawMode.stride;
		if (length > 0)
		{
			float[] verts = this.mesh.getVerticies();
			float[] texs = this.mesh.getTexs();
			float[] norms = this.mesh.getNormals();
			int[] inds = this.mesh.getIndicies();
			int size = inds.length / length;
			float[][][] faces = new float[size][][];
			int f = 0;
			for (int i = 0; i < inds.length; i += length)
			{
				float[][] face = faces[f++] = new float[length][];
				for (int j = 0; j < length; j++)
				{
					int v = inds[i + j];
					int vInd = v * 3;
					int tInd = v * 2;
					face[j] = new float[] {
							verts[vInd], verts[vInd + 1], verts[vInd + 2],
							texs[tInd], texs[tInd + 1],
							norms[vInd], norms[vInd + 1], norms[vInd + 2]
					};
				}
			}
			return faces;
		}
		else
		{
			GuiPopupException.onException("Failed to save mesh to OBJ file: unknown draw mode " + this.mesh().drawMode);
			return new float[0][0][0];
		}
	}

	public ModelMesh mesh()
	{
		return mesh;
	}
	
	@Override
	protected RaytraceResult doRaytrace(float fx, float fy, float fz, float dx, float dy, float dz, Matrix4d transformation)
	{
		RaytraceResult result = null;
		if (mesh != null)
		{
			Texture tex = this.getTexture();
			if (tex != null)
			{
				Vector3f res = MathUtil.rayTraceMeshUV(fx, fy, fz, dx, dy, dz, mesh, transformation);
				if (res != null && (result == null || res.x() < result.m)) result = new TextureRaytraceResult(this, res.x(), tex, res.y(), res.z());
			}
			else
			{
				Float res = MathUtil.rayTraceMesh(fx, fy, fz, dx, dy, dz, mesh, transformation);
				if (res != null && (result == null || res < result.m)) result = new RaytraceResult(this, res);
			}
		}
		return result;
	}
	
	@Override
	public void drawOnTexture(float x, float y, float sizeX, float sizeY)
	{
		GL11.glEnable(GL11.GL_BLEND);
		//GL14.glBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
		//GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		if (texMesh.drawMode.stride < 3) Main.instance.guiShader.setColor(0, 0, 0, 1);
		else Main.instance.guiShader.setColor(0, 0, 0, 0.5f);
		Main.instance.textureManager.unbindTexture();
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x, y, 0);
		GuiShader.MODEL.matrix().scale(sizeX, sizeY, 1);
		Main.instance.guiShader.updateModel();
		texMesh.render();
		Main.instance.guiShader.setColor(1, 1, 1, 1);
		GuiShader.MODEL.pop();
		Main.instance.guiShader.updateModel();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}