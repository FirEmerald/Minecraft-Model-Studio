package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import firemerald.mcms.api.data.BinaryElement;

public class AttributeShort implements IAttribute
{
	public final short val;
	
	public AttributeShort(short val)
	{
		this.val = val;
	}
	
	@Override
	public int getID()
	{
		return ID_SHORT;
	}

	@Override
	public String getString()
	{
		return Short.toString(val);
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
		return val;
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
		throw new Exception("Value is a short, not an enum");
	}

	@Override
	public void write(OutputStream out, Charset charset, int charsetSize) throws IOException
	{
		BinaryElement.writeShort(out, val);
	}
	
	public static AttributeShort read(InputStream in) throws IOException
	{
		return new AttributeShort(BinaryElement.readShort(in));
	}
}