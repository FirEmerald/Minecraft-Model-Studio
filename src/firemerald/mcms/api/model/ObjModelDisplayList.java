package firemerald.mcms.api.model;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.math.MathUtils;

public class ObjModelDisplayList extends ObjModel
{
	public ObjModelDisplayList(File modelFile, ISkeleton skeleton) throws Exception
	{
		super(modelFile, skeleton);
	}
	/*
	public ObjModelDisplayList(ResourceLocation modelFile, Skeleton skeleton) throws Exception
	{
		super(modelFile, skeleton);
	}
	*/
	public ObjModelDisplayList(InputStream model, String modelName, ISkeleton skeleton) throws Exception
	{
		super(model, modelName, skeleton);
	}

	@Override
	protected Bone makeObj(String name, Transformation transform, Bone parent, List<int[][]> mesh, ObjData obj, ISkeleton skeleton)
	{
		Matrix4d m = skeleton.getInverseTransforms().get(name);
		Matrix3d n = null;
		if (m != null) n = m.transpose3x3(new Matrix3d()).invert();
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
						Vector3f normal = obj.vertexNormals.get(norm);
						if (n != null) normal = n.transform(normal, new Vector3f());
						glNormal3f(normal.x(), normal.y(), normal.z());
					}
					else glNormal3f(0, 0, 0);
					if (tex >= 0)
					{
						Vector2f texture = obj.textureCoordinates.get(tex);
						glTexCoord2f(texture.x(), texture.y());
					}
					else glTexCoord2f(0, 0);
					Vector3f vertex = obj.vertices.get(vert);
					Vector4f vertexVec = new Vector4f(vertex, 1);
					if (m != null) vertexVec = MathUtils.toVector4f(m.transform(MathUtils.toVector4d(vertexVec)));
					glVertex3f(vertexVec.x(), vertexVec.y(), vertexVec.z());
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