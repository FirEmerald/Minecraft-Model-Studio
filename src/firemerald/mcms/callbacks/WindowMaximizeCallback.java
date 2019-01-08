package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFWWindowMaximizeCallback;

import firemerald.mcms.Main;

public class WindowMaximizeCallback extends GLFWWindowMaximizeCallback
{
	@Override
	public void invoke(long window, boolean maximized)
	{
		if (window == Main.instance.window) Main.instance.setMaximized(maximized);
	}
}