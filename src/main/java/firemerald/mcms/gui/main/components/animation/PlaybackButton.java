package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.Meshes;

public abstract class PlaybackButton extends ComponentButton
{
	public ThemeElement rect;
	
	public PlaybackButton(int x, int y)
	{
		super(x, y, x + 32, y + 32);
		onGuiUpdate(GuiUpdate.THEME);
	}

	public void setSize(int x, int y)
	{
		this.setSize(x, y, x + 32, y + 32);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(32, 32, 1);
		}
	}

	@Override
	public abstract boolean isEnabled();
	
	@Override
	public abstract void onRelease();
	
	public abstract ResourceLocation getIcon();

	@Override
	public void render(ButtonState state)
	{
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		Main.instance.guiShader.updateModel();
		state.applyButtonEffects();
		rect.bind();
		Meshes.X32.render();
		Main.instance.guiShader.setColor(getTheme().getOutlineColor());
		Main.instance.textureManager.bindTexture(getIcon());
		Meshes.X32.render();
		Main.instance.guiShader.setColor(1, 1, 1, 1);
		state.removeButtonEffects();
		GuiShader.MODEL.pop();
		Main.instance.guiShader.updateModel();
	}
}