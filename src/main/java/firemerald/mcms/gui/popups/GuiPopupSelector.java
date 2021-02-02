package firemerald.mcms.gui.popups;

import java.util.function.BiConsumer;

import org.joml.Vector4i;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.TriFunction;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.scrolling.ScrollableComponentPaneVertical;
import firemerald.mcms.window.api.MouseButtons;

public class GuiPopupSelector extends GuiPopup
{
	public final Runnable onCancel;
	public IGuiElement from;
	public final ScrollableComponentPaneVertical pane;
	public final int numVals;
	
	public GuiPopupSelector(IGuiElement from, String[] values, BiConsumer<Integer, String> action)
	{
		this(from, values, action, (Runnable) null);
	}
	
	public <T> GuiPopupSelector(IGuiElement from, T[] values, BiConsumer<Integer, T> action, TriFunction<T, Vector4i, Runnable, IGuiElement> newButton)
	{
		this(from, values, action, null, newButton);
	}
	
	public GuiPopupSelector(IGuiElement from, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		this(from, values, action, onCancel, (str, size, onRelease) -> new StandardButton(size.x, size.y, size.z, size.w, str, onRelease));
	}
	
	public <T> GuiPopupSelector(IGuiElement from, T[] values, BiConsumer<Integer, T> action, Runnable onCancel, TriFunction<T, Vector4i, Runnable, IGuiElement> newButton)
	{
		this.onCancel = onCancel;
		this.from = from;
		numVals = values.length;
		int x1 = from.getSelectorX1(this);
		int y1 = from.getSelectorY1(this);
		int x2 = from.getSelectorX2(this);
		int y2 = from.getSelectorY2(this);
		this.addElement(pane = new ScrollableComponentPaneVertical(x1, y1, x2, y2));
		int w = x2 - x1;
		int h = y2 - y1;
		int y = 0;
		for (int i = 0; i < values.length; i++)
		{
			final int j = i;
			final T val = values[i];
			pane.addElement(newButton.apply(val, new Vector4i(0, y, w, y += h), () ->
			{
				deactivate();
				action.accept(j, val);
			}));
		}
		pane.updateComponentSize();
	}
	
	@Override
	public void setSize(int wW, int wH)
	{
		super.setSize(wW, wH);
		int x1 = from.getSelectorX1(this);
		int y1 = from.getSelectorY1(this);
		int x2 = from.getSelectorX2(this);
		int y2 = from.getSelectorY2(this);
		int h = y2 - y1;
		int sY, eY;
		if ((y2 + y1) > Main.instance.sizeH) //on bottom
		{
			sY = Math.max(0, y2 - h * numVals);
			eY = y2;
		}
		else
		{
			sY = y1;
			eY = Math.min(wH, y1 + h * numVals);
		}
		pane.setSize(x1, sY, x2, eY);
	}
	
	public void cancel()
	{
		if (onCancel == null) deactivate();
		else onCancel.run();
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my, button, mods);
		if (button == MouseButtons.LEFT && this.focused == null) deactivate();
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.guiShader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.guiShader.setColor(1, 1, 1, 1);
	}
}