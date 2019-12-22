package firemerald.mcms.model;

import org.apache.logging.log4j.Level;
import org.joml.Matrix4d;
import org.joml.Vector3f;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.TextureRaytraceResult;
import firemerald.mcms.util.mesh.Mesh;

public abstract class ComponentMesh extends ModelComponent
{
	private final Mesh mesh;
	
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
	public void doRender()
	{
		boolean traced = (Main.instance.trace != null) && (Main.instance.trace.hit == this);
		if (traced) Main.instance.shader.setColor(.5f, .5f, 1, 1);
		mesh().render();
		if (traced) Main.instance.shader.setColor(1, 1, 1, 1);
	}

	@Override
	public void doCleanUp()
	{
		mesh().cleanUp();
	}

	@Override
	public float[][][] generateMesh()
	{
		int length = mesh().drawMode.stride;
		if (length > 0)
		{
			float[] verts = this.mesh().getVerticies();
			float[] texs = this.mesh().getTexs();
			float[] norms = this.mesh().getNormals();
			int[] inds = this.mesh().getIndicies();
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
			Main.LOGGER.log(Level.WARN, "Failed to save mesh to OBJ file: unknown draw mode " + this.mesh().drawMode);
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
}