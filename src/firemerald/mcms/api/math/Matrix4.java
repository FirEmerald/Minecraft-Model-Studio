package firemerald.mcms.api.math;

import java.nio.FloatBuffer;

public class Matrix4
{
	public static final Matrix4 IDENTITY = new Matrix4();
	
	private float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;
	
	public Matrix4()
	{
		m00 = m11 = m22 = m33 = 1;
		m01 = m02 = m03 = m10 = m12 = m13 = m20 = m21 = m23 = m30 = m31 = m32 = 0;
	}
	
	public Matrix4(Matrix4 mat)
	{
		mat.copy(this);
	}
	
	public Matrix4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}
	
	public Matrix4 identity()
	{
		m00 = m11 = m22 = m33 = 1;
		m01 = m02 = m03 = m10 = m12 = m13 = m20 = m21 = m23 = m30 = m31 = m32 = 0;
		return this;
	}
	
	public Matrix4 zero()
	{
		m00 = m01 = m02 = m03 = m10 = m11 = m12 = m13 = m20 = m21 = m22 = m23 = m30 = m31 = m32 = m33 = 0;
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
	
	public float m03()
	{
		return m03;
	}
	
	public void m03(float m03)
	{
		this.m03 = m03;
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
	
	public float m13()
	{
		return m13;
	}
	
	public void m13(float m13)
	{
		this.m13 = m13;
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
	
	public float m23()
	{
		return m23;
	}
	
	public void m23(float m23)
	{
		this.m23 = m23;
	}
	
	public float m30()
	{
		return m30;
	}
	
	public void m30(float m30)
	{
		this.m30 = m30;
	}
	
	public float m31()
	{
		return m31;
	}
	
	public void m31(float m31)
	{
		this.m31 = m31;
	}
	
	public float m32()
	{
		return m32;
	}
	
	public void m32(float m32)
	{
		this.m32 = m32;
	}
	
	public float m33()
	{
		return m33;
	}
	
	public void m33(float m33)
	{
		this.m33 = m33;
	}
	
	public Matrix3 matrix3()
	{
		return new Matrix3(m00, m01, m02, m10, m11, m12, m20, m21, m22);
	}
	
	public void copy(Matrix4 dest)
	{
		if (dest != this)
		{
			dest.m00 = m00;
			dest.m01 = m01;
			dest.m02 = m02;
			dest.m03 = m03;
			dest.m10 = m10;
			dest.m11 = m11;
			dest.m12 = m12;
			dest.m13 = m13;
			dest.m20 = m20;
			dest.m21 = m21;
			dest.m22 = m22;
			dest.m23 = m23;
			dest.m30 = m30;
			dest.m31 = m31;
			dest.m32 = m32;
			dest.m33 = m33;
		}
	}
	
	public Matrix4 translate(Vec3 vec)
	{
		return translate(vec, this);
	}
	
	public Matrix4 translate(Vec3 vec, Matrix4 des)
	{
		return translate(vec.x(), vec.y(), vec.z(), des);
	}
	
	public Matrix4 translate(float x, float y, float z)
	{
		return translate(x, y, z, this);
	}
	
	public Matrix4 translate(float x, float y, float z, Matrix4 dest)
	{
		copy(dest);
		dest.m30 += m00 * x + m10 * y + m20 * z;
		dest.m31 += m01 * x + m11 * y + m21 * z;
		dest.m32 += m02 * x + m12 * y + m22 * z;
		dest.m33 += m03 * x + m13 * y + m23 * z;
        return dest;
	}
	
	public Matrix4 transpose()
	{
		return transpose(this);
	}
	
	public Matrix4 transpose(Matrix4 dest)
	{
		dest.m00 = m00;
		dest.m11 = m11;
		dest.m22 = m22;
		dest.m33 = m33;
		float _m01 = m01;
		float _m02 = m02;
		float _m03 = m03;
		float _m12 = m12;
		float _m13 = m13;
		float _m23 = m23;
		dest.m01 = m10;
		dest.m02 = m20;
		dest.m03 = m30;
		dest.m12 = m21;
		dest.m13 = m31;
		dest.m23 = m32;
		dest.m10 = _m01;
		dest.m20 = _m02;
		dest.m30 = _m03;
		dest.m21 = _m12;
		dest.m31 = _m13;
		dest.m32 = _m23;
		return dest;
	}
	
	public Vec4 mul(Vec4 vec)
	{
		return mul(vec, vec);
	}
	
	public Vec4 mul(float x, float y, float z, float w)
	{
		return mul(new Vec4(x, y, z, w), new Vec4());
	}
	
	public Vec4 mul(float x, float y, float z, float w, Vec4 dest)
	{
		return mul(new Vec4(x, y, z, w), dest);
	}
	
	public Vec4 mul(Vec4 vec, Vec4 dest)
	{
		return vec.mul(this, dest);
	}
	
	public Matrix4 mul(Matrix4 right)
	{
		return mul(right, this);
	}
	
	public Matrix4 mul(Matrix4 right, Matrix4 dest)
	{
        float nm00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02 + m30 * right.m03;
        float nm01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02 + m31 * right.m03;
        float nm02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02 + m32 * right.m03;
        float nm03 = m03 * right.m00 + m13 * right.m01 + m23 * right.m02 + m33 * right.m03;
        float nm10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12 + m30 * right.m13;
        float nm11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12 + m31 * right.m13;
        float nm12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12 + m32 * right.m13;
        float nm13 = m03 * right.m10 + m13 * right.m11 + m23 * right.m12 + m33 * right.m13;
        float nm20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22 + m30 * right.m23;
        float nm21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22 + m31 * right.m23;
        float nm22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22 + m32 * right.m23;
        float nm23 = m03 * right.m20 + m13 * right.m21 + m23 * right.m22 + m33 * right.m23;
        float nm30 = m00 * right.m30 + m10 * right.m31 + m20 * right.m32 + m30 * right.m33;
        float nm31 = m01 * right.m30 + m11 * right.m31 + m21 * right.m32 + m31 * right.m33;
        float nm32 = m02 * right.m30 + m12 * right.m31 + m22 * right.m32 + m32 * right.m33;
        float nm33 = m03 * right.m30 + m13 * right.m31 + m23 * right.m32 + m33 * right.m33;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m03 = nm03;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m13 = nm13;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        dest.m23 = nm23;
        dest.m30 = nm30;
        dest.m31 = nm31;
        dest.m32 = nm32;
        dest.m33 = nm33;
        return dest;
	}
	
	public Matrix4 setOrtho(float left, float right, float bottom, float top, float zNear, float zFar)
	{
		return setOrtho(left, right, bottom, top, zNear, zFar, false);
	}
	
	public Matrix4 setOrtho(float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne)
	{
		this.identity();
        this.m00 = 2.0f / (right - left);
        this.m11 = 2.0f / (top - bottom);
        this.m22 = (zZeroToOne ? 1.0f : 2.0f) / (zNear - zFar);
        this.m30 = (right + left) / (left - right);
        this.m31 = (top + bottom) / (bottom - top);
        this.m32 = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);
        return this;
	}
	
	public Matrix4 ortho(float left, float right, float bottom, float top, float zNear, float zFar)
	{
		return ortho(left, right, bottom, top, zNear, zFar, false);
	}
	
	public Matrix4 ortho(float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne)
	{
		return ortho(left, right, bottom, top, zNear, zFar, zZeroToOne, this);
	}
	
	public Matrix4 ortho(float left, float right, float bottom, float top, float zNear, float zFar, Matrix4 dest)
	{
		return ortho(left, right, bottom, top, zNear, zFar, false, dest);
	}
	
	public Matrix4 ortho(float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne, Matrix4 dest)
	{
        // calculate right matrix elements
        float rm00 = 2.0f / (right - left);
        float rm11 = 2.0f / (top - bottom);
        float rm22 = (zZeroToOne ? 1.0f : 2.0f) / (zNear - zFar);
        float rm30 = (left + right) / (left - right);
        float rm31 = (top + bottom) / (bottom - top);
        float rm32 = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);
        // perform optimized multiplication
        // compute the last column first, because other columns do not depend on it
        dest.m30 = m00 * rm30 + m10 * rm31 + m20 * rm32 + m30;
        dest.m31 = m01 * rm30 + m11 * rm31 + m21 * rm32 + m31;
        dest.m32 = m02 * rm30 + m12 * rm31 + m22 * rm32 + m32;
        dest.m33 = m03 * rm30 + m13 * rm31 + m23 * rm32 + m33;
        dest.m00 = m00 * rm00;
        dest.m01 = m01 * rm00;
        dest.m02 = m02 * rm00;
        dest.m03 = m03 * rm00;
        dest.m10 = m10 * rm11;
        dest.m11 = m11 * rm11;
        dest.m12 = m12 * rm11;
        dest.m13 = m13 * rm11;
        dest.m20 = m20 * rm22;
        dest.m21 = m21 * rm22;
        dest.m22 = m22 * rm22;
        dest.m23 = m23 * rm22;
        return dest;
	}
	
	public Matrix4 setPerspective(float fovy, float aspect, float zNear, float zFar)
	{
		return setPerspective(fovy, aspect, zNear, zFar, false);
	}
	
	public Matrix4 setPerspective(float fovy, float aspect, float zNear, float zFar, boolean zZeroToOne)
	{
		this.zero();
        float h = (float) Math.tan(fovy * 0.5f);
        this.m00 = 1.0f / (h * aspect);
        this.m11 = 1.0f / h;
        boolean farInf = zFar > 0 && Float.isInfinite(zFar);
        boolean nearInf = zNear > 0 && Float.isInfinite(zNear);
        if (farInf)
        {
            // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
            float e = 1E-6f;
            this.m22 = e - 1.0f;
            this.m32 = (e - (zZeroToOne ? 1.0f : 2.0f)) * zNear;
        }
        else if (nearInf)
        {
            float e = 1E-6f;
            this.m22 = (zZeroToOne ? 0.0f : 1.0f) - e;
            this.m32 = ((zZeroToOne ? 1.0f : 2.0f) - e) * zFar;
        }
        else
        {
            this.m22 = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
            this.m32 = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
        }
        this.m23 = -1.0f;
        return this;
	}
	
	public Matrix4 perspective(float fovy, float aspect, float zNear, float zFar)
	{
		return perspective(fovy, aspect, zNear, zFar, false);
	}
	
	public Matrix4 perspective(float fovy, float aspect, float zNear, float zFar, boolean zZeroToOne)
	{
		return perspective(fovy, aspect, zNear, zFar, zZeroToOne, this);
	}
	
	public Matrix4 perspective(float fovy, float aspect, float zNear, float zFar, Matrix4 dest)
	{
		return perspective(fovy, aspect, zNear, zFar, false, dest);
	}
	
	public Matrix4 perspective(float fovy, float aspect, float zNear, float zFar, boolean zZeroToOne, Matrix4 dest)
	{
        float h = (float) Math.tan(fovy * 0.5f);
        // calculate right matrix elements
        float rm00 = 1.0f / (h * aspect);
        float rm11 = 1.0f / h;
        float rm22;
        float rm32;
        boolean farInf = zFar > 0 && Float.isInfinite(zFar);
        boolean nearInf = zNear > 0 && Float.isInfinite(zNear);
        if (farInf)
        {
            // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
            float e = 1E-6f;
            rm22 = e - 1.0f;
            rm32 = (e - (zZeroToOne ? 1.0f : 2.0f)) * zNear;
        }
        else if (nearInf)
        {
            float e = 1E-6f;
            rm22 = (zZeroToOne ? 0.0f : 1.0f) - e;
            rm32 = ((zZeroToOne ? 1.0f : 2.0f) - e) * zFar;
        }
        else
        {
            rm22 = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
            rm32 = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
        }
        // perform optimized matrix multiplication
        float nm20 = m20 * rm22 - m30;
        float nm21 = m21 * rm22 - m31;
        float nm22 = m22 * rm22 - m32;
        float nm23 = m23 * rm22 - m33;
        dest.m00 = m00 * rm00;
        dest.m01 = m01 * rm00;
        dest.m02 = m02 * rm00;
        dest.m03 = m03 * rm00;
        dest.m10 = m10 * rm11;
        dest.m11 = m11 * rm11;
        dest.m12 = m12 * rm11;
        dest.m13 = m13 * rm11;
        dest.m30 = m20 * rm32;
        dest.m31 = m21 * rm32;
        dest.m32 = m22 * rm32;
        dest.m33 = m23 * rm32;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        dest.m23 = nm23;
        return dest;
	}
	
	public Matrix3 transpose3()
	{
		return transpose3(new Matrix3());
	}
	
	public Matrix3 transpose3(Matrix3 dest)
	{
		dest.m00(m00);
		dest.m01(m10);
		dest.m02(m20);
		dest.m10(m01);
		dest.m11(m11);
		dest.m12(m21);
		dest.m20(m02);
		dest.m21(m12);
		dest.m22(m22);
		return dest;
	}

    public float determinant()
    {
        return (m00 * m11 - m01 * m10) * (m22 * m33 - m23 * m32)
             + (m02 * m10 - m00 * m12) * (m21 * m33 - m23 * m31)
             + (m00 * m13 - m03 * m10) * (m21 * m32 - m22 * m31)
             + (m01 * m12 - m02 * m11) * (m20 * m33 - m23 * m30)
             + (m03 * m11 - m01 * m13) * (m20 * m32 - m22 * m30)
             + (m02 * m13 - m03 * m12) * (m20 * m31 - m21 * m30);
    }
    
    public float determinant3()
    {
        return (m00 * m11 - m01 * m10) * m22
             + (m02 * m10 - m00 * m12) * m21
             + (m01 * m12 - m02 * m11) * m20;
    }
	
	public Matrix4 invert()
	{
		return invert(this);
	}
	
	public Matrix4 invert(Matrix4 dest)
	{
        float a = m00 * m11 - m01 * m10;
        float b = m00 * m12 - m02 * m10;
        float c = m00 * m13 - m03 * m10;
        float d = m01 * m12 - m02 * m11;
        float e = m01 * m13 - m03 * m11;
        float f = m02 * m13 - m03 * m12;
        float g = m20 * m31 - m21 * m30;
        float h = m20 * m32 - m22 * m30;
        float i = m20 * m33 - m23 * m30;
        float j = m21 * m32 - m22 * m31;
        float k = m21 * m33 - m23 * m31;
        float l = m22 * m33 - m23 * m32;
        float det = a * l - b * k + c * j + d * i - e * h + f * g;
        float nm00, nm01, nm02, nm03, nm10, nm11, nm12, nm13, nm20, nm21, nm22, nm23, nm30, nm31, nm32, nm33;
        det = 1.0f / det;
        nm00 = ( m11 * l - m12 * k + m13 * j) * det;
        nm01 = (-m01 * l + m02 * k - m03 * j) * det;
        nm02 = ( m31 * f - m32 * e + m33 * d) * det;
        nm03 = (-m21 * f + m22 * e - m23 * d) * det;
        nm10 = (-m10 * l + m12 * i - m13 * h) * det;
        nm11 = ( m00 * l - m02 * i + m03 * h) * det;
        nm12 = (-m30 * f + m32 * c - m33 * b) * det;
        nm13 = ( m20 * f - m22 * c + m23 * b) * det;
        nm20 = ( m10 * k - m11 * i + m13 * g) * det;
        nm21 = (-m00 * k + m01 * i - m03 * g) * det;
        nm22 = ( m30 * e - m31 * c + m33 * a) * det;
        nm23 = (-m20 * e + m21 * c - m23 * a) * det;
        nm30 = (-m10 * j + m11 * h - m12 * g) * det;
        nm31 = ( m00 * j - m01 * h + m02 * g) * det;
        nm32 = (-m30 * d + m31 * b - m32 * a) * det;
        nm33 = ( m20 * d - m21 * b + m22 * a) * det;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m03 = nm03;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m13 = nm13;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        dest.m23 = nm23;
        dest.m30 = nm30;
        dest.m31 = nm31;
        dest.m32 = nm32;
        dest.m33 = nm33;
        return dest;
	}
	
	public FloatBuffer put(FloatBuffer buffer)
	{
		return put(buffer, buffer.position());
	}
	
	public FloatBuffer put(FloatBuffer buffer, int pos)
	{
		return buffer
				.put(pos     , m00).put(pos +  1, m01).put(pos +  2, m02).put(pos +  3, m03)
				.put(pos +  4, m10).put(pos +  5, m11).put(pos +  6, m12).put(pos +  7, m13)
				.put(pos +  8, m20).put(pos +  9, m21).put(pos + 10, m22).put(pos + 11, m23)
				.put(pos + 12, m30).put(pos + 13, m31).put(pos + 14, m32).put(pos + 15, m33);
	}

	public Matrix4 rotateZ(float ang)
	{
		return rotateZ(ang, this);
	}

	public Matrix4 rotateZ(float ang, Matrix4 dest)
	{
        float dirX = (float) Math.sin(ang);
        float dirY = (float) Math.cos(ang);
        float rm00 = dirY;
        float rm01 = dirX;
        float rm10 = -dirX;
        float rm11 = dirY;
        float nm00 = m00 * rm00 + m10 * rm01;
        float nm01 = m01 * rm00 + m11 * rm01;
        float nm02 = m02 * rm00 + m12 * rm01;
        float nm03 = m03 * rm00 + m13 * rm01;
        dest.m10 = m00 * rm10 + m10 * rm11;
        dest.m11 = m01 * rm10 + m11 * rm11;
        dest.m12 = m02 * rm10 + m12 * rm11;
        dest.m13 = m03 * rm10 + m13 * rm11;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m03 = nm03;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;
        dest.m23 = m23;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
        return dest;
	}
	
	public Matrix4 setLookAlongYZ(Vec3 dir)
	{
		return setLookAlongYZ(dir.x(), dir.y(), dir.z());
	}
	
	public Matrix4 setLookAlongYZ(float dirX, float dirY, float dirZ)
	{
		return dirZ != 0 || dirX != 0 ? setLookAlong(dirX, dirY, dirZ, 0, 1, 0) : dirY != 0 ? setLookAlong(dirX, dirY, dirZ, 0, 0, 1) : identity(); 
	}
	
	public Matrix4 setLookAlong(Vec3 dir, Vec3 up)
	{
		return setLookAlong(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z());
	}
	
    public Matrix4 setLookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ)
    {
    	// Normalize direction
    	float invDirLength = 1.0f / (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
    	dirX *= -invDirLength;
    	dirY *= -invDirLength;
    	dirZ *= -invDirLength;
    	// left = up x direction
    	float leftX, leftY, leftZ;
    	leftX = upY * dirZ - upZ * dirY;
    	leftY = upZ * dirX - upX * dirZ;
    	leftZ = upX * dirY - upY * dirX;
    	// normalize left
    	float invLeftLength = 1.0f / (float) Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
    	leftX *= invLeftLength;
    	leftY *= invLeftLength;
    	leftZ *= invLeftLength;
    	// up = direction x left
    	float upnX = dirY * leftZ - dirZ * leftY;
    	float upnY = dirZ * leftX - dirX * leftZ;
    	float upnZ = dirX * leftY - dirY * leftX;

    	this.m00 = leftX;
    	this.m01 = upnX;
    	this.m02 = dirX;
    	this.m03 = 0.0f;
    	this.m10 = leftY;
    	this.m11 = upnY;
    	this.m12 = dirY;
    	this.m13 = 0.0f;
    	this.m20 = leftZ;
    	this.m21 = upnZ;
    	this.m22 = dirZ;
    	this.m23 = 0.0f;
    	this.m30 = 0.0f;
    	this.m31 = 0.0f;
    	this.m32 = 0.0f;
    	this.m33 = 1.0f;
    	return this;
    }
    
    public Matrix4 scale(float scale)
    {
    	return scale(scale, scale, scale);
    }
    
    public Matrix4 scale(Vec3 vec)
    {
    	return scale(vec.x(), vec.y(), vec.z());
    }
    
    public Matrix4 scale(float x, float y, float z)
    {
    	return scale(x, y, z, this);
    }
    
    public Matrix4 scale(Vec3 vec, Matrix4 dest)
    {
    	return scale(vec.x(), vec.y(), vec.z(), dest);
    }
    
    public Matrix4 scale(float x, float y, float z, Matrix4 dest)
    {
        dest.m00 = m00 * x;
        dest.m01 = m01 * x;
        dest.m02 = m02 * x;
        dest.m03 = m03 * x;
        dest.m10 = m10 * y;
        dest.m11 = m11 * y;
        dest.m12 = m12 * y;
        dest.m13 = m13 * y;
        dest.m20 = m20 * z;
        dest.m21 = m21 * z;
        dest.m22 = m22 * z;
        dest.m23 = m23 * z;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
        return dest;
    }
    
    public Matrix4 scaleX(float x)
    {
    	return scaleX(x, this);
    }
    
    public Matrix4 scaleX(float x, Matrix4 dest)
    {
        dest.m00 = m00 * x;
        dest.m01 = m01 * x;
        dest.m02 = m02 * x;
        dest.m03 = m03 * x;
        dest.m10 = m10;
        dest.m11 = m11;
        dest.m12 = m12;
        dest.m13 = m13;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;
        dest.m23 = m23;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
        return dest;
    }
    
    public Matrix4 scaleY(float y)
    {
    	return scaleY(y, this);
    }
    
    public Matrix4 scaleY(float y, Matrix4 dest)
    {
        dest.m00 = m00;
        dest.m01 = m01;
        dest.m02 = m02;
        dest.m03 = m03;
        dest.m10 = m10 * y;
        dest.m11 = m11 * y;
        dest.m12 = m12 * y;
        dest.m13 = m13 * y;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;
        dest.m23 = m23;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
        return dest;
    }
    
    public Matrix4 scaleZ(float z)
    {
    	return scaleZ(z, this);
    }
    
    public Matrix4 scaleZ(float z, Matrix4 dest)
    {
        dest.m00 = m00;
        dest.m01 = m01;
        dest.m02 = m02;
        dest.m03 = m03;
        dest.m10 = m10;
        dest.m11 = m11;
        dest.m12 = m12;
        dest.m13 = m13;
        dest.m20 = m20 * z;
        dest.m21 = m21 * z;
        dest.m22 = m22 * z;
        dest.m23 = m23 * z;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
        return dest;
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
    	return "{{" + m00 + ", " + m01 + ", " + m02 + ", " + m03 + "}, {" + m10 + ", " + m11 + ", " + m12 + ", " + m13 + "}, {" + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "}, {" + m30 + ", " + m31 + ", " + m32 + ", " + m33 + "}}";
    }
}