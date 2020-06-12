package firemerald.mcms.gui.decoration;

import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.IGuiHolder;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.Mesh;

public class DecoPane implements IGuiElement
{
	public int outline, radius;
	public int x1, y1, x2, y2;
	public ThemeElement rect = null;
	public final Mesh mesh = new Mesh();
	public IGuiHolder holder = null;

	@Override
	public void setHolder(IGuiHolder holder)
	{
		this.holder = holder;
	}
	
	@Override
	public IGuiHolder getHolder()
	{
		return this.holder;
	}
	
	public DecoPane(int x1, int y1, int x2, int y2, int outline, int radius)
	{
		this.outline = outline;
		this.radius = radius;
		setSize(x1, y1, x2, y2);
	}
	
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		onGuiUpdate(GuiUpdate.THEME);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
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
	public void tick(float mx, float my, float deltaTime) {}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		rect.bind();
		mesh.render();
	}
	
	@Override
	public int getX1()
	{
		return x1;
	}
	
	@Override
	public int getY1()
	{
		return y1;
	}
	
	@Override
	public int getX2()
	{
		return x2;
	}
	
	@Override
	public int getY2()
	{
		return y2;
	}

	@Override
	public void setThemeOverride(GuiTheme theme) {}
}