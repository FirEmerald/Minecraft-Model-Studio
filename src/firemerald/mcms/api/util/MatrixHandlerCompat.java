package firemerald.mcms.api.util;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.math.Matrix4;

public class MatrixHandlerCompat extends MatrixHandler
{
	@Override
	public void push()
	{
		GL11.glPushMatrix();
	}

	@Override
	public void multMatrix(Matrix4 matrix)
	{
		FloatBuffer buf = MemoryUtil.memAllocFloat(16);
		GL11.glMultMatrixf(matrix.put(buf));
		MemoryUtil.memFree(buf);
	}

	@Override
	public void pop()
	{
		GL11.glPopMatrix();
	}
}