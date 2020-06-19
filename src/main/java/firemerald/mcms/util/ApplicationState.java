package firemerald.mcms.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.*;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.theme.BasicTheme;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.util.hotkey.HotKey;
import firemerald.mcms.window.api.Window;

public class ApplicationState
{
	public static final File FILE = new File("state.xml");
	
	private final GuiTheme basicTheme = new BasicTheme();
	private GuiTheme theme;
	public final Map<Action, HotKey> hotkeys = new HashMap<>();
	private boolean showNodes = false, showBones = false;
	private final List<ColorModel> colorHistory = new ArrayList<>();
	private static final int MAX_COLOR_HISTORY = 16;
	
	public ApplicationState()
	{
		Action.ACTIONS.values().forEach(action -> {
			hotkeys.put(action, action.def);
		});
		final String defTheme = "themes/dark.xml";
		try
		{
			theme = GuiTheme.parseTheme(defTheme);
		}
		catch (Exception e)
		{
			Main.LOGGER.error("Could not load default theme " + defTheme);
			theme = basicTheme;
		}
	}
	
	public ColorModel getColorHistory(int index)
	{
		return index >= 0 && index < MAX_COLOR_HISTORY && index < colorHistory.size() ? colorHistory.get(index).copy() : null;
	}
	
	public void addToColorHistory(ColorModel color)
	{
		colorHistory.remove(color);
		colorHistory.add(0, color.copy());
		while (colorHistory.size() > MAX_COLOR_HISTORY) colorHistory.remove(MAX_COLOR_HISTORY);
		saveState();
	}
	
	public GuiTheme getTheme()
	{
		return theme;
	}
	
	public void setTheme(GuiTheme theme)
	{
		setThemeNoStateUpdate(theme);
		saveState();
	}
	
	public void setThemeNoStateUpdate(GuiTheme theme)
	{
		this.theme = theme;
		Main.instance.onGuiUpdate(GuiUpdate.THEME);
	}

	public boolean showNodes()
	{
		return showNodes;
	}

	public void setShowNodes(boolean showNodes)
	{
		this.showNodes = showNodes;
		saveState();
	}

	public boolean showBones()
	{
		return showBones;
	}

	public void setShowBones(boolean showBones)
	{
		this.showBones = showBones;
		saveState();
	}
	
	public void loadState()
	{
		try
		{
			AbstractElement root = FileUtil.readFile(FILE);
			String theme = "themes/dark.xml";
			Action.ACTIONS.values().forEach(action -> {
				hotkeys.put(action, action.def);
			});
			for (AbstractElement rootChild : root.getChildren()) switch (rootChild.getName())
			{
			case "window":
				Window window = Main.instance.window;
				int displayW = window.getDisplayW();
				int displayH = window.getDisplayH();
				boolean maximized = rootChild.getBoolean("maximized", true);
				window.setMaximized(maximized);
				if (!maximized)
				{
					int windowW = rootChild.getInt("w", 640, displayW, 1280);
					int windowH = rootChild.getInt("h", 480, displayH, 720);
					window.setSize(windowW, windowH);
					int dw = displayW - window.getW();
					int dh = displayH - window.getH();
					int windowX = rootChild.getInt("x", 0, dw, dw / 2);
					int windowY = rootChild.getInt("y", 0, dh, dh / 2);
					window.setPosition(windowX, windowY);
				}
				break;
			case "options":
				for (AbstractElement optionsChild : rootChild.getChildren()) switch (optionsChild.getName())
				{
				case "theme":
					theme = optionsChild.getValue();
					break;
				case "viewer":
					showNodes = optionsChild.getBoolean("showNodes", false);
					showBones = optionsChild.getBoolean("showBones", false);
					break;
				case "hotkeys":
					for (AbstractElement hotkeysChild : optionsChild.getChildren())
					{
						String name = hotkeysChild.getName();
						Action act = Action.ACTIONS.get(name);
						if (act == null)
						{
							GuiPopupException.onException("Couldn't load missing hotkey action " + name);
							continue;
						}
						else hotkeys.put(act, new HotKey(hotkeysChild));
					}
				}
				break;
			case "color_history":
				colorHistory.clear();
				for (AbstractElement colorEl : rootChild.getChildren())
				{
					if (colorEl.getName().equals("rgb")) colorHistory.add(new RGB(colorEl.getInt("r", 0, 255, 0) / 255f, colorEl.getInt("g", 0, 255, 0) / 255f, colorEl.getInt("b", 0, 255, 0) / 255f));
				}
				break;
			}
			try
			{
				setThemeNoStateUpdate(GuiTheme.parseTheme(theme));
			}
			catch (Exception e)
			{
				GuiPopupException.onException("Could not load theme " + theme);
				setThemeNoStateUpdate(basicTheme);
			}
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Failed to load previous application state", e);
		}
	}
	
	public void saveState()
	{
		try
		{
			Element root = new Element("state");
			AbstractElement window = root.addChild("window");
			Window wind = Main.instance.window;
			boolean maximized = wind.isMaximized();
			window.setBoolean("maximized", maximized);
			if (!maximized)
			{
				window.setInt("x", wind.getX());
				window.setInt("y", wind.getY());
			}
			window.setInt("w", wind.getW());
			window.setInt("h", wind.getH());
			AbstractElement options = root.addChild("options");
			AbstractElement theme = options.addChild("theme");
			theme.setValue(this.theme.origin);
			AbstractElement viewer = options.addChild("viewer");
			viewer.setBoolean("showNodes", showNodes);
			viewer.setBoolean("showBones", showBones);
			AbstractElement hotkeys = options.addChild("hotkeys");
			this.hotkeys.forEach((action, hotkey) -> hotkey.writeToElement(hotkeys.addChild(action.id)));
			if (!colorHistory.isEmpty())
			{
				AbstractElement colorHistory = root.addChild("color_history");
				this.colorHistory.forEach(color -> {
					RGB rgb = color.getRGB();
					AbstractElement colorEl = colorHistory.addChild("rgb");
					colorEl.setInt("r", (int) (rgb.r * 255));
					colorEl.setInt("g", (int) (rgb.g * 255));
					colorEl.setInt("b", (int) (rgb.b * 255));
				});
			}
			root.saveXML(FILE);
		}
		catch (IOException | TransformerException e)
		{
			GuiPopupException.onException("Failed to save application state", e);
		}
	}
}