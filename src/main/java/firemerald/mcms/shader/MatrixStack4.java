package firemerald.mcms.shader;

import java.util.Stack;

import org.joml.Matrix4d;

import firemerald.mcms.Main;

public class MatrixStack4
{
	private final Stack<Matrix4d> stack = new Stack<>();
	private Matrix4d matrix = new Matrix4d();
	
	public void push()
	{
		stack.push(matrix);
		matrix = new Matrix4d(matrix);
	}
	
	public void pop()
	{
		if (!stack.empty()) matrix = stack.pop();
		else Main.LOGGER.warn("tried to pop from empty matrix stack!");
	}
	
	public Matrix4d matrix()
	{
		return matrix;
	}
}
