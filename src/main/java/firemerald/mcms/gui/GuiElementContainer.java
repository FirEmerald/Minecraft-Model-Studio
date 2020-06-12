package firemerald.mcms.gui;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.IComponent;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.MouseButtons;

public abstract class GuiElementContainer implements IGuiInteractable, IGuiHolder
{
	private final List<IGuiElement> guiElements = new ArrayList<>();
	public IComponent focused;
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
	
	public synchronized IGuiElement[] getElementsCopy()
	{
		return guiElements.toArray(new IGuiElement[guiElements.size()]);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		for (IGuiElement element : getElementsCopy()) element.tick(mx, my, deltaTime);
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		for (IGuiElement element : getElementsCopy()) element.render(mx, my, canHover);
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT || !Main.instance.window.isMouseDown(MouseButtons.LEFT))
		{
			IComponent preFocused = focused;
			focused = null;
			IGuiElement[] elements = getElementsCopy();
			for (int i = elements.length - 1; i >= 0; i--)
			{
				IGuiElement element = elements[i];
				if (element instanceof IComponent)
				{
					IComponent c = (IComponent) element;
					if (c.contains(mx, my))
					{
						focused = c;
						break;
					}
				}
			}
			if (preFocused != focused)
			{
				if (preFocused != null) preFocused.onUnfocus();
				if (focused != null) focused.onFocus();
			}
		}
		if (focused != null) focused.onMousePressed(mx, my, button, mods);
	}
	
	public IComponent getHovered(float mx, float my)
	{
		IGuiElement[] elements = getElementsCopy();
		for (int i = elements.length - 1; i >= 0; i--)
		{
			IGuiElement element = elements[i];
			if (element instanceof IComponent)
			{
				IComponent c = (IComponent) element;
				if (c.contains(mx, my)) return c;
			}
		}
		return null;
	}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) 
	{
		if (focused != null) focused.onMouseRepeat(mx, my, button, mods);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods) 
	{
		if (focused != null) focused.onMouseReleased(mx, my, button, mods);
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (focused != null) focused.onDrag(mx, my, button);
	}
	
	@Override
	public boolean canScrollH(float mx, float my)
	{
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollH(mx, my));
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollV(mx, my));
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY) 
	{
		IComponent hovered = getHovered(mx, my);
		if (hovered != null && (hovered.canScrollH(mx, my) || hovered.canScrollV(mx, my))) hovered.onMouseScroll(mx, my, scrollX, scrollY);
	}

	@Override
	public void onCharTyped(char chr)
	{
		if (focused != null) focused.onCharTyped(chr);
	}

	@Override
	public boolean onKeyPressed(Key key, int scancode, int mods)
	{
		if (focused != null) return focused.onKeyPressed(key, scancode, mods);
		else return false;
	}

	@Override
	public boolean onKeyReleased(Key key, int scancode, int mods)
	{
		if (focused != null) return focused.onKeyReleased(key, scancode, mods);
		else return false;
	}

	@Override
	public boolean onKeyRepeat(Key key, int scancode, int mods)
	{
		if (focused != null) return focused.onKeyRepeat(key, scancode, mods);
		else return false;
	}
	
	@Override
	public Cursor getCursor(float mx, float my)
	{
		IGuiElement[] elements = this.getElementsCopy();
		for (int i = elements.length - 1; i >= 0; i--)
		{
			IGuiElement element = elements[i];
			if (element instanceof IComponent)
			{
				IComponent c = (IComponent) element;
				if (c.contains(mx, my)) return c.getCursor(mx, my);
			}
		} 
		return Cursor.STANDARD;
	}
	
	public void addElement(IGuiElement element)
	{
		if (element != null)
		{
			guiElements.add(element);
			element.setHolder(this);
		}
	}
	
	public void removeElement(IGuiElement element)
	{
		if (element != null)
		{
			guiElements.remove(element);
			if (focused == element)
			{
				focused.onUnfocus();
				focused = null;
			}
			element.setHolder(null);
		}
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		IGuiElement[] elements = getElementsCopy();
		for (int i = elements.length - 1; i >= 0; i--) elements[i].onGuiUpdate(reason);
	}
	
	@Override
	public void setThemeOverride(GuiTheme theme)
	{
		IGuiElement[] elements = getElementsCopy();
		for (int i = elements.length - 1; i >= 0; i--) elements[i].setThemeOverride(theme);
	}
}