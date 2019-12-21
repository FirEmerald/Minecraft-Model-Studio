package firemerald.mcms.gui.popups;

import java.awt.Menu;

import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.menu.ComponentMenu;
import firemerald.mcms.window.api.MouseButtons;

public class GuiPopupMenu extends GuiPopup
{
	public boolean allowClose = true;
	
	public GuiPopupMenu(int x, int y, Menu menu)
	{
		this.addElement(new ComponentMenu(x, y, menu, this));
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my, button, mods);
		if (allowClose && focused == null && button == MouseButtons.LEFT) deactivate();
	}
}