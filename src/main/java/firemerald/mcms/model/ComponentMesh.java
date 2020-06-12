package firemerald.mcms.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.TextureRaytraceResult;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.util.mesh.Mesh.DrawMode;

public abstract class ComponentMesh extends ModelComponent
{
	protected final Mesh mesh;
	protected final Mesh texMesh = new Mesh(DrawMode.LINES);
	
	public ComponentMesh(Mesh mesh, String name)
	{
		super(name);
		this.mesh = mesh;
	}
	
	public ComponentMesh(Mesh mesh, IComponentParent parent, String name)
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
	public void doRender(Runnable defaultTexture)
	{
		boolean traced = (Main.instance.trace != null) && (Main.instance.trace.hit == this);
		if (traced) Main.instance.shader.setColor(.5f, .5f, 1, 1);
		mesh.render();
		if (traced) Main.instance.shader.setColor(1, 1, 1, 1);
	}
	
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
		float[] vertsArray = new float[verts.size() * 3 / 2];
		float[] texsArray = new float[verts.size()];
		int j = 0;
		for (int i = 0; i < vertsArray.length; i += 3)
		{
			vertsArray[i] = texsArray[j] = verts.get(j);
			vertsArray[i + 1] = texsArray[j + 1] = verts.get(j + 1);
			vertsArray[i + 2] = 0;
			j += 2;
		}
		texMesh.setMesh(vertsArray, texsArray, new float[vertsArray.length], indsArray);
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

	public Mesh mesh()
	{
		return mesh;
	}
	
	@Override
	protected RaytraceResult doRaytrace(float fx, float fy, float fz, float dx, float dy, float dz, Matrix4d transformation)
	{
		RaytraceResult result = null;
		if (mesh instanceof Mesh)
		{
			Vector3f res = MathUtil.rayTraceMeshUV(fx, fy, fz, dx, dy, dz, mesh, transformation);
			if (res != null && (result == null || res.x() < result.m)) result = new TextureRaytraceResult(this, res.x(), Main.instance.project.getTexture(), res.y(), res.z()); //TODO texture
		}
		return result;
	}
	
	@Override
	public void drawOnTexture(float x, float y, float sizeX, float sizeY)
	{
		GL11.glEnable(GL11.GL_BLEND);
		//GL14.glBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
		//GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		if (texMesh.drawMode.stride < 3) Main.instance.shader.setColor(0, 0, 0, 1);
		else Main.instance.shader.setColor(0, 0, 0, 0.5f);
		Main.instance.textureManager.unbindTexture();
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x, y, 0);
		Shader.MODEL.matrix().scale(sizeX, sizeY, 1);
		Main.instance.shader.updateModel();
		texMesh.render();
		Main.instance.shader.setColor(1, 1, 1, 1);
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}