package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

import firemerald.mcms.Main;

public class WindowSizeCallback extends GLFWWindowSizeCallback
{
	@Override
	public void invoke(long window, int width, int height)
	{
		if (window == Main.instance.window) Main.instance.setSize(width, height);
	}
}