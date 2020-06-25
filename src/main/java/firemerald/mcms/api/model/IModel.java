package firemerald.mcms.api.model;

import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.util.IClonableObject;

public interface IModel<M extends IModel<M, T>, T extends Bone<T>> extends IRaytraceTarget, IRigged<IModel<M, T>, T>, IClonableObject<IModel<M, T>>
{
	public void render(Map<String, Matrix4d> pos, Runnable defaultTexture);
	
	public void cleanUp();
	
	public default RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> map)
	{
		return rayTrace(fx, fy, fz, dx, dy, dz, map, new Matrix4d());
	}
	
	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> map, Matrix4d root);
	
	public void updateTex();
	
	@Override
	public default String getElementName()
	{
		return "model";
	}
}