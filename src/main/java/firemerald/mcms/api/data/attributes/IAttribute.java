package firemerald.mcms.api.data.attributes;

import java.io.IOException;
import java.io.OutputStream;

import firemerald.mcms.api.data.BinaryFormat;

public interface IAttribute
{
	public static final int
	ID_STRING = 0,
	ID_BOOLEAN = 1,
	ID_BYTE = 2,
	ID_SHORT = 3,
	ID_INT = 4,
	ID_LONG = 5,
	ID_FLOAT = 6,
	ID_DOUBLE = 7;
	
	public int getID();
	
	public String getString();
	
	public boolean getBoolean() throws Exception;
	
	public byte getByte() throws Exception;
	
	public short getShort() throws Exception;
	
	public int getInt() throws Exception;
	
	public long getLong() throws Exception;
	
	public float getFloat() throws Exception;
	
	public double getDouble() throws Exception;
	
	public <T extends Enum<?>> T getEnum(T[] values) throws Exception;
	
	public void write(OutputStream out, BinaryFormat format) throws IOException;
}