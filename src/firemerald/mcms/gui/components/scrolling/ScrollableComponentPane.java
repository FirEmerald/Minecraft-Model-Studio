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

public class ScrollableComponentPane extends ComponentPane implements IScrollable, IScrollableHorizontal
{
	public int height = 0;
	protected float scroll = 0;
	protected float scrollSize = 0;
	public int width = 0;
	protected float scrollH = 0;
	protected float scrollSizeH = 0;
	public final Mesh border = new Mesh(), inside = new Mesh();
	public RoundedBoxFormat rect;
	public float h = 0, w = 0;
	public ScrollBar scrollBar = null;
	public ScrollBarH scrollBarH = null;
	
	public ScrollableComponentPane(float x1, float y1, float x2, float y2)
	{
		super(x1, y1, x2, y2);
		setSize(x1, y1, x2, y2);
	}
	
	public void setScrollBar(ScrollBar scrollBar)
	{
		this.scrollBar = scrollBar;
		scrollBar.setMaxScroll();
	}
	
	public void setScrollBarH(ScrollBarH scrollBar)
	{
		this.scrollBarH = scrollBar;
		scrollBar.setMaxScroll();
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		h = y2 - y1;
		w = x2 - x1;
		updateScrollSize();
		border.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		inside.setMesh(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0, 0, 0, 1, 1);
		rect = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1));
	}
	
	public void updateComponentSize()
	{
		float w = 0, h = 0;
		for (IGuiElement el : this.guiElements)
		{
			float x = el.getX2();
			if (x > w) w = x;
			float y = el.getY2();
			if (y > h) h = y;
		}
		height = MathUtil.ceil(h);
		width = MathUtil.ceil(w);
	}
	
	public void updateScrollSize()
	{
		scrollSize = height - h;
		if (scrollSize < 0) scroll = scrollSize = 0;
		else if (scroll > scrollSize) scroll = scrollSize;
		if (scrollBar != null) scrollBar.setMaxScroll();

		scrollSizeH = width - w;
		if (scrollSizeH < 0) scrollH = scrollSizeH = 0;
		else if (scrollH > scrollSizeH) scrollH = scrollSizeH;
		if (scrollBarH != null) scrollBarH.setMaxScroll();
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx + scrollH, my + scroll, button, mods);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		super.onMouseReleased(mx + scrollH, my + scroll, button, mods);
	}

	@Override
	public void onDrag(float mx, float my)
	{
		super.onDrag(mx + scrollH, my + scroll);
	}
	
	@Override
	public IComponent getHovered(float mx, float my)
	{
		return super.getHovered(mx + scrollH, my + scroll);
	}
	
	@Override
	public boolean canScrollH(float mx, float my)
	{
		if (scrollSizeH > 0) return true;
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollH(mx + scrollH, my + scroll));
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		if (scrollSize > 0) return true;
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollV(mx + scrollH, my + scroll));
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY)
	{
		IComponent hovered = getHovered(mx, my);
		boolean hScrollH, hScrollV;
		if (hovered != null)
		{
			hScrollH = hovered.canScrollH(mx + scrollH, my + scroll);
			hScrollV = hovered.canScrollV(mx + scrollH, my + scroll);
		}
		else hScrollH = hScrollV = false;
		if (hScrollH || hScrollV) hovered.onMouseScroll(mx + scrollH, my + scroll, scrollX, scrollY);
		if (!hScrollH)
		{
			scrollH -= scrollX * 4;
			if (scrollH < 0) scrollH = 0;
			else if (scrollH > scrollSizeH) scrollH = scrollSizeH;
		}
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
		Shader.MODEL.matrix().translate(-scrollH, -scroll, 0);
		main.shader.updateModel();
		super.render(mx + scrollH, my + scroll, canHover);
		Shader.MODEL.pop();
		main.shader.updateModel();
		//TODO rendering
		RenderUtil.popStencil();
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx + scrollH, my + scroll, deltaTime);
	}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) 
	{
		super.onMouseRepeat(mx + scrollH, my + scroll, button, mods);
	}
	
	@Override
	public long getCursor(float mx, float my)
	{
		return super.getCursor(mx + scrollH, my + scroll);
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

	@Override
	public float getMaxScrollH()
	{
		return scrollSizeH;
	}

	@Override
	public float getScrollH()
	{
		return scrollH;
	}

	@Override
	public void setScrollH(float scrollH)
	{
		this.scrollH = scrollH;
	}
}