package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.Cursor;

public class ComponentPane extends GuiElementContainer implements IComponent
{
	public int x1, y1, x2, y2;
	public boolean isFocused = false;
	protected int margin;
	protected int ex1, ey1;
	public final Mesh inside = new Mesh();
	
	public ComponentPane(int x1, int y1, int x2, int y2)
	{
		this(x1, y1, x2, y2, 0);
	}
	
	public ComponentPane(int x1, int y1, int x2, int y2, int margin)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.setMargin(margin);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		ex1 = x1 + margin;
		ey1 = y1 + margin;
		inside.setMesh(ex1, ey1, x2 - margin, y2 - margin, 0, 0, 0, 1, 1);
	}
	
	public void setMargin(int margin)
	{
		this.margin = margin;
		ex1 = x1 + margin;
		ey1 = y1 + margin;
		inside.setMesh(ex1, ey1, x2 - margin, y2 - margin, 0, 0, 0, 1, 1);
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
		super.tick(mx - ex1, my - ey1, deltaTime);
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Shader s = Main.instance.shader;
		RenderUtil.pushStencil();
		RenderUtil.startStencil(false);
		this.renderStencilArea();
		RenderUtil.endStencil();
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(ex1, ey1, 0);
		s.updateModel();
		super.render(mx - ex1, my - ey1, canHover);
		Shader.MODEL.pop();
		s.updateModel();
		RenderUtil.popStencil();
	}
	
	public void renderStencilArea()
	{
		Main.instance.textureManager.unbindTexture();
		inside.render();
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx - ex1, my - ey1, button, mods);
	}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) 
	{
		super.onMouseRepeat(mx - ex1, my - ey1, button, mods);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods) 
	{
		super.onMouseReleased(mx - ex1, my - ey1, button, mods);
	}
	
	@Override
	public boolean canScrollH(float mx, float my)
	{
		return super.canScrollH(mx - ex1, my - ey1);
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		return super.canScrollV(mx - ex1, my - ey1);
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY) 
	{
		super.onMouseScroll(mx - ex1, my - ey1, scrollX, scrollY);
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		super.onDrag(mx - ex1, my - ey1, button);
	}

	@Override
	public Cursor getCursor(float mx, float my)
	{
		return super.getCursor(mx - ex1, my - ey1);
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
	public int getComponentOffsetX()
	{
		return this.getHolderOffsetX() + this.getX1();
	}
	
	@Override
	public int getComponentOffsetY()
	{
		return this.getHolderOffsetY() + this.getY1();
	}
}