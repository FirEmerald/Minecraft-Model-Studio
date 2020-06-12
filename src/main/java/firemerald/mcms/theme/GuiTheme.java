package firemerald.mcms.theme;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.data.MergedElement;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.MathUtil;

public abstract class GuiTheme
{
	public static final GuiTheme DEFAULT = new BasicTheme();
	
	public final String name;
	public final String origin;
	
	public GuiTheme(String name, String origin)
	{
		this.name = name;
		this.origin = origin;
	}
	
	public abstract void cleanUp();
	
	public abstract void drawBackground();
	
	public ThemeElement genBox(int w, int h)
	{
		return genRoundedBox(w, h, 1, 0);
	}
	
	public ThemeElement genBox(int w, int h, int outline)
	{
		return genRoundedBox(w, h, outline, 0);
	}
	
	public ThemeElement genRoundedBox(int w, int h, int radius)
	{
		return genRoundedBox(w, h, 1, radius);
	}
	
	public abstract ThemeElement genRoundedBox(int w, int h, int outline, int radius);
	
	public ThemeElement genTextBox(int w, int h)
	{
		return genTextBox(w, h, 1);
	}
	
	public abstract ThemeElement genTextBox(int w, int h, int outline);
	
	public ThemeElement genScrollBar(int w, int h)
	{
		return genScrollBar(w, h, 1);
	}
	
	public abstract ThemeElement genScrollBar(int w, int h, int outline);
	
	public ThemeElement genScrollButton(int w, int h, EnumDirection direction)
	{
		return genScrollButton(w, h, 1, direction);
	}
	
	public abstract ThemeElement genScrollButton(int w, int h, int outline, EnumDirection direction);
	
	public ThemeElement genDirectionButton(int w, int h, EnumDirection direction)
	{
		return genDirectionButton(w, h, 1, 0, direction);
	}
	
	public ThemeElement genDirectionButton(int w, int h, int outline, EnumDirection direction)
	{
		return genDirectionButton(w, h, outline, 0, direction);
	}
	
	public abstract ThemeElement genDirectionButton(int w, int h, int outline, int radius, EnumDirection direction);
	
	public ThemeElement genArrowedButton(int w, int h, float x1, float y1, float x2, float y2, EnumDirection direction)
	{
		return genArrowedButton(w, h, 1, 0, x1, y1, x2, y2, direction);
	}
	
	public ThemeElement genArrowedButton(int w, int h, int outline, float x1, float y1, float x2, float y2, EnumDirection direction)
	{
		return genArrowedButton(w, h, outline, 0, x1, y1, x2, y2, direction);
	}
	
	public abstract ThemeElement genArrowedButton(int w, int h, int outline, int radius, float x1, float y1, float x2, float y2, EnumDirection direction);
	
	public ThemeElement genArrow(int h, EnumDirection direction)
	{
		return genArrow(h, 1, direction);
	}
	
	public abstract ThemeElement genArrow(int h, int outline, EnumDirection direction);
	
	public ThemeElement genMenuSeperator(int w, int h)
	{
		return genMenuSeperator(w, h, 1, 1);
	}
	
	public ThemeElement genMenuSeperator(int w, int h, int thickness)
	{
		return genMenuSeperator(w, h, thickness, 1);
	}
	
	public abstract ThemeElement genMenuSeperator(int w, int h, int thickness, int offset);
	
	public abstract ThemeElement genTab(int w, int h, int outline, int radius, EnumDirection direction, boolean connectLeft, boolean connectRight);
	
	public abstract Color getTextColor();
	
	public abstract Color getFillColor();
	
	public abstract Color getOutlineColor();
	
	public static void makeTexture(int tex, ByteBuffer texture, int w, int h)
	{
		glBindTexture(GL_TEXTURE_2D, tex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_BYTE, texture);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	public static class InstanceElement
	{
		public final String name;
		public final String variant;
		public final AbstractElement element;
		public final boolean compile;
		
		public InstanceElement(AbstractElement element, String parentName, String variant, boolean compile)
		{
			this.name = String.format((this.element = element).getString("name", ""), parentName);
			this.variant = variant;
			this.compile = compile;
		}
	}
	
	public static GuiTheme parseTheme(String origin)
	{
		String file;
		String[] variant;
		int pos = origin.indexOf('|');
		if (pos < 0)
		{
			file = origin;
			variant = new String[0];
		}
		else
		{
			file = origin.substring(0, pos);
			variant = origin.substring(pos + 1).split("\\|");
		}
		InstanceElement fin = null;
		try
		{
			AbstractElement root = FileUtil.readFile(new File(file));
			if (root.getBoolean("compile", true))
			{
				List<AbstractElement> files = new ArrayList<>();
				root.addToList(files);
				addParentToList(root, files);
				String name = "";
				for (int i = files.size() - 1; i >= 0; i--)
				{
					AbstractElement el = files.get(i);
					if (el.hasAttribute("name")) name = String.format(el.getString("name", "null"), name);
				}
				AbstractElement el;
				InstanceElement t = new InstanceElement(el = new MergedElement(files), name, origin, true);
				String prefix = file + "|";
				boolean flag;
				if (variant.length == 0) flag = true;
				else
				{
					flag = false;
					for (String id : variant)
					{
						flag = false;
						for (AbstractElement child : el.getChildren()) if (child.getName().equals("variant") && child.hasAttribute("id") && child.getString("id", "null").equals(id))
						{
							flag = true;
							AbstractElement merged = MergedElement.merge(child, t.element);
							t = new InstanceElement(merged, t.name, prefix + id, child.getBoolean("compile", true));
							prefix += id + "|";
							el = child;
							break;
						}
						if (!flag) break;
					}
				}
				if (!flag) GuiPopupException.onException("could not load theme " + origin + ": missing variant.");
				else if (variant.length > 0 && !el.getBoolean("compile", true)) GuiPopupException.onException("could not load theme " + origin + ": local do not compile flag set.");
				else fin = t;
			}
			else GuiPopupException.onException("could not load theme " + origin + ": master do not compile flag set.");
		}
		catch (FileNotFoundException e)
		{
			GuiPopupException.onException("could not open theme file", e);
		}
		catch (IOException e)
		{
			GuiPopupException.onException("could not parse theme file", e);
		}
		if (fin == null) return DEFAULT;
		else
		{
			GuiTheme theme = makeTheme(fin);
			return theme != null ? theme : DEFAULT;
		}
	}
	
	public static List<GuiTheme> makeTheme(AbstractElement root, String origin)
	{
		List<GuiTheme> themes = new ArrayList<>();
		if (root.getBoolean("compile", true))
		{
			List<AbstractElement> files = new ArrayList<>();
			root.addToList(files);
			addParentToList(root, files);
			String name = "";
			for (int i = files.size() - 1; i >= 0; i--)
			{
				AbstractElement el = files.get(i);
				if (el.hasAttribute("name")) name = String.format(el.getString("name", "null"), name);
			} 
			List<InstanceElement> list = new ArrayList<>();
			InstanceElement t;
			list.add(t = new InstanceElement(new MergedElement(files), name, origin, true));
			addVariantsToList(t.element, t, origin + "|", list);
			for (InstanceElement el: list) if (el.compile)
			{
				GuiTheme theme = makeTheme(el);
				if (theme != null) themes.add(theme);
			}
		}
		return themes;
	}
	
	public static void addParentToList(AbstractElement el, List<AbstractElement> list)
	{
		if (el.hasAttribute("parent"))
		{
			File parent = new File("themes/" + el.getString("parent", "null"));
			if (parent.exists())
			{
				try
				{
					AbstractElement parentEl = FileUtil.readFile(parent);
					list.add(parentEl);
					addParentToList(parentEl, list);
				}
				catch (IOException e)
				{
					GuiPopupException.onException("Couldn't load parent theme file: " + parent, e);
				}
			}
		}
	}
	
	public static void addVariantsToList(AbstractElement el, InstanceElement parent, String prefix, List<InstanceElement> list)
	{
		for (AbstractElement var : el.getChildren()) if (var.getName().equals("variant"))
		{
			if (var.hasAttribute("id"))
			{
				String id = var.getString("id", "null");
				if (!id.contains("|"))
				{
					AbstractElement merged = MergedElement.merge(var, parent.element);
					InstanceElement t2;
					list.add(t2 = new InstanceElement(merged, parent.name, prefix + id, var.getBoolean("compile", true)));
					addVariantsToList(var, t2, prefix + id + "|", list);
				}
				else GuiPopupException.onException("Invalid character '|' in variant ID: " + id);
			}
			else GuiPopupException.onException("Missing variant ID");
		}
	}
	
	public static GuiTheme makeTheme(InstanceElement t)
	{
		String className = t.element.getString("class", "firemerald.mcamc.theme.BasicTheme");
		try
		{
			return ((GuiTheme) Class.forName(className).getConstructor(String.class, String.class, AbstractElement.class).newInstance(t.name, t.variant, t.element));
		}
		catch (Throwable th)
		{
			GuiPopupException.onException("Couldn't instantiate theme from class " + className, th);
			return null;
		}
	}
	
	public static void mergeTextures(ByteBuffer src, ByteBuffer des)
	{
		for (int i = 0; i < src.capacity(); i += 4) des.putInt(i, MathUtil.mergeColors(src.getInt(i), des.getInt(i)));
	}
}