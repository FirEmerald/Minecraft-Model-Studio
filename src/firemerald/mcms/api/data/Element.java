package firemerald.mcms.api.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;

import firemerald.mcms.api.data.attributes.AttributeBoolean;
import firemerald.mcms.api.data.attributes.AttributeByte;
import firemerald.mcms.api.data.attributes.AttributeDouble;
import firemerald.mcms.api.data.attributes.AttributeFloat;
import firemerald.mcms.api.data.attributes.AttributeInt;
import firemerald.mcms.api.data.attributes.AttributeLong;
import firemerald.mcms.api.data.attributes.AttributeShort;
import firemerald.mcms.api.data.attributes.AttributeString;
import firemerald.mcms.api.data.attributes.IAttribute;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.util.FileUtils;

public class Element extends AbstractElement
{
	protected final String name;
	protected String value;
	protected final Map<String, IAttribute> attributes;
	protected final List<Element> children;
	
	public Element(String name)
	{
		this.name = name;
		this.value = null;
		attributes = new LinkedHashMap<>();
		children = new ArrayList<>();
	}
	
	public Element(String name, String value, Map<String, IAttribute> attributes, List<Element> children)
	{
		this.name = name;
		this.value = value;
		this.attributes = attributes;
		this.children = children;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public Map<String, IAttribute> getAttributes()
	{
		return attributes;
	}

	@Override
	public boolean hasAttribute(String name)
	{
		return attributes.containsKey(name);
	}

	@Override
	public IAttribute getAttribute(String name)
	{
		return attributes.get(name);
	}

	@Override
	public void setAttribute(String name, IAttribute value)
	{
		attributes.put(name, value);
	}

	@Override
	public List<? extends AbstractElement> getChildren()
	{
		return children;
	}

	@Override
	public Element addChild(String name)
	{
		Element el = new Element(name);
		children.add(el);
		return el;
	}
	
	@Override
	public Element toElement()
	{
		return this;
	}
	
	public void saveBinary(File file, BinaryFormat format) throws IOException
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			saveBinary(out, format);
		}
		catch (IOException e)
		{
			FileUtils.closeSafe(out);
			throw e;
		}
		FileUtils.closeSafe(out);
	}
	
	public void saveBinary(OutputStream out, BinaryFormat format) throws IOException
	{
		out.write(format.id);
		saveBinaryHeaderless(out, format);
	}
	
	public static final int END = 255, VALUE = 0, ATTRIBUTES = 1, CHILDREN = 2;
	
	public void saveBinaryHeaderless(OutputStream out, BinaryFormat format) throws IOException
	{
		FileUtil.writeString(out, name, format);
		if (value != null)
		{
			out.write(VALUE);
			FileUtil.writeString(out, value, format);
		}
		if (!attributes.isEmpty())
		{
			out.write(ATTRIBUTES);
			FileUtil.writeInt(out, attributes.size());
			for (Map.Entry<String, IAttribute> entry : attributes.entrySet())
			{
				String name = entry.getKey();
				IAttribute val = entry.getValue();
				FileUtil.writeString(out, name, format);
				out.write(val.getID());
				val.write(out, format);
			}
		}
		if (!children.isEmpty())
		{
			out.write(CHILDREN);
			FileUtil.writeInt(out, children.size());
			for (Element child : children) ((Element) child).saveBinaryHeaderless(out, format);
		}
		out.write(END);
	}
	
	public static Element loadBinary(InputStream in) throws IOException
	{
		int id = in.read();
		for (BinaryFormat format : BinaryFormat.values()) if (format.id == id) return loadBinaryHeaderless(in, format);
		throw new IOException("Invalid binary header " + id);
	}
	
	public static Element loadBinaryHeaderless(InputStream in, BinaryFormat format) throws IOException
	{
		String name = FileUtil.readString(in, format);
		String value;
		Map<String, IAttribute> attributes;
		List<Element> children;
		int id = in.read();
		if (id == -1) throw new IOException("Unexpected end of stream " + format.charset.name());
		if (id == VALUE)
		{
			value = FileUtil.readString(in, format);
			id = in.read();
		}
		else value = null;
		if (id == ATTRIBUTES)
		{
			int length = FileUtil.readInt(in);
			attributes = new LinkedHashMap<>(length);
			for (int i = 0; i < length; i++)
			{
				String attrName = FileUtil.readString(in, format);
				IAttribute attr;
				switch (id = in.read())
				{
				case IAttribute.ID_STRING:
					attr = AttributeString.read(in, format);
					break;
				case IAttribute.ID_BOOLEAN:
					attr = AttributeBoolean.read(in);
					break;
				case IAttribute.ID_BYTE:
					attr = AttributeByte.read(in);
					break;
				case IAttribute.ID_SHORT:
					attr = AttributeShort.read(in);
					break;
				case IAttribute.ID_INT:
					attr = AttributeInt.read(in);
					break;
				case IAttribute.ID_LONG:
					attr = AttributeLong.read(in);
					break;
				case IAttribute.ID_FLOAT:
					attr = AttributeFloat.read(in);
					break;
				case IAttribute.ID_DOUBLE:
					attr = AttributeDouble.read(in);
					break;
				default:
					throw new IOException("Invalid attribute ID " + id);
				}
				attributes.put(attrName, attr);
			}
			id = in.read();
		}
		else attributes = new LinkedHashMap<>();
		if (id == CHILDREN)
		{
			int length = FileUtil.readInt(in);
			children = new ArrayList<>(length);
			for (int i = 0; i < length; i++) children.add(loadBinaryHeaderless(in, format));
			id = in.read();
		}
		else children = new ArrayList<>();
		if (id != END) throw new IOException("Invalid data ID " + id);
		return new Element(name, value, attributes, children);
	}
	
	public void saveXML(File file) throws TransformerException, IOException
	{
		Document doc = FileUtil.createXML();
		saveXML(doc);
		FileUtil.saveXML(doc, file);
	}
	
	public void saveXML(OutputStream out) throws TransformerException, IOException
	{
		Document doc = FileUtil.createXML();
		saveXML(doc);
		FileUtil.saveXML(doc, out);
	}
	
	public void saveXML(Document doc)
	{
		setXML(doc, doc);
	}
	
	@SuppressWarnings("unchecked")
	public void setXML(Document doc, Node parent)
	{
		org.w3c.dom.Element element = doc.createElement(getName());
		parent.appendChild(element);
		element.setTextContent(getValue());
		getAttributes().forEach((name, attribute) -> element.setAttribute(name, attribute.getString()));
		((List<Element>) getChildren()).forEach(child -> child.setXML(doc, element));
	}
	
	public static Element loadJSON(JsonElement element)
	{
		String name = "root";
		if (element instanceof JsonObject)
		{
			JsonObject object2 = (JsonObject) element;
			if (object2.has("#name")) name = object2.get("#name").getAsString();
		}
		Element el = new Element(name);
		el.loadFromJSON(element);
		return el;
	}
	
	public void loadFromJSON(JsonElement element)
	{
		if (element instanceof JsonObject) loadFromJSON((JsonObject) element);
		else if (element instanceof JsonArray) loadFromJSON((JsonArray) element);
		else if (element instanceof JsonPrimitive) loadFromJSON((JsonPrimitive) element);
	}
	
	public void loadFromJSON(JsonObject object)
	{
		object.entrySet().forEach(entry -> {
			String name = entry.getKey();
			if (name.equals("#value"))
			{
				this.setValue(entry.getValue().getAsString());
			}
			else if (!name.equals("#name"))
			{
				JsonElement element = entry.getValue();
				if (element instanceof JsonPrimitive)
				{
					System.out.println(name);
					setAttribute(name, (JsonPrimitive) element);
				}
				else
				{
					if (element instanceof JsonObject)
					{
						JsonObject object2 = (JsonObject) element;
						if (object2.has("#name")) name = object2.get("#name").getAsString();
					}
					Element el = this.addChild(name);
					el.loadFromJSON(element);
				}
			}
		});
	}
	
	/*
	 * no children and all attributes have positive integer names
	 * or
	 * no attributes and all children have positive integer names
	 */
	public void loadFromJSON(JsonArray array)
	{
		boolean isPrimitive = true;
		for (int i = 0; i < array.size(); i++) if (!(array.get(i) instanceof JsonPrimitive))
		{
			isPrimitive = false;
			break;
		}
		for (int i = 0; i < array.size(); i++)
		{
			if (isPrimitive) setAttribute(Integer.toString(i), (JsonPrimitive) array.get(i));
			else
			{
				JsonElement element2 = array.get(i);
				Element el = this.addChild(Integer.toString(i));
				el.loadFromJSON(element2);
			}
		}
	}
	
	/*
	 * value != null, no attributes, no children.
	 */
	public void loadFromJSON(JsonPrimitive primitive)
	{
		this.setValue(primitive.getAsString());
	}
	
	public void setAttribute(String name, JsonPrimitive value)
	{
		if (value.isString()) this.setString(name, value.getAsString());
		else if (value.isBoolean()) this.setBoolean(name, value.getAsBoolean());
		else if (value.isNumber())
		{
			Number num = value.getAsNumber();
			if (num instanceof Byte) this.setByte(name, value.getAsByte());
			else if (num instanceof Short) this.setShort(name, value.getAsShort());
			else if (num instanceof Integer) this.setInt(name, value.getAsInt());
			else if (num instanceof Long) this.setLong(name, value.getAsLong());
			else if (num instanceof BigInteger) this.setLong(name, value.getAsBigInteger().longValueExact());
			else if (num instanceof Float) this.setFloat(name, value.getAsFloat());
			else if (num instanceof Double) this.setDouble(name, value.getAsDouble());
			else if (num instanceof BigDecimal) this.setDouble(name, value.getAsBigDecimal().doubleValue());
			else if (num instanceof LazilyParsedNumber)
			{
				LazilyParsedNumber l = (LazilyParsedNumber) num;
				if (l.toString().contains(".") || l.toString().contains("E") || l.toString().contains("e")) this.setDouble(name, l.doubleValue());
				else this.setLong(name, l.longValue());
			}
			else System.err.println("Invalid number type: " + num.getClass());
			//TODO else exception
		}
		else System.err.println("Invalid primitive type: " + value);
		//TODO else exception
	}
	
	public static class NumberedElement implements Comparable<NumberedElement>
	{
		public final Element el;
		public final int num;
		
		public NumberedElement(Element el, int num)
		{
			this.el = el;
			this.num = num;
		}
		
		@Override
		public int compareTo(NumberedElement arg0)
		{
			return num - arg0.num;
		}
	}
	
	public static class NumberedAttribute implements Comparable<NumberedAttribute>
	{
		public final IAttribute attr;
		public final int num;
		
		public NumberedAttribute(IAttribute attr, int num)
		{
			this.attr = attr;
			this.num = num;
		}
		
		@Override
		public int compareTo(NumberedAttribute arg0)
		{
			return num - arg0.num;
		}
	}
	
	public JsonElement makeElement(boolean needsName)
	{
		String value = this.getValue();
		Map<String, IAttribute> attributes = this.getAttributes();
		@SuppressWarnings("unchecked")
		List<Element> children = (List<Element>) this.getChildren();
		if (attributes.isEmpty())
		{
			if (children.isEmpty())
			{
				if (value == null) return new JsonObject();
				else return new JsonPrimitive(value);
			}
			else
			{
				if (value == null)
				{
					boolean flag = false;
					TreeSet<NumberedElement> s = new TreeSet<>();
					for (Element child : children)
					{
						try
						{
							NumberedElement el;
							Integer i = Integer.parseInt(child.getName());
							if (i < 0 || s.contains(el = new NumberedElement(child, i)))
							{
								flag = true;
								break;
							}
							else s.add(el);
						}
						catch (NumberFormatException e)
						{
							flag = true;
							break;
						}
					}
					if (!flag)
					{
						JsonArray array = new JsonArray();
						int targetNum = 0;
						for (NumberedElement el : s)
						{
							if (el.num == targetNum)
							{
								array.add(el.el.makeElement(false));
								targetNum++;
							}
							else
							{
								flag = true;
								break;
							}
						}
						if (!flag) return array;
					}
				}
			}
		}
		else
		{
			if (children.isEmpty())
			{
				if (value == null)
				{
					boolean flag = false;
					TreeSet<NumberedAttribute> s = new TreeSet<>();
					for (Entry<String, IAttribute> entry : attributes.entrySet())
					{
						try
						{
							NumberedAttribute el;
							Integer i = Integer.parseInt(entry.getKey());
							if (i < 0 || s.contains(el = new NumberedAttribute(entry.getValue(), i)))
							{
								flag = true;
								break;
							}
							else s.add(el);
						}
						catch (NumberFormatException e)
						{
							flag = true;
							break;
						}
					}
					if (!flag)
					{
						JsonArray array = new JsonArray();
						int targetNum = 0;
						for (NumberedAttribute attr : s)
						{
							if (attr.num == targetNum)
							{
								array.add(attr.attr.makeElement());
								targetNum++;
							}
							else
							{
								flag = true;
								break;
							}
						}
						if (!flag) return array;
					}
				}
			}
		}
		List<String> childNames = new ArrayList<>();
		JsonObject obj = new JsonObject();
		if (needsName) obj.addProperty("#name", getName());
		if (value != null) obj.addProperty("#value", value);
		attributes.forEach((name, attr) -> {
			childNames.add(name);
			obj.add(name, attr.makeElement());
		});
		int i = 0;
		for (Element child : children)
		{
			String name = child.getName();
			if (childNames.contains(name))
			{
				while (childNames.contains(name = "duplicate_name_" + (i++))) {};
				childNames.add(name);
				obj.add(name, child.makeElement(true));
			}
			else
			{
				childNames.add(name);
				obj.add(name, child.makeElement(false));
			}
		}
		return obj;
	}
}