package firemerald.mcms.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.util.FileUtil;

public class AnimatedTexture extends Texture
{
	protected boolean interpolate;
	protected double lastTime;
	protected double length;
	protected ByteBuffer[] frameBuffers = new ByteBuffer[0];
	protected final NavigableMap<Double, Integer> frames = new TreeMap<>();
	
	protected AnimatedTexture() 
	{
		super();
	}
	
	protected AnimatedTexture(int w, int h)
	{
		super(w, h);
	}
	
	public ByteBuffer getFrame(int frame)
	{
		if (frameBuffers.length == 0) return getData();
		else if (frame < 0) return frameBuffers[0];
		else if (frame >= frameBuffers.length) return frameBuffers[frameBuffers.length - 1];
		else return frameBuffers[frame];
	}
	
	@Override
	public void bind()
	{
		Double adjTime = Double.valueOf(Main.time % length);
		if (lastTime != adjTime.doubleValue())
		{
			lastTime = adjTime.doubleValue();
			needsSet = true;
			Integer frame = frames.get(adjTime);
			if (frame != null) //exact frame
			{
				ByteBuffer buf = getFrame(frame);
				this.getData().put(buf);
				getData().flip();
				buf.flip();
			}
			else
			{
				Entry<Double, Integer> prevFrame = frames.floorEntry(adjTime);
				if (prevFrame == null) //exact first frame
				{
					prevFrame = frames.firstEntry();
					if (prevFrame != null)
					{
						ByteBuffer buf = getFrame(prevFrame.getValue());
						this.getData().put(buf);
						getData().flip();
						buf.flip();
					}
				}
				else if (!interpolate) //use prev frame
				{
					ByteBuffer buf = getFrame(prevFrame.getValue());
					this.getData().put(buf);
					getData().flip();
					buf.flip();
				}
				else
				{
					Entry<Double, Integer> nextFrame = frames.ceilingEntry(adjTime);
					if (nextFrame == null) //next is first
					{
						nextFrame = frames.firstEntry();
						if (nextFrame == null) //exact prev frame
						{
							ByteBuffer buf = getFrame(prevFrame.getValue());
							this.getData().put(buf);
							getData().flip();
							buf.flip();
						}
						else //interpolate prev and first
						{
							interpolate(getFrame(prevFrame.getValue()), getFrame(nextFrame.getValue()), getData(), (float) ((adjTime.doubleValue() - prevFrame.getKey().doubleValue()) / (length - prevFrame.getKey().doubleValue())));
						}
					}
					else //interpolate prev and next
					{
						interpolate(getFrame(prevFrame.getValue()), getFrame(nextFrame.getValue()), getData(), (float) ((adjTime.doubleValue() - prevFrame.getKey().doubleValue()) / (nextFrame.getKey().doubleValue() - prevFrame.getKey().doubleValue())));
					}
				}
			}
		}
		super.bind();
	}
	
	public static void interpolate(ByteBuffer prev, ByteBuffer next, ByteBuffer src, float lerp)
	{
		int size = Math.min(Math.min(prev.remaining(), next.remaining()), src.remaining());
		for (int i = 0; i < size; i++) src.put(i, interpolate(prev.get(i), next.get(i), lerp));
	}
	
	public static byte interpolate(byte prev, byte next, float lerp)
	{
		return (byte) (((prev) & 0xFF) + (next - prev) * lerp);
	}

	@Override
	public void cleanUp()
	{
		super.cleanUp();
		frames.clear();
		for (int i = 0; i < frameBuffers.length; i++) MemoryUtil.memFree(frameBuffers[i]);
		frameBuffers = new ByteBuffer[0];
	}
	
	public static Texture loadTexture(InputStream image, InputStream animation) throws IOException
	{
		AbstractElement anim = FileUtil.readStream(animation);
		if (anim == null) return Texture.loadTexture(image); //parse error
		BufferedImage img = ImageIO.read(image);
		int frames = anim.getInt("totalFrames", 1);
		int w = img.getWidth();
		int h = img.getHeight() / frames;
		AnimatedTexture tex = new AnimatedTexture(w, h);
		tex.length = anim.getDouble("length", 1);
		tex.interpolate = anim.getBoolean("interpolate", false);
		int y = 0;
    	int[] pixels = new int[w * h];
    	tex.frameBuffers = new ByteBuffer[frames];
		for (int i = 0; i < frames; i++)
		{
	    	img.getRGB(0, y, w, h, pixels, 0, w);
			ByteBuffer data = tex.frameBuffers[i] = MemoryUtil.memAlloc(w * h * 4);
	    	int ind = 0;
	    	for (int j = 0; j < pixels.length; j++)
	    	{
	    		int p = pixels[j];
	    		data.putInt(ind, p); //ARGB -> RGBA is automatic due to endiness
	    		ind += 4;
	    	}
	    	y += h;
		}
		anim.getChildren().forEach(child -> {
			if (child.getName().equals("frame"))
			{
				double time = child.getDouble("time", 0);
				int frame = child.getInt("index", 0);
				tex.frames.put(time, frame);
			}
		});
		return tex;
	}
}