package firemerald.mcms.gui.components.text;

import java.util.function.Consumer;

import firemerald.mcms.texture.Color;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentTextInt extends ComponentText
{
	protected int val;
	protected int min, max;
	protected boolean error = false;
	private final Consumer<Integer> onValueChange;
	private final String ambient;
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int val, int min, int max, Consumer<Integer> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, min, max, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int min, int max, Consumer<Integer> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int val, Consumer<Integer> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Consumer<Integer> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int val, int min, int max)
	{
		this(x1, y1, x2, y2, font, val, min, max, null, null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int min, int max)
	{
		this(x1, y1, x2, y2, font, 0, min, max, (String) null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int val)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE, (String) null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font)
	{
		this(x1, y1, x2, y2, font, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, (String) null);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Integer val, int min, int max, Consumer<Integer> onValueChange, String ambient)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		if (val != null) setVal(val);
		else if (ambient == null) setVal(0);
		else setText("");
		this.onValueChange = onValueChange;
		this.ambient = ambient;
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int min, int max, Consumer<Integer> onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, null, min, max, onValueChange, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Integer val, Consumer<Integer> onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Consumer<Integer> onValueChange, String ambient)
	{
		this(x1, y1, x2, y2, font, null, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Integer val, int min, int max, String ambient)
	{
		this(x1, y1, x2, y2, font, val, min, max, null, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, int min, int max, String ambient)
	{
		this(x1, y1, x2, y2, font, (Integer) null, min, max, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, Integer val, String ambient)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE, ambient);
	}
	
	public ComponentTextInt(int x1, int y1, int x2, int y2, FontRenderer font, String ambient)
	{
		this(x1, y1, x2, y2, font, (Integer) null, Integer.MIN_VALUE, Integer.MAX_VALUE, ambient);
	}
	
	public void setVal(int val)
	{
		this.val = val;
		this.setText(Integer.toString(val));
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
	public void onTextUpdate()
	{
		super.onTextUpdate();
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