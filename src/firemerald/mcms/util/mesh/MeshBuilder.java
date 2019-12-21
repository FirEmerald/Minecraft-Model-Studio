package firemerald.mcms.util.mesh;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class MeshBuilder
{
	public static MeshBuilder INSTANCE = new MeshBuilder();

	protected final List<Vector3f> positions = new ArrayList<>();
	protected final List<Vector2f> textures = new ArrayList<>();
	protected final List<Vector3f> normals = new ArrayList<>();
	protected final List<Integer> indicies = new ArrayList<>();
	protected int ind = 0;
	
	public void addQuad(Vector3f pos1, Vector2f tex1, Vector3f pos2, Vector2f tex2, Vector3f pos3, Vector2f tex3, Vector3f pos4, Vector2f tex4, Vector3f norm)
	{
		positions.add(pos1);
		textures.add(tex1);
		normals.add(norm);
		positions.add(pos2);
		textures.add(tex2);
		normals.add(norm);
		positions.add(pos3);
		textures.add(tex3);
		normals.add(norm);
		positions.add(pos4);
		textures.add(tex4);
		normals.add(norm);
		indicies.add(ind++);
		indicies.add(ind++);
		indicies.add(ind++);
		indicies.add(ind++);
	}

	public void addQuad(float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, float x4, float y4, float z4, float u4, float v4, float nx, float ny, float nz)
	{
		addQuad(new Vector3f(x1, y1, z1), new Vector2f(u1, v1), new Vector3f(x2, y2, z2), new Vector2f(u2, v2), new Vector3f(x3, y3, z3), new Vector2f(u3, v3), new Vector3f(x4, y4, z4), new Vector2f(u4, v4), new Vector3f(nx, ny, nz));
	}
	
	public void addRectXPosYU(float y1, float z1, float y2, float z2, float x, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x, y1, z1), new Vector2f(u1, v1), new Vector3f(x, y2, z1), new Vector2f(u2, v1), new Vector3f(x, y2, z2), new Vector2f(u2, v2), new Vector3f(x, y1, z2), new Vector2f(u1, v2), new Vector3f(1, 0, 0));
	}
	
	public void addRectXPosZU(float y1, float z1, float y2, float z2, float x, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x, y1, z1), new Vector2f(u1, v1), new Vector3f(x, y2, z1), new Vector2f(u1, v2), new Vector3f(x, y2, z2), new Vector2f(u2, v2), new Vector3f(x, y1, z2), new Vector2f(u2, v1), new Vector3f(1, 0, 0));
	}
	
	public void addRectXNegYU(float y1, float z1, float y2, float z2, float x, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x, y1, z1), new Vector2f(u1, v1), new Vector3f(x, y1, z2), new Vector2f(u1, v2), new Vector3f(x, y2, z2), new Vector2f(u2, v2), new Vector3f(x, y2, z1), new Vector2f(u2, v1), new Vector3f(-1, 0, 0));
	}
	
	public void addRectXNegZU(float y1, float z1, float y2, float z2, float x, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x, y1, z1), new Vector2f(u1, v1), new Vector3f(x, y1, z2), new Vector2f(u2, v1), new Vector3f(x, y2, z2), new Vector2f(u2, v2), new Vector3f(x, y2, z1), new Vector2f(u1, v2), new Vector3f(-1, 0, 0));
	}
	
	public void addRectYPosXU(float x1, float z1, float x2, float z2, float y, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y, z1), new Vector2f(u1, v1), new Vector3f(x2, y, z1), new Vector2f(u2, v1), new Vector3f(x2, y, z2), new Vector2f(u2, v2), new Vector3f(x1, y, z2), new Vector2f(u1, v2), new Vector3f(0, 1, 0));
	}
	
	public void addRectYPosZU(float x1, float z1, float x2, float z2, float y, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y, z1), new Vector2f(u1, v1), new Vector3f(x2, y, z1), new Vector2f(u1, v2), new Vector3f(x2, y, z2), new Vector2f(u2, v2), new Vector3f(x1, y, z2), new Vector2f(u2, v1), new Vector3f(0, 1, 0));
	}
	
	public void addRectYNegXU(float x1, float z1, float x2, float z2, float y, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y, z1), new Vector2f(u1, v1), new Vector3f(x1, y, z2), new Vector2f(u1, v2), new Vector3f(x2, y, z2), new Vector2f(u2, v2), new Vector3f(x2, y, z1), new Vector2f(u2, v1), new Vector3f(0, -1, 0));
	}
	
	public void addRectYNegZU(float x1, float z1, float x2, float z2, float y, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y, z1), new Vector2f(u1, v1), new Vector3f(x1, y, z2), new Vector2f(u2, v1), new Vector3f(x2, y, z2), new Vector2f(u2, v2), new Vector3f(x2, y, z1), new Vector2f(u1, v2), new Vector3f(0, -1, 0));
	}
	
	public void addRectZPosXU(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y1, z), new Vector2f(u1, v1), new Vector3f(x2, y1, z), new Vector2f(u2, v1), new Vector3f(x2, y2, z), new Vector2f(u2, v2), new Vector3f(x1, y2, z), new Vector2f(u1, v2), new Vector3f(0, 0, 1));
	}
	
	public void addRectZPosYU(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y1, z), new Vector2f(u1, v1), new Vector3f(x2, y1, z), new Vector2f(u1, v2), new Vector3f(x2, y2, z), new Vector2f(u2, v2), new Vector3f(x1, y2, z), new Vector2f(u2, v1), new Vector3f(0, 0, 1));
	}
	
	public void addRectZNegXU(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y1, z), new Vector2f(u1, v1), new Vector3f(x1, y2, z), new Vector2f(u1, v2), new Vector3f(x2, y2, z), new Vector2f(u2, v2), new Vector3f(x2, y1, z), new Vector2f(u2, v1), new Vector3f(0, 0, -1));
	}
	
	public void addRectZNegYU(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2)
	{
		addQuad(new Vector3f(x1, y1, z), new Vector2f(u1, v1), new Vector3f(x1, y2, z), new Vector2f(u2, v1), new Vector3f(x2, y2, z), new Vector2f(u2, v2), new Vector3f(x2, y1, z), new Vector2f(u1, v2), new Vector3f(0, 0, -1));
	}
	
	public void apply(Mesh mesh)
	{
		mesh.setMesh(vec3ToFloatArray(positions), vec2ToFloatArray(textures), vec3ToFloatArray(normals), intToIntArray(indicies));
		clear();
	}
	
	public void clear()
	{
		positions.clear();
		textures.clear();
		normals.clear();
		indicies.clear();
		ind = 0;
	}
	
	public int[] intToIntArray(List<Integer> list)
	{
		int[] array = new int[list.size()];
		int i = 0;
		for (Integer in : list) array[i++] = in.intValue();
		return array;
	}
	
	public float[] vec2ToFloatArray(List<Vector2f> list)
	{
		float[] array = new float[list.size() * 2];
		int i = 0;
		for (Vector2f vec : list)
		{
			array[i++] = vec.x;
			array[i++] = vec.y;
		}
		return array;
	}
	
	public float[] vec3ToFloatArray(List<Vector3f> list)
	{
		float[] array = new float[list.size() * 3];
		int i = 0;
		for (Vector3f vec : list)
		{
			array[i++] = vec.x;
			array[i++] = vec.y;
			array[i++] = vec.z;
		}
		return array;
	}
}