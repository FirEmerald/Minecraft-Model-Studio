package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.window.glfw.ModifierConverter;

public class MouseButtonCallback extends GLFWMouseButtonCallback
{
	public final Main main;
	
	public MouseButtonCallback(Main main)
	{
		this.main = main;
	}
	
	@Override
	public void invoke(long window, int button, int action, int mods)
	{
		GuiScreen gui;
		if ((gui = main.getGui()) != null)
		{
			mods = ModifierConverter.getModifiers(mods);
			switch (action)
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
}