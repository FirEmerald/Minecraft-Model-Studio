package firemerald.mcms.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.math.MathUtils;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.api.model.ObjModel;
import firemerald.mcms.api.model.Skeleton;
import firemerald.mcms.api.model.ISkeleton;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.util.mesh.Mesh;

public class VBOObjModel extends ObjModel
{	
	public VBOObjModel(File modelFile, ISkeleton skeleton) throws Exception
	{
		super(modelFile, skeleton);
	}

	@Override
	protected Bone makeObj(String name, Transformation transform, Bone parent, List<int[][]> mesh, ObjData obj, ISkeleton skeleton)
	{
		Matrix4d m = skeleton.getInverseTransforms().get(name);
		Matrix3d n = null;
		if (m != null) n = m.transpose3x3(new Matrix3d()).invert();
		List<Float> verts = new ArrayList<>();
		List<Float> texs = new ArrayList<>();
		List<Float> norms = new ArrayList<>();
		List<Integer> indicies = new ArrayList<>();
		for (int[][] face : mesh)
		{
			int index = verts.size() / 3;
			for (int i = 0; i < face.length; i++)
			{
				int[] data = face[i];
				int vert = data[0];
				int tex = data[1];
				int norm = data[2];
				Vector3f vertex = obj.vertices.get(vert);
				Vector4f vertexVec = new Vector4f(vertex, 1);
				if (m != null) vertexVec = MathUtils.toVector4f(m.transform(MathUtils.toVector4d(vertexVec)));
				verts.add(vertexVec.x());
				verts.add(vertexVec.y());
				verts.add(vertexVec.z());
				if (tex >= 0)
				{
					Vector2f texture = obj.textureCoordinates.get(tex);
					texs.add(texture.x());
					texs.add(texture.y());
				}
				else
				{
					texs.add(0f);
					texs.add(0f);
				}
				if (norm >= 0)
				{
					Vector3f normal = obj.vertexNormals.get(norm);
					if (n != null) normal = MathUtils.toVector3f(n.transform(MathUtils.toVector3d(normal)));
					norms.add(normal.x());
					norms.add(normal.y());
					norms.add(normal.z());
				}
				else
				{
					norms.add(0f);
					norms.add(0f);
					norms.add(0f);
				}
				if (i >= 2)
				{
					indicies.add(index);
					indicies.add(index + i - 1);
					indicies.add(index + i);
				}
			}
		}
		return new VBORenderObject(name, transform, parent, new Mesh(toFloats(verts), toFloats(texs), toFloats(norms), toInts(indicies)));
	}
	
	private float[] toFloats(List<Float> list)
	{
		float[] array = new float[list.size()];
		Iterator<Float> it = list.iterator();
		for (int i = 0; i < array.length; i++) array[i] = it.next().floatValue();
		return array;
	}
	
	private int[] toInts(List<Integer> list)
	{
		int[] array = new int[list.size()];
		Iterator<Integer> it = list.iterator();
		for (int i = 0; i < array.length; i++) array[i] = it.next();
		return array;
	}

	public static VBOObjModel tryLoadModel(File file, ISkeleton skeleton)
	{
		try
		{
			return new VBOObjModel(file, skeleton);
		}
		catch (Exception e)
		{
			Main.LOGGER.error("failed to load model at " + file, e);
			return null;
		}
	}

	public static VBOObjModel tryLoadModel(File modelFile, File skeletonFile)
	{
		try
		{
			ISkeleton skeleton = new Skeleton(FileUtil.readFile(skeletonFile));
			try
			{
				return new VBOObjModel(modelFile, skeleton);
			}
			catch (Exception e)
			{
				Main.LOGGER.error("failed to load model at " + modelFile, e);
				return null;
			}
		}
		catch (IOException e)
		{
			Main.LOGGER.error("failed to load skeleton at " + skeletonFile, e);
			return null;
		}
	}
}