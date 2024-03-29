package firemerald.mcms.api.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import firemerald.mcms.api.API;
import firemerald.mcms.api.data.*;
import firemerald.mcms.gui.popups.GuiPopupException;

public class FileUtil
{
	public static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
	public static final DocumentBuilder DB;
    public static final TransformerFactory TFF = TransformerFactory.newInstance();
    public static final Transformer TF;
	
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
			API.LOGGER.error("Failed to create the XML reader", e);
		}
		DB = db;
		db.setErrorHandler(new ErrorHandler() {
		    @Override
		    public void warning(SAXParseException e) throws SAXException {
		    	//GuiPopupException.onException(e);
		    }

		    @Override
		    public void fatalError(SAXParseException e) throws SAXException {
		    	//GuiPopupException.onException(e);
		    }

		    @Override
		    public void error(SAXParseException e) throws SAXException {
		    	//GuiPopupException.onException(e);
		    }
		});
		Transformer tf;
		try
		{
			TFF.setAttribute("indent-number", Integer.valueOf(4));
			tf = TFF.newTransformer();
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
		}
		catch (TransformerConfigurationException e)
		{
			tf = null;
			API.LOGGER.error("Failed to create the XML writer", e);
		}
		TF = tf;
	}
    
	public static AbstractElement readFile(File file) throws IOException
    {
    	InputStream in = null;
    	AbstractElement el = null;
    	try
    	{
			el = readStream(in = new FileInputStream(file));
		}
    	catch (IOException e)
    	{
    		closeSafe(in);
    		throw e;
		}
    	return el;
    }
    
    @SuppressWarnings("resource")
	public static AbstractElement readStream(InputStream in) throws IOException
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
    	AbstractElement el = null;
    	try
    	{
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
			case 0:
				if (length >= 4 && 
				header[1] == 0x3C && 
				header[2] == 0x00 && 
				header[3] == 0x3F)
				{
					el = loadXML(buffered);
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
			// EF BB BF xx = UTF8 XML, skip 3
			case (byte) 0xEF:
				if (length >= 3 && header[1] == (byte) 0xBB && header[2] == (byte) 0xBF)
				{
					el = loadXML(buffered);
				}
				break;
			// FE FF xx xx = UTF16BE XML, skip 2
			case (byte) 0xFE:
				if (length >= 2 && header[1] == (byte) 0xFF)
				{
					el = loadXML(buffered);
				}
				break;
			// FF FE xx xx = UTF16LE XML, skip 2
			case (byte) 0xFF:
				if (length >= 2 && header[1] == (byte) 0xFE)
				{
					el = loadXML(buffered);
				}
				break;
			}
			// anything else = UTF8 XML without declaration
			if (el == null) el = loadXML(buffered);
		}
    	catch (SAXException e)
    	{
    		closeSafe(in);
    		throw new IOException(e);
    	}
    	catch (IOException e)
    	{
    		closeSafe(in);
    		throw e;
		}
		closeSafe(in);
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
		writeInt(out, Float.floatToRawIntBits(val));
	}
	
	public static float readFloat(InputStream in) throws IOException
	{
		return Float.intBitsToFloat(readInt(in));
	}
	
	public static void writeDouble(OutputStream out, double val) throws IOException
	{
		writeLong(out, Double.doubleToRawLongBits(val));
	}
	
	public static double readDouble(InputStream in) throws IOException
	{
		return Double.longBitsToDouble(readLong(in));
	}
	
	public static void closeSafe(Closeable out)
	{
		if (out != null) try
		{
			out.close();
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Failed to close closeable of type " + out.getClass().toString(), e);
		}
	}
	
	public static String getExtension(String fileName)
	{
	    char ch;
	    int len;
	    if(fileName==null || 
	            (len = fileName.length())==0 || 
	            (ch = fileName.charAt(len-1))=='/' || ch=='\\' || //in the case of a directory
	             ch=='.' ) //in the case of . or ..
	        return "";
	    int dotInd = fileName.lastIndexOf('.'),
	        sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
	    if( dotInd<=sepInd )
	        return "";
	    else
	        return fileName.substring(dotInd+1).toLowerCase();
	}
	
	public static DataType getAppropriateDataType(String file)
	{
		String ext = getExtension(file);
		for (DataType type : DataType.values())
		{
			if (type.extension.equalsIgnoreCase(ext)) return type;
			for (String alias : type.aliases) if (alias.equalsIgnoreCase(ext)) return type;
		}
		GuiPopupException.onException("Couldn't determine appropriate data type for saving: " + file + ". Defaulting to XML");
		return DataType.XML;
	}
	
	public static enum DataType
	{
		XML("xml", "mcms", "skel", "anim") {
			@Override
			public AbstractElement newElement(String name)
			{
				Document doc = createXML();
				org.w3c.dom.Element element = doc.createElement(name);
				doc.appendChild(element);
				return new W3CElement(element);
			}
			
			@Override
			public void saveElement(AbstractElement el, File file) throws Exception
			{
				W3CElement wc3 = W3CElement.convert(el);
				wc3.save(file);
			}
		},
		BINARY("bin") {
			@Override
			public AbstractElement newElement(String name)
			{
				return new Element(name);
			}
			
			@Override
			public void saveElement(AbstractElement el, File file) throws Exception
			{
				el.toElement().saveBinary(file, BinaryFormat.UTF_8); //TODO different binary elements
			}
		};
		
		public final String extension;
		public final String[] aliases;
		
		DataType(String extension, String... aliases)
		{
			this.extension = extension;
			this.aliases = aliases;
		}
		
		public abstract AbstractElement newElement(String name);
		
		public abstract void saveElement(AbstractElement el, File toSave) throws Exception;
	}
}
