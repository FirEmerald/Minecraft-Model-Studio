package firemerald.mcms.window.glfw.callbacks;

import org.lwjgl.glfw.GLFWScrollCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;

public class ScrollCallback extends GLFWScrollCallback
{
	public final Main main;
	
	public ScrollCallback(Main main)
	{
		this.main = main;
	}
	
	@Override
	public void invoke(long window, double xoffset, double yoffset)
	{
		GuiScreen gui;
		if ((gui = main.getGui()) != null && (gui.canScrollH((float) main.mX, (float) main.mY) || gui.canScrollV((float) main.mX, (float) main.mY)))
		{
			gui.onMouseScroll((float) main.mX, (float) main.mY, (float) xoffset, (float) yoffset);
		}
	}
}