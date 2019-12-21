package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWWindowFocusCallback;

import firemerald.mcms.Main;

public class WindowFocusCallback extends GLFWWindowFocusCallback
{
	public final Main main;
	
	public WindowFocusCallback(Main main)
	{
		this.main = main;
	}
	
	@Override
	public void invoke(long window, boolean focused)
	{
		main.focused = focused;
	}
}