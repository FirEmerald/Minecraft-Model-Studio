package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;

public class MouseButtonCallback extends GLFWMouseButtonCallback
{
	@Override
	public void invoke(long window, int button, int action, int mods)
	{
		Main main = Main.instance;
		GuiScreen gui;
		if ((gui = main.gui) != null) switch (action)
		{
		case GLFW.GLFW_PRESS:
			gui.onMousePressed((float) main.mX, (float) main.mY, button, mods);
			break;
		case GLFW.GLFW_RELEASE:
			gui.onMouseReleased((float) main.mX, (float) main.mY, button, mods);
			break;
		case GLFW.GLFW_REPEAT:
			gui.onMouseRepeat((float) main.mX, (float) main.mY, button, mods);
			break;
		}
	}
}