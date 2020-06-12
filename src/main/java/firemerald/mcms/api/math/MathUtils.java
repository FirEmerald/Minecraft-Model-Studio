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
	
	public static Vector3f toVector3f(Vector4d vec)
	{
		return new Vector3f((float) vec.x(), (float) vec.y(), (float) vec.z());
	}
	
	public static Float rayTraceFull(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f from, Vector3f dir)
	{
		return rayTraceFull(p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z(), p3.x(), p3.y(), p3.z(), from.x(), from.y(), from.z(), dir.x(), dir.y(), dir.z());
	}
	
	/* returns magnitude of vector from start to intersect, divided by magnitude of direction vector, or null for fail (counts magnitude out of range as fail!)*/
	public static Float rayTraceFull(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float fx, float fy, float fz, float dx, float dy, float dz)
	{
		Float m = rayTrace(x1, y1, z1, x2, y2, z2, x3, y3, z3, fx, fy, fz, dx, dy, dz);
		if (m != null && m >= 0 && m <= 1) return m;
		else return null;
	}
	
	public static Float rayTrace(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f from, Vector3f dir)
	{
		return rayTrace(p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z(), p3.x(), p3.y(), p3.z(), from.x(), from.y(), from.z(), dir.x(), dir.y(), dir.z());
	}
	
	/* returns magnitude of vector from start to intersect, divided by magnitude of direction vector, or null for fail (does not count negative magnitude as fail!)*/
	public static Float rayTrace(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float fx, float fy, float fz, float dx, float dy, float dz)
	{
		float yz13 = y1 * z3 - y3 * z1;
		float yz21 = y2 * z1 - y1 * z2;
		float yz32 = y3 * z2 - y2 * z3;
		float zx13 = z1 * x3 - z3 * x1;
		float zx21 = z2 * x1 - z1 * x2;
		float zx32 = z3 * x2 - z2 * x3;
		float xy13 = x1 * y3 - x3 * y1;
		float xy21 = x2 * y1 - x1 * y2;
		float xy32 = x3 * y2 - x2 * y3;
		
		float nm03 = dx * yz32 + dy * zx32 + dz * xy32;
		float nm13 = dx * yz13 + dy * zx13 + dz * xy13;
		float nm23 = dx * yz21 + dy * zx21 + dz * xy21;
		float det = nm03 + nm13 + nm23;
		if (det == 0) return null;
		else
		{
			float nm00 = dy * (z2 - z3) + dz * (y3 - y2);
			float nm01 = dz * (x2 - x3) + dx * (z3 - z2);
			float nm02 = dx * (y2 - y3) + dy * (x3 - x2);
			float nm10 = dy * (z3 - z1) + dz * (y1 - y3);
			float nm11 = dz * (x3 - x1) + dx * (z1 - z3);
			float nm12 = dx * (y3 - y1) + dy * (x1 - x3);
			float nm20 = dy * (z1 - z2) + dz * (y2 - y1);
			float nm21 = dz * (x1 - x2) + dx * (z2 - z1);
			float nm22 = dx * (y1 - y2) + dy * (x2 - x1);
			float nm30 = yz13 + yz21 + yz32;
			float nm31 = zx13 + zx21 + zx32;
			float nm32 = xy13 + xy21 + xy32;
			float nnm33 = x1 * yz32 + x2 * yz13 + x3 * yz21;
			
			return (fx * nm00 + fy * nm01 + fz * nm02 + nm03 >= 0 && 
					fx * nm10 + fy * nm11 + fz * nm12 + nm13 >= 0 && 
					fx * nm20 + fy * nm21 + fz * nm22 + nm23 >= 0) ? 
							(-(fx * nm30 + fy * nm31 + fz * nm32) + nnm33) / det : null;
		}
	}
}