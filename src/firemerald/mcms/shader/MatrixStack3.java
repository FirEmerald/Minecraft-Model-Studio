package firemerald.mcms.shader;

import java.util.Stack;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.Matrix3;

public class MatrixStack3
{
	private final Stack<Matrix3> stack = new Stack<>();
	private Matrix3 matrix = new Matrix3();
	
	public void push()
	{
		stack.push(matrix);
		matrix = new Matrix3(matrix);
	}
	
	public void pop()
	{
		if (!stack.empty()) matrix = stack.pop();
		else Main.LOGGER.warn("tried to pop from empty matrix stack!");
	}
	
	public Matrix3 matrix()
	{
		return matrix;
	}
}
