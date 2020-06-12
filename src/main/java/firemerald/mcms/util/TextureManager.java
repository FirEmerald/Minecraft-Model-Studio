package firemerald.mcms.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.texture.AnimatedTexture;
import firemerald.mcms.texture.Texture;

public class TextureManager
{
	private final Map<ResourceLocation, Texture> textureMap = new HashMap<>();
	public final Texture missingTex, noTex;
	
	public TextureManager()
	{
		missingTex = new Texture(2, 2);
		missingTex.setPixel(0, 0, 0xFF000000);
		missingTex.setPixel(1, 0, 0xFF7F007F);
		missingTex.setPixel(0, 1, 0xFF7F007F);
		missingTex.setPixel(1, 1, 0xFF000000);
		noTex = new Texture(1, 1);
		noTex.setPixel(0, 0, 0xFFFFFFFF);
	}
	
	public Texture getTexture(ResourceLocation texLoc)
	{
		Texture tex = textureMap.get(texLoc);
		if (tex == null)
		{
			ResourceLocation texture = texLoc.prependPath("textures/");
			InputStream in = Main.getResource(texture);
			if (in != null)
			{
				InputStream animIn = Main.getResource(texture.appendPath(".anim"));
				if (animIn != null) //load animated texture
				{
					try
					{
						tex = AnimatedTexture.loadTexture(in, animIn);
					}
					catch (IOException e)
					{
						tex = missingTex;
						Main.LOGGER.log(Level.WARN, "Couldn't load texture " + texture, e);
					}
					FileUtil.closeSafe(in);
					FileUtil.closeSafe(animIn);
				}
				else //load static texture
				{
					try
					{
						tex = Texture.loadTexture(in);
					}
					catch (IOException e)
					{
						tex = missingTex;
						Main.LOGGER.log(Level.WARN, "Couldn't load texture " + texture, e);
					}
					FileUtil.closeSafe(in);
				}
			}
			else
			{
				Main.LOGGER.log(Level.WARN, new Exception("Missing texture: " + texture));
				tex = missingTex;
			}
			textureMap.put(texLoc, tex);
		}
		return tex;
	}
	
	public void bindTexture(ResourceLocation texture)
	{
		getTexture(texture).bind();
	}
	
	public void unbindTexture()
	{
		noTex.bind();
	}
}