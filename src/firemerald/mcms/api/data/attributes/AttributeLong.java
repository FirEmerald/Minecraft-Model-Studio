package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import firemerald.mcms.api.data.BinaryElement;

public class AttributeLong implements IAttribute
{
	public final long val;
	
	public AttributeLong(long val)
	{
		this.val = val;
	}
	
	@Override
	public int getID()
	{
		return ID_LONG;
	}

	@Override
	public String getString()
	{
		return Long.toString(val);
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
		return (int) val;
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
		throw new Exception("Value is a long, not an enum");
	}

	@Override
	public void write(OutputStream out, Charset charset, int charsetSize) throws IOException
	{
		BinaryElement.writeLong(out, val);
	}
	
	public static AttributeLong read(InputStream in) throws IOException
	{
		return new AttributeLong(BinaryElement.readLong(in));
	}
}