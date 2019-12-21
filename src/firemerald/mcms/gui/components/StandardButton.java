package firemerald.mcms.gui.components;

import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.Mesh;

public class StandardButton extends StandardFloatingButton
{
	public ThemeElement rect;
	public final Mesh mesh = new Mesh();
	public int radius = 0;
	
	public StandardButton(int x1, int y1, int x2, int y2, String text, Runnable onRelease)
	{
		super(x1, y1, x2, y2, text, onRelease);
		setSize(x1, y1, x2, y2);
	}
	
	public StandardButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, Runnable onRelease)
	{
		super(x1, y1, x2, y2, outline, text, onRelease);
		this.radius = radius;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		if (mesh != null) mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genRoundedBox(x2 - x1, y2 - y1, outline, radius);
		}
	}

	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		rect.bind();
		mesh.render();
		state.removeButtonEffects();
		super.render(state);
	}
}