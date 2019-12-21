package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWWindowMaximizeCallback;

import firemerald.mcms.Main;
import firemerald.mcms.window.glfw.GLFWWindow;

public class WindowMaximizeCallback extends GLFWWindowMaximizeCallback
{
	public final Main main;
	public final GLFWWindow window;
	
	public WindowMaximizeCallback(Main main, GLFWWindow window)
	{
		this.main = main;
		this.window = window;
	}
	
	@Override
	public void invoke(long window, boolean maximized)
	{
		this.window.maximized = maximized;
		main.state.saveState();
	}
}