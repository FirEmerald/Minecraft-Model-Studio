package firemerald.mcms.gui.components;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import firemerald.mcms.gui.popups.GuiPopupSelector;

public class SelectorButton extends StandardButton
{
	public String[] values;
	
	public SelectorButton(int x1, int y1, int x2, int y2, String text, String[] values, BiConsumer<Integer, String> action)
	{
		this(x1, y1, x2, y2, 1, 0, text, values, action, null);
	}
	
	public SelectorButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, String[] values, BiConsumer<Integer, String> action)
	{
		this(x1, y1, x2, y2, outline, radius, text, values, action, null);
	}
	
	public SelectorButton(int x1, int y1, int x2, int y2, String text, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		this(x1, y1, x2, y2, 1, 0, text, values, action, onCancel);
	}
	
	public SelectorButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		super(x1, y1, x2, y2, outline, radius, text, () -> {});
		this.onRelease = () -> {
			if (this.values.length != 0) new GuiPopupSelector(this, this.values, action.andThen((ind, val) -> setText(val)), onCancel).activate();
		};
		this.values = values;
	}
	
	public <E extends Enum<?>> SelectorButton(int x1, int y1, int x2, int y2, E value, E[] values, Consumer<E> onSelected)
	{
		this(x1, y1, x2, y2, 1, 0, value, values, onSelected);
	}
	
	public <E extends Enum<?>> SelectorButton(int x1, int y1, int x2, int y2, int outline, int radius, E value, E[] values, Consumer<E> onSelected)
	{
		super(x1, y1, x2, y2, outline, radius, value.name(), () -> {});
		this.onRelease = () -> {
			if (this.values.length != 0) new GuiPopupSelector(this, this.values, (index, name) -> {
				onSelected.accept(values[index]);
				this.setText(name);
			}, () -> {}).activate();
		};
		this.values = new String[values.length];
		for (int i = 0; i < values.length; i++) this.values[i] = values[i].name();
	}
	
	public SelectorButton(int x1, int y1, int x2, int y2, String text, String[] values, BiFunction<Integer, String, String> action)
	{
		this(x1, y1, x2, y2, 1, 0, text, values, action, null);
	}
	
	public SelectorButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, String[] values, BiFunction<Integer, String, String> action)
	{
		this(x1, y1, x2, y2, outline, radius, text, values, action, null);
	}
	
	public SelectorButton(int x1, int y1, int x2, int y2, String text, String[] values, BiFunction<Integer, String, String> action, Runnable onCancel)
	{
		this(x1, y1, x2, y2, 1, 0, text, values, action, onCancel);
	}
	
	public SelectorButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, String[] values, BiFunction<Integer, String, String> action, Runnable onCancel)
	{
		super(x1, y1, x2, y2, outline, radius, text, () -> {});
		this.onRelease = () -> {
			if (this.values.length != 0) new GuiPopupSelector(this, this.values, (ind, val) -> setText(action.apply(ind, val)), onCancel).activate();
		};
		this.values = values;
	}
	
	public void setValues(String[] values)
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