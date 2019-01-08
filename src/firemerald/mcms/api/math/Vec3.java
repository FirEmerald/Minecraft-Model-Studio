package firemerald.mcms.api.math;

public class Vec3
{
	private float x, y, z;

	public static final Vec3 ZERO = new Vec3();
	public static final Vec3 ONE = new Vec3(1);
	
	public static Vec3 random(float min, float max)
	{
		return new Vec3(min + (float) Math.random() * max, min + (float) Math.random() * max, min + (float) Math.random() * max); 
	}
	
	public Vec3()
	{
		x = y = z = 0;
	}
	
	public Vec3(float v)
	{
		x = y = z = v;
	}
	
	public Vec3(Vec3 vec)
	{
		this(vec.x, vec.y, vec.z);
	}
	
	public Vec3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float x()
	{
		return x;
	}
	
	public void x(float x)
	{
		this.x = x;
	}
	
	public float y()
	{
		return y;
	}
	
	public void y(float y)
	{
		this.y = y;
	}
	
	public float z()
	{
		return z;
	}
	
	public void z(float z)
	{
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (!(o instanceof Vec3)) return false;
		else
		{
			Vec3 v = (Vec3) o;
			return (v.x == x && v.y == y && v.z == z);
		}
	}
	
	@Override
	public String toString()
	{
		return "{" + x + ", " + y + ", " + z + "}";
	}
	
	public Vec3 subtract(Vec3 vec)
	{
		return subtract(vec, this);
	}
	
	public Vec3 subtract(Vec3 vec, Vec3 dest)
	{
		return subtract(vec.x, vec.y, vec.z, dest);
	}
	
	public Vec3 subtract(float x, float y, float z)
	{
		return subtract(x, y, z, this);
	}
	
	public Vec3 subtract(float x, float y, float z, Vec3 dest)
	{
		dest.x = this.x - x;
		dest.y = this.y - y;
		dest.z = this.z - z;
		return dest;
	}
	
	public Vec3 mul(Matrix3 mat)
	{
		return mul(mat, this);
	}

    public Vec3 mul(Matrix3 mat, Vec3 dest)
    {
        float rx = mat.m00() * x + mat.m10() * y + mat.m20() * z;
        float ry = mat.m01() * x + mat.m11() * y + mat.m21() * z;
        float rz = mat.m02() * x + mat.m12() * y + mat.m22() * z;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }
    
    public float magnitude()
    {
    	return (float) Math.sqrt(x * x + y * y + z * z);
    }

	public Vec3 normalize()
	{
		float m = magnitude();
		if (m == 0)
		{
			x = 0;
			y = 0;
			z = 0;
		}
		else
		{
			x /= m;
			y /= m;
			z /= m;
		}
		return this;
	}
}