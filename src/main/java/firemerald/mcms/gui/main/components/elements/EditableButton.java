package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.Meshes;

public abstract class EditableButton extends ComponentButton
{
	public ThemeElement rect;
	
	public EditableButton(int x, int y)
	{
		super(x, y, x + 32, y + 32);
		onGuiUpdate(GuiUpdate.THEME);
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
	public void render(ButtonState state)
	{
		Main main = Main.instance;
		Shader s = main.shader;
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		state.applyButtonEffects();
		rect.bind();
		Meshes.X32.render();
		main.textureManager.bindTexture(getTexture());
		Meshes.X32.render();
		state.removeButtonEffects();
		Shader.MODEL.pop();
		s.updateModel();
	}
	
	@Override
	public abstract void onRelease();
	
	public abstract ResourceLocation getTexture();
	
	public void setSize(int x, int y)
	{
		this.setSize(x, y, x + 32, y + 32);
	}
}