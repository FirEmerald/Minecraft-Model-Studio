package firemerald.mcms.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CircularByteBuffer
{
	private byte[] data = new byte[0];
	public final OutputStream out = new Output(this);
	public final InputStream in = new Input(this);
	
	public byte read() throws IOException
	{
		try
		{
			byte out = data[0];
			byte[] oldData = data;
			data = new byte[oldData.length - 1];
			System.arraycopy(oldData, 1, data, 0, data.length);
			return out;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new IOException("no data to read!", e);
		}
	}
	
	public int read(byte[] des, int desPos, int length)
	{
		int toRead = Math.min(data.length, length);
		if (toRead > 0)
		{
			System.arraycopy(data, 0, des, desPos, toRead);
			byte[] oldData = data;
			data = new byte[oldData.length - toRead];
			System.arraycopy(oldData, 1, data, 0, data.length);
		}
		return toRead;
	}
	
	public void write(int bite)
	{
		byte[] oldData = data;
		data = new byte[oldData.length + 1];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
		data[oldData.length] = (byte) (bite & 0xFF);
	}
	
	public void write(byte[] src, int srcPos, int length)
	{
		if (length > 0)
		{
			byte[] oldData = data;
			data = new byte[oldData.length + length];
			System.arraycopy(oldData, 0, data, 0, oldData.length);
			System.arraycopy(src, srcPos, data, oldData.length, length);
		}
	}
	
	static class Output extends OutputStream
	{
		final CircularByteBuffer buffer;
		
		Output(CircularByteBuffer buffer)
		{
			this.buffer = buffer;
		}
		
		@Override
		public void write(int b)
		{
			buffer.write(b);
		}
		
		@Override
		public void write(byte[] src, int srcPos, int length)
		{
			buffer.write(src, srcPos, length);
		}
	}
	
	static class Input extends InputStream
	{
		final CircularByteBuffer buffer;
		
		Input(CircularByteBuffer buffer)
		{
			this.buffer = buffer;
		}
		
		@Override
		public int read() throws IOException
		{
			return buffer.read();
		}
		
		@Override
		public int read(byte[] des, int desPos, int length)
		{
			return buffer.read(des, desPos, length);
		}
	}
}