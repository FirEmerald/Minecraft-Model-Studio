package firemerald.mcms.gui.components.scrolling;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.gui.components.ComponentButton.ButtonState;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.BoxFormat;
import firemerald.mcms.theme.RoundedBoxFormat;

public class ScrollBarH extends Component
{
	public final Mesh outline = new Mesh(), bar = new Mesh();
	private float scrollSize, scrollWidth, scrollBarSize;
	public boolean enabled = false;
	public final IScrollableHorizontal scrollable;
	private float pX = 0;
	private boolean pressedScroll = false;
	private float pressedScrollVal;
	private RoundedBoxFormat rectangle;
	private BoxFormat scrollBarRectangle;
	
	public ScrollBarH(float x1, float y1, float x2, float y2, IScrollableHorizontal scrollable)
	{
		super(x1, y1, x2, y2);
		this.scrollable = scrollable;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		outline.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		rectangle = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1));
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
			scrollBarSize = scrollSize * scrollSize / (scrollSize + size);
			if (scrollBarSize < 10) scrollBarSize = 10;
			bar.setMesh(x1 + 1, y1 + 1, x1 + 1 + scrollBarSize, y2 - 1, 0, 0, 0, 1, 1);
			scrollBarRectangle = new BoxFormat((int) scrollBarSize, (int) (y2 - y1 - 2), 0);
			scrollWidth = scrollSize - scrollBarSize;
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
		getTheme().bindRoundedBox(rectangle);
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
			Shader.MODEL.push();
			Shader.MODEL.matrix().translate(scrollWidth * scroll / max, 0, 0);
			main.shader.updateModel();
			getTheme().bindScrollBar(scrollBarRectangle);
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
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
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
	public void onDrag(float mx, float my)
	{
		if (pressedScroll)
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
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			pressedScroll = false;
		}
	}
}