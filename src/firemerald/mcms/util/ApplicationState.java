package firemerald.mcms.util;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.*;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.theme.GuiTheme;

public class ApplicationState
{
	public static final File FILE = new File("state.bin");
	public static final File FILE2 = new File("state.xml");
	
	private Element root;
	private AbstractElement window;
	private AbstractElement theme;
	
	public ApplicationState()
	{
		root = new Element("state");
		window = root.addChild("window");
		theme = root.addChild("theme");
		
		try {
			Element el = FileUtil.readFile(new File("state.json")).toElement();
			el.saveXML(new File("state_out.xml"));
		} catch (IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setPos(int x, int y)
	{
		window.setInt("x", x);
		window.setInt("y", y);
		saveState();
	}
	
	public void setSize(int w, int h)
	{
		window.setInt("w", w);
		window.setInt("h", h);
		saveState();
	}
	
	public void setMaximized(boolean maximized)
	{
		window.setBoolean("maximized", maximized);
		saveState();
	}
	
	public boolean getMaximized()
	{
		return window.getBoolean("maximized", false);
	}
	
	public int getWindowW()
	{
		int val = window.getInt("w", 1280);
		if (val < 640) val = 640;
		return val;
	}
	
	public int getWindowH()
	{
		int val = window.getInt("h", 720);
		if (val < 480) val = 480;
		return val;
	}
	
	public int getWindowX(int displayW, int windowW)
	{
		int m = displayW - windowW;
		int val = window.getInt("x", m / 2);
		if (val < 0) val = 0;
		else if (val > m) val = m;
		return val;
	}
	
	public int getWindowY(int displayH, int windowH)
	{
		int m = displayH - windowH;
		int val = window.getInt("y", m / 2);
		if (val < 0) val = 0;
		else if (val > m) val = m;
		return val;
	}
	
	public void setTheme(String origin)
	{
		theme.setValue(origin);
		saveState();
	}
	
	public void loadState()
	{
		try
		{
			root = FileUtil.readFile(FILE).toElement();
			window = null;
			theme = null;
			for (AbstractElement child : root.getChildren())
			{
				switch (child.getName())
				{
				case "window":
					window = child;
					break;
				case "theme":
					theme = child;
					break;
				}
			}
			if (window == null) window = root.addChild("window");
			if (theme == null) theme = root.addChild("theme");
			if (theme.getValue() != null) Main.instance.theme = GuiTheme.parseTheme(theme.getValue());
		}
		catch (Exception e)
		{
			Main.LOGGER.log(Level.WARN, "Failed to load previous application state", e);
		}
	}
	
	public void saveState()
	{
		try
		{
			root.saveBinary(FILE, BinaryFormat.UTF_16BE);
			root.saveXML(FILE2);
		}
		catch (IOException | TransformerException e)
		{
			Main.LOGGER.log(Level.WARN, "Failed to save application state", e);
		}
	}
}