package firemerald.mcms.util;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public enum TransformType //in order of how common
{
    FIXED(convert(0, 0, 0, 0, 180, 0, 1)),
    THIRD_PERSON_RIGHT_HAND(convert(0, 3, 1, 0, 0, 0, 0.55f)),
    THIRD_PERSON_LEFT_HAND(leftify(THIRD_PERSON_RIGHT_HAND.transformation)),
    HEAD(convert(0, 13, 7, 0, 180, 0, 1)),
    GROUND(convert(0, 2, 0, 0, 0, 0, 0.5f)),
    GUI(TRSRTransformation.identity()),
    FIRST_PERSON_RIGHT_HAND(convert(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f)),
    FIRST_PERSON_LEFT_HAND(leftify(FIRST_PERSON_RIGHT_HAND.transformation)),
    NONE(TRSRTransformation.identity());
	
	final TRSRTransformation transformation;
	
	TransformType(TRSRTransformation transformation)
	{
		this.transformation = transformation;
	}
	
	public Matrix4d matrix()
	{
		return new Matrix4d(transformation.matrix);
	}
	
    public static TRSRTransformation convert(float tx, float ty, float tz, float ax, float ay, float az, float s)
    {
        return convert(tx, ty, tz, ax, ay, az, s, s, s);
    }

    public static TRSRTransformation convert(float tx, float ty, float tz, float ax, float ay, float az, float sx, float sy, float sz)
    {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
                new Vector3f(sx, sy, sz),
                null
        ));
    }

    public static TRSRTransformation leftify(TRSRTransformation transform)
    {
        return TRSRTransformation.blockCenterToCorner(TRSRTransformation.flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(TRSRTransformation.flipX));
    }
    
    public static class TRSRTransformation
    {

        private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);
    	public static final TRSRTransformation THIRD_PERSON_RIGHT_HAND_TRANSFORM = convert(0, 3, 1, 0, 0, 0, 0.55f);
    	public static final TRSRTransformation FIRST_PERSON_RIGHT_HAND_TRANSFORM = convert(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
    	
        private final Matrix4f matrix;

        private boolean full;
        @SuppressWarnings("unused")
		private Vector3f translation;
        private Quaternionf leftRot;
        @SuppressWarnings("unused")
		private Vector3f scale;
        @SuppressWarnings("unused")
		private Quaternionf rightRot;

        @SuppressWarnings("unused")
		private Matrix3f normalTransform;

        private static final TRSRTransformation identity;

        static
        {
            Matrix4f m = new Matrix4f();
            identity = new TRSRTransformation(m);
            identity.getLeftRot();
        }

        public static TRSRTransformation identity()
        {
            return identity;
        }

		public TRSRTransformation(@Nullable Matrix4f matrix)
        {
            if (matrix == null)
            {
                this.matrix = identity.matrix;
            }
            else
            {
                this.matrix = matrix;
            }
        }

        public TRSRTransformation(@Nullable Vector3f translation, @Nullable Quaternionf leftRot, @Nullable Vector3f scale, @Nullable Quaternionf rightRot)
        {
            this.matrix = mul(translation, leftRot, scale, rightRot);
            this.translation = translation != null ? translation : new Vector3f();
            this.leftRot = leftRot != null ? leftRot : new Quaternionf(0, 0, 0, 1);
            this.scale = scale != null ? scale : new Vector3f(1, 1, 1);
            this.rightRot = rightRot!= null ? rightRot : new Quaternionf(0, 0, 0, 1);
            full = true;
        }

        public Quaternionf getLeftRot()
        {
            genCheck();
            return new Quaternionf(leftRot);
        }

        private void genCheck()
        {
            if(!full)
            {
                Pair<Matrix3f, Vector3f> pair = toAffine(matrix);
                Triple<Quaternionf, Vector3f, Quaternionf> triple = svdDecompose(pair.left);
                this.translation = pair.right;
                this.leftRot = triple.left;
                this.scale = triple.middle;
                this.rightRot = triple.right;
                full = true;
            }
        }
        
        public static Pair<Matrix3f, Vector3f> toAffine(Matrix4f m)
        {
            m.scale(1.f / m.m33());
            Vector3f trans = new Vector3f(m.m03(), m.m13(), m.m23());
            Matrix3f linear = new Matrix3f(m.m00(), m.m01(), m.m02(), m.m10(), m.m11(), m.m12(), m.m20(), m.m21(), m.m22());
            return new Pair<Matrix3f, Vector3f>(linear, trans);
        }

        public static TRSRTransformation blockCenterToCorner(TRSRTransformation transform)
        {
            if (transform.isIdentity()) return transform;

            Matrix4f ret = new Matrix4f(transform.getMatrix()), tmp = new Matrix4f();
            tmp.m03(.5f);
            tmp.m13(.5f);
            tmp.m23(.5f);
            ret.mul(tmp, ret);
            tmp.m03(-.5f);
            tmp.m13(-.5f);
            tmp.m23(-.5f);
            ret.mul(tmp);
            return new TRSRTransformation(ret);
        }

        public static TRSRTransformation blockCornerToCenter(TRSRTransformation transform)
        {
            if (transform.isIdentity()) return transform;

            Matrix4f ret = new Matrix4f(transform.getMatrix()), tmp = new Matrix4f();
            tmp.m03(-.5f);
            tmp.m13(-.5f);
            tmp.m23(-.5f);
            ret.mul(tmp, ret);
            tmp.m03(.5f);
            tmp.m13(.5f);
            tmp.m23(.5f);
            ret.mul(tmp);
            return new TRSRTransformation(ret);
        }

        public static Quaternionf quatFromXYZDegrees(Vector3f xyz)
        {
            return quatFromXYZ((float)Math.toRadians(xyz.x), (float)Math.toRadians(xyz.y), (float)Math.toRadians(xyz.z));
        }

        public static Quaternionf quatFromXYZ(Vector3f xyz)
        {
            return quatFromXYZ(xyz.x, xyz.y, xyz.z);
        }

        public static Quaternionf quatFromXYZ(float x, float y, float z)
        {
        	Quaternionf ret = new Quaternionf(0, 0, 0, 1), t = new Quaternionf();
            t.set((float)Math.sin(x/2), 0, 0, (float)Math.cos(x/2));
            ret.mul(t);
            t.set(0, (float)Math.sin(y/2), 0, (float)Math.cos(y/2));
            ret.mul(t);
            t.set(0, 0, (float)Math.sin(z/2), (float)Math.cos(z/2));
            ret.mul(t);
            return ret;
        }

        public TRSRTransformation compose(TRSRTransformation b)
        {
            if (this.isIdentity()) return b;
            if (b.isIdentity()) return this;
            Matrix4f m = getMatrix();
            m.mul(b.getMatrix());
            return new TRSRTransformation(m);
        }

        public static Matrix4f mul(@Nullable Vector3f translation, @Nullable Quaternionf leftRot, @Nullable Vector3f scale, @Nullable Quaternionf rightRot)
        {
            Matrix4f res = new Matrix4f(), t = new Matrix4f();
            if(leftRot != null)
            {
                t.set(leftRot);
                res.mul(t);
            }
            if(scale != null)
            {
                t.identity();
                t.m00(scale.x);
                t.m11(scale.y);
                t.m22(scale.z);
                res.mul(t);
            }
            if(rightRot != null)
            {
                t.set(rightRot);
                res.mul(t);
            }
            if(translation != null) res.setTranslation(translation);
            return res;
        }

        /*
         * Performs SVD decomposition of m, accumulating reflection in the scale (U and V are pure rotations).
         */
        public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f m)
        {
            // determine V by doing 5 steps of Jacobi iteration on MT * M
        	Quaternionf u = new Quaternionf(0, 0, 0, 1), v = new Quaternionf(0, 0, 0, 1), qt = new Quaternionf();
            Matrix3f b = new Matrix3f(m), t = new Matrix3f();
            t.transpose(m);
            b.mul(t, b);

            for(int i = 0; i < 5; i++) v.mul(stepJacobi(b));

            v.normalize();
            t.set(v);
            b.set(m);
            b.mul(t);

            // FIXME: this doesn't work correctly for some reason; not crucial, so disabling for now; investigate in the future.
            //sortSingularValues(b, v);

            Pair<Float, Float> p;

            float ul = 1f;

            p = qrGivensQuat(b.m00, b.m10);
            qt.set(0, 0, p.left, p.right);
            u.mul(qt);
            t.identity();
            t.m00 = qt.w * qt.w - qt.z * qt.z;
            t.m11 = t.m00;
            t.m10 = -2 * qt.z * qt.w;
            t.m01 = -t.m10;
            t.m22 = qt.w * qt.w + qt.z * qt.z;
            ul *= t.m22;
            b.mul(t, b);

            p = qrGivensQuat(b.m00, b.m20);
            qt.set(0, -p.left, 0, p.right);
            u.mul(qt);
            t.identity();
            t.m00 = qt.w * qt.w - qt.y * qt.y;
            t.m22 = t.m00;
            t.m20 = 2 * qt.y * qt.w;
            t.m02 = -t.m20;
            t.m11 = qt.w * qt.w + qt.y * qt.y;
            ul *= t.m11;
            b.mul(t, b);

            p = qrGivensQuat(b.m11, b.m21);
            qt.set(p.left, 0, 0, p.right);
            u.mul(qt);
            t.identity();
            t.m11 = qt.w * qt.w - qt.x * qt.x;
            t.m22 = t.m11;
            t.m21 = -2 * qt.x * qt.w;
            t.m12 = -t.m21;
            t.m00 = qt.w * qt.w + qt.x * qt.x;
            ul *= t.m00;
            b.mul(t, b);

            ul = 1f / ul;
            u.scale((float)Math.sqrt(ul));

            Vector3f s = new Vector3f(b.m00 * ul, b.m11 * ul, b.m22 * ul);

            return new Triple<Quaternionf, Vector3f, Quaternionf>(u, s, v);
        }

        public boolean isIdentity()
        {
            return this.equals(identity);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            TRSRTransformation other = (TRSRTransformation) obj;
            return matrix.equals(other.matrix);
        }

        public Matrix4f getMatrix()
        {
            return new Matrix4f(matrix);
        }

        private static Quaternionf stepJacobi(Matrix3f m)
        {
            Matrix3f t = new Matrix3f();
            Quaternionf qt = new Quaternionf(), ret = new Quaternionf(0, 0, 0, 1);
            Pair<Float, Float> p;
            // 01
            if(m.m01 * m.m01 + m.m10 * m.m10 > eps)
            {
                p = approxGivensQuat(m.m00, .5f * (m.m01 + m.m10), m.m11);
                qt.set(0, 0, p.left, p.right);
                //qt.normalize();
                ret.mul(qt);
                //t.set(qt);
                t.identity();
                t.m00 = qt.w * qt.w - qt.z * qt.z;
                t.m11 = t.m00;
                t.m10 = 2 * qt.z * qt.w;
                t.m01 = -t.m10;
                t.m22 = qt.w * qt.w + qt.z * qt.z;
                m.mul(m, t);
                t.transpose();
                m.mul(t, m);
            }
            // 02
            if(m.m02 * m.m02 + m.m20 * m.m20 > eps)
            {
                p = approxGivensQuat(m.m00, .5f * (m.m02 + m.m20), m.m22);
                qt.set(0, -p.left, 0, p.right);
                //qt.normalize();
                ret.mul(qt);
                //t.set(qt);
                t.identity();
                t.m00 = qt.w * qt.w - qt.y * qt.y;
                t.m22 = t.m00;
                t.m20 = -2 * qt.y * qt.w;
                t.m02 = -t.m20;
                t.m11 = qt.w * qt.w + qt.y * qt.y;
                m.mul(m, t);
                t.transpose();
                m.mul(t, m);
            }
            // 12
            if(m.m12 * m.m12 + m.m21 * m.m21 > eps)
            {
                p = approxGivensQuat(m.m11, .5f * (m.m12 + m.m21), m.m22);
                qt.set(p.left, 0, 0, p.right);
                //qt.normalize();
                ret.mul(qt);
                //t.set(qt);
                t.identity();
                t.m11 = qt.w * qt.w - qt.x * qt.x;
                t.m22 = t.m11;
                t.m21 = 2 * qt.x * qt.w;
                t.m12 = -t.m21;
                t.m00 = qt.w * qt.w + qt.x * qt.x;
                m.mul(m, t);
                t.transpose();
                m.mul(t, m);
            }
            return ret;
        }
        
        private static final float eps = 1e-6f;
        private static final float g = 3f + 2f * (float)Math.sqrt(2);
        private static final float cs = (float)Math.cos(Math.PI / 8);
        private static final float ss = (float)Math.sin(Math.PI / 8);

        private static Pair<Float, Float> qrGivensQuat(float a1, float a2)
        {
            float p = (float)Math.sqrt(a1 * a1 + a2 * a2);
            float sh = p > eps ? a2 : 0;
            float ch = Math.abs(a1) + Math.max(p, eps);
            if(a1 < 0)
            {
                float f = sh;
                sh = ch;
                ch = f;
            }
            //float w = 1.f / (float)Math.sqrt(ch * ch + sh * sh);
            float w = rsqrt(ch * ch + sh * sh);
            ch *= w;
            sh *= w;
            return new Pair<Float, Float>(sh, ch);
        }

        private static Pair<Float, Float> approxGivensQuat(float a11, float a12, float a22)
        {
            float ch = 2f * (a11 - a22);
            float sh = a12;
            boolean b = g * sh * sh < ch * ch;
            float w = rsqrt(sh * sh + ch * ch);
            ch = b ? w * ch : cs;
            sh = b ? w * sh : ss;
            return new Pair<Float, Float>(sh, ch);
        }

        private static float rsqrt(float f)
        {
            float f2 = .5f * f;
            int i = Float.floatToIntBits(f);
            i = 0x5f3759df - (i >> 1);
            f = Float.intBitsToFloat(i);
            f *= 1.5f - f2 * f * f;
            return f;
        }
    }
}