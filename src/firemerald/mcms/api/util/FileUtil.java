package firemerald.mcms.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.bind.TypeAdapters;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.*;
import firemerald.mcms.util.FileUtils;

public class FileUtil
{
	public static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
	public static final DocumentBuilder DB;
    public static final TransformerFactory TFF = TransformerFactory.newInstance();
    public static final Transformer TF;
	public static final Gson GSON = new Gson();
	
    static
	{
		DocumentBuilder db;
		try
		{
			db = DBF.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			db = null;
			Main.LOGGER.error("Failed to create the XML reader", e);
		}
		DB = db;
		Transformer tf;
		try
		{
			TFF.setAttribute("indent-number", new Integer(4));
			tf = TFF.newTransformer();
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
		}
		catch (TransformerConfigurationException e)
		{
			tf = null;
			Main.LOGGER.error("Failed to create the XML writer", e);
		}
		TF = tf;
	}
    
    @SuppressWarnings("resource")
	public static AbstractElement readFile(File file) throws IOException
    {
    	/*
    	 * XML:
    	 * <(name) (attribute)=(value)>(value)</(name)>
    	 * <(name)>
    	 *     <(child)/>
    	 * </(name)>
    	 * 
    	 * Binary: it's custom binary, no text representation or data conversion required.
    	 * 
    	 * JSON:
    	 * {#name:(name),#value:(value),(attribute):(value),(child):{},(child):[0:(value),1:(value)],(child):[0:{},1:{}]}
    	 * 
    	 * There is no JSON saver in place, since JSON does not support multiple elements with the same name - I need to find a way around this. In the meantime, learn XML as it is far better than JSON anyway.
    	 * 
    	 * if there is no '#name', the name is 'root' for the root object or the name used to define it e.g. 'child:{}' would be named 'child'
    	 * if there is no '#value' the value is null - conversely, for re-encoding if the value is null there will be no '#value' present.
    	 * if the value is null, there are no children, and the attributes' names are all positive integers in sequence, it's a value array.
    	 *   the value is determined either by all attributes being the same type, or by using the highest-level format they all fit in this sequence:
    	 *     boolean
    	 *     byte
    	 *     short
    	 *     int
    	 *     long
    	 *     float
    	 *     double
    	 *     string
    	 * if the value is null, there are no attributes, and all childrens' names are all positive integers in sequence, it's an object array.
    	 * 
    	 */
    	InputStream in = null;
    	AbstractElement el = null;
    	try
    	{
			in = new FileInputStream(file);
			byte[] header = new byte[4];
			int length = in.read(header);
			InputStream buffered = new PartiallyLoadedInputStream(in, header, length);
			if (length == 0)
			{
				throw new IOException("Cannot parse empty file!");
			}
			// id xx xx xx = binary
			for (BinaryFormat format : BinaryFormat.values()) if (format.id == header[0])
			{
				el = loadBinary(buffered, format);
				break;
			}
			if (el == null) switch (header[0])
			{
			// 00 3C 00 3F = UTF16BE XML
			// 00 5B xx xx = UTF16BE JSON
			// 00 7B xx xx = UTF16BE JSON
			case 0:
				if (length >= 2)
				{
					if (length >= 4 && header[1] == 0x3C)
					{
						if (header[2] == 0x00 && header[3] == 0x3F) el = loadXML(buffered);
					}
					else if (header[1] == 0x5B || header[1] == 0x7B) el = loadJSON(buffered, StandardCharsets.UTF_16BE);
				}
				break;
			// 3C 00 3F 00 = UTF16LE XML
			// 3C 3F 78 6D = UTF8/ASCII/ISO 8859 XML - use encoding declaration
			case 0x3C:
				if (length >= 4)
				{
					if (header[1] == 0x00)
					{
						if (header[2] == 0x3F && header[3] == 0x00) el = loadXML(buffered);
					}
					else if (header[1] == 0x3F && header[2] == 0x78 && header[3] == 0x6D) el = loadXML(buffered);
				}
				break;
			// 5B 00 xx xx = UTF16LE JSON
			// 5B xx xx xx = UTF8 JSON
			// 7B 00 xx xx = UTF16LE JSON
			// 7B xx xx xx = UTF8 JSON
			case 0x5B:
			case 0x7B:
				if (length >= 2 && header[1] == 0x00) el = loadJSON(buffered, StandardCharsets.UTF_16LE);
				else el = loadJSON(buffered, StandardCharsets.UTF_8);
				break;
			// EF BB BF 5B = UTF8 JSON, skip 3
			// EF BB BF 7B = UTF8 JSON, skip 3
			// EF BB BF xx = UTF8 XML, skip 3
			case (byte) 0xEF:
				if (length >= 3 && header[1] == (byte) 0xBB && header[2] == (byte) 0xBF)
				{
					if (length >= 4 && (header[3] == 0x5B || header[3] == 0x7B)) el = loadJSON(buffered, StandardCharsets.UTF_8);
					else el = loadXML(buffered);
				}
				break;
			// FE FF 00 5B = UTF16BE JSON, skip 2
			// FE FF 00 7B = UTF16BE JSON, skip 2
			// FE FF xx xx = UTF16BE XML, skip 2
			case (byte) 0xFE:
				if (length >= 2 && header[1] == (byte) 0xFF)
				{
					if (length >= 4 && header[2] == 0x00)
					{
						if (header[3] == 0x5B || header[3] == 0x7B) el = loadJSON(buffered, StandardCharsets.UTF_16BE);
					}
					else el = loadXML(buffered);
				}
				break;
			// FF FE 5B 00 = UTF16LE JSON, skip 2
			// FF FE 7B 00 = UTF16LE JSON, skip 2
			// FF FE xx xx = UTF16LE XML, skip 2
			case (byte) 0xFF:
				if (length >= 2 && header[1] == (byte) 0xFE)
				{
					if (length >= 4 && header[3] == 0x00)
					{
						if (header[2] == 0x5B || header[2] == 0x7B) el = loadJSON(buffered, StandardCharsets.UTF_16LE);
					}
					else el = loadXML(buffered);
				}
				break;
			}
			// anything else = UTF8 XML without declaration
			if (el == null) el = loadXML(buffered);
		}
    	catch (SAXException e)
    	{
    		FileUtils.closeSafe(in);
    		throw new IOException(e);
    	}
    	catch (IOException e)
    	{
    		FileUtils.closeSafe(in);
    		throw e;
		}
		FileUtils.closeSafe(in);
    	return el;
    }
    
    public static AbstractElement loadXML(InputStream in) throws SAXException, IOException
    {
		Document doc = readXML(in);
		return new W3CElement(doc);
    }
    
    public static Element loadBinary(InputStream in, BinaryFormat format) throws IOException
    {
		in.read();
		return Element.loadBinaryHeaderless(in, format);
    }
    
    public static Element loadJSON(InputStream in, Charset charset) throws IOException, IllegalArgumentException
    {
    	return loadJSON(new InputStreamReader(in, charset));
    }
    
    public static Element loadJSON(Reader reader) throws IOException, IllegalArgumentException
    {
    	JsonElement element = TypeAdapters.JSON_ELEMENT.fromJson(reader);
    	return Element.loadJSON(element);
    }
	
	public static Document readXML(InputStream in) throws SAXException, IOException
	{
		return DB.parse(in);
	}
	
	public static Document createXML()
	{
		return DB.newDocument();
	}
	
	public static void saveXML(Document doc, File output) throws TransformerException, IOException
	{
		if (output.getParentFile() != null) output.getParentFile().mkdirs();
		output.createNewFile();
	    DOMSource source = new DOMSource(doc);
	    FileWriter writer = new FileWriter(output, false);
	    StreamResult result = new StreamResult(writer);
		TF.transform(source, result);
		writer.close();
	}
	
	public static void saveXML(Document doc, OutputStream out) throws TransformerException, IOException
	{
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(out);
		TF.transform(source, result);
	}
	
	public static void printXML(Document doc) throws TransformerException
	{
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(System.out);
		TF.transform(source, result);
	}
	
	public static void writeString(OutputStream out, String val, BinaryFormat format) throws IOException
	{
		out.write(val.getBytes(format.charset));
		out.write(format.nullChar);
	}
	
	public static String readString(InputStream in, BinaryFormat format) throws IOException
	{
		byte[] data = new byte[format.nullChar.length];
		byte[] bytes = new byte[0];
		while (in.read(data) == data.length && !arrayEquals(data, format.nullChar)) bytes = append(bytes, data);
		return new String(bytes, format.charset);
	}
	
	public static boolean arrayEquals(byte[] a1, byte[] a2)
	{
		if (a1.length != a2.length) return false;
		for (int i = 0; i < a1.length; i++) if (a1[i] != a2[i]) return false;
		return true;
	}
	
	public static byte[] append(byte[] a1, byte[] a2)
	{
		byte[] a3 = new byte[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}
	
	public static void writeBoolean(OutputStream out, boolean val) throws IOException
	{
		out.write(val ? 1 : 0);
	}
	
	public static boolean readBoolean(InputStream in) throws IOException
	{
		return in.read() > 0;
	}
	
	public static void writeByte(OutputStream out, byte val) throws IOException
	{
		out.write(val);
	}
	
	public static byte readByte(InputStream in) throws IOException
	{
		return (byte) in.read();
	}
	
	public static void writeShort(OutputStream out, short val) throws IOException
	{
		out.write(new byte[] {(byte) ((val & 0xFF00) >>> 8), (byte) (val & 0xFF)});
	}
	
	public static short readShort(InputStream in) throws IOException
	{
		return (short) ((in.read() << 8) | in.read());
	}
	
	public static void writeInt(OutputStream out, int val) throws IOException
	{
		out.write(new byte[] {(byte) ((val & 0xFF000000) >>> 24), (byte) ((val & 0xFF0000) >>> 16), (byte) ((val & 0xFF00) >>> 8), (byte) (val & 0xFF)});
	}
	
	public static int readInt(InputStream in) throws IOException
	{
		return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
	}
	
	public static void writeLong(OutputStream out, long val) throws IOException
	{
		out.write(new byte[] {(byte) ((val & 0xFF00000000000000L) >>> 56), (byte) ((val & 0xFF000000000000L) >>> 48), (byte) ((val & 0xFF0000000000L) >>> 40), (byte) ((val & 0xFF00000000L) >> 32), (byte) ((val & 0xFF000000L) >>> 24), (byte) ((val & 0xFF0000L) >>> 16), (byte) ((val & 0xFF00L) >>> 8), (byte) (val & 0xFFL)});
	}
	
	public static long readLong(InputStream in) throws IOException
	{
		return ((long) in.read() << 56) | ((long) in.read() << 48) | ((long) in.read() << 40) | ((long) in.read() << 32) | ((long) in.read() << 24) | ((long) in.read() << 16) | ((long) in.read() << 8) | in.read();
	}
	
	public static void writeFloat(OutputStream out, float val) throws IOException
	{
		writeInt(out, Float.floatToIntBits(val));
	}
	
	public static float readFloat(InputStream in) throws IOException
	{
		return Float.intBitsToFloat(readInt(in));
	}
	
	public static void writeDouble(OutputStream out, double val) throws IOException
	{
		writeLong(out, Double.doubleToLongBits(val));
	}
	
	public static double readDouble(InputStream in) throws IOException
	{
		return Double.longBitsToDouble(readLong(in));
	}
}
