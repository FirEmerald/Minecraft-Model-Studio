package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import firemerald.mcms.api.data.BinaryElement;

public class AttributeInt implements IAttribute
{
	public final int val;
	
	public AttributeInt(int val)
	{
		this.val = val;
	}
	
	@Override
	public int getID()
	{
		return ID_INT;
	}

	@Override
	public String getString()
	{
		return Integer.toString(val);
	}

	@Override
	public boolean getBoolean()
	{
		return val > 0;
	}

	@Override
	public byte getByte()
	{
		return (byte) val;
	}

	@Override
	public short getShort()
	{
		return (short) val;
	}

	@Override
	public int getInt() 
	{
		return val;
	}

	@Override
	public long getLong()
	{
		return val;
	}

	@Override
	public float getFloat()
	{
		return val;
	}

	@Override
	public double getDouble()
	{
		return val;
	}

	@Override
	public <T extends Enum<?>> T getEnum(T[] values) throws Exception
	{
		throw new Exception("Value is an int, not an enum");
	}

	@Override
	public void write(OutputStream out, Charset charset, int charsetSize) throws IOException
	{
		BinaryElement.writeInt(out, val);
	}
	
	public static AttributeInt read(InputStream in) throws IOException
	{
		return new AttributeInt(BinaryElement.readInt(in));
	}
}