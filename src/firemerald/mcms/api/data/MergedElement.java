package firemerald.mcms.api.data;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import firemerald.mcms.api.data.attributes.IAttribute;

public class MergedElement extends Element
{
	public final Element base;
	public final List<Element> elements;
	
	public MergedElement(List<Element> elements)
	{
		this(elements.get(0), elements);
	}
	
	public MergedElement(Element base, List<Element> elements)
	{
		this.base = base;
		this.elements = elements;
	}

	@Override
	public String getName()
	{
		return base.getName();
	}

	@Override
	public String getValue()
	{
		return base.getValue();
	}

	@Override
	public void setValue(String value)
	{
		base.setValue(value);
	}

	@Override
	public Map<String, IAttribute> getAttributes()
	{
		Map<String, IAttribute> map = new LinkedHashMap<>();
		elements.forEach(el -> {
			el.getAttributes().forEach((name, value) -> {
				if (!map.containsKey(name)) map.put(name, value);
			});
		});
		return map;
	}

	@Override
	public IAttribute getAttribute(String name)
	{
		for (Element el : elements) if (el.hasAttribute(name)) return el.getAttribute(name);
		return null;
	}

	@Override
	public void setAttribute(String name, IAttribute value)
	{
		base.setAttribute(name, value);
	}

	@Override
	public boolean hasAttribute(String name)
	{
		for (Element el : elements) if (el.hasAttribute(name)) return true;
		return false;
	}

	@Override
	public List<Element> getChildren()
	{
		List<Element> list = new ArrayList<Element>();
		elements.forEach(el -> list.addAll(el.getChildren()));
		return list;
	}

	@Override
	public Element addChild(String name)
	{
		return base.addChild(name);
	}
	
	@Override
	public void addToList(List<Element> list)
	{
		list.addAll(elements);
	}
	
	@Override
	public void setString(String attr, String value)
	{
		base.setString(attr, value);
	}
	
	@Override
	public String getString(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getString(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public String getString(String attr, String def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getString(attr, def);
		return def;
	}
	
	@Override
	public void setByte(String attr, byte value)
	{
		base.setByte(attr, value);
	}
	
	@Override
	public byte getByte(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getByte(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public byte getByte(String attr, byte def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getByte(attr, def);
		return def;
	}
	
	@Override
	public void setShort(String attr, short value)
	{
		base.setShort(attr, value);
	}
	
	@Override
	public short getShort(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getShort(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public short getShort(String attr, short def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getShort(attr, def);
		return def;
	}
	
	@Override
	public void setInt(String attr, int value)
	{
		base.setInt(attr, value);
	}
	
	@Override
	public int getInt(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getInt(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public int getInt(String attr, int def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getInt(attr, def);
		return def;
	}
	
	@Override
	public void setLong(String attr, long value)
	{
		base.setLong(attr, value);
	}
	
	@Override
	public long getLong(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getLong(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public long getLong(String attr, long def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getLong(attr, def);
		return def;
	}
	
	@Override
	public void setFloat(String attr, float value)
	{
		base.setFloat(attr, value);
	}
	
	@Override
	public float getFloat(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getFloat(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public float getFloat(String attr, float def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getFloat(attr, def);
		return def;
	}
	
	@Override
	public void setDouble(String attr, double value)
	{
		base.setDouble(attr, value);
	}
	
	@Override
	public double getDouble(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getDouble(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public double getDouble(String attr, double def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getDouble(attr, def);
		return def;
	}
	
	@Override
	public void setBoolean(String attr, boolean value)
	{
		base.setBoolean(attr, value);
	}
	
	@Override
	public boolean getBoolean(String attr) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getBoolean(attr);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public boolean getBoolean(String attr, boolean def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getBoolean(attr, def);
		return def;
	}
	
	@Override
	public void setEnum(String attr, Enum<?> value)
	{
		base.setEnum(attr, value);
	}
	
	@Override
	public <T extends Enum<?>> T getEnum(String attr, T[] values) throws Exception
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getEnum(attr, values);
		throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public <T extends Enum<?>> T getEnum(String attr, T[] values, T def)
	{
		for (Element el : elements) if (el.hasAttribute(attr)) return el.getEnum(attr, values, def);
		return def;
	}
	
	public static MergedElement merge(Element... elements)
	{
		List<Element> list = new ArrayList<>();
		for (Element el : elements)
		{
			if (el instanceof MergedElement) list.addAll(((MergedElement) el).elements);
			else list.add(el);
		}
		return new MergedElement(list.get(0), list);
	}

	@Override
	public void save(OutputStream out)
	{
		throw new UnsupportedOperationException("Merged elements cannot be saved");
	}
}