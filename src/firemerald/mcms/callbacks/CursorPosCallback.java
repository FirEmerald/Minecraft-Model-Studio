package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import firemerald.mcms.Main;

public class CursorPosCallback extends GLFWCursorPosCallback
{
	@Override
	public void invoke(long window, double xpos, double ypos)
	{
		if (window == Main.instance.window)
		{
			Main.instance.mX = xpos;
			Main.instance.mY = ypos;
		}
	}
}