package firemerald.mcms.gui.themes;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.GuiUpdate;

public class Theme extends StandardButton
{
	public Theme(int x1, int y1, int x2, int y2, GuiThemes gui, GuiTheme theme)
	{
		super(x1, y1, x2, y2, 1, 8, theme.name + " (" + theme.origin + ")", null);
		this.theme = theme;
		this.onRelease = () -> {
			if (gui.getTheme() == this.theme)
			{
				Main.instance.setTheme(this.theme);
				gui.deactivate();
			}
			else gui.setTheme(this.theme);
		};
		this.onGuiUpdate(GuiUpdate.THEME);
	}
}