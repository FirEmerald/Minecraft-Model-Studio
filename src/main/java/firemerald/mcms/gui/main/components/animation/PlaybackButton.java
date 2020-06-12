package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.Meshes;

public abstract class PlaybackButton extends ComponentButton
{
	public ResourceLocation icon;
	public ThemeElement rect;
	
	public PlaybackButton(int x, int y, ResourceLocation icon)
	{
		super(x, y, x + 32, y + 32);
		this.icon = icon;
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

	@Override
	public void render(ButtonState state)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x1, y1, 0);
		Main.instance.shader.updateModel();
		state.applyButtonEffects();
		rect.bind();
		Meshes.X32.render();
		Main.instance.shader.setColor(getTheme().getOutlineColor());
		Main.instance.textureManager.bindTexture(icon);
		Meshes.X32.render();
		Main.instance.shader.setColor(1, 1, 1, 1);
		state.removeButtonEffects();
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
}