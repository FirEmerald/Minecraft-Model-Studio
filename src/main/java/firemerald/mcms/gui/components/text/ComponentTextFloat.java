package firemerald.mcms.gui.components.text;

import firemerald.mcms.texture.Color;
import firemerald.mcms.util.FloatConsumer;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentTextFloat extends ComponentText
{
	private float initialVal;
	protected float val;
	protected float min, max;
	protected boolean error = false;
	private final FloatConsumer onValueChange;
	private final String ambient;
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, float min, float max, FloatConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, val, min, max, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float min, float max, FloatConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, FloatConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, FloatConsumer onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, null);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, float min, float max, FloatConsumer onValueChange, String ambient)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		this.setValNoUpdate(val);
		this.onValueChange = onValueChange;
		this.ambient = ambient;
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float min, float max, FloatConsumer onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, float val, FloatConsumer onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, val, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, ambient);
	}
	
	public ComponentTextFloat(int x1, int y1, int x2, int y2, FontRenderer font, FloatConsumer onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, onValueChange, ambient);
	}
	
	@Override
	public boolean shouldUndo()
	{
		return super.shouldUndo() || this.onValueChange != null;
	}
	
	public void setValNoUpdate(float val)
	{
		this.setTextNoUpdate(Float.toString(this.initialVal = this.val = val));
	}
	
	public void setVal(float val)
	{
		this.setText(Float.toString(this.initialVal = this.val = val));
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
	public void onTextUpdateAction()
	{
		super.onTextUpdateAction();
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

	@Override
	public Runnable getUndo()
	{
		final Runnable sup = super.getUndo();
		final FloatConsumer onValueChange = this.onValueChange;
		final float initialVal = this.initialVal;
		return () -> {
			sup.run();
			if (onValueChange != null) onValueChange.accept(initialVal);
		};
	}

	@Override
	public Runnable getRedo()
	{
		final Runnable sup = super.getRedo();
		final FloatConsumer onValueChange = this.onValueChange;
		final float val = this.val;
		return () -> {
			sup.run();
			if (onValueChange != null) onValueChange.accept(val);
		};
	}
}