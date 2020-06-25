package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.gui.IGuiHolder;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.history.IHistoryAction;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;

public abstract class Component implements IComponent
{
	protected GuiTheme theme = null;
	public int x1, y1, x2, y2;
	public boolean focused = false;
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
	
	public Component(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public void onFocus()
	{
		this.focused = true;
	}
	
	@Override
	public void onUnfocus()
	{
		this.focused = false;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods) {}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) {}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods) {}

	@Override
	public void onDrag(float mx, float my, int button) {}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY) {}

	@Override
	public void tick(float mx, float my, float deltaTime) {}

	@Override
	public void onCharTyped(char chr) {}

	@Override
	public boolean onKeyPressed(Key key, int scancode, int mods)
	{
		return false;
	}

	@Override
	public boolean onKeyReleased(Key key, int scancode, int mods)
	{
		return false;
	}

	@Override
	public boolean onKeyRepeat(Key key, int scancode, int mods)
	{
		return false;
	}

	@Override
	public Cursor getCursor(float mx, float my)
	{
		return Cursor.STANDARD;
	}
	
	@Override
	public boolean contains(float x, float y)
	{
		return (x >= getX1() && y >= getY1() && x < getX2() && y < getY2());
	}
	
	@Override
	public int getX1()
	{
		return x1;
	}
	
	@Override
	public int getY1()
	{
		return y1;
	}
	
	@Override
	public int getX2()
	{
		return x2;
	}
	
	@Override
	public int getY2()
	{
		return y2;
	}
	
	@Override
	public GuiTheme getTheme()
	{
		return theme == null ? Main.instance.getTheme() : theme;
	}

	@Override
	public void setThemeOverride(GuiTheme theme)
	{
		this.theme = theme;
	}

	@Override
	public boolean onHotkey(Action action)
	{
		return false;
	}

	@Override
	public boolean onAction(IHistoryAction<?> action)
	{
		return false;
	}
}