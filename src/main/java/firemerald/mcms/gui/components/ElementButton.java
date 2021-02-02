package firemerald.mcms.gui.components;

import firemerald.mcms.api.util.TriFunction;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.GuiMesh;

public class ElementButton extends ComponentButton
{
	public TriFunction<Integer, Integer, GuiTheme, ThemeElement> icon;
	public ThemeElement el;
	public Runnable onRelease;
	public boolean enabled = true;
	public final GuiMesh mesh = new GuiMesh();
	
	public ElementButton(int x1, int y1, int x2, int y2, TriFunction<Integer, Integer, GuiTheme, ThemeElement> icon, Runnable onRelease)
	{
		super(x1, y1, x2, y2);
		this.icon = icon;
		this.onRelease = onRelease;
		setSize(x1, y1, x2, y2);
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
		el = icon.apply(x2 - x1, y2 - y1, this.getTheme());
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (el != null) el.release();
			el = icon.apply(x2 - x1, y2 - y1, this.getTheme());
		}
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void onRelease()
	{
		onRelease.run();
	}

	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		el.bind();
		mesh.render();
		state.removeButtonEffects();
	}
}