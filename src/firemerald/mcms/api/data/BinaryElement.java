package firemerald.mcms.api.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import firemerald.mcms.api.data.attributes.IAttribute;

public abstract class BinaryElement<T extends BinaryElement<?>> extends Element
{
	public static final int ID_ASCII = 1, ID_UTF8 = 2, ID_UTF16LE = 3, ID_UTF16BE = 4;
	
	protected final String name;
	protected String value;
	protected final Map<String, IAttribute> attributes;
	protected final List<T> children;
	public final Charset charset;
	public final int charsetLength;
	
	public BinaryElement(String name, Charset charset, int charsetLength)
	{
		this.name = name;
		this.value = null;
		attributes = new LinkedHashMap<>();
		children = new ArrayList<>();
		this.charset = charset;
		this.charsetLength = charsetLength;
	}
	
	public BinaryElement(String name, String value, Map<String, IAttribute> attributes, List<T> children, Charset charset, int charsetLength)
	{
		this.name = name;
		this.value = value;
		this.attributes = attributes;
		this.children = children;
		this.charset = charset;
		this.charsetLength = charsetLength;
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
	public List<? extends Element> getChildren()
	{
		return children;
	}
	
	public static final int END = 255, VALUE = 0, ATTRIBUTES = 1, CHILDREN = 2;
	
	public abstract int getIdentifierByte();
	
	@Override
	public void save(OutputStream out) throws IOException
	{
		out.write(getIdentifierByte());
		write(out);
	}
	
	public void write(OutputStream out) throws IOException
	{
		writeString(out, name, charset, charsetLength);
		if (value != null)
		{
			out.write(VALUE);
			writeString(out, value, charset, charsetLength);
		}
		if (!attributes.isEmpty())
		{
			out.write(ATTRIBUTES);
			writeInt(out, attributes.size());
			for (Map.Entry<String, IAttribute> entry : attributes.entrySet())
			{
				String name = entry.getKey();
				IAttribute val = entry.getValue();
				writeString(out, name, charset, charsetLength);
				out.write(val.getID());
				val.write(out, charset, charsetLength);
			}
		}
		if (!children.isEmpty())
		{
			out.write(CHILDREN);
			writeInt(out, children.size());
			for (T child : children) child.write(out);
		}
		out.write(END);
	}
	
	public static void writeString(OutputStream out, String val, Charset charset, int charsetLength) throws IOException
	{
		out.write(val.getBytes(charset));
		out.write(new byte[charsetLength]);
	}
	
	public static String readString(InputStream in, Charset charset, int charsetLength) throws IOException
	{
		byte[] data = new byte[charsetLength];
		byte[] bytes = new byte[0];
		while (in.read(data) == charsetLength)
		{
			boolean nill = true;
			for (int i = 0; i < charsetLength; i++) if (data[i] != 0)
			{
				nill = false;
				break;
			}
			if (nill) break;
			byte[] newBytes = new byte[bytes.length + data.length];
			System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
			System.arraycopy(data, 0, newBytes, bytes.length, data.length);
			bytes = newBytes;
		}
		return new String(bytes, charset);
	}
	
	public static void writeBoolean(OutputStream out, boolean val) throws IOException
	{
		out.write(val ? 1 : 0);
	}
	
	public static boolean readBoolean(InputStream in) throws IOException
	{
		return in.read() > 0;
	}
	
	public static void writeByte(OutputStream out, byte val) throws IOException
	{
		out.write(val);
	}
	
	public static byte readByte(InputStream in) throws IOException
	{
		return (byte) in.read();
	}
	
	public static void writeShort(OutputStream out, short val) throws IOException
	{
		out.write(new byte[] {(byte) ((val & 0xFF00) >>> 8), (byte) (val & 0xFF)});
	}
	
	public static short readShort(InputStream in) throws IOException
	{
		return (short) ((in.read() << 8) | in.read());
	}
	
	public static void writeInt(OutputStream out, int val) throws IOException
	{
		out.write(new byte[] {(byte) ((val & 0xFF000000) >>> 24), (byte) ((val & 0xFF0000) >>> 16), (byte) ((val & 0xFF00) >>> 8), (byte) (val & 0xFF)});
	}
	
	public static int readInt(InputStream in) throws IOException
	{
		return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
	}
	
	public static void writeLong(OutputStream out, long val) throws IOException
	{
		out.write(new byte[] {(byte) ((val & 0xFF00000000000000L) >>> 56), (byte) ((val & 0xFF000000000000L) >>> 48), (byte) ((val & 0xFF0000000000L) >>> 40), (byte) ((val & 0xFF00000000L) >> 32), (byte) ((val & 0xFF000000L) >>> 24), (byte) ((val & 0xFF0000L) >>> 16), (byte) ((val & 0xFF00L) >>> 8), (byte) (val & 0xFFL)});
	}
	
	public static long readLong(InputStream in) throws IOException
	{
		return ((long) in.read() << 56) | ((long) in.read() << 48) | ((long) in.read() << 40) | ((long) in.read() << 32) | ((long) in.read() << 24) | ((long) in.read() << 16) | ((long) in.read() << 8) | in.read();
	}
	
	public static void writeFloat(OutputStream out, float val) throws IOException
	{
		writeInt(out, Float.floatToIntBits(val));
	}
	
	public static float readFloat(InputStream in) throws IOException
	{
		return Float.intBitsToFloat(readInt(in));
	}
	
	public static void writeDouble(OutputStream out, double val) throws IOException
	{
		writeLong(out, Double.doubleToLongBits(val));
	}
	
	public static double readDouble(InputStream in) throws IOException
	{
		return Double.longBitsToDouble(readLong(in));
	}
}