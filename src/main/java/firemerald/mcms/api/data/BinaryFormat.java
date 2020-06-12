package firemerald.mcms.api.data;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum BinaryFormat
{
	ASCII(1, StandardCharsets.US_ASCII),
	ISO_8859_1(2, StandardCharsets.ISO_8859_1),
	UTF_8(3, StandardCharsets.UTF_8),
	UTF_16LE(4, StandardCharsets.UTF_16LE),
	UTF_16BE(5, StandardCharsets.UTF_16BE);
	
	public final int id;
	public final Charset charset;
	public final byte[] nullChar;
	
	BinaryFormat(int id, Charset charset)
	{
		this.id = id;
		this.charset = charset;
		ByteBuffer bytes = charset.encode("\0");
		nullChar = new byte[bytes.remaining()];
		bytes.get(nullChar);
	}
}