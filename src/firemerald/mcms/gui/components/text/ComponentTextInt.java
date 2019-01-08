package firemerald.mcms.gui.components.text;

import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.FontRenderer;

public class ComponentTextInt extends ComponentText
{
	public static ComponentTextInt makeIntControl(GuiElementContainer container, float x, float y, float w, int val, int min, int max, int increment, Consumer<Integer> onValueChange)
	{
		ComponentTextInt text;
		container.addElement(text = new ComponentTextInt(x, y, x + w - 10, y + 20, Main.instance.fontMsg, val, min, max, onValueChange));
		container.addElement(new ComponentIncrementInt(x + w - 10, y, text, 1));
		container.addElement(new ComponentIncrementInt(x + w - 10, y + 10, text, -1));
		return text;
	}
	
	public static ComponentTextInt makeIntControl(GuiElementContainer container, float x, float y, float w, int val, int min, int max, int increment)
	{
		return makeIntControl(container, x, y, w, val, min, max, increment, null);
	}
	
	protected int val;
	protected int min, max;
	protected boolean error = false;
	private final Consumer<Integer> onValueChange;
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font, int val, int min, int max, Consumer<Integer> onValueChange)
	{
		super(x1, y1, x2, y2, font, null);
		setBounds(min, max);
		setVal(val);
		this.onValueChange = onValueChange;
	}
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font, int min, int max, Consumer<Integer> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, min, max, onValueChange);
	}
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font, int val, Consumer<Integer> onValueChange)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange);
	}
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font, Consumer<Integer> onValueChange)
	{
		this(x1, y1, x2, y2, font, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, onValueChange);
	}
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font, int val, int min, int max)
	{
		this(x1, y1, x2, y2, font, val, min, max, null);
	}
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font, int min, int max)
	{
		this(x1, y1, x2, y2, font, 0, min, max);
	}
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font, int val)
	{
		this(x1, y1, x2, y2, font, val, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public ComponentTextInt(float x1, float y1, float x2, float y2, FontRenderer font)
	{
		this(x1, y1, x2, y2, font, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
	
	@Override
	public Color getTextColor()
	{
		return error ? Color.RED : getTheme().getTextColor();
	}
}