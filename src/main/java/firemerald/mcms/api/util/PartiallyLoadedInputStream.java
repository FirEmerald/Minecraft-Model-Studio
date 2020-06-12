package firemerald.mcms.api.util;

import java.io.IOException;
import java.io.InputStream;

public class PartiallyLoadedInputStream extends InputStream
{
	public final InputStream in;
	private final byte[] data;
	private final int length;
	private int position;
	
	public PartiallyLoadedInputStream(InputStream in, byte[] data, int length)
	{
		this.in = in;
		this.data = data;
		this.length = length;
		position = 0;
	}
	
	public PartiallyLoadedInputStream(InputStream in, byte[] data)
	{
		this(in, data, data.length);
	}
	
	@Override
	public int read() throws IOException
	{
		if (position < length) return data[position++];
		else return in.read();
	}
}
