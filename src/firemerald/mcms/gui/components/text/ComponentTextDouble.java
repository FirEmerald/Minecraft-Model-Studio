package firemerald.mcms.gui.components.text;

import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.FontRenderer;

public class ComponentTextDouble extends ComponentText
{
	public static ComponentTextDouble makeDoubleControl(GuiElementContainer container, float x, float y, float w, double val, double min, double max, double increment, Consumer<Double> onValueChange)
	{
		ComponentTextDouble text;
		container.addElement(text = new ComponentTextDouble(x, y, x + w - 10, y + 20, Main.instance.fontMsg, val, min, max, onValueChange));
		container.addElement(new ComponentIncrementDouble(x + w - 10, y, text, 1));
		container.addElement(new ComponentIncrementDouble(x + w - 10, y + 10, text, -1));
		return text;
	}
	
	public static ComponentTextDouble makeDoubleControl(GuiElementContainer container, float x, float y, float w, double val, double min, double max, double increment)
	{
		return makeDoubleControl(container, x, y, w, val, min, max, increment, null);
	}
	
	protected double val;
	protected double min, max;
	protected boolean error = false;
	private final Consumer<Double> onValueChange;
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font, double val, double min, double max, Consumer<Double> onValueChange)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		setVal(val);
		this.onValueChange = onValueChange;
	}
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font, double min, double max, Consumer<Double> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange);
	}
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font, double val, Consumer<Double> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, onValueChange);
	}
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font, Consumer<Double> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, onValueChange);
	}
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font, double val, double min, double max)
	{
		this(x1, y1, x2, y2, font, val, min, max, null);
	}
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font, double min, double max)
	{
		this(x1, y1, x2, y2, font, 0, min, max);
	}
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font, double val)
	{
		this(x1, y1, x2, y2, font, val, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}
	
	public ComponentTextDouble(float x1, float y1, float x2, float y2, FontRenderer font)
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