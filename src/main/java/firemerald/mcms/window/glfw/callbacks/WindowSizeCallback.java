package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

import firemerald.mcms.Main;
import firemerald.mcms.window.glfw.GLFWWindow;

public class WindowSizeCallback extends GLFWWindowSizeCallback
{
	public final Main main;
	public final GLFWWindow window;
	
	public WindowSizeCallback(Main main, GLFWWindow window)
	{
		this.main = main;
		this.window = window;
	}
	
	@Override
	public void invoke(long window, int width, int height)
	{
		main.setSize(width, height);
		this.window.w = width;
		this.window.h = height;
		main.state.saveState();
	}
}