package firemerald.mcms.shader;

import java.util.Stack;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.Matrix4;

public class MatrixStack4
{
	private final Stack<Matrix4> stack = new Stack<>();
	private Matrix4 matrix = new Matrix4();
	
	public void push()
	{
		stack.push(matrix);
		matrix = new Matrix4(matrix);
	}
	
	public void pop()
	{
		if (!stack.empty()) matrix = stack.pop();
		else Main.LOGGER.warn("tried to pop from empty matrix stack!");
	}
	
	public Matrix4 matrix()
	{
		return matrix;
	}
}
