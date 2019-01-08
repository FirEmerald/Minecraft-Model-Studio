package firemerald.mcms.util;

import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.math.Vec4;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;

public class MathUtil
{
	public static final float PI = (float) Math.PI;
	public static final float TAU = (float) (Math.PI * 2);
	public static final float RAD_TO_DEG = (float) (180 / Math.PI);
	public static final float DEG_TO_RAD = (float) (Math.PI / 180);
	
	public static float random(float min, float max)
	{
		return (float) (min + max * Math.random());
	}
	
	public static int clampInt(float val, int min, int max) //clamps and returns min to max from a float of clamped range 0 to 1 
	{
		return val <= 0 ? min : val >= 1 ? max : min + Math.round(val * max);
	}
	
	public static Vec4 rayTrace(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 from, Vec3 dir)
	{
		return rayTrace(p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z(), p3.x(), p3.y(), p3.z(), from.x(), from.y(), from.z(), dir.x(), dir.y(), dir.z());
	}
	
	/*returns a vec4 containing:
	 * a1: amount point 1
	 * a2: amount point 2
	 * a3: amount point 3
	 * m: magnitude of vector from start to intersect, divided by magnitude of direction vector.
	 * or null if zero area triangle or the triangle is parallel to the ray
	 */
	public static Vec4 rayTrace(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float fx, float fy, float fz, float dx, float dy, float dz)
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
			
			
			float a1 = (fx * nm00 + fy * nm01 + fz * nm02 + nm03) / det;
			float a2 = (fx * nm10 + fy * nm11 + fz * nm12 + nm13) / det;
			float a3 = (fx * nm20 + fy * nm21 + fz * nm22 + nm23) / det;
			float m = (-(fx * nm30 + fy * nm31 + fz * nm32) + nnm33) / det;
			return new Vec4(a1, a2, a3, m);
		}
	}
	
	public static Float rayTrace2(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 from, Vec3 dir)
	{
		return rayTrace2(p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z(), p3.x(), p3.y(), p3.z(), from.x(), from.y(), from.z(), dir.x(), dir.y(), dir.z());
	}
	
	/* returns magnitude of vector from start to intersect, divided by magnitude of direction vector, or null for fail (does not count negative magnitude as fail!)*/
	public static Float rayTrace2(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float fx, float fy, float fz, float dx, float dy, float dz)
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
	
	public static Float rayTraceMesh(Vec3 from, Vec3 dir, Mesh mesh, Matrix4 transformation)
	{
		return rayTraceMesh(from.x(), from.y(), from.z(), dir.x(), dir.y(), dir.z(), mesh, transformation);
	}
	
	public static Float rayTraceMesh(float fx, float fy, float fz, float dx, float dy, float dz, Mesh mesh, Matrix4 transformation)
	{
		if (mesh.drawMode == Mesh.DrawMode.TRIANGLES)
		{
			Float res = null;
			float[] vertices = mesh.getVerticies();
			Vec4[] verts = new Vec4[vertices.length / 3];
			int vInd = 0;
			for (int i = 0; i < verts.length; i++) verts[i] = transformation.mul(new Vec4(vertices[vInd++], vertices[vInd++], vertices[vInd++], 1));
			int[] inds = mesh.getIndicies();
			for (int i = 0; i < inds.length; i += 3)
			{
				Vec4 vert1 = verts[inds[i]];
				Vec4 vert2 = verts[inds[i + 1]];
				Vec4 vert3 = verts[inds[i + 2]];
				Float r = rayTrace2(vert1.x(), vert1.y(), vert1.z(), vert2.x(), vert2.y(), vert2.z(), vert3.x(), vert3.y(), vert3.z(), fx, fy, fz, dx, dy, dz);
				if (r != null && r >= 0 && (res == null || r < res)) res = r;
			}
			return res;
		}
		else if (mesh.drawMode == Mesh.DrawMode.QUADS)
		{
			Float res = null;
			float[] vertices = mesh.getVerticies();
			Vec4[] verts = new Vec4[vertices.length / 3];
			int vInd = 0;
			for (int i = 0; i < verts.length; i++) verts[i] = transformation.mul(new Vec4(vertices[vInd++], vertices[vInd++], vertices[vInd++], 1));
			int[] inds = mesh.getIndicies();
			for (int i = 0; i < inds.length; i += 4)
			{
				Vec4 vert1 = verts[inds[i]];
				Vec4 vert2 = verts[inds[i + 1]];
				Vec4 vert3 = verts[inds[i + 2]];
				Vec4 vert4 = verts[inds[i + 3]];
				Float r = rayTrace2(vert1.x(), vert1.y(), vert1.z(), vert2.x(), vert2.y(), vert2.z(), vert3.x(), vert3.y(), vert3.z(), fx, fy, fz, dx, dy, dz);
				if (r != null && r >= 0 && (res == null || r < res)) res = r;
				r = rayTrace2(vert3.x(), vert3.y(), vert3.z(), vert4.x(), vert4.y(), vert4.z(), vert1.x(), vert1.y(), vert1.z(), fx, fy, fz, dx, dy, dz);
				if (r != null && r >= 0 && (res == null || r < res)) res = r;
			}
			return res;
		}
		else return null;
	}
	
	public static int mergeColors(int src, int des)
	{
		float srcA = ((src & 0xFF000000) >>> 24) / 255f;
		int srcR = (src & 0xFF0000) >>> 16;
		int srcG = (src & 0xFF00) >>> 8;
		int srcB = (src & 0xFF);
		float desA = ((des & 0xFF000000) >>> 24) / 255f;
		int desR = (des & 0xFF0000) >>> 16;
		int desG = (des & 0xFF00) >>> 8;
		int desB = (des & 0xFF);
		desA += (1 - desA) * srcA;
		desR += (srcR - desR) * srcA;
		desG += (srcG - desG) * srcA;
		desB += (srcB - desB) * srcA;
		return (((int) (desA * 255)) << 24) | (desR << 16) | (desG << 8) | desB;
	}
	
	public static int mergeColors(Color src, int des)
	{
		float srcA = src.a;
		RGB srcRGB = src.c.getRGB();
		int srcR = (int) (srcRGB.r * 255);
		int srcG = (int) (srcRGB.g * 255);
		int srcB = (int) (srcRGB.b * 255);
		float desA = ((des & 0xFF000000) >>> 24) / 255f;
		int desR = (des & 0xFF0000) >>> 16;
		int desG = (des & 0xFF00) >>> 8;
		int desB = (des & 0xFF);
		desA += (1 - desA) * srcA;
		desR += (srcR - desR) * srcA;
		desG += (srcG - desG) * srcA;
		desB += (srcB - desB) * srcA;
		return (((int) (desA * 255)) << 24) | (desR << 16) | (desG << 8) | desB;
	}
	
	public static Color mergeColors(Color src, Color des)
	{
		float srcA = src.a;
		RGB srcRGB = src.c.getRGB();
		float desA = des.a;
		RGB desRGB = des.c.getRGB();
		float a = desA + (1 - desA) * srcA;
		float r = desRGB.r + (srcRGB.r - desRGB.r) * srcA;
		float g = desRGB.g + (srcRGB.g - desRGB.g) * srcA;
		float b = desRGB.b + (srcRGB.b - desRGB.b) * srcA;
		return new Color(r, g, b, a);
	}
	
	public static float[] getDistancesFrom(float x, float y, float x1, float y1, float x2, float y2)
	{
		if (x1 == x2 && y1 == y2) return null;
		x -= x1;
		y -= y1;
		x2 -= x1;
		y2 -= y1;
		float ds = x2 * x2 + y2 * y2;
		float m = (float) (1 / Math.sqrt(ds));
		float nx = (x * x2 + y * y2);
		if (nx >= 0 && nx <= ds) nx = 0;
		else
		{
			if (nx > 0) nx = (nx - ds) * m;
			else nx = nx * m;
		}
		float ny = (y * x2 - x * y2) * m;
		return new float[] {nx, ny};
	}
	
	public static int floor(float v)
	{
		return (int) Math.floor(v + .00001f);
	}
	
	public static int floor(double v)
	{
		return (int) Math.floor(v + .00001);
	}
	
	public static int ceil(float v)
	{
		return (int) Math.ceil(v - .00001f);
	}
	
	public static int ceil(double v)
	{
		return (int) Math.ceil(v - .00001);
	}
	
	public static int round(float v)
	{
		return Math.round(v);
	}
	
	public static int round(double v)
	{
		return (int) Math.round(v);
	}
}