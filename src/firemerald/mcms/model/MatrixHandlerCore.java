package firemerald.mcms.model;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.util.MatrixHandler;
import firemerald.mcms.shader.Shader;

public class MatrixHandlerCore extends MatrixHandler
{
	@Override
	public void push()
	{
		Shader.MODEL.push();
	}

	@Override
	public void multMatrix(Matrix4 matrix)
	{
		Shader.MODEL.matrix().mul(matrix);
		Main.instance.shader.updateModel();
	}

	@Override
	public void pop()
	{
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
}