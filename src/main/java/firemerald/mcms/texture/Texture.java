package firemerald.mcms.texture;

import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.api.util.IClonableObject;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.util.FileUtils;

public class Texture implements IClonableObject<Texture>
{
	protected ByteBuffer data;
	public int w, h;
	protected int texID;
	protected boolean needsSet;
	
	protected Texture() 
	{
		texID = glGenTextures();
	}
	
	public Texture(int w, int h)
	{
		this();
		data = MemoryUtil.memAlloc((this.w = w) * (this.h = h) * 4);
		for (int i = 0; i < data.capacity(); i++) data.put(i, (byte) 0);
		needsSet = true;
	}
	
	public Texture(Texture from)
	{
		this();
		data = MemoryUtil.memAlloc((this.w = from.w) * (this.h = from.h) * 4);
		for (int i = 0; i < data.capacity(); i++) data.put(i, from.data.get(i));
		needsSet = true;
	}
	
	@Override
	public Texture cloneObject()
	{
		return new Texture(this);
	}
	
	public int getU(float u)
	{
		return (int) (u * w) % w;
	}
	
	public int getV(float v)
	{
		return (int) (v * h) % h;
	}
	
	/** this clears the texture! **/
	public void setSize(int w, int h)
	{
		if (data != null) MemoryUtil.memFree(data);
		data = MemoryUtil.memAlloc((this.w  = w) * (this.h = h) * 4);
		for (int i = 0; i < data.capacity(); i++) data.put(i, (byte) 0);
		needsSet = true;
	}
	
	/** this does not clear the texture! **/
	public void resize(int w, int h)
	{
		if (data == null)
		{
			data = MemoryUtil.memAlloc((this.w  = w) * (this.h = h) * 4);
			for (int i = 0; i < data.capacity(); i++) data.put(i, (byte) 0);
		}
		else
		{
			ByteBuffer oldData = data;
			int oldW = this.w, oldH = this.h;
			data = MemoryUtil.memAlloc((this.w  = w) * (this.h = h) * 4);
			int oldPos1 = 0, pos1 = 0;
			for (int y = 0; y < h; y++)
			{
				int oldPos = oldPos1, pos = pos1;
				for (int x = 0; x < w; x++)
				{
					if (x < oldW && y < oldH) data.putInt(pos, oldData.getInt(oldPos));
					else data.putInt(pos, 0);
					oldPos += 4;
					pos += 4;
				}
				oldPos1 += oldW * 4;
				pos1 += w * 4;
			}
			MemoryUtil.memFree(oldData);
		}
		needsSet = true;
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, texID);
		if (needsSet)
		{
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_BYTE, data);
			glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 16);
	    	glGenerateMipmap(GL_TEXTURE_2D);
	    	needsSet = false;
		}
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	@Override
	public void finalize()
	{
		cleanUp();
	}
	
	public void cleanUp()
	{
		if (texID < 0)
		{
			MemoryUtil.memFree(data);
			glDeleteTextures(texID);
			texID = -1;
		}
	}
	
	public void setPixel(int x, int y, int argb)
	{
		if (x < 0 || x >= w) throw new IllegalArgumentException("invalid x coordinate: " + x);
		if (y < 0 || y >= h) throw new IllegalArgumentException("invalid y coordinate: " + y);
		data.putInt((x + (y * w)) * 4, argb);
		needsSet = true;
	}
	
	public void setPixel(int x, int y, Color color)
	{
		setPixel(x, y, color.toARGB());
	}
	
	public void setPixel(int x, int y, float r, float g, float b, float a)
	{
		if (x < 0 || x >= w) throw new IllegalArgumentException("invalid x coordinate: " + x);
		if (y < 0 || y >= h) throw new IllegalArgumentException("invalid y coordinate: " + y);
		byte rb = r <= 0 ? 0 : r >= 1 ? -1 : (byte) Math.round(r * 255);
		byte gb = g <= 0 ? 0 : g >= 1 ? -1 : (byte) Math.round(g * 255);
		byte bb = b <= 0 ? 0 : b >= 1 ? -1 : (byte) Math.round(b * 255);
		byte ab = a <= 0 ? 0 : a >= 1 ? -1 : (byte) Math.round(a * 255);
		int ind = (x + (y * w)) * 4;
		data.put(ind, bb);
		data.put(ind + 1, gb);
		data.put(ind + 2, rb);
		data.put(ind + 3, ab);
		needsSet = true;
	}
	
	public Color getPixel(int x, int y)
	{
		if (x < 0 || x >= w) throw new IllegalArgumentException("invalid x coordinate: " + x);
		if (y < 0 || y >= h) throw new IllegalArgumentException("invalid y coordinate: " + y);
		return new Color(data.getInt((x + (y * w)) * 4));
	}
	
	public Color[][] getRegion(int x1, int y1, int x2, int y2)
	{
		if (x1 >= x2) throw new IllegalArgumentException("x2 must be greater than x1!");
		if (x1 < 0) throw new IllegalArgumentException("invalid x1 coordinate: " + x1);
		if (x2 >= w) throw new IllegalArgumentException("invalid x2 coordinate: " + x2);
		if (y1 >= y2) throw new IllegalArgumentException("y2 must be greater than y1!");
		if (y1 < 0) throw new IllegalArgumentException("invalid y1 coordinate: " + y1);
		if (y2 >= h) throw new IllegalArgumentException("invalid y2 coordinate: " + y2);
		Color[][] pixels = new Color[x2 - x1][y2 - y1];
		int xo = 0;
		for (int x = x1; x < x2; x++)
		{
			int yo = 0;
			Color[] ar = pixels[xo];
			for (int y = y1; y < y2; y++)
			{
				ar[yo] = getPixel(x, y);
				yo++;
			}
			xo++;
		}
		return pixels;
	}
	
	public void setRegion(int x1, int y1, int x2, int y2, Color[][] pixels)
	{
		if (x1 > x2) throw new IllegalArgumentException("x2 must be greater than x1!");
		if (x1 < 0) throw new IllegalArgumentException("invalid x1 coordinate: " + x1);
		if (x2 >= w) throw new IllegalArgumentException("invalid x2 coordinate: " + x2);
		if (y1 > y2) throw new IllegalArgumentException("y2 must be greater than y1!");
		if (y1 < 0) throw new IllegalArgumentException("invalid y1 coordinate: " + y1);
		if (y2 >= h) throw new IllegalArgumentException("invalid y2 coordinate: " + y2);
		if (pixels.length != x2 - x1) throw new IllegalArgumentException("region width does not match supplied pixel array");
		int xo = 0;
		for (int x = x1; x < x2; x++)
		{
			int yo = 0;
			Color[] ar = pixels[xo];
			if (ar.length != y2 - y1) throw new IllegalArgumentException("region height does not match supplied pixel array");
			for (int y = 0; y < y2; y++)
			{
				setPixel(x, y, ar[yo]);
				yo++;
			}
			xo++;
		}
	}
	
	public void setRegion(int x1, int y1, int x2, int y2, int color)
	{
		if (x1 >= x2) throw new IllegalArgumentException("x2 must be greater than x1!");
		if (x1 < 0) throw new IllegalArgumentException("invalid x1 coordinate: " + x1);
		if (x2 > w) throw new IllegalArgumentException("invalid x2 coordinate: " + x2);
		if (y1 >= y2) throw new IllegalArgumentException("y2 must be greater than y1!");
		if (y1 < 0) throw new IllegalArgumentException("invalid y1 coordinate: " + y1);
		if (y2 > h) throw new IllegalArgumentException("invalid y2 coordinate: " + y2);
		for (int x = x1; x < x2; x++)
		{
			for (int y = 0; y < y2; y++)
			{
				setPixel(x, y, color);
			}
		}
	}
	
	public void clearTexture()
	{
		for (int i = 0; i < data.capacity(); i++) data.put(i, (byte) 0);
		needsSet = true;
	}
	
	public static Texture loadTexture(File img)
	{
		InputStream in = null;
		Texture tex = null;
		try
		{
			in = new FileInputStream(img);
			tex = loadTexture(in);
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Failed to load texure from " + img, e);
		}
		FileUtil.closeSafe(in);
		return tex;
	}
	
	public static Texture loadTexture(InputStream in) throws IOException
	{
		BufferedImage img = ImageIO.read(in);
		int w = img.getWidth();
		int h = img.getHeight();
		Texture tex = new Texture(w, h);
    	int[] pixels = new int[w * h];
    	img.getRGB(0, 0, w, h, pixels, 0, w);
    	int ind = 0;
    	for (int i = 0; i < pixels.length; i++)
    	{
    		int p = pixels[i];
    		tex.data.putInt(ind, p); //ARGB -> RGBA is automatic due to endiness
    		ind += 4;
    	}
    	tex.needsSet = true;
		return tex;
	}
	
	public void saveTexture(File file)
	{
		OutputStream out = null;
		try
		{
			saveTexture(out = new FileOutputStream(file), FileUtil.getExtension(file.toString()));
		}
		catch (IOException e)
		{
			//TODO remove files left over due to not being able to save image
			GuiPopupException.onException("Could not save texture to " + file.toString(), e);
		}
		FileUtil.closeSafe(out);
	}
	
	public void saveTexture(OutputStream out, String format) throws IOException
	{
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[data.remaining() / 4];
		data.asIntBuffer().get(pixels).flip();
		img.setRGB(0, 0, w, h, pixels, 0, w);
		if (!ImageIO.write(img, format, out)) //TODO ask if want to exclude alpha?
		{
			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			img.setRGB(0, 0, w, h, pixels, 0, w);
			if (!ImageIO.write(img, format, out))
			{
				throw new IOException("Unable to save image with type INT_ARGB or type INT_RGB and format " + format);
			}
		}
	}
	
	public void load(File file)
	{
		InputStream in = null;
		try
		{
			load(in = new FileInputStream(file));
		}
		catch (IOException e)
		{
			GuiPopupException.onException(e);
		}
		FileUtil.closeSafe(in);
	}
	
	public void load(InputStream in) throws IOException
	{
		BufferedImage img = ImageIO.read(in);
    	setSize(img.getWidth(), img.getHeight());
    	int[] pixels = new int[w * h];
    	img.getRGB(0, 0, w, h, pixels, 0, w);
    	if (data != null) MemoryUtil.memFree(data);
		data = MemoryUtil.memAlloc(w * h * 4);
    	int ind = 0;
    	for (int i = 0; i < pixels.length; i++)
    	{
    		int p = pixels[i];
    		data.putInt(ind, p); //ARGB -> BGRA is automatic due to endiness
    		ind += 4;
    	}
		needsSet = true;
	}
	
	public byte[] getBytes()
	{
		List<Byte> bytes = new ArrayList<>();
		OutputStream out = new OutputStream() {
			@Override
			public void write(int arg0) throws IOException
			{
				bytes.add(Byte.valueOf((byte) arg0));
			}
		};
		try {
			saveTexture(out, "png");
		} catch (IOException e) {
			GuiPopupException.onException("Could not get bytes for texture", e);
		}
		byte[] array = new byte[bytes.size()];
		for (int i = 0; i < array.length; i++) array[i] = bytes.get(i);
		return array;
	}
	
	public void save(AbstractElement el)
	{
		el.setValue(this.getBase64());
	}
	
	public String getBase64()
	{
		ByteArrayOutputStream texOut = null;
		String base64 = null;
		try
		{
			texOut = new ByteArrayOutputStream();
			saveTexture(texOut, "png");
			base64 = FileUtils.encode64(texOut.toByteArray());
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't save texture as base64 data", e);
		}
		FileUtil.closeSafe(texOut);
		return base64;
	}
	
	public static Texture load(AbstractElement el)
	{
		if (el.hasAttribute("file"))
		{
			String fileName = el.getString("file", null);
			if (fileName != null)
			{
				File file = new File(fileName);
				if (file.exists()) try
				{
					return new ReloadingTexture(file);
				}
				catch (IOException e)
				{
					GuiPopupException.onException("Couldn't reload texture from " + fileName, e);
				}
			}
		}
		return loadBase64(el.getValue());
	}
	
	public static Texture loadBase64(String base64)
	{
		ByteArrayInputStream texIn = null;
		Texture texture = null;
		try
		{
			byte[] data = FileUtils.decode64(base64);
			texIn = new ByteArrayInputStream(data);
			texture = Texture.loadTexture(texIn);
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load texture from base64 data: " + base64, e);
		}
		FileUtil.closeSafe(texIn);
		return texture;
	}

	public ByteBuffer getData()
	{
		return data;
	}
	
	public void setNeedsUpdate()
	{
		this.needsSet = true;
	}
}