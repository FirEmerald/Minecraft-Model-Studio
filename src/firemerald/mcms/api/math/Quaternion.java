package firemerald.mcms.api.math;

import firemerald.mcms.util.MathUtil;

public class Quaternion
{
	private double x, y, z, w;
	
	public static final Quaternion IDENTITY = new Quaternion();
	
	public Quaternion()
	{
		x = y = z = 0;
		w = 1;
	}
	
	public Quaternion(double x, double y, double z, double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Quaternion(Quaternion q)
	{
		this(q.x, q.y, q.z, q.w);
	}

	public Quaternion normalize()
	{
		double invNorm = 1.0 / Math.sqrt(x * x + y * y + z * z + w * w);
		if (invNorm != 1)
		{
			x *= invNorm;
        	y *= invNorm;
        	z *= invNorm;
        	w *= invNorm;
		}
		return this;
	}
	
	public double x()
	{
		return x;
	}
	
	public double y()
	{
		return y;
	}
	
	public double z()
	{
		return z;
	}
	
	public double w()
	{
		return w;
	}
	
	public Matrix3 getMatrix3()
	{
        double w2 = w * w;
        double x2 = x * x;
        double y2 = y * y;
        double z2 = z * z;
        double zw = z * w;
        double xy = x * y;
        double xz = x * z;
        double yw = y * w;
        double yz = y * z;
        double xw = x * w;
        float m00 = (float) (w2 + x2 - z2 - y2);
        float m01 = (float) (xy + zw + zw + xy);
        float m02 = (float) (xz - yw + xz - yw);
        float m10 = (float) (-zw + xy - zw + xy);
        float m11 = (float) (y2 - z2 + w2 - x2);
        float m12 = (float) (yz + yz + xw + xw);
        float m20 = (float) (yw + xz + xz + yw);
        float m21 = (float) (yz + yz - xw - xw);
        float m22 = (float) (z2 - y2 - x2 + w2);
        return new Matrix3(m00, m01, m02, m10, m11, m12, m20, m21, m22);
	}
	
	public Matrix4 getMatrix4()
	{
        double w2 = w * w;
        double x2 = x * x;
        double y2 = y * y;
        double z2 = z * z;
        double zw = z * w;
        double xy = x * y;
        double xz = x * z;
        double yw = y * w;
        double yz = y * z;
        double xw = x * w;
        float m00 = (float) (w2 + x2 - z2 - y2);
        float m01 = (float) (xy + zw + zw + xy);
        float m02 = (float) (xz - yw + xz - yw);
        float m10 = (float) (-zw + xy - zw + xy);
        float m11 = (float) (y2 - z2 + w2 - x2);
        float m12 = (float) (yz + yz + xw + xw);
        float m20 = (float) (yw + xz + xz + yw);
        float m21 = (float) (yz + yz - xw - xw);
        float m22 = (float) (z2 - y2 - x2 + w2);
        return new Matrix4(m00, m01, m02, 0, m10, m11, m12, 0, m20, m21, m22, 0, 0, 0, 0, 1);
	}
	
	public void setFromMatrix(Matrix4 m1)
	{
		w = Math.sqrt(1.0 + m1.m00() + m1.m11() + m1.m22()) / 2.0;
		double w4 = (4.0 * w);
		x = (m1.m21() - m1.m12()) / w4 ;
		y = (m1.m02() - m1.m20()) / w4 ;
		z = (m1.m10() - m1.m01()) / w4 ;
	}
	
	public static double dot(Quaternion q1, Quaternion q2)
	{
		return q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w;
	}
	
	public static Quaternion slerp(Quaternion q1, Quaternion q2, double amount)
	{
	    // v0 and v1 should be unit length or else something broken will happen.
	    // Compute the cosine of the angle between the two vectors.
		double dot = dot(q1, q2);
	    final double THRESHOLD = 0.9995f;
	    if (dot > THRESHOLD) return lerpNorm(q1, q2, amount); // If the inputs are too close for comfort, lerp and normalize
	    else
	    {
	    	if (dot < -1) dot = -1;
	    	else if (dot > 1) dot = 1;
	    	double theta_0 = Math.acos(dot);
	    	double theta = theta_0 * amount;
	    	double x3 = q2.x - q1.x * dot;
	    	double y3 = q2.y - q1.y * dot;
	    	double z3 = q2.z - q1.z * dot;
	    	double w3 = q2.w - q1.w * dot;
	    	double invNorm = 1.0 / Math.sqrt(x3 * x3 + y3 * y3 + z3 * z3 + w3 * w3);
	        x3 *= invNorm;
	        y3 *= invNorm;
	        z3 *= invNorm;
	        w3 *= invNorm;
	        double cos = Math.cos(theta);
	        double sin = Math.sin(theta);
	        return new Quaternion(q1.x * cos + x3 * sin, q1.y * cos + y3 * sin, q1.z * cos + z3 * sin, q1.w * cos + w3 * sin);
	    }
	}
	
	/** lerp and normalize **/
	public static Quaternion lerpNorm(Quaternion q1, Quaternion q2, double amount)
	{
		double x = q1.x + (q2.x - q1.x) * amount;
		double y = q1.y + (q2.y - q1.y) * amount;
		double z = q1.z + (q2.z - q1.z) * amount;
		double w = q1.w + (q2.w - q1.w) * amount;
		double invNorm = 1.0 / Math.sqrt(x * x + y * y + z * z + w * w);
		return new Quaternion(x * invNorm, y * invNorm, z * invNorm, w * invNorm);
	}
	
	public static Quaternion forAngle(double ang, double x, double y, double z)
	{
		double hAng = ang / 2d;
		double sin = Math.sin(hAng);
		return new Quaternion(x * sin, y * sin, z * sin, Math.cos(hAng));
	}
	
	public static Quaternion forXAngle(double ang)
	{
		double hAng = ang / 2d;
		return new Quaternion(Math.sin(hAng), 0, 0, Math.cos(hAng));
	}
	
	public static Quaternion forYAngle(double ang)
	{
		double hAng = ang / 2d;
		return new Quaternion(0, Math.sin(hAng), 0, Math.cos(hAng));
	}
	
	public static Quaternion forZAngle(double ang)
	{
		double hAng = ang / 2d;
		return new Quaternion(0, 0, Math.sin(hAng), Math.cos(hAng));
	}
	
	public static Quaternion forEulerXZY(double angX, double angY, double angZ)
	{
		//x = sin(ax/2)cos(ay/2)cos(az/2) + cos(ax/2)sin(ay/2)sin(az/2)
		//y = sin(ax/2)cos(ay/2)sin(az/2) + cos(ax/2)sin(ay/2)cos(az/2)
		//z = cos(ax/2)cos(ay/2)sin(az/2) - sin(ax/2)sin(ay/2)cos(az/2)
		//w = cos(ax/2)cos(ay/2)cos(az/2) - sin(ax/2)sin(ay/2)sin(az/2)
		angX *= .5 * MathUtil.DEG_TO_RAD;
		angY *= .5 * MathUtil.DEG_TO_RAD;
		angZ *= .5 * MathUtil.DEG_TO_RAD;
		double csx = Math.cos(angX);
		double snx = Math.sin(angX);
		double csz = Math.cos(angZ);
		double snz = Math.sin(angZ);
		double csy = Math.cos(angY);
		double sny = Math.sin(angY);
		return new Quaternion(
				snx * csz * csy + csx * snz * sny, 
				snx * snz * csy + csx * csz * sny,
				csx * snz * csy - snx * csz * sny,
				csx * csz * csy - snx * snz * sny);
	}
	
	public Vec3 toEulerXZY()
	{
		//ax=atan2(2*(x*w-y*z),1-2*(x*x+z*z))
		//ay=atan2(2*(x*z-y*w),1-2*(y*y+z*z))
		//az=asin(-2*(x*y+z*w))
		float rX, rY, rZ;
		double s = x * y + z * w;
		if (s == 0.5)
		{
			rX = 2 * (float) Math.atan2(y, w) * MathUtil.RAD_TO_DEG;
			rY = 0;
			rZ = 90;
		}
		else if (s == -0.5)
		{
			rX = -2 * (float) Math.atan2(y, w) * MathUtil.RAD_TO_DEG;
			rY = 0;
			rZ = -90;
		}
		else
		{
			rX = (float) Math.atan2(2 * (x * w - y * z), 1 - 2 * (x * x + z * z)) * MathUtil.RAD_TO_DEG;
			rY = (float) Math.atan2(2 * (y * w - x * z), 1 - 2 * (y * y + z * z)) * MathUtil.RAD_TO_DEG;
			rZ = (float) Math.asin(2 * s) * MathUtil.RAD_TO_DEG;
		}
		return new Vec3(rX, rY, rZ);
	}
	
	public static Quaternion forEulerYZX(double angX, double angY, double angZ)
	{
		//heading = rY, attitude = rZ, bank = rX
		//c1 = csy, s1 = sny
		//c2 = csz, s2 = snz
		//c3 = csx, s3 = snx
		angX *= .5 * MathUtil.DEG_TO_RAD;
		angY *= .5 * MathUtil.DEG_TO_RAD;
		angZ *= .5 * MathUtil.DEG_TO_RAD;
		double csx = Math.cos(angX);
		double snx = Math.sin(angX);
		double csz = Math.cos(angZ);
		double snz = Math.sin(angZ);
		double csy = Math.cos(angY);
		double sny = Math.sin(angY);
		return new Quaternion(
				sny * snz * csx + csy * csz * snx, 
				sny * csz * csx + csy * snz * snx,
				csy * snz * csx - sny * csz * snx,
				csy * csz * csx - sny * snz * snx);
	}
	
	public Vec3 toEulerYZX()
	{
		float rX, rY, rZ;
		double s = x * y + z * w;
		if (s == 0.5)
		{
			rX = 0;
			rY = 2 * (float) Math.atan2(x, w) * MathUtil.RAD_TO_DEG;
			rZ = 90;
		}
		else if (s == -0.5)
		{
			rX = 0;
			rY = -2 * (float) Math.atan2(x, w) * MathUtil.RAD_TO_DEG;
			rZ = -90;
		}
		else
		{
			rX = (float) Math.atan2(2 * (x * w - y * z), 1 - 2 * (x * x + z * z)) * MathUtil.RAD_TO_DEG;
			rY = (float) Math.atan2(2 * (y * w - x * z), 1 - 2 * (y * y + z * z)) * MathUtil.RAD_TO_DEG;
			rZ = (float) Math.asin(2 * s) * MathUtil.RAD_TO_DEG;
		}
		return new Vec3(rX, rY, rZ);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (!(o instanceof Quaternion)) return false;
		else
		{
			Quaternion q = (Quaternion) o;
			return (q.x == x && q.y == y && q.z == z && q.w == w);
		}
	}
	
	@Override
	public String toString()
	{
		return "{" + x + ", " + y + ", " + z + ", " + w + "}";
	}
	
	public Quaternion mul(Quaternion q)
	{
		return mul(q, this);
	}
	
	public Quaternion mul(Quaternion q, Quaternion dest)
	{
		dest.x = w * q.x + x * q.w + y * q.z - z * q.y;
		dest.y = w * q.y - x * q.z + y * q.w + z * q.x;
		dest.z = w * q.z + x * q.y - y * q.x + z * q.w;
		dest.w = w * q.w - x * q.x - y * q.y - z * q.z;
       return dest;
	}
}