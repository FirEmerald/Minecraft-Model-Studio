package firemerald.mcms.gui.components;

import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.util.mesh.Meshes;

public class ButtonItem32 extends ButtonItem
{
	public ButtonItem32(int x, int y, String texture, Runnable action)
	{
		super(x, y, 32, texture, action);
	}

	@Override
	public Mesh getMesh()
	{
		return Meshes.X32;
	}
}