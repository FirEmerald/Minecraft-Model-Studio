package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.Main;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.gui.components.IComponent;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.RoundedBoxFormat;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.RenderUtil;

public class ScrollableComponentVertical extends ComponentPane implements IScrollable
{
	public int height = 0;
	protected float scroll = 0;
	protected float scrollSize = 0;
	public final Mesh border = new Mesh(), inside = new Mesh();
	public RoundedBoxFormat rect;
	public float h = 0;
	public ScrollBar scrollBar = null;
	
	public ScrollableComponentVertical(float x1, float y1, float x2, float y2)
	{
		super(x1, y1, x2, y2);
		setSize(x1, y1, x2, y2);
	}
	
	public void setScrollBar(ScrollBar scrollBar)
	{
		this.scrollBar = scrollBar;
		scrollBar.setMaxScroll();
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		h = y2 - y1;
		updateScrollSize();
		border.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		inside.setMesh(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0, 0, 0, 1, 1);
		rect = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1));
	}
	
	public void updateComponentSize()
	{
		float h = 0;
		for (IGuiElement el : this.guiElements)
		{
			float y = el.getY2();
			if (y > h) h = y;
		}
		height = MathUtil.ceil(h);
	}
	
	public void updateScrollSize()
	{
		scrollSize = height - h;
		if (scrollSize < 0) scroll = scrollSize = 0;
		else if (scroll > scrollSize) scroll = scrollSize;
		if (scrollBar != null) scrollBar.setMaxScroll();
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my + scroll, button, mods);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		super.onMouseReleased(mx, my + scroll, button, mods);
	}

	@Override
	public void onDrag(float mx, float my)
	{
		super.onDrag(mx, my + scroll);
	}
	
	@Override
	public IComponent getHovered(float mx, float my)
	{
		return super.getHovered(mx, my + scroll);
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		if (scrollSize > 0) return true;
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollV(mx, my + scroll));
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY)
	{
		IComponent hovered = getHovered(mx, my);
		boolean hScrollV;
		if (hovered != null)
		{
			hScrollV = hovered.canScrollV(mx, my + scroll);
		}
		else hScrollV = false;
		if (hScrollV) hovered.onMouseScroll(mx, my + scroll, scrollX, scrollY);
		if (!hScrollV)
		{
			scroll -= scrollY * 4;
			if (scroll < 0) scroll = 0;
			else if (scroll > scrollSize) scroll = scrollSize;
		}
		//TODO if (mouseDown) onDrag(mx, my); //Because it's kinda a drag XD
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		getTheme().bindRoundedBox(rect);
		border.render();
		main.textureManager.unbindTexture();
		main.shader.setColor(1, 1, 1, 1);
		RenderUtil.pushStencil();
		RenderUtil.startStencil(false);
		inside.render();
		RenderUtil.endStencil();
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(0, -scroll, 0);
		main.shader.updateModel();
		super.render(mx, my + scroll, canHover);
		Shader.MODEL.pop();
		main.shader.updateModel();
		//TODO rendering
		RenderUtil.popStencil();
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my + scroll, deltaTime);
	}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) 
	{
		super.onMouseRepeat(mx, my + scroll, button, mods);
	}
	
	@Override
	public long getCursor(float mx, float my)
	{
		return super.getCursor(mx, my + scroll);
	}

	@Override
	public float getMaxScroll()
	{
		return scrollSize;
	}

	@Override
	public float getScroll()
	{
		return scroll;
	}

	@Override
	public void setScroll(float scroll)
	{
		this.scroll = scroll;
	}
}