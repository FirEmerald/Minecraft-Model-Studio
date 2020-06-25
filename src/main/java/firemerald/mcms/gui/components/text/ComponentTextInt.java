package firemerald.mcms.gui.components.text;

import java.util.function.IntConsumer;

import firemerald.mcms.texture.Color;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentTextInt extends ComponentText
{
	protected int val;
	protected int min, max;
	protected boolean error = false;
	private final IntConsumer onValueChange;
	private final String ambient;
	private int initialVal;
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int val, int min, int max, IntConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, val, min, max, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int min, int max, IntConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int val, IntConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, IntConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Integer val, int min, int max, IntConsumer onValueChange, String ambient)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		if (val != null) this.setValNoUpdate(val);
		else if (ambient == null) this.setValNoUpdate(0);
		else this.setTextNoUpdate("");
		this.onValueChange = onValueChange;
		this.ambient = ambient;
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int min, int max, IntConsumer onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, null, min, max, onValueChange, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Integer val, IntConsumer onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, IntConsumer onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, null, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, ambient);
	}
	
	@Override
	public boolean shouldUndo()
	{
		return super.shouldUndo() || this.onValueChange != null;
	}
	
	public void setValNoUpdate(int val)
	{
		this.setTextNoUpdate(Integer.toString(this.initialVal = this.val = val));
	}
	
	public void setVal(int val)
	{
		this.setText(Integer.toString(this.initialVal = this.val = val));
	}
	
	public int getVal()
	{
		return val;
	}
	
	public void setBounds(int min, int max)
	{
		this.min = min;
		this.max = max;
		if (val < min) val = min;
		else if (val > max) val = max;
	}
	
	public int getMin()
	{
		return min;
	}
	
	public int getMax()
	{
		return max;
	}
	
	@Override
	public void onTextUpdateAction()
	{
		super.onTextUpdateAction();
		try
		{
			val = Integer.parseInt(text);
			if (val < min)
			{
				val = min;
				error = true;
			}
			else if (val > max)
			{
				val = max;
				error = true;
			}
			else error = false;
			if (onValueChange != null) onValueChange.accept(val);
		}
		catch (Exception e)
		{
			error = true;
		}
	}

	@Override
	public Runnable getUndo()
	{
		final Runnable sup = super.getUndo();
		final IntConsumer onValueChange = this.onValueChange;
		final int initialVal = this.initialVal;
		return () -> {
			sup.run();
			if (onValueChange != null) onValueChange.accept(initialVal);
		};
	}

	@Override
	public Runnable getRedo()
	{
		final Runnable sup = super.getRedo();
		final IntConsumer onValueChange = this.onValueChange;
		final int val = this.val;
		return () -> {
			sup.run();
			if (onValueChange != null) onValueChange.accept(val);
		};
	}
	
	public boolean isValid()
	{
		return !error || (ambient != null && text.length() == 0);
	}
	
	@Override
	public String getDisplayString()
	{
		return text.length() == 0 && ambient != null ? ambient : super.getDisplayString();
	}
	
	@Override
	public Color getTextColor()
	{
		return text.length() == 0 && ambient != null ? new Color(super.getTextColor(), .25f) : error ? Color.RED : super.getTextColor();
	}
}