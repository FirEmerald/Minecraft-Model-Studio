package firemerald.mcms.texture;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;
import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;
import firemerald.mcms.util.FileUtils;

public class ReloadingTexture extends Texture
{
	public final File file;
	public final Reloader reloader;
	
	public ReloadingTexture(File file) throws IOException
	{
		super();
		this.file = file;
		this.reloader = new Reloader(this);
		Main.instance.watcher.addWatcher(file, reloader, StandardWatchEventKinds.ENTRY_MODIFY);
		load();
	}
	
	public void load()
	{
		Main.LOGGER.log(Level.INFO, "Attempting to load texture from " + file);
		try
		{
			InputStream in = new FileInputStream(file);
			try
			{
				BufferedImage img = ImageIO.read(in);
				w = img.getWidth();
				h = img.getHeight();
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
			catch (Exception e)
			{
				Main.LOGGER.log(Level.WARN, "Loading failed", e);
			}
			FileUtils.closeSafe(in);
		}
		catch (FileNotFoundException e)
		{
			Main.LOGGER.log(Level.WARN, "File not found", e);
		}
	}
	
	@Override
	public void cleanUp()
	{
		Main.instance.watcher.removeWatcher(file, reloader, StandardWatchEventKinds.ENTRY_MODIFY);
		super.cleanUp();
	}
	
	protected static class Reloader implements Consumer<WatchEvent<?>>
	{
		protected final ReloadingTexture tex;
		
		protected Reloader(ReloadingTexture tex)
		{
			this.tex = tex;
		}
		
		@Override
		public void accept(WatchEvent<?> arg0)
		{
			tex.load();
		}
	}
}