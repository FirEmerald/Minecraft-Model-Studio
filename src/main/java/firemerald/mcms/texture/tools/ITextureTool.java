package firemerald.mcms.texture.tools;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.Material;

public interface ITextureTool extends ITool
{
	@Override
	public default void onMouseClick(Material mat, double u, double v, int button)
	{
		Texture tex = mat.getTexture(Main.instance.activeSpace);
		if (tex != null) onMouseClick(tex, u, v, button);
	}
	
	public void onMouseClick(Texture tex, double u, double v, int button);
	
	@Override
	public default void onMouseDrag(Material mat, double prevU, double prevV, double u, double v, int button, boolean isNewObject)
	{
		Texture tex = mat.getTexture(Main.instance.activeSpace);
		if (tex != null) onMouseDrag(tex, prevU, prevV, u, v, button, isNewObject);
	}

	public void onMouseDrag(Texture tex, double prevU, double prevV, double u, double v, int button, boolean isNewObject);
	
	@Override
	public default void onMouseRelease(Material mat, double u, double v, int button)
	{
		Texture tex = mat.getTexture(Main.instance.activeSpace);
		if (tex != null) onMouseRelease(tex, u, v, button);
	}

	public void onMouseRelease(Texture tex, double u, double v, int button);
}