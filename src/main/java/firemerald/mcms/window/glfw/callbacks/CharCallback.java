package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWCharCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;

public class CharCallback extends GLFWCharCallback
{
	public final Main main;
	
	public CharCallback(Main main)
	{
		this.main = main;
	}
	
	@Override
	public void invoke(long window, int codepoint)
	{
		GuiScreen gui;
		if ((gui = main.getGui()) != null) gui.onCharTyped((char) codepoint);
	}
}