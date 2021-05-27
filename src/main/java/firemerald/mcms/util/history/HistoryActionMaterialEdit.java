package firemerald.mcms.util.history;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.Material;

public class HistoryActionMaterialEdit implements IHistoryAction<HistoryActionMaterialEdit>
{
	public final Material material;
	public String oldName, newName;
	public final ByteBuffer oldDiffuseData;
	public final Map<String, ByteBuffer> oldSecondaryData;
	public int oldW, oldH;
	
	public HistoryActionMaterialEdit(Material material, String oldName, String newName, int oldW, int oldH)
	{
		this.material = material;
		this.oldName = oldName;
		this.newName = newName;
		this.oldW = oldW;
		this.oldH = oldH;
		Texture diffuse = material.getDiffuse();
		if (oldW != diffuse.w || oldH != diffuse.h)
		{
			oldDiffuseData = MemoryUtil.memAlloc(diffuse.getData().capacity());
			oldDiffuseData.put(diffuse.getData());
			diffuse.getData().flip();
			oldDiffuseData.flip();
			oldSecondaryData = new HashMap<>();
			material.getSecondaryData().forEach((name, tex) -> {
				ByteBuffer oldData = MemoryUtil.memAlloc(tex.getData().capacity());
				oldData.put(tex.getData());
				tex.getData().flip();
				oldData.flip();
				oldSecondaryData.put(name, oldData);
			});
		}
		else
		{
			oldDiffuseData = null;
			oldSecondaryData = null;
		}
	}

	@Override
	public HistoryActionMaterialEdit perform()
	{
		Texture diffuse = material.getDiffuse();
		if (oldW != diffuse.w || oldH != diffuse.h)
		{
			{
				ByteBuffer data = MemoryUtil.memAlloc(diffuse.getData().capacity());
				
				data.put(oldDiffuseData);
				data.flip();
				oldDiffuseData.flip();
				
				oldDiffuseData.put(diffuse.getData());
				diffuse.getData().flip();
				oldDiffuseData.flip();
				
				int tempW = diffuse.w;
				int tempH = diffuse.h;
				diffuse.resize(oldW, oldH);
				oldW = tempW;
				oldH = tempH;
				
				diffuse.getData().put(data);
				data.flip();
				diffuse.getData().flip();

				MemoryUtil.memFree(data);
				diffuse.setNeedsUpdate();
			}
			material.getSecondaryData().forEach((name, tex) -> {
				ByteBuffer oldData = this.oldSecondaryData.get(name);
				if (oldData != null)
				{
					ByteBuffer data = MemoryUtil.memAlloc(tex.getData().capacity());
					
					data.put(oldData);
					data.flip();
					oldData.flip();
					
					oldData.put(tex.getData());
					tex.getData().flip();
					oldData.flip();
					
					int tempW = tex.w;
					int tempH = tex.h;
					tex.resize(oldW, oldH);
					oldW = tempW;
					oldH = tempH;
					
					tex.getData().put(data);
					data.flip();
					tex.getData().flip();

					MemoryUtil.memFree(data);
					tex.setNeedsUpdate();
				}
				else tex.resize(oldW, oldH);
			});
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
		if (oldDiffuseData != null) MemoryUtil.memFree(oldDiffuseData);
		if (oldSecondaryData != null) oldSecondaryData.values().forEach(MemoryUtil::memFree);
	}
}