package firemerald.mcms.gui;

import java.util.HashMap;
import java.util.Map;

import firemerald.mcms.Main;
import firemerald.mcms.events.GuiEvent;

public abstract class GuiScreen extends GuiElementContainer
{
	protected final Map<Integer, Boolean> pressed = new HashMap<>();
	public IGuiHolder holder = null;

	@Override
	public void setHolder(IGuiHolder holder)
	{
		this.holder = holder;
	}
	
	@Override
	public IGuiHolder getHolder()
	{
		return this.holder;
	}

	@Override
	public int getComponentOffsetX()
	{
		return 0;
	}

	@Override
	public int getComponentOffsetY()
	{
		return 0;
	}
	
	public void setSize(int w, int h)
	{
		Main.instance.EVENT_BUS.post(new GuiEvent.Resize(this, w, h));
	}
	
	public void onMouseMoved(float mx, float my)
	{
		pressed.forEach((button, isPressed) -> {if (isPressed) onDrag(mx, my, button);});
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		pressed.put(button, true);
		super.onMousePressed(mx, my, button, mods);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		pressed.put(button, false);
		super.onMouseReleased(mx, my, button, mods);
	}
	
	@Override
	public int getX1()
	{
		return 0;
	}
	
	@Override
	public int getY1()
	{
		return 0;
	}
	
	@Override
	public int getX2()
	{
		return Main.instance.sizeW;
	}
	
	@Override
	public int getY2()
	{
		return Main.instance.sizeH;
	}
}