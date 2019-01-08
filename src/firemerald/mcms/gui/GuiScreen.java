package firemerald.mcms.gui;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;

public abstract class GuiScreen extends GuiElementContainer
{
	protected boolean mouseDown = false, clicked = false;
	protected float pmx, pmy;
	
	public abstract void setSize(int w, int h);
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my, deltaTime);
		if (GLFW.glfwGetMouseButton(Main.instance.window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS)
		{
			if (!mouseDown) clicked = true;
			else if (clicked && (mx != pmx || my != pmy)) onDrag(mx, my);
			mouseDown = true;
		}
		else mouseDown = clicked = false;
		pmx = mx;
		pmy = my;
	}
	
	@Override
	public float getX1()
	{
		return 0;
	}
	
	@Override
	public float getY1()
	{
		return 0;
	}
	
	@Override
	public float getX2()
	{
		return Main.instance.sizeW;
	}
	
	@Override
	public float getY2()
	{
		return Main.instance.sizeH;
	}
}