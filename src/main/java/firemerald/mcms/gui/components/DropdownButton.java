package firemerald.mcms.gui.components;

import java.util.function.BiConsumer;

import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.popups.GuiPopupSelector;
import firemerald.mcms.theme.EnumDirection;

public class DropdownButton extends ElementButton
{
	public String[] values;
	public IGuiElement from;
	
	public DropdownButton(int x1, int y1, int x2, int y2, String[] values, BiConsumer<Integer, String> action)
	{
		this(x1, y1, x2, y2, null, values, action, null);
	}
	
	public DropdownButton(int x1, int y1, int x2, int y2, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		this(x1, y1, x2, y2, null, values, action, onCancel);
	}
	
	public DropdownButton(int x1, int y1, int x2, int y2, IGuiElement from, String[] values, BiConsumer<Integer, String> action)
	{
		this(x1, y1, x2, y2, from, values, action, null);
	}
	
	public DropdownButton(int x1, int y1, int x2, int y2, IGuiElement from, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		super(x1, y1, x2, y2, (w, h, theme) -> {
			float bx1 = w * .2f, bx2 = w * .8f;
			float by1 = h * .35f, by2 = h * .65f;
			return theme.genArrowedButton(w.intValue(), h.intValue(), 1, 0, bx1, by1, bx2, by2, EnumDirection.DOWN);
		}, () -> {});
		this.from = from == null ? this : from;
		this.onRelease = () -> {
			if (this.values.length != 0) new GuiPopupSelector(from, this.values, action, onCancel).activate();
		};
		this.values = values;
	}
	
	public void setValues(String[] values)
	{
		this.values = values;
	}

	@Override
	public boolean isEnabled()
	{
		return values.length > 0 && super.isEnabled();
	}
}