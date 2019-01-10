package firemerald.mcms.theme;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.data.MergedElement;
import firemerald.mcms.api.util.FileUtil;
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
	
	public abstract void bindRoundedBox(RoundedBoxFormat box);
	
	public abstract void bindTextBox(BoxFormat textBox);
	
	public abstract void bindScrollBar(BoxFormat scrollBar);
	
	public abstract void bindScrollButton(DirectionButtonFormat scrollButton);
	
	public abstract void bindDirectionButton(DirectionButtonFormat directionButton);
	
	public abstract Color getTextColor();
	
	public static int makeTexture(ByteBuffer texture, int w, int h)
	{
		int t = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, t);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_BYTE, texture);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    	return t;
	}
	
	public static class ThemeElement
	{
		public final String name;
		public final String variant;
		public final AbstractElement element;
		public final boolean compile;
		
		public ThemeElement(AbstractElement element, String parentName, String variant, boolean compile)
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
		ThemeElement fin = null;
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
				ThemeElement t = new ThemeElement(el = new MergedElement(files), name, origin, true);
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
							t = new ThemeElement(merged, t.name, prefix + id, child.getBoolean("compile", true));
							prefix += id + "|";
							el = child;
							break;
						}
						if (!flag) break;
					}
				}
				if (!flag) Main.LOGGER.log(Level.WARN, "could not load theme " + origin + ": missing variant.");
				else if (variant.length > 0 && !el.getBoolean("compile", true)) Main.LOGGER.log(Level.WARN, "could not load theme " + origin + ": local do not compile flag set.");
				else fin = t;
			}
			else Main.LOGGER.log(Level.WARN, "could not load theme " + origin + ": master do not compile flag set.");
		}
		catch (FileNotFoundException e)
		{
			Main.LOGGER.log(Level.WARN, "could not open theme file", e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Main.LOGGER.log(Level.WARN, "could not parse theme file", e);
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
			List<ThemeElement> list = new ArrayList<>();
			ThemeElement t;
			list.add(t = new ThemeElement(new MergedElement(files), name, origin, true));
			addVariantsToList(t.element, t, origin + "|", list);
			for (ThemeElement el: list) if (el.compile)
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void addVariantsToList(AbstractElement el, ThemeElement parent, String prefix, List<ThemeElement> list)
	{
		for (AbstractElement var : el.getChildren()) if (var.getName().equals("variant"))
		{
			if (var.hasAttribute("id"))
			{
				String id = var.getString("id", "null");
				if (!id.contains("|"))
				{
					AbstractElement merged = MergedElement.merge(var, parent.element);
					ThemeElement t2;
					list.add(t2 = new ThemeElement(merged, parent.name, prefix + id, var.getBoolean("compile", true)));
					addVariantsToList(var, t2, prefix + id + "|", list);
				}
				else Main.LOGGER.warn("Invalid character '|' in variant ID");
			}
			else Main.LOGGER.warn("Missing variant ID");
		}
	}
	
	public static GuiTheme makeTheme(ThemeElement t)
	{
		String className = t.element.getString("class", "firemerald.mcamc.theme.BasicTheme");
		try
		{
			return ((GuiTheme) Class.forName(className).getConstructor(String.class, String.class, AbstractElement.class).newInstance(t.name, t.variant, t.element));
		}
		catch (Throwable th)
		{
			//TODO exception
			th.printStackTrace();
			return null;
		}
	}
	
	public static void mergeTextures(ByteBuffer src, ByteBuffer des)
	{
		for (int i = 0; i < src.capacity(); i += 4) des.putInt(i, MathUtil.mergeColors(src.getInt(i), des.getInt(i)));
	}
}