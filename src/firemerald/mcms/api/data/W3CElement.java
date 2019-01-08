package firemerald.mcms.api.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import firemerald.mcms.api.data.attributes.AttributeString;
import firemerald.mcms.api.data.attributes.IAttribute;
import firemerald.mcms.api.util.DataUtil;

public class W3CElement extends Element
{
	public final org.w3c.dom.Element element;
	public final Document document;
	
	public W3CElement(Document doc)
	{
		this(doc.getDocumentElement(), doc);
	}
	
	public W3CElement(org.w3c.dom.Element element)
	{
		this(element, element.getOwnerDocument());
	}
	
	public W3CElement(org.w3c.dom.Element element, Document document)
	{
		this.element = element;
		this.document = document;
	}

	@Override
	public String getName()
	{
		return element.getNodeName();
	}

	@Override
	public String getValue()
	{
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			org.w3c.dom.Node node = nodes.item(i);
			if (node.getNodeName().equals("#text")) return node.getNodeValue();
		}
		return null;
	}

	@Override
	public void setValue(String value)
	{
		boolean flag = true;
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			org.w3c.dom.Node node = nodes.item(i);
			if (node.getNodeName().equals("#text"))
			{
				node.setNodeValue(value);
				flag = false;
			}
		}
		if (flag) element.appendChild(document.createTextNode(value));
	}

	@Override
	public Map<String, IAttribute> getAttributes()
	{
		NamedNodeMap attrs = element.getAttributes();
		Map<String, IAttribute> map = new LinkedHashMap<>(attrs.getLength());
		for (int i = 0; i < attrs.getLength(); i++)
		{
			org.w3c.dom.Node node = attrs.item(i);
			map.put(node.getNodeName(), new AttributeString(node.getNodeValue()));
		}
		return map;
	}

	@Override
	public boolean hasAttribute(String name)
	{
		return element.hasAttribute(name);
	}

	@Override
	public IAttribute getAttribute(String name)
	{
		return new AttributeString(element.getAttribute(name));
	}

	@Override
	public void setAttribute(String name, IAttribute value)
	{
		element.setAttribute(name, value.getString());
	}
	
	@Override
	public void setString(String attr, String value)
	{
		element.setAttribute(attr, value);
	}
	
	@Override
	public String getString(String attr) throws Exception
	{
		if (element.hasAttribute(attr)) return element.getAttribute(attr);
		else throw new Exception("Attribute " + attr + " not found!");
	}
	
	@Override
	public void setBoolean(String attr, boolean value)
	{
		setString(attr, Boolean.toString(value));
	}
	
	@Override
	public boolean getBoolean(String attr) throws Exception
	{
		if (hasAttribute(attr))
		{
			String val = getString(attr);
			if (val.equalsIgnoreCase("true") || val.equals("yes")) return true;
			else if (val.equalsIgnoreCase("false") || val.equals("no")) return false;
			else throw new Exception("Attribute " + attr + " is not a valid boolean");
		}
		else throw new Exception("Attribute " + attr + " not found");
	}
	
	@Override
	public void setByte(String attr, byte value)
	{
		setString(attr, Byte.toString(value));
	}
	
	@Override
	public byte getByte(String attr) throws Exception
	{
		if (hasAttribute(attr)) try
		{
			return Byte.parseByte(getString(attr));
		}
		catch (Throwable t)
		{
			throw new Exception("Atrribute " + attr + " is not a valid byte", t);
		}
		else throw new Exception("Atrribute " + attr + " not found!");
	}
	
	@Override
	public void setShort(String attr, short value)
	{
		setString(attr, Short.toString(value));
	}
	
	@Override
	public short getShort(String attr) throws Exception
	{
		if (hasAttribute(attr)) try
		{
			return Short.parseShort(getString(attr));
		}
		catch (Throwable t)
		{
			throw new Exception("Atrribute " + attr + " is not a valid short", t);
		}
		else throw new Exception("Atrribute " + attr + " not found!");
	}
	
	@Override
	public void setInt(String attr, int value)
	{
		setString(attr, Integer.toString(value));
	}
	
	@Override
	public int getInt(String attr) throws Exception
	{
		if (hasAttribute(attr)) try
		{
			return Integer.parseInt(getString(attr));
		}
		catch (Throwable t)
		{
			throw new Exception("Atrribute " + attr + " is not a valid int", t);
		}
		else throw new Exception("Atrribute " + attr + " not found!");
	}
	
	@Override
	public void setLong(String attr, long value)
	{
		setString(attr, Long.toString(value));
	}
	
	@Override
	public long getLong(String attr) throws Exception
	{
		if (hasAttribute(attr)) try
		{
			return Long.parseLong(getString(attr));
		}
		catch (Throwable t)
		{
			throw new Exception("Atrribute " + attr + " is not a valid long", t);
		}
		else throw new Exception("Atrribute " + attr + " not found!");
	}
	
	@Override
	public void setFloat(String attr, float value)
	{
		setString(attr, Float.toString(value));
	}
	
	@Override
	public float getFloat(String attr) throws Exception
	{
		if (hasAttribute(attr)) try
		{
			return Float.parseFloat(getString(attr));
		}
		catch (Throwable t)
		{
			throw new Exception("Atrribute " + attr + " is not a valid float", t);
		}
		else throw new Exception("Atrribute " + attr + " not found!");
	}
	
	@Override
	public void setDouble(String attr, double value)
	{
		setString(attr, Double.toString(value));
	}
	
	@Override
	public double getDouble(String attr) throws Exception
	{
		if (hasAttribute(attr)) try
		{
			return Double.parseDouble(getString(attr));
		}
		catch (Throwable t)
		{
			throw new Exception("Atrribute " + attr + " is not a valid double", t);
		}
		else throw new Exception("Atrribute " + attr + " not found!");
	}

	@Override
	public List<Element> getChildren()
	{
		NodeList childs = element.getChildNodes();
		List<Element> list = new ArrayList<>();
		for (int i = 0; i < childs.getLength(); i++)
		{
			org.w3c.dom.Node node = childs.item(i);
			if (!node.getNodeName().equals("#text") && node instanceof org.w3c.dom.Element) list.add(new W3CElement((org.w3c.dom.Element) node));
		}
		return list;
	}

	@Override
	public Element addChild(String name)
	{
		org.w3c.dom.Element el = document.createElement(name);
		element.appendChild(el);
		return new W3CElement(el);
	}

	@Override
	public void save(File file) throws IOException
	{
		if (element != document.getDocumentElement()) throw new UnsupportedOperationException("W3C elements can only be saved if they are the root element.");
		else try
		{
			DataUtil.saveXML(document, file);
		}
		catch (TransformerException e)
		{
			throw new IOException("Couldn't transform document", e);
		}
	}

	@Override
	public void save(OutputStream out) throws IOException
	{
		if (element != document.getDocumentElement()) throw new UnsupportedOperationException("W3C elements can only be saved if they are the root element.");
		else try
		{
			DataUtil.saveXML(document, out);
		}
		catch (TransformerException e)
		{
			throw new IOException("Couldn't transform document", e);
		}
	}
	
	public static W3CElement convert(Element el)
	{
		Document doc = DataUtil.createXML();
		return new W3CElement(convert(el, doc, doc));
	}
	
	private static org.w3c.dom.Element convert(Element el, Document doc, Node parent)
	{
		org.w3c.dom.Element element = doc.createElement(el.getName());
		parent.appendChild(element);
		String val = el.getValue();
		if (val != null) element.setTextContent(val);
		el.getAttributes().forEach((name, attribute) -> element.setAttribute(name, attribute.getString()));
		el.getChildren().forEach(child -> convert(child, doc, element));
		return element;
	}
}