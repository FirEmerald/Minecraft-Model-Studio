package firemerald.mcms.gui.themes;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.scrolling.*;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.GuiUpdate;

public class GuiThemes extends GuiPopup
{
	public final List<Theme> themes = new ArrayList<>();
	public final ScrollableComponentPaneVertical pane;
	public final ScrollBar scrollBar;
	public final ScrollUp scrollUp;
	public final ScrollDown scrollDown;
	public final ThemeExamplePanel panel;
	private GuiTheme theme = Main.instance.getTheme();
	
	public GuiThemes()
	{
		this.addElement(pane = new ScrollableComponentPaneVertical(0, 0, 1, 3));
		this.addElement(scrollBar = new ScrollBar(1, 1, 2, 2, pane));
		this.addElement(scrollUp = new ScrollUp(1, 0, 2, 1, pane));
		this.addElement(scrollDown = new ScrollDown(1, 2, 2, 3, pane));
		this.addElement(panel = new ThemeExamplePanel(0, 0, 1, 1, this));
		pane.setScrollBar(scrollBar);
		File[] candidates = new File("themes").listFiles((FilenameFilter) (dir, name) -> {return name.endsWith(".xml");});
		for (File candidate : candidates)
		{
			AbstractElement el = null;
			try
			{
				el = FileUtil.readFile(candidate);
			}
			catch (Exception e1)
			{
				Main.LOGGER.log(Level.WARN, e1);
			}
			if (el != null)
			{
				for (GuiTheme theme : GuiTheme.makeTheme(el, candidate.toString()))
				{
					Theme t;
					themes.add(t = new Theme(0, 0, 16, 16, this, theme));
					pane.addElement(t);
				}
			}
		}
	}
	
	@Override
	public GuiTheme getTheme()
	{
		return theme;
	}
	
	public void setTheme(GuiTheme theme)
	{
		this.theme = theme;
		panel.setThemeOverride(theme);
		panel.onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		pane.setSize(0, 0, w - 320, h);
		scrollBar.setSize(w - 320, 20, w - 300, h - 20);
		scrollUp.setSize(w - 320, 0, w - 300, 20);
		scrollDown.setSize(w - 320, h - 20, w - 300, h);
		int y = 1;
		for (Theme theme : themes) theme.setSize(1, y, w - 321, y += 20);
		pane.updateComponentSize();
		panel.setSize(w - 300, 0, w, h);
	}
}