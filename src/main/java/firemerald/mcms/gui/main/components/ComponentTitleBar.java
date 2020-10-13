package firemerald.mcms.gui.main.components;

import java.awt.Menu;
import java.awt.PopupMenu;
import java.util.LinkedHashMap;
import java.util.Map;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.popups.GuiPopupMenu;

public class ComponentTitleBar extends ComponentPanelMain
{
	public final Map<String, TitleButton> buttons = new LinkedHashMap<>();
	
	public static class TitleButton extends StandardButton
	{
		public TitleButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, Menu menu)
		{
			super(x1, y1, x2, y2, outline, radius, text, null);
			this.onRelease = () -> new GuiPopupMenu(this.getTrueX1(), this.getTrueY2(), menu).activate();
		}
		
		public TitleButton(int x1, int y1, int x2, int y2, String text, Menu menu)
		{
			super(x1, y1, x2, y2, text, null);
			this.onRelease = () -> new GuiPopupMenu(this.getTrueX1(), this.getTrueY2(), menu).activate();
		}
	}
	
	public ComponentTitleBar(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
		final int w = 68;
		int x = 0;
		for (Map.Entry<String, PopupMenu> entry : Main.makeTitlebar().entrySet())
		{
			TitleButton button = new TitleButton(x, 0, x += w, 16, 1, 0, entry.getKey(), entry.getValue());
			buttons.put(entry.getKey(), button);
			this.addElement(button);
		}
	}
	
	@Override
	public void onSize(int w, int h) {}
}