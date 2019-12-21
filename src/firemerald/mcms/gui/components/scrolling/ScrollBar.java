package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.gui.components.ComponentButton.ButtonState;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.MouseButtons;

public class ScrollBar extends Component
{
	public final Mesh outline = new Mesh(), bar = new Mesh();
	private float scrollSize, scrollHeight, scrollBarSize;
	public boolean enabled = false;
	public final IScrollable scrollable;
	private float pY = 0;
	private boolean pressedScroll = false;
	private float pressedScrollVal;
	private ThemeElement rectangle, scrollBarRectangle;
	
	public ScrollBar(int x1, int y1, int x2, int y2, IScrollable scrollable)
	{
		super(x1, y1, x2, y2);
		this.scrollable = scrollable;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		outline.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		scrollSize = y2 - y1 - 2;
		setMaxScroll();
	}
	
	public void setMaxScroll()
	{
		float size = scrollable.getMaxScroll();
		if (size <= 0)
		{
			enabled = false;
		}
		else
		{
			enabled = true;
			scrollBarSize = scrollSize * scrollSize / (scrollSize + size);
			if (scrollBarSize < 10) scrollBarSize = 10;
			bar.setMesh(x1 + 1, y1 + 1, x2 - 1, y1 + 1 + scrollBarSize, 0, 0, 0, 1, 1);
			scrollHeight = scrollSize - scrollBarSize;
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
			if (enabled) scrollBarRectangle = getTheme().genScrollBar(x2 - x1 - 2, (int) scrollBarSize, 1);
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
			float scroll = scrollable.getScroll();
			float max = scrollable.getMaxScroll();
			ButtonState state;
			if (pressedScroll) state = ButtonState.PUSH;
			else
			{
				if (canHover && mx >= x1 + 1 && mx < x2 - 1)
				{
					float scrollPos = y1 + 1 + scrollHeight * scroll / max;
					if (my >= scrollPos && my < scrollPos + scrollBarSize) state = ButtonState.HOVER;
					else state = ButtonState.NONE;
				}
				else state = ButtonState.NONE;
			}
			state.applyButtonEffects();
			Shader.MODEL.push();
			Shader.MODEL.matrix().translate(0, scrollHeight * scroll / max, 0);
			main.shader.updateModel();
			scrollBarRectangle.bind();
			bar.render();
			Shader.MODEL.pop();
			main.shader.updateModel();
			state.removeButtonEffects();
		}
		else
		{
			ButtonState.DISABLED.applyButtonEffects();
			outline.render();
			ButtonState.DISABLED.removeButtonEffects();
		}
		main.shader.setColor(1, 1, 1, 1);
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY)
	{
		float size = scrollable.getMaxScroll();
		float scroll = scrollable.getScroll();
		scroll -= scrollY * 4;
		if (scroll < 0) scroll = 0;
		else if (scroll > size) scroll = size;
		scrollable.setScroll(scroll);
	}
	
	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
		{
			pY = my;
			if (mx >= x1 + 1 && mx < x2 - 1)
			{
				float scroll = scrollable.getScroll();
				float scrollPos = y1 + 1 + scrollHeight * scroll / scrollable.getMaxScroll();
				if (my >= scrollPos && my < scrollPos + scrollBarSize)
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
			float dY = my - pY;
			float max = scrollable.getMaxScroll();
			float scroll = pressedScrollVal + dY * max / scrollHeight;
			if (scroll < 0) scroll = 0;
			else if (scroll > max) scroll = max;
			scrollable.setScroll(scroll);
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