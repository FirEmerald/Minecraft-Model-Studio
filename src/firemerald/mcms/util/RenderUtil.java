package firemerald.mcms.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.util.Map;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.math.Vec4;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.api.util.MatrixHandler;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;

public class RenderUtil
{
	private static int stencil = 0;
	
	public static final Mesh STENCIL_CLEAR = new Mesh(-1, 1, 1, -1, 0, 0, 0, 1, 1);
	
	public static final Mesh SKELETON_NODE_BOX = new Mesh(new float[] {
			 .09375f, -.09375f,  .09375f,
			 .09375f, -.09375f, -.09375f,
			 .09375f,  .09375f, -.09375f,
			 .09375f,  .09375f,  .09375f,
			-.09375f,  .09375f,  .09375f,
			 .09375f,  .09375f,  .09375f,
			 .09375f,  .09375f, -.09375f,
			-.09375f,  .09375f, -.09375f,
			-.09375f, -.09375f,  .09375f,
			 .09375f, -.09375f,  .09375f,
			 .09375f,  .09375f,  .09375f,
			-.09375f,  .09375f,  .09375f,
			-.09375f, -.09375f, -.09375f,
			-.09375f, -.09375f,  .09375f,
			-.09375f,  .09375f,  .09375f,
			-.09375f,  .09375f, -.09375f,
			 .09375f, -.09375f,  .09375f,
			-.09375f, -.09375f,  .09375f,
			-.09375f, -.09375f, -.09375f,
			 .09375f, -.09375f, -.09375f,
			 .09375f, -.09375f, -.09375f,
			-.09375f, -.09375f, -.09375f,
			-.09375f,  .09375f, -.09375f,
			 .09375f,  .09375f, -.09375f
	}, new float[] {
			0, .5f,
			.25f, .5f,
			.25f, 1,
			0, 1,
			.25f, 0,
			.5f, 0,
			.5f, .5f,
			.25f, .5f,
			.25f, .5f,
			.5f, .5f,
			.5f, 1,
			.25f, 1,
			.5f, .5f,
			.75f, .5f,
			.75f, 1,
			.5f, 1,
			.5f, 0,
			.75f, 0,
			.75f, .5f,
			.5f, .5f,
			.75f, .5f,
			1, .5f,
			1, 1,
			.75f, 1
	}, new float[] {
			1, 0, 0,
			1, 0, 0,
			1, 0, 0,
			1, 0, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, -1, 0,
			0, 0, -1,
			0, 0, -1,
			0, 0, -1,
			0, 0, -1
	}, new int[] {
			0, 1, 3,
			3, 1, 2,
			4, 5, 7,
			7, 5, 6,
			8, 9, 11,
			11, 9, 10,
			12, 13, 15,
			15, 13, 14,
			16, 17, 19,
			19, 17, 18,
			20, 21, 23,
			23, 21, 22
	}, Mesh.DrawMode.TRIANGLES, GL_STATIC_DRAW);
	
	public static final Mesh BONE_MESH;
	
	static
	{
		Mesh boneMesh = null;
		try
		{
			boneMesh = ObjUtil.makeMesh(new ObjData(Main.getResource("bone.obj")));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		BONE_MESH = boneMesh;
	}
	
	public static void renderSkeleton(Bone bone, Map<String, Matrix4> transforms)
	{
		Main.instance.textureManager.bindTexture(Textures.BOX);
		Main.MODMESH.drawMode = Mesh.DrawMode.LINES;
		Main.MODMESH.setMesh(new float[] {
				0, 0, 0,
				0, 0, 1
		}, new float[4], new float[] {
				0, 0, 1,
				0, 0, 1
		}, new int[] {
				0, 1
		});
		generateSkeletonMesh(bone, transforms);
		Main.MODMESH.drawMode = Mesh.DrawMode.TRIANGLES;
	}
	
	public static void generateSkeletonMesh(Bone bone, Map<String, Matrix4> transforms)
	{
		Matrix4 m = transforms.get(bone.name);
		MatrixHandler.instance.push();
		if (m != null) MatrixHandler.instance.multMatrix(m);
		else m = new Matrix4();
		Main.instance.textureManager.bindTexture(Textures.BOX);
		SKELETON_NODE_BOX.render();
		for (Bone child : bone.children)
		{
			Matrix4 m2 = transforms.get(child.name);
			Vec4 v = new Vec4(0, 0, 0, 1);
			if (m2 != null) v = m2.mul(v);
			Vec3 pos = v.xyz();
			float mag = pos.magnitude();
			if (mag > 0)
			{
				Main.instance.shader.setColor(.5f, .5f, .5f, 1);
				Matrix4 o = new Matrix4().setLookAlongYZ(pos).scale(mag);
				Main.instance.textureManager.unbindTexture();
				MatrixHandler.instance.push();
				MatrixHandler.instance.multMatrix(o);
				BONE_MESH.render();
				//Main.MODMESH.render();
				MatrixHandler.instance.pop();
				Main.instance.shader.setColor(1, 1, 1, 1);
			}
			generateSkeletonMesh(child, transforms);
		}
		MatrixHandler.instance.pop();
	}
	
	public static void pushStencil()
	{
		if (stencil >= 255)
		{
			Main.LOGGER.log(Level.FATAL, "Stencil buffer overflow");
			Thread.dumpStack();
			System.exit(-1);
		}
		else if (stencil == 0)
		{
			glEnable(GL_STENCIL_TEST);
			stencil = 1;
		}
		else
		{
			stencil++;
		}
	}
	
	public static void popStencil()
	{
		if (stencil <= 0)
		{
			Main.LOGGER.log(Level.FATAL, "Stencil buffer underflow");
			Thread.dumpStack();
			System.exit(-1);
		}
		else if (stencil == 1)
		{
			glDisable(GL_STENCIL_TEST);
			glClear(GL_STENCIL_BUFFER_BIT);
			stencil = 0;
		}
		else
		{
			Shader.MODEL.push();
			Shader.VIEW.push();
			Shader.PROJECTION.push();
			Shader.MODEL.matrix().identity();
			Shader.VIEW.matrix().identity();
			Shader.PROJECTION.matrix().identity();
			Main.instance.shader.updateModelViewProjection();
			startStencil(true);
			STENCIL_CLEAR.render();
			stencil--;
			endStencil();
			Shader.MODEL.pop();
			Shader.VIEW.pop();
			Shader.PROJECTION.pop();
			Main.instance.shader.updateModelViewProjection();
		}
	}
	
	public static void startStencil(boolean subtract)
	{
		glColorMask(false, false, false, false);
		if (subtract)
		{
			glStencilFunc(GL_NOTEQUAL, stencil, 0xFF);
			glStencilOp(GL_DECR, GL_KEEP, GL_KEEP);
		}
		else
		{
			glStencilFunc(GL_NOTEQUAL, stencil - 1, 0xFF);
			glStencilOp(GL_INCR, GL_KEEP, GL_KEEP);
		}
		glStencilMask(0xFF);
	}
	
	public static void endStencil()
	{
		glColorMask(true, true, true, true);
		glStencilMask(0x00);
		glStencilFunc(GL_EQUAL, stencil, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
	}
}