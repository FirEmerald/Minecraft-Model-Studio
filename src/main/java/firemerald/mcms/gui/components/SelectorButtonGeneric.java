package firemerald.mcms.gui.components;

import java.util.function.BiFunction;

import org.joml.Vector4i;

import firemerald.mcms.api.util.TriFunction;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.popups.GuiPopupSelector;

public class SelectorButtonGeneric<T> extends StandardButton
{
	public T[] values;
	
	public SelectorButtonGeneric(int x1, int y1, int x2, int y2, String text, T[] values, BiFunction<Integer, T, String> action, TriFunction<T, Vector4i, Runnable, IGuiElement> newButton)
	{
		this(x1, y1, x2, y2, 1, 0, text, values, action, null, newButton);
	}
	
	public SelectorButtonGeneric(int x1, int y1, int x2, int y2, int outline, int radius, String text, T[] values, BiFunction<Integer, T, String> action, TriFunction<T, Vector4i, Runnable, IGuiElement> newButton)
	{
		this(x1, y1, x2, y2, outline, radius, text, values, action, null, newButton);
	}
	
	public SelectorButtonGeneric(int x1, int y1, int x2, int y2, String text, T[] values, BiFunction<Integer, T, String> action, Runnable onCancel, TriFunction<T, Vector4i, Runnable, IGuiElement> newButton)
	{
		this(x1, y1, x2, y2, 1, 0, text, values, action, onCancel, newButton);
	}
	
	public SelectorButtonGeneric(int x1, int y1, int x2, int y2, int outline, int radius, String text, T[] values, BiFunction<Integer, T, String> action, Runnable onCancel, TriFunction<T, Vector4i, Runnable, IGuiElement> newButton)
	{
		super(x1, y1, x2, y2, outline, radius, text, () -> {});
		this.onRelease = () -> {
			if (this.values.length != 0) new GuiPopupSelector(this, this.values, (ind, val) -> setText(action.apply(ind, val)), onCancel, newButton).activate();
		};
		this.values = values;
	}
	
	public void setValues(T[] values)
	{
		this.values = values;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
	}

	@Override
	public boolean isEnabled()
	{
		return values.length > 0 && super.isEnabled();
	}
}