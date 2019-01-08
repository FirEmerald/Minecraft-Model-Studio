package firemerald.mcms.api.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import firemerald.mcms.api.data.attributes.*;
import firemerald.mcms.util.FileUtils;

public abstract class Element
{
	public abstract String getName();
	
	public abstract String getValue();
	
	public abstract void setValue(String value);
	
	public abstract Map<String, IAttribute> getAttributes();
	
	public abstract IAttribute getAttribute(String name);
	
	public abstract void setAttribute(String name, IAttribute value);
	
	public abstract boolean hasAttribute(String name);
	
	public abstract List<? extends Element> getChildren();
	
	public abstract Element addChild(String name);
	
	public void setString(String attr, String value)
	{
		setAttribute(attr, new AttributeString(value));
	}
	
	public String getString(String attr) throws Exception
	{
		return getAttribute(attr).getString();
	}
	
	public String getString(String attr, String def)
	{
		try
		{
			return getString(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setBoolean(String attr, boolean value)
	{
		setAttribute(attr, new AttributeBoolean(value));
	}
	
	public boolean getBoolean(String attr) throws Exception
	{
		return getAttribute(attr).getBoolean();
	}
	
	public boolean getBoolean(String attr, boolean def)
	{
		try
		{
			return getBoolean(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setByte(String attr, byte value)
	{
		setAttribute(attr, new AttributeByte(value));
	}
	
	public byte getByte(String attr) throws Exception
	{
		return getAttribute(attr).getByte();
	}
	
	public byte getByte(String attr, byte def)
	{
		try
		{
			return getByte(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setShort(String attr, short value)
	{
		setAttribute(attr, new AttributeShort(value));
	}
	
	public short getShort(String attr) throws Exception
	{
		return getAttribute(attr).getShort();
	}
	
	public short getShort(String attr, short def)
	{
		try
		{
			return getShort(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setInt(String attr, int value)
	{
		setAttribute(attr, new AttributeInt(value));
	}
	
	public int getInt(String attr) throws Exception
	{
		return getAttribute(attr).getInt();
	}
	
	public int getInt(String attr, int def)
	{
		try
		{
			return getInt(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setLong(String attr, long value)
	{
		setAttribute(attr, new AttributeLong(value));
	}
	
	public long getLong(String attr) throws Exception
	{
		return getAttribute(attr).getLong();
	}
	
	public long getLong(String attr, long def)
	{
		try
		{
			return getLong(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setFloat(String attr, float value)
	{
		setAttribute(attr, new AttributeFloat(value));
	}
	
	public float getFloat(String attr) throws Exception
	{
		return getAttribute(attr).getFloat();
	}
	
	public float getFloat(String attr, float def)
	{
		try
		{
			return getFloat(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setDouble(String attr, double value)
	{
		setAttribute(attr, new AttributeDouble(value));
	}
	
	public double getDouble(String attr) throws Exception
	{
		return getAttribute(attr).getDouble();
	}
	
	public double getDouble(String attr, double def)
	{
		try
		{
			return getDouble(attr);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public void setEnum(String attr, Enum<?> value)
	{
		setString(attr, value.name());
	}
	
	public <T extends Enum<?>> T getEnum(String attr, T[] values) throws Exception
	{
		return getAttribute(attr).getEnum(values);
	}
	
	public <T extends Enum<?>> T getEnum(String attr, T[] values, T def)
	{
		try
		{
			return getEnum(attr, values);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	@Override
	public String toString()
	{
		return toString("");
	}
	
	public String toString(String prefix)
	{
		StringBuilder str = new StringBuilder(prefix);
		str.append("<");
		str.append(getName());
		getAttributes().forEach((name, value) -> {
			str.append(" ");
			str.append(name);
			str.append("=\"");
			str.append(value.getString());
			str.append("\"");
		});
		if (getValue() != null)
		{
			str.append(">");
			String val = getValue();
			String[] split = val.split("\n");
			String pre;
			if (split.length == 1) pre = "    ";
			else 
			{
				pre = split[split.length - 1];
				char[] chr = new char[pre.length()];
				for (int i = 0; i < chr.length; i++) chr[i] = ' ';
				pre = String.valueOf(chr);
				val = val.substring(0, val.length() - chr.length);
			}
			str.append(val);
			for (Element el: getChildren())
			{
				str.append(el.toString(pre));
				str.append('\n');
			}
			str.append("</");
			str.append(getName());
			str.append(">");
		}
		else str.append("/>");
		return str.toString().replaceAll("\n", "\n" + prefix);
	}
	
	public void addToList(List<Element> list)
	{
		list.add(this);
	}
	
	public void save(File file) throws IOException
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			save(out);
		}
		catch (IOException e)
		{
			FileUtils.closeSafe(out);
			throw e;
		}
	}
	
	public abstract void save(OutputStream out) throws IOException;
}