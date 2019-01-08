package firemerald.mcms.callbacks;

import org.lwjgl.glfw.GLFWScrollCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;

public class ScrollCallback extends GLFWScrollCallback
{
	@Override
	public void invoke(long window, double xoffset, double yoffset)
	{
		Main main = Main.instance;
		GuiScreen gui;
		if ((gui = main.gui) != null && (gui.canScrollH((float) main.mX, (float) main.mY) || gui.canScrollV((float) main.mX, (float) main.mY)))
		{
			gui.onMouseScroll((float) main.mX, (float) main.mY, (float) xoffset, (float) yoffset);
		}
	}
}