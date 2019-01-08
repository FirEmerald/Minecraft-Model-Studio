package firemerald.mcms.api.math;

public class Vec4
{
	private float x, y, z, w;

	public static final Vec4 ZERO = new Vec4();
	public static final Vec4 ONE = new Vec4(1);
	
	public static Vec4 random(float min, float max)
	{
		return new Vec4(min + (float) Math.random() * max, min + (float) Math.random() * max, min + (float) Math.random() * max, min + (float) Math.random() * max); 
	}
	
	public Vec4()
	{
		x = y = z = w = 0;
	}
	
	public Vec4(float v)
	{
		x = y = z = w = v;
	}
	
	public Vec4(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4(Vec3 v, float w)
	{
		this.x = v.x();
		this.y = v.y();
		this.z = v.z();
		this.w = w;
	}

	public float x()
	{
		return x;
	}
	
	public float y()
	{
		return y;
	}
	
	public float z()
	{
		return z;
	}
	
	public float w()
	{
		return w;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (!(o instanceof Vec4)) return false;
		else
		{
			Vec4 v = (Vec4) o;
			return (v.x == x && v.y == y && v.z == z && v.w == w);
		}
	}
	
	public Vec3 xyz()
	{
		return new Vec3(x, y, z);
	}
	
	@Override
	public String toString()
	{
		return "{" + x + ", " + y + ", " + z + ", " + w + "}";
	}
	
	public Vec4 mul(Matrix4 mat)
	{
		return mul(mat, this);
	}

    public Vec4 mul(Matrix4 mat, Vec4 dest)
    {
        float rx = mat.m00() * x + mat.m10() * y + mat.m20() * z + mat.m30() * w;
        float ry = mat.m01() * x + mat.m11() * y + mat.m21() * z + mat.m31() * w;
        float rz = mat.m02() * x + mat.m12() * y + mat.m22() * z + mat.m32() * w;
        float rw = mat.m03() * x + mat.m13() * y + mat.m23() * z + mat.m33() * w;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = rw;
        return dest;
    }
    
    public float magnitude()
    {
    	return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }
    
    public Vec4 scale(float scalar)
    {
    	return scale(scalar, this);
    }
    
    public Vec4 scale(float scalar, Vec4 dest)
    {
    	dest.x = x * scalar;
    	dest.y = y * scalar;
    	dest.z = z * scalar;
    	dest.w = w * scalar;
    	return dest;
    }
}