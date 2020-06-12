package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import firemerald.mcms.Main;

public class CursorPosCallback extends GLFWCursorPosCallback
{
	public final Main main;
	
	public CursorPosCallback(Main main)
	{
		this.main = main;
	}
	
	@Override
	public void invoke(long window, double xpos, double ypos)
	{
		main.gui.onMouseMoved((float) (main.mX = xpos), (float) (main.mY = ypos));
	}
}