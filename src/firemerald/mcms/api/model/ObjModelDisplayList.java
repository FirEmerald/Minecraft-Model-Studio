package firemerald.mcms.api.model;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.math.Matrix3;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Vec2;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.math.Vec4;

public class ObjModelDisplayList extends ObjModel
{
	public ObjModelDisplayList(File modelFile, Skeleton skeleton) throws Exception
	{
		super(modelFile, skeleton);
	}
	/*
	public ObjModelDisplayList(ResourceLocation modelFile, Skeleton skeleton) throws Exception
	{
		super(modelFile, skeleton);
	}
	*/
	public ObjModelDisplayList(InputStream model, String modelName, Skeleton skeleton) throws Exception
	{
		super(model, modelName, skeleton);
	}

	@Override
	protected Bone makeObj(String name, Transformation transform, Bone parent, List<int[][]> mesh, ObjData obj, Skeleton skeleton)
	{
		Matrix4 m = skeleton.inverts.get(name);
		Matrix3 n = null;
		if (m != null) n = m.transpose3().invert();
		int drawMode = -1;
		int list = glGenLists(1);
		glNewList(list, GL_COMPILE);
		for (int[][] face : mesh)
		{
			int mode;
			switch (face.length)
			{
			case 1: mode = GL_POINTS; break;
			case 2: mode = GL_LINES; break;
			case 3: mode = GL_TRIANGLES; break;
			case 4: mode = GL_QUADS; break;
			default: mode = -1; break;
			}
			if (mode != drawMode)
			{
				if (drawMode != -1) glEnd();
				if ((drawMode = mode) != -1) glBegin(drawMode);
			}
			if (drawMode != -1)
			{
				for (int i = 0; i < face.length; i++)
				{
					int[] data = face[i];
					int vert = data[0];
					int tex = data[1];
					int norm = data[2];
					if (norm >= 0)
					{
						Vec3 normal = obj.vertexNormals.get(norm);
						if (n != null) normal = n.mul(normal, new Vec3());
						glNormal3d(normal.x(), normal.y(), normal.z());
					}
					else glNormal3f(0, 0, 0);
					if (tex >= 0)
					{
						Vec2 texture = obj.textureCoordinates.get(tex);
						glTexCoord2d(texture.x(), texture.y());
					}
					else glTexCoord2f(0, 0);
					Vec3 vertex = obj.vertices.get(vert);
					Vec4 vertexVec = new Vec4(vertex, 1);
					if (m != null) vertexVec = m.mul(vertexVec, new Vec4());
					glVertex3d(vertexVec.x(), vertexVec.y(), vertexVec.z());
				}
			}
		}
		if (drawMode != -1) glEnd();
		glEndList();
		return new RenderObjectDisplayList(name, transform, parent, list);
	}
	/*
	public static ObjModelDisplayList tryLoadModel(ResourceLocation model, Skeleton skeleton)
	{
		try
		{
			return new ObjModelDisplayList(model, skeleton);
		}
		catch (Exception e)
		{
			Main.LOGGER.error("failed to load model at " + model, e);
			return null;
		}
	}

	public static ObjModelDisplayList tryLoadModel(ResourceLocation model, ResourceLocation skeleton)
	{
		Skeleton skel = Skeleton.tryLoadXML(skeleton);
		if (skel == null) return null;
		else try
		{
			return new ObjModelDisplayList(model, skel);
		}
		catch (Exception e)
		{
			Main.LOGGER.error("failed to load model at " + model, e);
			return null;
		}
	}
	*/
}