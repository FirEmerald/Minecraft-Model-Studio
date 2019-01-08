package firemerald.mcms.gui.themes;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.theme.GuiTheme;

public class Theme extends StandardButton
{
	public final GuiTheme theme;
	
	public Theme(float x1, float y1, float x2, float y2, GuiThemes gui, GuiTheme theme)
	{
		super(x1, y1, x2, y2, 1, 10, theme.name + " (" + theme.origin + ")", () -> {
			if (gui.theme == theme)
			{
				Main.instance.setTheme(theme);
				gui.deactivate();
			}
			else gui.theme = theme;
		});
		this.theme = theme;
	}
	
	@Override
	public GuiTheme getTheme()
	{
		return theme;
	}
}