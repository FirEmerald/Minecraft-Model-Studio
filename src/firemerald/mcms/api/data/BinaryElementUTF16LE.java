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

public class BinaryElementUTF16LE extends BinaryElement<BinaryElementUTF16LE>
{
	public static final Charset CHARSET = StandardCharsets.UTF_16LE;
	public static final int CHARSET_LENGTH = 2;
	
	public BinaryElementUTF16LE(String name)
	{
		super(name, CHARSET, CHARSET_LENGTH);
	}
	
	public BinaryElementUTF16LE(String name, String value, Map<String, IAttribute> attributes, List<BinaryElementUTF16LE> children)
	{
		super(name, value, attributes, children, CHARSET, CHARSET_LENGTH);
	}

	@Override
	public Element addChild(String name)
	{
		BinaryElementUTF16LE el;
		children.add(el = new BinaryElementUTF16LE(name));
		return el;
	}
	
	public static BinaryElementUTF16LE load(InputStream in) throws IOException
	{
		String name = readString(in, CHARSET, CHARSET_LENGTH);
		String value;
		Map<String, IAttribute> attributes;
		List<BinaryElementUTF16LE> children;
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
		return new BinaryElementUTF16LE(name, value, attributes, children);
	}
	
	public static BinaryElementUTF16LE convertToBinary(Element element)
	{
		if (element instanceof BinaryElementUTF16LE) return (BinaryElementUTF16LE) element;
		else
		{
			List<? extends Element> elChildren = element.getChildren();
			List<BinaryElementUTF16LE> children = new ArrayList<>(elChildren.size());
			for (Element child : elChildren) children.add(convertToBinary(child));
			return new BinaryElementUTF16LE(element.getName(), element.getValue(), element.getAttributes(), children);
		}
	}

	@Override
	public int getIdentifierByte()
	{
		return ID_UTF16LE;
	}
}