package firemerald.mcms.gui.main.components.tools;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.Meshes;

public class ColorSwapButton extends ComponentButton
{
	public ThemeElement rect;
	
	public ColorSwapButton(int x, int y)
	{
		super(x, y, x + 12, y + 12);
		onGuiUpdate(GuiUpdate.THEME);
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(12, 12, 1);
		}
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
		Meshes.X12.render();
		main.textureManager.bindTexture(Textures.COLOR_SWAP);
		Meshes.X12.render();
		state.removeButtonEffects();
		GuiShader.MODEL.pop();
		s.updateModel();
	}
	
	public void setSize(int x, int y)
	{
		this.setSize(x, y, x + 12, y + 12);
	}
	
	@Override
	public void onRelease()
	{
		ColorModel c1 = Main.instance.toolHolder.getColor1().c;
		ColorModel c2 = Main.instance.toolHolder.getColor2().c;
		Main.instance.toolHolder.setColor1(c2);
		Main.instance.toolHolder.setColor2(c1);
	}
}