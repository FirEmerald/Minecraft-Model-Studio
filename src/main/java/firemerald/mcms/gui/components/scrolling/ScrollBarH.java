package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.gui.components.ComponentButton.ButtonState;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.window.api.MouseButtons;

public class ScrollBarH extends Component
{
	public final GuiMesh outline = new GuiMesh(), bar = new GuiMesh();
	private float scrollSize, scrollWidth, scrollBarSize;
	public boolean enabled = false;
	public final IScrollableHorizontal scrollable;
	private float pX = 0;
	private boolean pressedScroll = false;
	private float pressedScrollVal;
	private ThemeElement rectangle, scrollBarRectangle;
	
	public ScrollBarH(int x1, int y1, int x2, int y2, IScrollableHorizontal scrollable)
	{
		super(x1, y1, x2, y2);
		this.scrollable = scrollable;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		outline.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
		scrollSize = x2 - x1 - 2;
		setMaxScroll();
	}
	
	public void setMaxScroll()
	{
		float size = scrollable.getMaxScrollH();
		if (size <= 0)
		{
			enabled = false;
		}
		else
		{
			enabled = true;
			scrollBarSize = (scrollSize == -size) ? 0 : scrollSize * scrollSize / (scrollSize + size);
			if (scrollBarSize < 10) scrollBarSize = 10;
			bar.setMesh(x1 + 1, y1 + 1, x1 + 1 + scrollBarSize, y2 - 1, 0, 0, 1, 1);
			scrollWidth = scrollSize - scrollBarSize;
		}
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rectangle != null) rectangle.release();
			rectangle = getTheme().genBox(x2 - x1, y2 - y1, 1);
			if (scrollBarRectangle != null) scrollBarRectangle.release();
			if (enabled) scrollBarRectangle = getTheme().genScrollBar((int) scrollBarSize, y2 - y1 - 2, 1);
			else scrollBarRectangle = null;
		}
	}
	
	@Override
	public boolean canScrollH(float mx, float my)
	{
		return enabled;
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		return enabled;
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		rectangle.bind();
		if (enabled)
		{
			outline.render();
			float scroll = scrollable.getScrollH();
			float max = scrollable.getMaxScrollH();
			ButtonState state;
			if (pressedScroll) state = ButtonState.PUSH;
			else
			{
				if (canHover && my >= y1 + 1 && my < y2 - 1)
				{
					float scrollPos = x1 + 1 + scrollWidth * scroll / max;
					if (my >= scrollPos && my < scrollPos + scrollBarSize) state = ButtonState.HOVER;
					else state = ButtonState.NONE;
				}
				else state = ButtonState.NONE;
			}
			state.applyButtonEffects();
			GuiShader.MODEL.push();
			GuiShader.MODEL.matrix().translate(scrollWidth * scroll / max, 0, 0);
			main.guiShader.updateModel();
			scrollBarRectangle.bind();
			bar.render();
			GuiShader.MODEL.pop();
			main.guiShader.updateModel();
			state.removeButtonEffects();
		}
		else
		{
			ButtonState.DISABLED.applyButtonEffects();
			outline.render();
			ButtonState.DISABLED.removeButtonEffects();
		}
		main.guiShader.setColor(1, 1, 1, 1);
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY)
	{
		float size = scrollable.getMaxScrollH();
		float scroll = scrollable.getScrollH();
		scroll -= scrollX * 4;
		if (scroll < 0) scroll = 0;
		else if (scroll > size) scroll = size;
		scrollable.setScrollH(scroll);
	}
	
	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
		{
			pX = mx;
			if (my >= y1 + 1 && my < y2 - 1)
			{
				float scroll = scrollable.getScrollH();
				float scrollPos = x1 + 1 + scrollWidth * scroll / scrollable.getMaxScrollH();
				if (mx >= scrollPos && mx < scrollPos + scrollBarSize)
				{
					pressedScroll = true;
					pressedScrollVal = scroll;
				}
			}
		}
	}
	
	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT && pressedScroll)
		{
			float dX = mx - pX;
			float max = scrollable.getMaxScrollH();
			float scroll = pressedScrollVal + dX * max / scrollWidth;
			if (scroll < 0) scroll = 0;
			else if (scroll > max) scroll = max;
			scrollable.setScrollH(scroll);
		}
	}
	
	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
		{
			pressedScroll = false;
		}
	}
}