package firemerald.mcms.gui.components;

import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.util.mesh.Meshes;

public abstract class ItemButton16 extends ItemButton
{
	public ItemButton16(int x, int y)
	{
		super(x, y, 16);
	}

	@Override
	public GuiMesh getMesh()
	{
		return Meshes.X16;
	}
}