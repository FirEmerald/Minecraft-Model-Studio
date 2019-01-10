package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import firemerald.mcms.api.data.BinaryFormat;
import firemerald.mcms.api.util.FileUtil;

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
	public void write(OutputStream out, BinaryFormat format) throws IOException
	{
		FileUtil.writeString(out, val, format);
	}
	
	public static AttributeString read(InputStream in, BinaryFormat format) throws IOException
	{
		return new AttributeString(FileUtil.readString(in, format));
	}

	@Override
	public JsonElement makeElement()
	{
		return new JsonPrimitive(val);
	}
}