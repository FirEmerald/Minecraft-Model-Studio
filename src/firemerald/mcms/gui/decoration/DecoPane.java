package firemerald.mcms.gui.decoration;

import firemerald.mcms.Main;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.theme.RoundedBoxFormat;

public class DecoPane implements IGuiElement
{
	public int outline, radius;
	public float x1, y1, x2, y2;
	public RoundedBoxFormat rect;
	public final Mesh mesh = new Mesh();
	
	public DecoPane(float x1, float y1, float x2, float y2, int outline, int radius)
	{
		this.outline = outline;
		this.radius = radius;
		setSize(x1, y1, x2, y2);
	}
	
	public void setSize(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		rect = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1), outline, radius);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime) {}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Main.instance.theme.bindRoundedBox(rect);
		mesh.render();
	}
	
	@Override
	public float getX1()
	{
		return x1;
	}
	
	@Override
	public float getY1()
	{
		return y1;
	}
	
	@Override
	public float getX2()
	{
		return x2;
	}
	
	@Override
	public float getY2()
	{
		return y2;
	}
}