package firemerald.mcms.gui.colors;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.gui.components.text.ComponentText;

public class GuiColor extends GuiScreen
{
	public final ComponentColorPicker picker;
	
	public GuiColor()
	{
		this.guiElements.add(picker = new ComponentColorPicker(310, 10));
		this.guiElements.add(new ComponentText(360, 10, 640, 30, Main.instance.fontMsg, "MCAMC", title -> GLFW.glfwSetWindowTitle(Main.instance.window, title)));
	}

	@Override
	public void setSize(int w, int h)
	{
		picker.setPosition(w - 240, 0);
	}
}