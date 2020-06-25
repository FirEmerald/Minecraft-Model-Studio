package firemerald.mcms.util.history;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Texture;

public class HistoryActionTextureEdit implements IHistoryAction<HistoryActionTextureEdit>
{
	public final Texture texture;
	public String oldName, newName;
	public final ByteBuffer oldData;
	public int oldW, oldH;
	
	public HistoryActionTextureEdit(Texture texture, String oldName, String newName, int oldW, int oldH)
	{
		this.texture = texture;
		this.oldName = oldName;
		this.newName = newName;
		this.oldW = oldW;
		this.oldH = oldH;
		if (oldW != texture.w || oldH != texture.h)
		{
			oldData = MemoryUtil.memAlloc(texture.getData().capacity());
			oldData.put(texture.getData());
			texture.getData().flip();
			oldData.flip();
		}
		else oldData = null;
	}

	@Override
	public HistoryActionTextureEdit perform()
	{
		if (oldW != texture.w || oldH != texture.h)
		{
			ByteBuffer data = MemoryUtil.memAlloc(texture.getData().capacity());
			
			data.put(oldData);
			data.flip();
			oldData.flip();
			
			oldData.put(texture.getData());
			texture.getData().flip();
			oldData.flip();
			
			int tempW = texture.w;
			int tempH = texture.h;
			texture.resize(oldW, oldH);
			oldW = tempW;
			oldH = tempH;
			
			texture.getData().put(data);
			data.flip();
			texture.getData().flip();

			MemoryUtil.memFree(data);
			texture.setNeedsUpdate();
		}
		
		if (!oldName.equals(newName))
		{
			Main.instance.project.setTextureName(newName, oldName);
			String temp = oldName;
			oldName = newName;
			newName = temp;
		}
		
		return this;
	}
	
	@Override
	public void finalize()
	{
		if (oldData != null) MemoryUtil.memFree(oldData);
	}
}