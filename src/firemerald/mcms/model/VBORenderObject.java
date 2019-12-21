package firemerald.mcms.model;

import java.util.Map;

import org.joml.Matrix4d;
import org.joml.Vector3f;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.TextureRaytraceResult;
import firemerald.mcms.util.mesh.Mesh;

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
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation)
	{
		RaytraceResult result = null;
		if (visible && mesh instanceof Mesh)
		{
			Vector3f res = MathUtil.rayTraceMeshUV(fx, fy, fz, dx, dy, dz, mesh, transformation);
			if (res != null && (result == null || res.x() < result.m)) result = new TextureRaytraceResult(this, res.x(), Main.instance.project.getTexture(), res.y(), res.z()); //TODO texture
		}
		if (childrenVisible) for (Bone child : children)
		{
			Matrix4d transform = transformations.get(child.name);
			if (transform == null) transform = new Matrix4d(transformation);
			else transform = transformation.mul(transform, new Matrix4d());
			RaytraceResult res = child.raytrace(fx, fy, fz, dx, dy, dz, transformations, transform);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		return result;
	}
}