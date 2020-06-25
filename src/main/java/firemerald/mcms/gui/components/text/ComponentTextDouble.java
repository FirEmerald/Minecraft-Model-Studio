package firemerald.mcms.gui.components.text;

import java.util.function.DoubleConsumer;

import firemerald.mcms.texture.Color;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentTextDouble extends ComponentText
{
	private double initialVal;
	protected double val;
	protected double min, max;
	protected boolean error = false;
	private final DoubleConsumer onValueChange;
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double val, double min, double max, DoubleConsumer onValueChange)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		this.setValNoUpdate(val);
		this.onValueChange = onValueChange;
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double min, double max, DoubleConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double val, DoubleConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, onValueChange);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, DoubleConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, onValueChange);
	}
	
	@Override
	public boolean shouldUndo()
	{
		return super.shouldUndo() || this.onValueChange != null;
	}

	public void setValNoUpdate(double val)
	{
		this.setTextNoUpdate(Double.toString(this.initialVal = this.val = val));
	}
	
	public void setVal(double val)
	{
		this.setText(Double.toString(this.initialVal = this.val = val));
	}
	
	public double getVal()
	{
		return val;
	}
	
	public void setBounds(double min, double max)
	{
		this.min = min;
		this.max = max;
		if (val < min) val = min;
		else if (val > max) val = max;
	}
	
	public double getMin()
	{
		return min;
	}
	
	public double getMax()
	{
		return max;
	}
	
	@Override
	public void onTextUpdateAction()
	{
		super.onTextUpdateAction();
		try
		{
			val = Double.parseDouble(text);
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
	public Color getTextColor()
	{
		return error ? Color.RED : getTheme().getTextColor();
	}

	@Override
	public Runnable getUndo()
	{
		final Runnable sup = super.getUndo();
		final DoubleConsumer onValueChange = this.onValueChange;
		final double initialVal = this.initialVal;
		return () -> {
			sup.run();
			if (onValueChange != null) onValueChange.accept(initialVal);
		};
	}

	@Override
	public Runnable getRedo()
	{
		final Runnable sup = super.getRedo();
		final DoubleConsumer onValueChange = this.onValueChange;
		final double val = this.val;
		return () -> {
			sup.run();
			if (onValueChange != null) onValueChange.accept(val);
		};
	}
}