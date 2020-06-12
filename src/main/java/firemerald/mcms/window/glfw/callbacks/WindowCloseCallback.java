package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWWindowCloseCallback;

import firemerald.mcms.Main;
import firemerald.mcms.window.glfw.GLFWWindow;

public class WindowCloseCallback extends GLFWWindowCloseCallback
{
	public final Main main;
	public final GLFWWindow window;
	
	public WindowCloseCallback(Main main, GLFWWindow window)
	{
		this.main = main;
		this.window = window;
	}

	@Override
	public void invoke(long window)
	{
		main.tryClose();
	}
}