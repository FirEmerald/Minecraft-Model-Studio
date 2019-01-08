package firemerald.mcms.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;

public class TextureManager
{
	private final Map<String, Integer> textureMap = new HashMap<>();
	private final int missingTex, noTex;
	
	public TextureManager()
	{
		missingTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, missingTex);
		ByteBuffer data = MemoryUtil.memAlloc(16);
		data.putInt(0, 0xFF000000);
		data.putInt(4, 0xFF7F007F);
		data.putInt(8, 0xFF7F007F);
		data.putInt(12, 0xFF000000);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 2, 2, 0, GL_BGRA, GL_UNSIGNED_BYTE, data);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    	MemoryUtil.memFree(data);
    	noTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, noTex);
		data = MemoryUtil.memAlloc(4);
		data.putInt(0, 0xFFFFFFFF);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_BGRA, GL_UNSIGNED_BYTE, data);
    	MemoryUtil.memFree(data);
	}
	
	public int getTexture(String texture)
	{
		Integer tex = textureMap.get(texture);
		if (tex == null)
		{
			InputStream in = Main.getResource("textures/" + texture);
			if (in != null)
			{
				try
				{
					BufferedImage img = ImageIO.read(in);
					int w = img.getWidth();
					int h = img.getHeight();
			    	int[] pixels = new int[w * h];
			    	img.getRGB(0, 0, w, h, pixels, 0, w);
					ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
			    	int ind = 0;
			    	for (int i = 0; i < pixels.length; i++)
			    	{
			    		int p = pixels[i];
			    		data.putInt(ind, p); //ARGB -> BGRA is automatic due to endiness
			    		ind += 4;
			    	}
					int t;
					tex = new Integer(t = glGenTextures());
					glBindTexture(GL_TEXTURE_2D, t);
					glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_BYTE, data);
					glGenerateMipmap(GL_TEXTURE_2D);
			    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			    	MemoryUtil.memFree(data);
				}
				catch (IOException e)
				{
					tex = new Integer(missingTex);
					e.printStackTrace();
				}
				FileUtils.closeSafe(in);
			}
			else
			{
				new Exception("Missing texture: " + texture).printStackTrace();
				tex = new Integer(missingTex);
			}
			textureMap.put(texture, tex);
		}
		return tex;
	}
	
	public int[] getTextureData(String texture)
	{
		InputStream in = Main.getResource("textures/" + texture);
		if (in != null)
		{
			int[] data = null;
			try
			{
				BufferedImage img = ImageIO.read(in);
				int w = img.getWidth();
				int h = img.getHeight();
		    	int[] pixels = new int[w * h];
		    	img.getRGB(0, 0, w, h, pixels, 0, w);
		    	data =  pixels;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			FileUtils.closeSafe(in);
			return data;
		}
		else
		{
			new Exception("Missing texture: " + texture).printStackTrace();
			return null;
		}
	}
	/*
	public int getTexture2(String texture)
	{
		int tex;
		InputStream in = Main.getResource("textures/" + texture);
		try
		{
			BufferedImage img = ImageIO.read(in);
			int w = img.getWidth();
			int h = img.getHeight();
	    	int[] pixels = new int[w * h];
	    	img.getRGB(0, 0, w, h, pixels, 0, w);
			ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
	    	int ind = 0;
	    	for (int i = 0; i < pixels.length; i++)
	    	{
	    		int p = pixels[i];
	    		int val = 255 - Math.min(Math.min(((p & 0xFF0000) >>> 16), ((p & 0xFF00) >>> 8)), p & 0xFF);
	    		p = (p & 0xFF000000) | (val << 16) | (val << 8) | (val);
	    		data.putInt(ind, p); //ARGB -> BGRA is automatic due to endiness
	    		ind += 4;
	    	}
			tex = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, tex);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_BYTE, data);
			glGenerateMipmap(GL_TEXTURE_2D);
	    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	    	MemoryUtil.memFree(data);
		}
		catch (IOException e)
		{
			tex = new Integer(missingTex);
			e.printStackTrace();
		}
		try
		{
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return tex;
	}
	*/
	public void bindTexture(String texture)
	{
		glBindTexture(GL_TEXTURE_2D, getTexture(texture));
	}
	
	public void unbindTexture()
	{
		glBindTexture(GL_TEXTURE_2D, noTex);
	}
}