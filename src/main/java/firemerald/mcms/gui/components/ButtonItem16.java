package firemerald.mcms.gui.components;

import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.util.mesh.Meshes;

public class ButtonItem16 extends ButtonItem
{
	public ButtonItem16(int x, int y, ResourceLocation texture, Runnable action)
	{
		super(x, y, 16, texture, action);
	}

	@Override
	public GuiMesh getMesh()
	{
		return Meshes.X16;
	}
}