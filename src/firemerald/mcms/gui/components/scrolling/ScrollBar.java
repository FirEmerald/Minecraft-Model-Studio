package firemerald.mcms.gui.components.scrolling;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.gui.components.ComponentButton.ButtonState;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.BoxFormat;
import firemerald.mcms.theme.RoundedBoxFormat;

public class ScrollBar extends Component
{
	public final Mesh outline = new Mesh(), bar = new Mesh();
	private float scrollSize, scrollHeight, scrollBarSize;
	public boolean enabled = false;
	public final IScrollable scrollable;
	private float pY = 0;
	private boolean pressedScroll = false;
	private float pressedScrollVal;
	private RoundedBoxFormat rectangle;
	private BoxFormat scrollBarRectangle;
	
	public ScrollBar(float x1, float y1, float x2, float y2, IScrollable scrollable)
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
			scrollBarRectangle = new BoxFormat((int) (x2 - x1 - 2), (int) scrollBarSize, 0);
			scrollHeight = scrollSize - scrollBarSize;
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
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
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
	public void onDrag(float mx, float my)
	{
		if (pressedScroll)
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
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			pressedScroll = false;
		}
	}
}