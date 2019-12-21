package firemerald.mcms.gui.components;

import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.util.mesh.Meshes;

public abstract class ItemButton16 extends ItemButton
{
	public ItemButton16(int x, int y)
	{
		super(x, y, 16);
	}

	@Override
	public Mesh getMesh()
	{
		return Meshes.X16;
	}
}