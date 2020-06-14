package firemerald.mcms.api.model;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.model.RenderObjectComponents;

public interface IModel extends IRaytraceTarget, IRigged<IModel>
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
	public default Bone makeNew(String name, Transformation defaultTransform, @Nullable Bone parent)
	{
		return new RenderObjectComponents(name, defaultTransform, parent);
	}
	
	@Override
	public default String getElementName()
	{
		return "model";
	}
	
	public IModel cloneObject();
}