package firemerald.mcms.util;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.api.data.Element;
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
	
	public static Color getColor(Element el, Color def)
	{
		try
		{
			return Color.parseColor(el.getValue());
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			return def;
		}
	}
	
	public static Color getColor(Element el, String attr, Color def)
	{
		try
		{
			if (el.hasAttribute(attr)) return Color.parseColor(el.getString(attr));
			else return def;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
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
}