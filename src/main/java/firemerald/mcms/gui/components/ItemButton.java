package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.GuiMesh;

public abstract class ItemButton extends ComponentButton
{
	public ThemeElement rect;
	public boolean enabled = false;
	public final int size;
	
	public ItemButton(int x, int y, int size)
	{
		super(x, y, x + size, y + size);
		this.size = size;
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(size, size, 1);
		}
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void render(ButtonState state)
	{
		Main main = Main.instance;
		GuiShader s = main.guiShader;
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		state.applyButtonEffects();
		rect.bind();
		getMesh().render();
		main.textureManager.bindTexture(getTexture());
		getMesh().render();
		state.removeButtonEffects();
		GuiShader.MODEL.pop();
		s.updateModel();
	}
	
	public abstract GuiMesh getMesh();
	
	public abstract ResourceLocation getTexture();
	
	public void setSize(int x, int y)
	{
		this.setSize(x, y, x + size, y + size);
	}
}