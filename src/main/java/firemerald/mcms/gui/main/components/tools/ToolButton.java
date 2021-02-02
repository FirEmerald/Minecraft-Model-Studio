package firemerald.mcms.gui.main.components.tools;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.texture.tools.ITool;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.Meshes;

public class ToolButton extends ComponentButton
{
	public final GuiSection section;
	public final ResourceLocation texture;
	public final ITool tool;
	public ThemeElement rect;
	
	public ToolButton(int x, int y, GuiSection section, ResourceLocation texture, ITool tool)
	{
		super(x, y, x + 32, y + 32);
		this.section = section;
		this.texture = texture;
		this.tool = tool;
		onGuiUpdate(GuiUpdate.THEME);
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.tool != tool;
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
		GuiShader s = main.guiShader;
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		state.applyButtonEffects();
		rect.bind();
		Meshes.X32.render();
		main.textureManager.bindTexture(texture);
		Meshes.X32.render();
		state.removeButtonEffects();
		GuiShader.MODEL.pop();
		s.updateModel();
	}
	
	public void setSize(int x, int y)
	{
		this.setSize(x, y, x + 32, y + 32);
	}
	
	@Override
	public void onRelease()
	{
		Main.instance.tool.onDeselect(section);
		Main.instance.tool = tool;
		tool.onSelect(section);
	}
}