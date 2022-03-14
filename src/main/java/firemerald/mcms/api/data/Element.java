package firemerald.mcms.api.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
			FileUtil.closeSafe(out);
			throw e;
		}
		FileUtil.closeSafe(out);
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
			for (Element child : children) child.saveBinaryHeaderless(out, format);
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
}