package firemerald.mcms.gui.components;

import java.util.function.BiConsumer;

import firemerald.mcms.gui.popups.GuiPopupSelector;
import firemerald.mcms.theme.EnumDirection;

public class DropdownButton extends ElementButton
{
	public String[] values;
	public int dx1, dy1, dx2, dy2;
	
	public DropdownButton(int x1, int y1, int x2, int y2, String[] values, BiConsumer<Integer, String> action)
	{
		this(x1, y1, x2, y2, x1, y1, x2, y2, values, action, null);
	}
	
	public DropdownButton(int x1, int y1, int x2, int y2, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		this(x1, y1, x2, y2, x1, y1, x2, y2, values, action, onCancel);
	}
	
	public DropdownButton(int x1, int y1, int x2, int y2, int dx1, int dy1, int dx2, int dy2, String[] values, BiConsumer<Integer, String> action)
	{
		this(x1, y1, x2, y2, dx1, dy1, dx2, dy2, values, action, null);
	}
	
	public DropdownButton(int x1, int y1, int x2, int y2, int dx1, int dy1, int dx2, int dy2, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		super(x1, y1, x2, y2, (w, h, theme) -> {
			float bx1 = w * .2f, bx2 = w * .8f;
			float by1 = h * .35f, by2 = h * .65f;
			return theme.genArrowedButton(w.intValue(), h.intValue(), 1, 0, bx1, by1, bx2, by2, EnumDirection.DOWN);
		}, () -> {});
		this.dx1 = dx1;
		this.dy1 = dy1;
		this.dx2 = dx2;
		this.dy2 = dy2;
		this.onRelease = () -> {
			if (this.values.length != 0) new GuiPopupSelector(this.getHolderOffsetX() + this.dx1, this.getHolderOffsetY() + this.dy1, this.getHolderOffsetX() + this.dx2, this.getHolderOffsetY() + this.dy2, this.values, action, onCancel).activate();
		};
		this.values = values;
	}
	
	public void setValues(String[] values)
	{
		this.values = values;
	}
	
	public void setSize(int x1, int y1, int x2, int y2, int dx1, int dy1, int dx2, int dy2)
	{
		this.setSize(x1, y1, x2, y2);
		this.dx1 = dx1;
		this.dy1 = dy1;
		this.dx2 = dx2;
		this.dy2 = dy2;
	}

	@Override
	public boolean isEnabled()
	{
		return values.length > 0 && super.isEnabled();
	}
}