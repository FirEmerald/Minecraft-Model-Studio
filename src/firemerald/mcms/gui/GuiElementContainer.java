package firemerald.mcms.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.IComponent;
import firemerald.mcms.util.Cursors;

public abstract class GuiElementContainer implements IGuiInteractable
{
	protected final List<IGuiElement> guiElements = new ArrayList<>();
	public IComponent focused;
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		for (IGuiElement element : guiElements) element.tick(mx, my, deltaTime);
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		for (IGuiElement element : guiElements) element.render(mx, my, canHover);
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || GLFW.glfwGetMouseButton(Main.instance.window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_RELEASE)
		{
			IComponent preFocused = focused;
			focused = null;
			for (IGuiElement element : guiElements) if (element instanceof IComponent)
			{
				IComponent c = (IComponent) element;
				if (c.contains(mx, my))
				{
					focused = c;
					break;
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
		for (IGuiElement element : guiElements) if (element instanceof IComponent)
		{
			IComponent c = (IComponent) element;
			if (c.contains(mx, my)) return c;
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
	public void onDrag(float mx, float my)
	{
		if (focused != null) focused.onDrag(mx, my);
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
	public void onKeyPressed(int key, int scancode, int mods)
	{
		if (focused != null) focused.onKeyPressed(key, scancode, mods);
	}

	@Override
	public void onKeyReleased(int key, int scancode, int mods)
	{
		if (focused != null) focused.onKeyReleased(key, scancode, mods);
	}

	@Override
	public void onKeyRepeat(int key, int scancode, int mods)
	{
		if (focused != null) focused.onKeyRepeat(key, scancode, mods);
	}
	
	@Override
	public long getCursor(float mx, float my)
	{
		for (IGuiElement element : guiElements) if (element instanceof IComponent)
		{
			IComponent c = (IComponent) element;
			if (c.contains(mx, my)) return c.getCursor(mx, my);
		}
		return Cursors.standard;
	}
	
	public void addElement(IGuiElement element)
	{
		if (element != null) guiElements.add(element);
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
		}
	}
}