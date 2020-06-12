package firemerald.mcms.util.action;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.texture.Texture;

public class TextureDrawAction implements IAction
{
	public final Texture texture;
	public final ByteBuffer oldData;
	
	public TextureDrawAction(Texture texture)
	{
		this.texture = texture;
		oldData = MemoryUtil.memAlloc(texture.getData().capacity());
		oldData.put(texture.getData());
		texture.getData().flip();
		oldData.flip();
	}

	@Override
	public TextureDrawAction get()
	{
		ByteBuffer data = MemoryUtil.memAlloc(texture.getData().capacity());
		
		data.put(oldData);
		data.flip();
		oldData.flip();
		
		oldData.put(texture.getData());
		texture.getData().flip();
		oldData.flip();
		
		texture.getData().put(data);
		data.flip();
		texture.getData().flip();

		MemoryUtil.memFree(data);
		texture.setNeedsUpdate();
		
		return this;
	}
	
	@Override
	public void finalize()
	{
		MemoryUtil.memFree(oldData);
	}
}