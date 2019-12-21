package firemerald.mcms.api.math;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

public class MathUtils
{
	public static final double TAU = Math.PI * 2;
	public static final double RAD_TO_DEG = 180 / Math.PI;
	public static final double DEG_TO_RAD = Math.PI / 180;
	public static final float PI_F = (float) Math.PI;
	public static final float TAU_F = (float) (Math.PI * 2);
	public static final float RAD_TO_DEG_F = (float) (180 / Math.PI);
	public static final float DEG_TO_RAD_F = (float) (Math.PI / 180);
	
	public static Vector4d toVector4d(Vector4f vec)
	{
		return new Vector4d(vec.x(), vec.y(), vec.z(), vec.w());
	}
	
	public static Vector4f toVector4f(Vector4d vec)
	{
		return new Vector4f((float) vec.x(), (float) vec.y(), (float) vec.z(), (float) vec.w());
	}
	
	public static Vector3d toVector3d(Vector3f vec)
	{
		return new Vector3d(vec.x(), vec.y(), vec.z());
	}
	
	public static Vector3f toVector3f(Vector3d vec)
	{
		return new Vector3f((float) vec.x(), (float) vec.y(), (float) vec.z());
	}
}