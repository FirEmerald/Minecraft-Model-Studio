package firemerald.mcms.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.texture.Color;

public class MiscUtil
{
	public static String[] removeWhitespace(String[] strs)
	{
		List<String> out = new ArrayList<>();
		for (String str : strs) if (str != null && (str = str.trim()).length() > 0) out.add(str);
		return out.toArray(new String[out.size()]);
	}
	
	public static Color getColor(AbstractElement el, Color def)
	{
		try
		{
			return Color.parseColor(el.getValue());
		}
		catch (Throwable t)
		{
			Main.LOGGER.log(Level.WARN, "Couldn't parse color: " + el.getValue(), t);
			return def;
		}
	}
	
	public static Color getColor(AbstractElement el, String attr, Color def)
	{
		try
		{
			if (el.hasAttribute(attr)) return Color.parseColor(el.getString(attr));
			else return def;
		}
		catch (Throwable t)
		{
			Main.LOGGER.log(Level.WARN, "Couldn't parse color: " + el.getString(attr, "null"), t);
			return def;
		}
	}
	
	public static String getNewBoneName(String name, IModel model)
	{
		if (!model.isNameUsed(name)) return name;
		int i = 2;
		if (!name.endsWith("(copy)"))
		{
			boolean flag = true;
			int pos = name.lastIndexOf(" (");
			if (pos > 0)
			{
				int pos2 = name.lastIndexOf(')');
				if (pos2 > pos)
				{
					String val = name.substring(pos + 2, pos2);
					try
					{
						i = Integer.parseInt(val) + 1;
						name = name.substring(0, pos);
						flag = false;
					}
					catch (NumberFormatException e) {}
				}
			}
			if (flag)
			{
				name = name + " (copy)";
				if (!model.isNameUsed(name)) return name;
			}
		}
		String orig = name;
		while (model.isNameUsed(name = (orig + " (" + Integer.toString(i) + ")"))) i++;
		return name;
	}
	
	public static String ensureUnique(String name, Set<String> names)
	{
		if (names.contains(name))
		{
			String orig = name;
			int i = 2;
			while (names.contains(name = orig + " (" + i++ + ")")) {}
		}
		return name;
	}
	
	public static String[] array(String prepend, Collection<String> append)
	{
		return array(prepend, append.toArray(new String[append.size()]));
	}
	
	public static String[] array(String prepend, String[] append)
	{
		String[] ret = new String[append.length + 1];
		ret[0] = prepend;
		System.arraycopy(append, 0, ret, 1, append.length);
		return ret;
	}
    
    public static float[] copy(float[] toCopy)
    {
    	float[] res = new float[toCopy.length];
    	System.arraycopy(toCopy, 0, res, 0, toCopy.length);
    	return res;
    }
    
    public static int[] copy(int[] toCopy)
    {
    	int[] res = new int[toCopy.length];
    	System.arraycopy(toCopy, 0, res, 0, toCopy.length);
    	return res;
    }
}