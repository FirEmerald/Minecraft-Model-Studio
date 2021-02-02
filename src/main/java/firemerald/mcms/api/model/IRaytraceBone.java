package firemerald.mcms.api.model;

import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.util.RaytraceResult;

public interface IRaytraceBone<T extends IRaytraceBone<T>> extends IRaytraceTarget
{
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation);
}