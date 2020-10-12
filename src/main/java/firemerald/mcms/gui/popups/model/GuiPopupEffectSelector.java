package firemerald.mcms.gui.popups.model;

import java.util.function.BiConsumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.scrolling.ScrollableComponentPaneVertical;
import firemerald.mcms.window.api.MouseButtons;

public class GuiPopupEffectSelector extends GuiPopup
{
	public final Runnable onCancel;
	public IGuiElement from;
	public final ScrollableComponentPaneVertical pane;
	public final int numVals;
	
	public GuiPopupEffectSelector(IGuiElement from, String[] values, BiConsumer<Integer, String> action)
	{
		this(from, values, action, null);
	}
	
	public GuiPopupEffectSelector(IGuiElement from, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		this.onCancel = onCancel;
		this.from = from;
		numVals = values.length;
		int x1 = from.getTrueX1();
		int y1 = from.getTrueY1();
		int x2 = from.getTrueX2();
		int y2 = from.getTrueY2();
		this.addElement(pane = new ScrollableComponentPaneVertical(x1, y1, x2, y2));
		int w = x2 - x1;
		int h = y2 - y1;
		int y = 0;
		for (int i = 0; i < values.length; i++)
		{
			final int j = i;
			final String str = values[i];
			pane.addElement(new StandardButton(0, y, w, y += h, values[i], () ->
			{
				deactivate();
				action.accept(j, str);
			}));
		}
		pane.updateComponentSize();
	}
	
	@Override
	public void setSize(int wW, int wH)
	{
		super.setSize(wW, wH);
		int x1 = from.getTrueX1();
		int y1 = from.getTrueY1();
		int x2 = from.getTrueX2();
		int y2 = from.getTrueY2();
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
		main.shader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.shader.setColor(1, 1, 1, 1);
	}
}