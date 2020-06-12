package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.Mesh;

public class ColorHistoryButton extends ComponentButton
{
	public ThemeElement rect;
	public final Mesh button = new Mesh();
	public final Mesh color = new Mesh();
	public final ColorModel c;
	public final ComponentColorPicker picker;
	
	public ColorHistoryButton(int x1, int y1, int x2, int y2, ColorModel color, ComponentColorPicker picker)
	{
		super(x1, y1, x2, y2);
		setSize(x1, y1, x2, y2);
		this.c = color;
		this.picker = picker;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		if (button != null) button.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		if (color != null) color.setMesh(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(x2 - x1, y2 - y1, 1);
		}
	}

	@Override
	public boolean isEnabled()
	{
		return c != null;
	}
	
	@Override
	public void onRelease()
	{
		picker.setColor(c);
	}

	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		rect.bind();
		button.render();
		state.removeButtonEffects();
		if (c != null)
		{
			Main.instance.textureManager.unbindTexture();
			Main.instance.shader.setColor(new Color(c, 1));
			color.render();
		}
		Main.instance.shader.setColor(1f, 1f, 1f, 1f);
	}
}