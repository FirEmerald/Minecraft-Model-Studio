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
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.popups.GuiPopupException;

public class FileTexture extends Texture
{
	public final File file;
	public final Reloader reloader;
	
	public FileTexture(File file) throws IOException
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
		    	setSize(img.getWidth(), img.getHeight());
		    	int[] pixels = new int[w * h];
		    	img.getRGB(0, 0, w, h, pixels, 0, w);
		    	if (getData() != null) MemoryUtil.memFree(getData());
				data = MemoryUtil.memAlloc(w * h * 4);
		    	int ind = 0;
		    	for (int i = 0; i < pixels.length; i++)
		    	{
		    		int p = pixels[i];
		    		getData().putInt(ind, p); //ARGB -> BGRA is automatic due to endiness
		    		ind += 4;
		    	}
				needsSet = true;
			}
			catch (Exception e)
			{
				GuiPopupException.onException("Couldn't load texture from " + file, e);
			}
			FileUtil.closeSafe(in);
		}
		catch (FileNotFoundException e)
		{
			GuiPopupException.onException("Couldn't load texture from missing file " + file, e);
		}
	}
	
	@Override
	public void cleanUp()
	{
		Main.instance.watcher.removeWatcher(file, reloader, StandardWatchEventKinds.ENTRY_MODIFY);
		super.cleanUp();
	}
	
	@Override
	public void save(AbstractElement el)
	{
		super.save(el);
		el.setString("file", file.toString());
	}
	
	protected static class Reloader implements Consumer<WatchEvent<?>>
	{
		protected final FileTexture tex;
		
		protected Reloader(FileTexture tex)
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