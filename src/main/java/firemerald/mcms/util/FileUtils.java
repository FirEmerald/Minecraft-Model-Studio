package firemerald.mcms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringJoiner;

import javax.imageio.ImageIO;

import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NFDPathSet;
import org.lwjgl.util.nfd.NativeFileDialog;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.popups.GuiPopupException;

public class FileUtils
{
	public static String readTextFile(ResourceLocation res, Charset encoding)
	{
		try
		{
			return readTextFile(Main.getResource(res), encoding);
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Failed to load text file resource: " + res, e);
			return "";
		}
	}
	
	public static String readTextFile(InputStream in, Charset encoding)
	{
		String str = "";
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(in, encoding));
			boolean first = true;
			String ln;
			while ((ln = reader.readLine()) != null)
			{
				if (!first) str += "\n";
				else first = false;
				str += ln;
			}
		}
		catch (Throwable t)
		{
			throw new IllegalStateException(t);
		}
		FileUtil.closeSafe(reader);
		return str;
	}
	
	public static void saveTextFile(String text, File file, Charset encoding)
	{
		try
		{
			saveTextFile(text, new FileOutputStream(file), encoding);
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Failed to save text to " + file, e);
		}
	}
	
	public static void saveTextFile(String text, OutputStream out, Charset encoding)
	{
		Writer writer = null;
		try
		{
			writer = new OutputStreamWriter(out, encoding);
			writer.write(text);
		}
		catch (Throwable t)
		{
			throw new IllegalStateException(t);
		}
		if (writer != null) try
		{
			writer.close();
		}
		catch (Throwable t) {}
	}
	
	public static String removeExtension(File file)
	{
		if (file.isDirectory()) return file.toString();
		else
		{
			String dir = file.getParent();
			String name = file.getName();
			int ind = name.lastIndexOf('.');
			if (ind > 0) name = name.substring(0, ind);
			return dir == null ? name : dir + "/" + name;
		}
	}
	
	public static ByteBuffer getBuffer(CharSequence string)
	{
		ByteBuffer buf1 = StandardCharsets.US_ASCII.encode(string.toString());
		ByteBuffer buf2 = ByteBuffer.allocateDirect(buf1.capacity() + 2);
		buf2.put(buf1).put(new byte[] {0, 0}).flip();
		return buf2;
	}
	
	public static File getOpenFile(String directory, String filter)
	{
		long n = System.nanoTime();
		if (directory == null) directory = "";
        PointerBuffer output = PointerBuffer.allocateDirect(1);
        int status = NativeFileDialog.NFD_OpenDialog(filter, directory, output);
        File file;
        switch (status)
        {
        case NativeFileDialog.NFD_OKAY:
        	String fileName = output.getStringUTF8(0);
            NativeFileDialog.nNFD_Free(output.get(0));
            file = new File(fileName);
            break;
        case NativeFileDialog.NFD_ERROR:
            GuiPopupException.onException("Error in open file dialog:\n" + NativeFileDialog.NFD_GetError());
        case NativeFileDialog.NFD_CANCEL:
        default:
        	file = null;
            break;
        }
		Main.instance.lastNanos += (System.nanoTime() - n);
		return file;
	}
	
	public static File[] getOpenFiles(String directory, String filter)
	{
		long n = System.nanoTime();
		if (directory == null) directory = "";
        final NFDPathSet pathSet = NFDPathSet.calloc();
        final int status = NativeFileDialog.NFD_OpenDialogMultiple(filter, directory, pathSet);
        File[] files;
        switch (status)
        {
        case NativeFileDialog.NFD_OKAY:
            final int count = (int) NativeFileDialog.NFD_PathSet_GetCount(pathSet);
            files = new File[count];
            for (int i = 0; i < count; ++i)
            {
                final String path = NativeFileDialog.NFD_PathSet_GetPath(pathSet, i);
                files[i] = new File(path);
            }
            break;
        case NativeFileDialog.NFD_ERROR:
            GuiPopupException.onException("Error in open files dialog:\n" + NativeFileDialog.NFD_GetError());
        case NativeFileDialog.NFD_CANCEL:
        default:
        	files = new File[0];
            break;
        }
		Main.instance.lastNanos += (System.nanoTime() - n);
		return files;
	}
	
	public static File getSaveFile(String directory, String filter, String defExt)
	{
		long n = System.nanoTime();
		if (directory == null) directory = "";
        final PointerBuffer output = PointerBuffer.allocateDirect(1);
        final int status = NativeFileDialog.NFD_SaveDialog(filter, directory, output);
        File file;
        switch (status)
        {
        case NativeFileDialog.NFD_OKAY:
        	String fileName = output.getStringUTF8(0);
            NativeFileDialog.nNFD_Free(output.get(0));
            if (defExt != null) fileName = ensureHasExtension(fileName, defExt);
            file = new File(fileName);
            break;
        case NativeFileDialog.NFD_ERROR:
            GuiPopupException.onException("Error in save file dialog:\n" + NativeFileDialog.NFD_GetError());
        case NativeFileDialog.NFD_CANCEL:
        default:
        	file = null;
            break;
        }
		Main.instance.lastNanos += (System.nanoTime() - n);
		return file;
	}
	
	public static File getFolder(String directory)
	{
		long n = System.nanoTime();
		if (directory == null) directory = "";
        final PointerBuffer output = PointerBuffer.allocateDirect(1);
        final int status = NativeFileDialog.NFD_PickFolder(directory, output);
        File file;
        switch (status)
        {
        case NativeFileDialog.NFD_OKAY:
            final String path = output.getStringUTF8(0);
            NativeFileDialog.nNFD_Free(output.get(0));
            file = new File(path);
            break;
        case NativeFileDialog.NFD_ERROR:
            GuiPopupException.onException("Error in select folder dialog:\n" + NativeFileDialog.NFD_GetError());
        case NativeFileDialog.NFD_CANCEL:
        default:
        	file = null;
            break;
        }
		Main.instance.lastNanos += (System.nanoTime() - n);
		return file;
	}
	
	public static String getDefaultExtension(CharSequence filter)
	{
		String fil = filter.toString();
		int ind = fil.indexOf(';');
		if (ind < 0) return fil;
		else return fil.substring(0, ind);
	}
	
	public static String ensureHasExtension(String file, String defaultExtension)
	{
		String ext = FileUtil.getExtension(file);
		if (ext == null || ext.length() == 0) return file + "." + defaultExtension;
		else return file;
	}
	
	public static String encode64(InputStream in) throws IOException
	{
		byte[] data = new byte[65536];
		byte[] out = new byte[0];
		int s;
		while ((s = in.read(data)) > 0)
		{
			byte[] temp = out;
			out = new byte[temp.length + s];
			System.arraycopy(temp, 0, out, 0, temp.length);
			System.arraycopy(data, temp.length, out, temp.length, s);
		}
		return encode64(out);
	}
	
	public static String encode64(byte[] input)
	{
		return Base64.getEncoder().encodeToString(input);
	}
	
	public static void decode64(String str, OutputStream out) throws IOException
	{
		out.write(decode64(str));
	}
	
	public static byte[] decode64(String str)
	{
		return Base64.getDecoder().decode(str);
	}
	
	public static String getLoadImageFilter()
	{
		StringJoiner joiner = new StringJoiner(";");
		joiner.add("png");
		for (String format : ImageIO.getReaderFileSuffixes()) if (!format.equalsIgnoreCase("png") && !format.equalsIgnoreCase("jpg") && !format.equalsIgnoreCase("jpeg")) joiner.add(format);
		return joiner.toString();
	}
	
	public static String getSaveImageFilter()
	{
		StringJoiner joiner = new StringJoiner(";");
		joiner.add("png");
		for (String format : ImageIO.getWriterFileSuffixes()) if (!format.equalsIgnoreCase("png") && !format.equalsIgnoreCase("jpg") && !format.equalsIgnoreCase("jpeg")) joiner.add(format);
		return joiner.toString();
	}
}