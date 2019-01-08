package firemerald.mcms.gui.components.text;

import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.FontRenderer;

public class ComponentTextFloat extends ComponentText
{
	public static ComponentTextFloat makeFloatControl(GuiElementContainer container, float x, float y, float w, float val, float min, float max, float increment, Consumer<Float> onValueChange)
	{
		ComponentTextFloat text;
		container.addElement(text = new ComponentTextFloat(x, y, x + w - 10, y + 20, Main.instance.fontMsg, val, min, max, onValueChange));
		container.addElement(new ComponentIncrementFloat(x + w - 10, y, text, increment));
		container.addElement(new ComponentIncrementFloat(x + w - 10, y + 10, text, -increment));
		return text;
	}
	
	public static ComponentTextFloat makeFloatControl(GuiElementContainer container, float x, float y, float w, float val, float min, float max, float increment)
	{
		return makeFloatControl(container, x, y, w, val, min, max, increment, null);
	}
	
	protected float val;
	protected float min, max;
	protected boolean error = false;
	private final Consumer<Float> onValueChange;
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font, float val, float min, float max, Consumer<Float> onValueChange)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		setVal(val);
		this.onValueChange = onValueChange;
	}
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font, float min, float max, Consumer<Float> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange);
	}
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font, float val, Consumer<Float> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange);
	}
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font, Consumer<Float> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange);
	}
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font, float val, float min, float max)
	{
		this(x1, y1, x2, y2, font, val, min, max, null);
	}
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font, float min, float max)
	{
		this(x1, y1, x2, y2, font, 0, min, max);
	}
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font, float val)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}
	
	public ComponentTextFloat(float x1, float y1, float x2, float y2, FontRenderer font)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}
	
	public void setVal(float val)
	{
		this.val = val;
		this.setText(Float.toString(val));
	}
	
	public float getVal()
	{
		return val;
	}
	
	public void setBounds(float min, float max)
	{
		this.min = min;
		this.max = max;
		if (val < min) val = min;
		else if (val > max) val = max;
	}
	
	public float getMin()
	{
		return min;
	}
	
	public float getMax()
	{
		return max;
	}
	
	@Override
	public void onTextUpdate()
	{
		super.onTextUpdate();
		try
		{
			val = Float.parseFloat(text);
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
		return error ? Color.RED : super.getTextColor();
	}
}