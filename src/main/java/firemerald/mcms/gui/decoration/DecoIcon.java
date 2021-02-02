package firemerald.mcms.gui.decoration;

import firemerald.mcms.Main;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.IGuiHolder;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.mesh.GuiMesh;

public class DecoIcon implements IGuiElement
{
	public int x1, y1, x2, y2;
	public final GuiMesh mesh = new GuiMesh();
	public IGuiHolder holder = null;
	public ResourceLocation icon;

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
	
	public DecoIcon(int x1, int y1, int x2, int y2, ResourceLocation icon)
	{
		setSize(x1, y1, x2, y2);
		this.icon = icon;
	}
	
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime) {}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Main.instance.textureManager.bindTexture(icon);
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