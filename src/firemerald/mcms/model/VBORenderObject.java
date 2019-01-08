package firemerald.mcms.model;

import java.util.Map;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.util.MathUtil;

public class VBORenderObject extends Bone
{
	public final Mesh mesh;
	
	public VBORenderObject(String name, Transformation defaultTransform, Mesh mesh)
	{
		super(name, defaultTransform);
		this.mesh = mesh;
	}

	public VBORenderObject(String name, Transformation defaultTransform, Bone parent, Mesh mesh)
	{
		super(name, defaultTransform, parent);
		this.mesh = mesh;
	}

	@Override
	public void doRender()
	{
		mesh.render();
	}

	@Override
	public void doCleanUp()
	{
		mesh.cleanUp();
	}
	
	@Override
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4> transformations, Matrix4 transformation)
	{
		RaytraceResult result = null;
		if (visible && mesh instanceof Mesh)
		{
			Float res = MathUtil.rayTraceMesh(fx, fy, fz, dx, dy, dz, mesh, transformation);
			if (res != null) result = new RaytraceResult(this, res);
		}
		if (childrenVisible) for (Bone child : children)
		{
			Matrix4 transform = transformations.get(child.name);
			if (transform == null) transform = new Matrix4(transformation);
			else transform = transformation.mul(transform, new Matrix4());
			RaytraceResult res = child.raytrace(fx, fy, fz, dx, dy, dz, transformations, transform);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		return result;
	}
}