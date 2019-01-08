package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFWWindowPosCallback;

import firemerald.mcms.Main;

public class WindowPosCallback extends GLFWWindowPosCallback
{
	@Override
	public void invoke(long window, int x, int y)
	{
		if (window == Main.instance.window) Main.instance.setPos(x, y);
	}
}