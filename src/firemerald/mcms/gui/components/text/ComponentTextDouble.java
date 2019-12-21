package firemerald.mcms.gui.components.text;

import java.util.function.Consumer;

import firemerald.mcms.texture.Color;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentTextDouble extends ComponentText
{
	protected double val;
	protected double min, max;
	protected boolean error = false;
	private final Consumer<Double> onValueChange;
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double val, double min, double max, Consumer<Double> onValueChange)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		setVal(val);
		this.onValueChange = onValueChange;
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double min, double max, Consumer<Double> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double val, Consumer<Double> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, onValueChange);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, Consumer<Double> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, onValueChange);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double val, double min, double max)
	{
		this(x1, y1, x2, y2, font, val, min, max, null);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double min, double max)
	{
		this(x1, y1, x2, y2, font, 0, min, max);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font, double val)
	{
		this(x1, y1, x2, y2, font, val, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}
	
	public ComponentTextDouble(int x1, int y1, int x2, int y2, FontRenderer font)
	{
		this(x1, y1, x2, y2, font, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}
	
	public void setVal(double val)
	{
		this.val = val;
		this.setText(Double.toString(val));
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
	public void onTextUpdate()
	{
		super.onTextUpdate();
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
}