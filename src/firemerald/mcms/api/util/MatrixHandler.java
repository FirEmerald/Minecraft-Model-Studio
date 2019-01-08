package firemerald.mcms.api.util;

import firemerald.mcms.api.math.Matrix4;

public abstract class MatrixHandler
{
	public static MatrixHandler instance = new MatrixHandlerCompat();
	
	public abstract void push();
	
	public abstract void multMatrix(Matrix4 matrix);
	
	public abstract void pop();
}