package firemerald.mcms.gui.themes;

import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.theme.GuiTheme;

public class ThemeExamplePanel extends ComponentPane
{
	public ThemeExamplePanel(float x1, float y1, float x2, float y2, GuiThemes themes)
	{
		super(x1, y1, x2, y2);
		this.addElement(new StandardButton(0, 0, 300, 20, 1, 4, "This is a button", () ->  {}) {
			@Override
			public GuiTheme getTheme()
			{
				return themes.theme;
			}
		});
		
	}
}