package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import firemerald.mcms.api.data.BinaryElement;

public class AttributeString implements IAttribute
{
	public final String val;
	
	public AttributeString(String val)
	{
		this.val = val;
	}
	
	@Override
	public int getID()
	{
		return ID_STRING;
	}

	@Override
	public String getString()
	{
		return val;
	}

	@Override
	public boolean getBoolean() throws Exception
	{
		if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")) return true;
		else if (val.equalsIgnoreCase("false") || val.equalsIgnoreCase("no")) return false;
		else throw new Exception("Not a boolean value: " + val);
	}

	@Override
	public byte getByte()
	{
		return Byte.parseByte(val);
	}

	@Override
	public short getShort()
	{
		return Short.parseShort(val);
	}

	@Override
	public int getInt() 
	{
		return Integer.parseInt(val);
	}

	@Override
	public long getLong()
	{
		return Long.parseLong(val);
	}

	@Override
	public float getFloat()
	{
		return Float.parseFloat(val);
	}

	@Override
	public double getDouble()
	{
		return Double.parseDouble(val);
	}

	@Override
	public <T extends Enum<?>> T getEnum(T[] values) throws Exception
	{
		for (T t : values) if (t.name().equalsIgnoreCase(val)) return t;
		throw new Exception("Not an enum of " + values.getClass().getName() + ":" + val);
	}

	@Override
	public void write(OutputStream out, Charset charset, int charsetSize) throws IOException
	{
		BinaryElement.writeString(out, val, charset, charsetSize);
	}
	
	public static AttributeString read(InputStream in, Charset charset, int charsetSize) throws IOException
	{
		return new AttributeString(BinaryElement.readString(in, charset, charsetSize));
	}
}