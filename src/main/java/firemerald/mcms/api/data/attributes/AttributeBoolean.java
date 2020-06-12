package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import firemerald.mcms.api.data.BinaryFormat;
import firemerald.mcms.api.util.FileUtil;

public class AttributeBoolean implements IAttribute
{
	public final boolean val;
	
	public AttributeBoolean(boolean val)
	{
		this.val = val;
	}
	
	@Override
	public int getID()
	{
		return ID_BOOLEAN;
	}

	@Override
	public String getString()
	{
		return Boolean.toString(val);
	}

	@Override
	public boolean getBoolean()
	{
		return val;
	}

	@Override
	public byte getByte()
	{
		return (byte) (val ? 1 : 0);
	}

	@Override
	public short getShort()
	{
		return (short) (val ? 1 : 0);
	}

	@Override
	public int getInt() 
	{
		return val ? 1 : 0;
	}

	@Override
	public long getLong()
	{
		return val ? 1 : 0;
	}

	@Override
	public float getFloat()
	{
		return val ? 1 : 0;
	}

	@Override
	public double getDouble()
	{
		return val ? 1 : 0;
	}

	@Override
	public <T extends Enum<?>> T getEnum(T[] values) throws Exception
	{
		throw new Exception("Value is a boolean, not an enum");
	}

	@Override
	public void write(OutputStream out, BinaryFormat format) throws IOException
	{
		FileUtil.writeBoolean(out, val);
	}
	
	public static AttributeBoolean read(InputStream in) throws IOException
	{
		return new AttributeBoolean(FileUtil.readBoolean(in));
	}

	@Override
	public JsonElement makeElement()
	{
		return new JsonPrimitive(val);
	}
}