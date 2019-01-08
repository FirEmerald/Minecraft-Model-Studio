package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import firemerald.mcms.api.data.BinaryElement;

public class AttributeFloat implements IAttribute
{
	public final float val;
	
	public AttributeFloat(float val)
	{
		this.val = val;
	}
	
	@Override
	public int getID()
	{
		return ID_FLOAT;
	}

	@Override
	public String getString()
	{
		return Float.toString(val);
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
		return (long) val;
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
		throw new Exception("Value is a float, not an enum");
	}

	@Override
	public void write(OutputStream out, Charset charset, int charsetSize) throws IOException
	{
		BinaryElement.writeFloat(out, val);
	}
	
	public static AttributeFloat read(InputStream in) throws IOException
	{
		return new AttributeFloat(BinaryElement.readFloat(in));
	}
}