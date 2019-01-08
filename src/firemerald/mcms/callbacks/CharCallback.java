package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFWCharCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;

public class CharCallback extends GLFWCharCallback
{
	@Override
	public void invoke(long window, int codepoint)
	{
		GuiScreen gui;
		if ((gui = Main.instance.gui) != null) gui.onCharTyped((char) codepoint);
	}
}