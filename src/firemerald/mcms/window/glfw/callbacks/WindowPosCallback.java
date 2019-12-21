package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWWindowPosCallback;

import firemerald.mcms.Main;
import firemerald.mcms.window.glfw.GLFWWindow;

public class WindowPosCallback extends GLFWWindowPosCallback
{
	public final Main main;
	public final GLFWWindow window;
	
	public WindowPosCallback(Main main, GLFWWindow window)
	{
		this.main = main;
		this.window = window;
	}
	
	@Override
	public void invoke(long window, int x, int y)
	{
		this.window.x = x;
		this.window.y = y;
		main.state.saveState();
	}
}