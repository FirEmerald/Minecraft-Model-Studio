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
import java.nio.charset.Charset;
import java.util.Base64;

import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NFDPathSet;
import org.lwjgl.util.nfd.NativeFileDialog;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.FileUtil;

public class FileUtils
{
	public static String readTextFile(String res, Charset encoding)
	{
		try
		{
			return readTextFile(Main.getResource(res), encoding);
		}
		catch (Exception e)
		{
			Main.LOGGER.warn("Failed to load text file", e);
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
			Main.LOGGER.warn("Failed to save text file", e);
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
			return dir + "/" + name;
		}
	}
	
	public static File getOpenFile(CharSequence filter, CharSequence directory)
	{
		long n = System.nanoTime();
		File file;
		PointerBuffer output = PointerBuffer.allocateDirect(1);
		int status = NativeFileDialog.NFD_OpenDialog(filter, directory, output);
		switch (status)
		{
		case NativeFileDialog.NFD_OKAY:
			String fileName = output.getStringUTF8(0);
			NativeFileDialog.nNFD_Free(output.get(0));
			file = new File(fileName);
			break;
		case NativeFileDialog.NFD_ERROR:
			Main.LOGGER.error("Error in open file dialog");
			Main.LOGGER.error("==========================");
			String err;
			while ((err = NativeFileDialog.NFD_GetError()) != null) Main.LOGGER.error("* " + err);
			Main.LOGGER.error("==========================");
		case NativeFileDialog.NFD_CANCEL:
		default:
			file = null;
			break;
		}
		Main.instance.lastNanos += (System.nanoTime() - n);
		return file;
	}
	
	public static File[] getOpenFiles(CharSequence filter, CharSequence directory)
	{
		long n = System.nanoTime();
		File[] files;
		NFDPathSet pathSet = NFDPathSet.calloc();
		int status = NativeFileDialog.NFD_OpenDialogMultiple(filter, directory, pathSet);
		switch (status)
		{
		case NativeFileDialog.NFD_OKAY:
            int count = (int) NativeFileDialog.NFD_PathSet_GetCount(pathSet);
            files = new File[count];
            for (int i = 0; i < count; i++)
            {
                String path = NativeFileDialog.NFD_PathSet_GetPath(pathSet, i);
                files[i] = new File(path);
            }
    		NativeFileDialog.NFD_PathSet_Free(pathSet);
    		break;
		case NativeFileDialog.NFD_ERROR:
			Main.LOGGER.error("Error in open files dialog");
			Main.LOGGER.error("==========================");
			String err;
			while ((err = NativeFileDialog.NFD_GetError()) != null) Main.LOGGER.error("* " + err);
			Main.LOGGER.error("==========================");
		case NativeFileDialog.NFD_CANCEL:
		default:
			files = new File[0];
			break;
		}
		Main.instance.lastNanos += (System.nanoTime() - n);
		return files;
	}
	
	public static File getSaveFile(CharSequence filter, CharSequence directory)
	{
		long n = System.nanoTime();
		File file;
		PointerBuffer output = PointerBuffer.allocateDirect(1);
		int status = NativeFileDialog.NFD_SaveDialog(filter, directory, output);
		switch (status)
		{
		case NativeFileDialog.NFD_OKAY:
			String fileName = output.getStringUTF8(0);
			NativeFileDialog.nNFD_Free(output.get(0));
			file = new File(fileName);
			break;
		case NativeFileDialog.NFD_ERROR:
			Main.LOGGER.error("Error in save file dialog");
			Main.LOGGER.error("* " + NativeFileDialog.NFD_GetError());
		case NativeFileDialog.NFD_CANCEL:
		default:
			file = null;
			break;
		}
		Main.instance.lastNanos += (System.nanoTime() - n);
		return file;
	}
	
	public static File getFolder(CharSequence directory)
	{
		long n = System.nanoTime();
		File file;
		PointerBuffer output = PointerBuffer.allocateDirect(1);
		int status = NativeFileDialog.NFD_PickFolder(directory, output);
		switch (status)
		{
		case NativeFileDialog.NFD_OKAY:
			String path = output.getStringUTF8(0);
			NativeFileDialog.nNFD_Free(output.get(0));
			file = new File(path);
			break;
		case NativeFileDialog.NFD_ERROR:
			Main.LOGGER.error("Error in folder dialog");
			Main.LOGGER.error("==========================");
			String err;
			while ((err = NativeFileDialog.NFD_GetError()) != null) Main.LOGGER.error("* " + err);
			Main.LOGGER.error("==========================");
		case NativeFileDialog.NFD_CANCEL:
		default:
			file = null;
			break;
		}
		Main.instance.lastNanos += (System.nanoTime() - n);
		return file;
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
}