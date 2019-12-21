package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.Main;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.gui.components.IComponent;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.Cursor;

public class ScrollableComponentPaneVertical extends ComponentPane implements IScrollable
{
	public int height = 0;
	protected float scroll = 0;
	protected float scrollSize = 0;
	public final Mesh border = new Mesh();
	public ThemeElement rect;
	public float h = 0;
	public ScrollBar scrollBar = null;
	
	public ScrollableComponentPaneVertical(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2, 1);
		setSize(x1, y1, x2, y2);
	}
	
	public void setScrollBar(ScrollBar scrollBar)
	{
		this.scrollBar = scrollBar;
		scrollBar.setMaxScroll();
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		h = y2 - y1;
		updateScrollSize();
		border.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(x2 - x1, y2 - y1, 1);
		}
	}
	
	public void updateComponentSize()
	{
		float h = 0;
		for (IGuiElement el : this.getElementsCopy())
		{
			float y = el.getY2();
			if (y > h) h = y;
		}
		height = MathUtil.ceil(h) + margin * 2;
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
	public void onDrag(float mx, float my, int button)
	{
		super.onDrag(mx, my + scroll, button);
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
		rect.bind();
		border.render();
		super.render(mx, my + scroll, canHover);
		Shader.MODEL.pop();
		main.shader.updateModel();
	}
	
	@Override
	public void renderStencilArea()
	{
		Main.instance.textureManager.unbindTexture();
		inside.render();
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(0, -scroll, 0);
		Main.instance.shader.updateModel();
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
	public Cursor getCursor(float mx, float my)
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
	
	@Override
	public int getComponentOffsetY()
	{
		return super.getComponentOffsetY() + (int) Math.floor(scroll);
	}
}