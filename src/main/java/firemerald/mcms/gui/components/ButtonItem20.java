package firemerald.mcms.gui.components;

import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.util.mesh.Meshes;

public class ButtonItem20 extends ButtonItem
{
	public ButtonItem20(int x, int y, ResourceLocation texture, Runnable action)
	{
		super(x, y, 20, texture, action);
	}

	@Override
	public Mesh getMesh()
	{
		return Meshes.X20;
	}
}