package firemerald.mcms.gui.components;

import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.util.mesh.Meshes;

public class ButtonItem16 extends ButtonItem
{
	public ButtonItem16(int x, int y, String texture, Runnable action)
	{
		super(x, y, 16, texture, action);
	}

	@Override
	public Mesh getMesh()
	{
		return Meshes.X16;
	}
}