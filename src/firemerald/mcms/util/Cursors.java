package firemerald.mcms.util;

import static org.lwjgl.glfw.GLFW.*;

public class Cursors
{
	public static long standard, text, crosshair, hand, hResize, vResize;
	
	public static void init()
	{
		standard = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		text = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
		crosshair = glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
		hand = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
		hResize = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
		vResize = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
	}
}