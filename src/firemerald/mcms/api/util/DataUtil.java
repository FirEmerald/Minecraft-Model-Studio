package firemerald.mcms.api.util;

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
import org.xml.sax.SAXException;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.*;
import firemerald.mcms.util.FileUtils;

public class DataUtil
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
    
    public static Element readFile(File file) throws IOException
    {
    	/*
    	 * FE FF xx xx = UTF16BE
    	 * FF FE xx xx = UTF16LE
    	 * EF BB BF xx = UTF8
    	 * 00 3C 00 3F = UTF16BE
    	 * 3C 00 3F 00 = UTF16LE
    	 * 3C 3F 78 6D = UTF8/ASCII/ISO 8859 - use encoding declaration
    	 * 01 xx xx xx = ASCII binary
    	 * 02 xx xx xx = UTF8 binary
    	 * 03 xx xx xx = UTF16LE binary
    	 * 04 xx xx xx = UTF16BE binary
    	 * anything else = UTF8 without declaration
    	 */
    	InputStream in = null;
    	Element el = null;
    	try
    	{
			in = new FileInputStream(file);
			byte[] header = new byte[4];
			int length = in.read(header);
			InputStream buffered = new PartiallyLoadedInputStream(in, header, length);
			if ((length >= 2 && ((header[0] == 0xFE && header[1] == 0xFF) || (header[0] == 0xFF && header[1] == 0xFE))) || 
					(length >= 3 && ((header[0] == 0xEF && header[1] == 0xBB && header[2] == 0xBF))) ||
					(length >= 4 && ((header[0] == 0x00 && header[1] == 0x3C && header[2] == 0x00 && header[3] == 0x3F) ||
							         (header[0] == 0x3C && header[1] == 0x00 && header[2] == 0x3F && header[3] == 0x00) ||
							         (header[0] == 0x3C && header[1] == 0x3F && header[2] == 0x78 && header[3] == 0x6D))) || 
					!(header[0] == 0x01 || header[0] == 0x02 || header[0] == 0x03 || header[0] == 0x04))
			{
				Document doc = readXML(buffered);
				el = new W3CElement(doc);
			}
			else switch(header[0])
			{
			case BinaryElement.ID_ASCII:
				buffered.read();
				el = BinaryElementASCII.load(buffered);
				break;
			case BinaryElement.ID_UTF8:
				buffered.read();
				el = BinaryElementUTF8.load(buffered);
				break;
			case BinaryElement.ID_UTF16LE:
				buffered.read();
				el = BinaryElementUTF16LE.load(buffered);
				break;
			case BinaryElement.ID_UTF16BE:
				buffered.read();
				el = BinaryElementUTF16BE.load(buffered);
				break;
			}
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
}
