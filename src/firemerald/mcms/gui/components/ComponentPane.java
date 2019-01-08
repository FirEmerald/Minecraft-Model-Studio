package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.shader.Shader;

public class ComponentPane extends GuiElementContainer implements IComponent
{
	public float x1, y1, x2, y2;
	public boolean isFocused = false;
	
	public ComponentPane(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public void onFocus()
	{
		this.isFocused = true;
	}
	
	@Override
	public void onUnfocus()
	{
		this.isFocused = false;
	}
	
	@Override
	public boolean contains(float x, float y)
	{
		return (x >= x1 && y >= y1 && x < x2 && y < y2);
	}

	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		if (!isFocused && focused != null)
		{
			focused.onUnfocus();
			focused = null;
		}
		super.tick(mx - x1, my - y1, deltaTime);
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Shader s = Main.instance.shader;
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		super.render(mx - x1, my - y1, canHover);
		Shader.MODEL.pop();
		s.updateModel();
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx - x1, my - y1, button, mods);
	}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) 
	{
		super.onMouseRepeat(mx - x1, my - y1, button, mods);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods) 
	{
		super.onMouseReleased(mx - x1, my - y1, button, mods);
	}

	@Override
	public void onDrag(float mx, float my)
	{
		super.onDrag(mx - x1, my - y1);
	}

	@Override
	public long getCursor(float mx, float my)
	{
		return super.getCursor(mx - x1, my - y1);
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