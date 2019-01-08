package firemerald.mcms.api.math;

import java.nio.FloatBuffer;

public class Matrix3
{
	public static final Matrix3 IDENTITY = new Matrix3();
	
	private float m00, m01, m02, m10, m11, m12, m20, m21, m22;
	
	public Matrix3()
	{
		m00 = m11 = m22 = 1;
		m01 = m02 = m10 = m12 = m20 = m21 = 0;
	}
	
	public Matrix3(Matrix3 mat)
	{
		mat.copy(this);
	}
	
	public Matrix3(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
	}

	public Matrix3 identity()
	{
		m00 = m11 = m22 = 1;
		m01 = m02 = m10 = m12 = m20 = m21 = 0;
		return this;
	}
	
	public Matrix3 zero()
	{
		m00 = m01 = m02 = m10 = m11 = m12 = m20 = m21 = m22 = 0;
		return this;
	}
	
	public float m00()
	{
		return m00;
	}
	
	public void m00(float m00)
	{
		this.m00 = m00;
	}
	
	public float m01()
	{
		return m01;
	}
	
	public void m01(float m01)
	{
		this.m01 = m01;
	}
	
	public float m02()
	{
		return m02;
	}
	
	public void m02(float m02)
	{
		this.m02 = m02;
	}
	
	public float m10()
	{
		return m10;
	}
	
	public void m10(float m10)
	{
		this.m10 = m10;
	}
	
	public float m11()
	{
		return m11;
	}
	
	public void m11(float m11)
	{
		this.m11 = m11;
	}
	
	public float m12()
	{
		return m12;
	}
	
	public void m12(float m12)
	{
		this.m12 = m12;
	}
	
	public float m20()
	{
		return m20;
	}
	
	public void m20(float m20)
	{
		this.m20 = m20;
	}
	
	public float m21()
	{
		return m21;
	}
	
	public void m21(float m21)
	{
		this.m21 = m21;
	}
	
	public float m22()
	{
		return m22;
	}
	
	public void m22(float m22)
	{
		this.m22 = m22;
	}
	
	public Matrix4 matrix4()
	{
		return new Matrix4(m00, m01, m02, 0, m10, m11, m12, 0, m20, m21, m22, 0, 0, 0, 0, 1);
	}
	
	public void copy(Matrix3 dest)
	{
		if (dest != this)
		{
			dest.m00 = m00;
			dest.m01 = m01;
			dest.m02 = m02;
			dest.m10 = m10;
			dest.m11 = m11;
			dest.m12 = m12;
			dest.m20 = m20;
			dest.m21 = m21;
			dest.m22 = m22;
		}
	}
	
	public Matrix3 transpose()
	{
		return transpose(this);
	}
	
	public Matrix3 transpose(Matrix3 dest)
	{
		dest.m00 = m00;
		dest.m11 = m11;
		dest.m22 = m22;
		float _m01 = m01;
		float _m02 = m02;
		float _m12 = m12;
		dest.m01 = m10;
		dest.m02 = m20;
		dest.m12 = m21;
		dest.m10 = _m01;
		dest.m20 = _m02;
		dest.m21 = _m12;
		return dest;
	}
	
	public Vec3 mul(Vec3 vec)
	{
		return mul(vec, vec);
	}
	
	public Vec3 mul(float x, float y, float z)
	{
		return mul(new Vec3(x, y, z), new Vec3());
	}
	
	public Vec3 mul(float x, float y, float z, Vec3 dest)
	{
		return mul(new Vec3(x, y, z), dest);
	}
	
	public Vec3 mul(Vec3 vec, Vec3 dest)
	{
		return vec.mul(this, dest);
	}
	
	public Matrix3 mul(Matrix3 right)
	{
		return mul(right, this);
	}
	
	public Matrix3 mul(Matrix3 right, Matrix3 dest)
	{
        float nm00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02;
        float nm01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02;
        float nm02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02;
        float nm10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12;
        float nm11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12;
        float nm12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12;
        float nm20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22;
        float nm21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22;
        float nm22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
	}
    
    public float determinant()
    {
        return (m00 * m11 - m01 * m10) * m22
             + (m02 * m10 - m00 * m12) * m21
             + (m01 * m12 - m02 * m11) * m20;
    }
    
    public Matrix3 invert()
    {
    	return invert(this);
    }
    
    public Matrix3 invert(Matrix3 dest)
    {
        float s = 1.0f / determinant();
        float nm00 = (m11 * m22 - m21 * m12) * s;
        float nm01 = (m21 * m02 - m01 * m22) * s;
        float nm02 = (m01 * m12 - m11 * m02) * s;
        float nm10 = (m20 * m12 - m10 * m22) * s;
        float nm11 = (m00 * m22 - m20 * m02) * s;
        float nm12 = (m10 * m02 - m00 * m12) * s;
        float nm20 = (m10 * m21 - m20 * m11) * s;
        float nm21 = (m20 * m01 - m00 * m21) * s;
        float nm22 = (m00 * m11 - m10 * m01) * s;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }
    
    public Matrix3 translate(Vec2 vec)
    {
    	return translate(vec, this);
    }
    
    public Matrix3 translate(Vec2 vec, Matrix3 dest)
    {
    	return translate(vec.x(), vec.y(), dest);
    }
    
    public Matrix3 translate(float x, float y)
    {
    	return translate(x, y, this);
    }
    
    public Matrix3 translate(float x, float y, Matrix3 dest)
    {
		copy(dest);
		dest.m20 += m00 * x + m10 * y;
		dest.m21 += m01 * x + m11 * y;
		dest.m22 += m02 * x + m12 * y;
        return dest;
    }
    
    public Matrix3 setTranslate(Vec2 vec)
    {
    	return setTranslate(vec.x(), vec.y());
    }
    
    public Matrix3 setTranslate(float x, float y)
    {
    	m00 = 1;
    	m01 = 0;
    	m02 = 0;
    	m10 = 0;
    	m11 = 1;
    	m12 = 0;
    	m20 = x;
    	m21 = y;
    	m22 = 1;
        return this;
    }
    
    public Matrix3 view(float x1, float y1, float x2, float y2)
    {
    	return view(x1, y1, x2, y2, this);
    }
    
    public Matrix3 view(float x1, float y1, float x2, float y2, Matrix3 dest)
    {
    	float scaleX = x2 - x1, scaleY = y2 - y1;
    	dest.m20 = m00 * x1 + m10 * y1 + m20;
    	dest.m21 = m01 * x1 + m11 * y1 + m21;
    	dest.m22 = m02 * x1 + m12 * y1 + m22;
    	dest.m00 = m00 * scaleX;
    	dest.m10 = m10 * scaleX;
    	dest.m20 = m20 * scaleX;
    	dest.m01 = m01 * scaleY;
    	dest.m11 = m11 * scaleY;
    	dest.m21 = m21 * scaleY;
        return dest;
    }
    
    public Matrix3 setView(float x1, float y1, float x2, float y2)
    {
    	m00 = x2 - x1;
    	m10 = 0;
    	m20 = 0;
    	m01 = 0;
    	m11 = y2 - y1;
    	m21 = 0;
    	m20 = x1;
    	m21 = y1;
    	m22 = 1;
        return this;
    }
	
	public FloatBuffer put(FloatBuffer buffer)
	{
		return put(buffer, buffer.position());
	}
	
	public FloatBuffer put(FloatBuffer buffer, int pos)
	{
		return buffer
				.put(pos    , m00).put(pos + 1, m01).put(pos + 2, m02)
				.put(pos + 3, m10).put(pos + 4, m11).put(pos + 5, m12)
				.put(pos + 6, m20).put(pos + 7, m21).put(pos + 8, m22);
	}
	
    /*
    @Override
    public String toString()
    {
    	return 
    			m00 + "|" + m01 + "|" + m02 + "|" + m03 + "\n" + 
    			m10 + "|" + m11 + "|" + m12 + "|" + m13 + "\n" + 
    			m20 + "|" + m21 + "|" + m22 + "|" + m23 + "\n" + 
    			m30 + "|" + m31 + "|" + m32 + "|" + m33; 
    			
    }
    */
    @Override
    public String toString()
    {
    	return "{{" + m00 + ", " + m01 + ", " + m02 + "}, {" + m10 + ", " + m11 + ", " + m12 + "}, {" + m20 + ", " + m21 + ", " + m22 + "}}";
    }
}