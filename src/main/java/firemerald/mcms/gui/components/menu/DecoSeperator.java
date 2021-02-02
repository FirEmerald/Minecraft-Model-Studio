package firemerald.mcms.gui.components.menu;

import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.IGuiHolder;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.GuiMesh;

public class DecoSeperator implements IGuiElement
{
	public int thickness, offset;
	public int x1, y1, x2, y2;
	public ThemeElement rect = null;
	public final GuiMesh mesh = new GuiMesh();
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
	
	public DecoSeperator(int x1, int y1, int x2, int y2)
	{
		this(x1, y1, x2, y2, 1, 1);
	}
	
	public DecoSeperator(int x1, int y1, int x2, int y2, int thickness)
	{
		this(x1, y1, x2, y2, thickness, 1);
	}
	
	public DecoSeperator(int x1, int y1, int x2, int y2, int thickness, int offset)
	{
		this.thickness = thickness;
		this.offset = offset;
		setSize(x1, y1, x2, y2);
	}
	
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		onGuiUpdate(GuiUpdate.THEME);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
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
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genMenuSeperator(x2 - x1, y2 - y1, thickness, offset);
		}
	}

	@Override
	public void setThemeOverride(GuiTheme theme) {}
}