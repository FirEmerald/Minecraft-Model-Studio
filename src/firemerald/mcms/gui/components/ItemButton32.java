package firemerald.mcms.gui.components;

import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.util.mesh.Meshes;

public abstract class ItemButton32 extends ItemButton
{
	public ItemButton32(int x, int y)
	{
		super(x, y, 32);
	}

	@Override
	public Mesh getMesh()
	{
		return Meshes.X32;
	}
}