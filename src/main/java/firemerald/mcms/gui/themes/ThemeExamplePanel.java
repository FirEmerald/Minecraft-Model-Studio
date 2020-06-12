package firemerald.mcms.gui.themes;

import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.gui.components.StandardButton;

public class ThemeExamplePanel extends ComponentPane
{
	public ThemeExamplePanel(int x1, int y1, int x2, int y2, GuiThemes themes)
	{
		super(x1, y1, x2, y2);
		this.addElement(new StandardButton(0, 0, 300, 20, 1, 4, "This is a button", () ->  {}));
	}
}