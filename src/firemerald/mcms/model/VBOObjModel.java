package firemerald.mcms.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.math.Matrix3;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Vec2;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.math.Vec4;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.api.model.ObjModel;
import firemerald.mcms.api.model.Skeleton;

public class VBOObjModel extends ObjModel
{	
	public VBOObjModel(File modelFile, Skeleton skeleton) throws Exception
	{
		super(modelFile, skeleton);
	}

	@Override
	protected Bone makeObj(String name, Transformation transform, Bone parent, List<int[][]> mesh, ObjData obj, Skeleton skeleton)
	{
		Matrix4 m = skeleton.inverts.get(name);
		Matrix3 n = null;
		if (m != null) n = m.transpose3().invert();
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
				Vec3 vertex = obj.vertices.get(vert);
				Vec4 vertexVec = new Vec4(vertex, 1);
				if (m != null) vertexVec = m.mul(vertexVec);
				verts.add(vertexVec.x());
				verts.add(vertexVec.y());
				verts.add(vertexVec.z());
				if (tex >= 0)
				{
					Vec2 texture = obj.textureCoordinates.get(tex);
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
					Vec3 normal = obj.vertexNormals.get(norm);
					if (n != null) normal = n.mul(normal, new Vec3());
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
		for (int i = 0; i < array.length; i++) array[i] = it.next();
		return array;
	}
	
	private int[] toInts(List<Integer> list)
	{
		int[] array = new int[list.size()];
		Iterator<Integer> it = list.iterator();
		for (int i = 0; i < array.length; i++) array[i] = it.next();
		return array;
	}

	public static VBOObjModel tryLoadModel(File file, Skeleton skeleton)
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
		Skeleton skeleton = Skeleton.tryLoadXML(skeletonFile);
		if (skeleton == null) return null;
		else try
		{
			return new VBOObjModel(modelFile, skeleton);
		}
		catch (Exception e)
		{
			Main.LOGGER.error("failed to load model at " + modelFile, e);
			return null;
		}
	}
}