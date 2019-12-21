package firemerald.mcms.gui.components;

import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.util.mesh.Meshes;

public abstract class ItemButton20 extends ItemButton
{
	public ItemButton20(int x, int y)
	{
		super(x, y, 20);
	}

	@Override
	public Mesh getMesh()
	{
		return Meshes.X20;
	}
}