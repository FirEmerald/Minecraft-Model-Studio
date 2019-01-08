package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;

public class KeyCallback extends GLFWKeyCallback
{
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods)
	{
		GuiScreen gui;
		if ((gui = Main.instance.gui) != null) switch (action)
		{
		case GLFW.GLFW_PRESS:
			gui.onKeyPressed(key, scancode, mods);
			break;
		case GLFW.GLFW_RELEASE:
			gui.onKeyReleased(key, scancode, mods);
			break;
		case GLFW.GLFW_REPEAT:
			gui.onKeyRepeat(key, scancode, mods);
			break;
		}
	}
}