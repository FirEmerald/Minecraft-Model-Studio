package firemerald.mcms.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.util.FileUtils;

public class Texture
{
	protected ByteBuffer data;
	public int w, h;
	public final int texID;
	protected boolean needsSet;
	
	protected Texture() 
	{
		texID = glGenTextures();
	}
	
	public Texture(int w, int h)
	{
		this();
		data = MemoryUtil.memAlloc((this.w = w) * (this.h = h) * 4);
		for (int i = 0; i < data.capacity(); i++) data.put(i, (byte) -1);
		needsSet = true;
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, texID);
		if (needsSet)
		{
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_BYTE, data);
	    	glGenerateMipmap(GL_TEXTURE_2D);
	    	needsSet = false;
		}
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	public void cleanUp()
	{
		MemoryUtil.memFree(data);
		glDeleteTextures(texID);
	}
	
	public void setPixel(int x, int y, int argb)
	{
		if (x < 0 || x >= w) throw new IllegalArgumentException("invalid x coordinate: " + x);
		if (y < 0 || y >= w) throw new IllegalArgumentException("invalid y coordinate: " + y);
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
		if (y < 0 || y >= w) throw new IllegalArgumentException("invalid y coordinate: " + y);
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
		if (y < 0 || y >= w) throw new IllegalArgumentException("invalid y coordinate: " + y);
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
		if (x1 >= x2) throw new IllegalArgumentException("x2 must be greater than x1!");
		if (x1 < 0) throw new IllegalArgumentException("invalid x1 coordinate: " + x1);
		if (x2 >= w) throw new IllegalArgumentException("invalid x2 coordinate: " + x2);
		if (y1 >= y2) throw new IllegalArgumentException("y2 must be greater than y1!");
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
			e.printStackTrace();
		}
		FileUtils.closeSafe(in);
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
			saveTexture(out = new FileOutputStream(file));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		FileUtils.closeSafe(out);
	}
	
	public void saveTexture(OutputStream out) throws IOException
	{
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[data.remaining()];
		data.asIntBuffer().get(pixels).flip();
		img.setRGB(0, 0, w, h, pixels, 0, w);
		ImageIO.write(img, "png", out);
	}
}