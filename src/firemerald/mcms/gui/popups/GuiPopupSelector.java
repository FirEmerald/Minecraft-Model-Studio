package firemerald.mcms.gui.popups;

import java.util.function.BiConsumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.IComponent;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.window.api.MouseButtons;

public class GuiPopupSelector extends GuiPopup
{
	public final Runnable onCancel;
	
	public GuiPopupSelector(int x1, int y1, int x2, int y2, String[] values, BiConsumer<Integer, String> action)
	{
		this(x1, y1, x2, y2, values, action, null);
	}
	
	public GuiPopupSelector(int x1, int y1, int x2, int y2, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		this.onCancel = onCancel;
		int w = (y2 - y1);
		int y = y1;
		for (int i = 0; i < values.length; i++)
		{
			final int j = i;
			final String str = values[i];
			this.addElement(new StandardButton(x1, y, x2, y += w, values[i], () ->
			{
				action.accept(j, str);
				deactivate();
			}));
		}
	}
	
	public void cancel()
	{
		if (onCancel == null) deactivate();
		else onCancel.run();
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
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
		else cancel();
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.shader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.shader.setColor(1, 1, 1, 1);
	}
}