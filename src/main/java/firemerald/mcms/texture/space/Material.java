package firemerald.mcms.texture.space;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.util.IClonableObject;
import firemerald.mcms.texture.Texture;

public class Material implements IClonableObject<Material>
{
	private Texture diffuse;
	private final Map<String, Texture> secondaryData;
	
	public Material(int w, int h)
	{
		this(new Texture(w, h));
	}
	
	public Material(Texture diffuse)
	{
		this.diffuse = diffuse;
		this.secondaryData = new HashMap<>();
	}
	
	@NonNull
	public Texture getDiffuse()
	{
		return diffuse;
	}
	
	@Nullable
	public Texture getTexture(EnumTextureSpace space)
	{
		return space == EnumTextureSpace.DIFFUSE ? diffuse : secondaryData.get(space.name);
	}
	
	@Nullable
	public Texture getOrCreateTexture(EnumTextureSpace space)
	{
		if (space == EnumTextureSpace.DIFFUSE) return diffuse;
		Texture tex = secondaryData.get(space.name);
		if (tex == null)
		{
			secondaryData.put(space.name, tex = new Texture(diffuse.w, diffuse.h));
			tex.clearTexture(space.defaultColor.toARGB());
		}
		return tex;
	}
	
	public void setTexture(EnumTextureSpace space, Texture tex)
	{
		if (space == EnumTextureSpace.DIFFUSE) diffuse = tex;
		else if (tex != null) secondaryData.put(space.name, tex);
	}
	
	public void removeTexture(EnumTextureSpace space)
	{
		Texture tex = secondaryData.remove(space.name);
		if (tex != null) tex.cleanUp();
	}
	
	public void cleanUp()
	{
		diffuse.cleanUp();
		secondaryData.values().forEach(Texture::cleanUp);
	}
	
	@Override
	public void finalize()
	{
		cleanUp();
	}
	
	public void save(AbstractElement el)
	{
		diffuse.save(el);
		secondaryData.forEach((name, tex) -> {
			AbstractElement el2 = el.addChild(name);
			tex.save(el2);
		});
	}
	
	public void resize(int w, int h)
	{
		diffuse.resize(w, h);
		secondaryData.forEach((name, tex) -> {
			int col = 0;
			for (EnumTextureSpace space : EnumTextureSpace.values()) if (space.name.equals(name))
			{
				col = space.defaultColor.toARGB();
				break;
			}
			tex.resize(w, h, col);
		});
	}
	
	public static Material load(AbstractElement el)
	{
		Texture texture = Texture.load(el);
		if (texture != null)
		{
			Material mat = new Material(texture);
			el.getChildren().forEach(child -> {
				Texture tex = Texture.load(child);
				if (tex == null) Main.LOGGER.warn("Unable to load material texture space " + child.getName() + ": texture load error");
				else if (tex.w != texture.w || tex.h != texture.h) Main.LOGGER.warn("Unable to load material texture space " + child.getName() + ": texture dimension mismatch");
				mat.secondaryData.put(child.getName(), tex);
			});
			//TODO message for missing texture space
			return mat;
		}
		else return null;
	}

	@Override
	public Material cloneObject()
	{
		Material mat = new Material(diffuse.cloneObject());
		secondaryData.forEach((name, tex) -> mat.secondaryData.put(name, tex.cloneObject()));
		return mat;
	}
	
	public Map<String, Texture> getSecondaryData()
	{
		return this.secondaryData;
	}
}