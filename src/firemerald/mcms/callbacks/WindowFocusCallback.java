package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFWWindowFocusCallback;

import firemerald.mcms.Main;

public class WindowFocusCallback extends GLFWWindowFocusCallback
{
	@Override
	public void invoke(long window, boolean focused)
	{
		if (window == Main.instance.window) Main.instance.focused = focused;
	}
}