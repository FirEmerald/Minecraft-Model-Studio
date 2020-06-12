package firemerald.mcms.gui.components.text;

import java.util.function.Consumer;

import firemerald.mcms.texture.Color;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentTextFloat extends ComponentText
{
	protected float val;
	protected float min, max;
	protected boolean error = false;
	private final Consumer<Float> onValueChange;
	private final String ambient;
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, float min, float max, Consumer<Float> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, min, max, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float min, float max, Consumer<Float> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, Consumer<Float> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, Consumer<Float> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, float min, float max)
	{
		this(x1, y1, x2, y2, font, val, min, max, null, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float min, float max)
	{
		this(x1, y1, x2, y2, font, 0, min, max, (String) null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, (String) null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, (String) null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, float min, float max, Consumer<Float> onValueChange, String ambient)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		setVal(val);
		this.onValueChange = onValueChange;
		this.ambient = ambient;
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float min, float max, Consumer<Float> onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, Consumer<Float> onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, Consumer<Float> onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, float min, float max, String ambient)
	{
		this(x1, y1, x2, y2, font, val, min, max, null, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float min, float max, String ambient)
	{
		this(x1, y1, x2, y2, font, 0, min, max, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, String ambient)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, String ambient)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, ambient);
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