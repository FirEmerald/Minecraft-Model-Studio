package firemerald.mcms.api.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import firemerald.mcms.api.data.attributes.*;

public class BinaryElementASCII extends BinaryElement<BinaryElementASCII>
{
	public static final Charset CHARSET = StandardCharsets.US_ASCII;
	public static final int CHARSET_LENGTH = 1;
	
	public BinaryElementASCII(String name)
	{
		super(name, CHARSET, CHARSET_LENGTH);
	}
	
	public BinaryElementASCII(String name, String value, Map<String, IAttribute> attributes, List<BinaryElementASCII> children)
	{
		super(name, value, attributes, children, CHARSET, CHARSET_LENGTH);
	}

	@Override
	public Element addChild(String name)
	{
		BinaryElementASCII el;
		children.add(el = new BinaryElementASCII(name));
		return el;
	}
	
	public static BinaryElementASCII load(InputStream in) throws IOException
	{
		String name = readString(in, CHARSET, CHARSET_LENGTH);
		String value;
		Map<String, IAttribute> attributes;
		List<BinaryElementASCII> children;
		int id = in.read();
		if (id == -1) throw new IOException("Unexpected end of stream");
		if (id == VALUE)
		{
			value = readString(in, CHARSET, CHARSET_LENGTH);
			id = in.read();
		}
		else value = null;
		if (id == ATTRIBUTES)
		{
			int length = readInt(in);
			attributes = new LinkedHashMap<>(length);
			for (int i = 0; i < length; i++)
			{
				String attrName = readString(in, CHARSET, CHARSET_LENGTH);
				IAttribute attr;
				switch (id = in.read())
				{
				case IAttribute.ID_STRING:
					attr = AttributeString.read(in, CHARSET, CHARSET_LENGTH);
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
			int length = readInt(in);
			children = new ArrayList<>(length);
			for (int i = 0; i < length; i++) children.add(load(in));
			id = in.read();
		}
		else children = new ArrayList<>();
		if (id != END) throw new IOException("Invalid data ID " + id);
		return new BinaryElementASCII(name, value, attributes, children);
	}
	
	public static BinaryElementASCII convertToBinary(Element element)
	{
		if (element instanceof BinaryElementASCII) return (BinaryElementASCII) element;
		else
		{
			List<? extends Element> elChildren = element.getChildren();
			List<BinaryElementASCII> children = new ArrayList<>(elChildren.size());
			for (Element child : elChildren) children.add(convertToBinary(child));
			return new BinaryElementASCII(element.getName(), element.getValue(), element.getAttributes(), children);
		}
	}

	@Override
	public int getIdentifierByte()
	{
		return ID_ASCII;
	}
}