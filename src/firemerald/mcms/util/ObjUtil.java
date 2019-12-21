package firemerald.mcms.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.util.mesh.Mesh;

public class ObjUtil
{
	public static void addQuad(ObjData obj, List<int[][]> mesh, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, Vector3f norm)
	{
		int ind1 = getSetIndex(obj.vertices, v1);
		int ind2 = getSetIndex(obj.vertices, v2);
		int ind3 = getSetIndex(obj.vertices, v3);
		int ind4 = getSetIndex(obj.vertices, v4);
		int indn = getSetIndex(obj.vertexNormals, norm);
		mesh.add(new int[][] {{ind1, -1, indn}, {ind2, -1, indn}, {ind3, -1, indn}, {ind4, -1, indn}});
	}
	
	public static <T> int getSetIndex(List<T> list, T vec)
	{
		int ind;
		if ((ind = list.indexOf(vec)) < 0)
		{
			ind = list.size();
			list.add(vec);
		}
		return ind;
	}
	
	public static Mesh makeMesh(ObjData obj)
	{
		List<Float> verts = new ArrayList<>();
		List<Float> texs = new ArrayList<>();
		List<Float> norms = new ArrayList<>();
		List<Integer> indicies = new ArrayList<>();
		for (List<int[][]> mesh : obj.groupObjects.values()) for (int[][] face : mesh)
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
		return new Mesh(toFloats(verts), toFloats(texs), toFloats(norms), toInts(indicies));
	}
	
	private static float[] toFloats(List<Float> list)
	{
		float[] array = new float[list.size()];
		Iterator<Float> it = list.iterator();
		for (int i = 0; i < array.length; i++) array[i] = it.next().floatValue();
		return array;
	}
	
	private static int[] toInts(List<Integer> list)
	{
		int[] array = new int[list.size()];
		Iterator<Integer> it = list.iterator();
		for (int i = 0; i < array.length; i++) array[i] = it.next();
		return array;
	}
}