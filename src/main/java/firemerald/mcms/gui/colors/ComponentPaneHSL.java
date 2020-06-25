package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSL;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentPaneHSL extends ComponentPaneColorPicker
{
	public final ColorPickerHSL hslPicker;
	public final ComponentTextInt hHSL, sHSL, lHSL;
	
	public ComponentPaneHSL(int x1, int y1, int x2, int y2, ColorModel color)
	{
		super(x1, y1, x2, y2);
		HSL hsl = color.getHSL();
		this.addElement(hslPicker = new ColorPickerHSL(0, 0, hsl));
		FontRenderer font = Main.instance.fontMsg;
		this.addElement(new ComponentFloatingLabel(160, 00, 172, 20, font, "H"));
		this.addElement(hHSL = new ComponentTextInt(172, 0, 206, 20, font, 0, 360, null));
		this.addElement(new ComponentIncrementInt(206, 0, hHSL, 1));
		this.addElement(new ComponentIncrementInt(206, 10, hHSL, -1));
		this.addElement(new ComponentFloatingLabel(160, 20, 172, 40, font, "S"));
		this.addElement(sHSL = new ComponentTextInt(172, 20, 206, 40, font, 0, 100, null));
		this.addElement(new ComponentIncrementInt(206, 20, sHSL, 1));
		this.addElement(new ComponentIncrementInt(206, 30, sHSL, -1));
		this.addElement(new ComponentFloatingLabel(160, 40, 172, 60, font, "L"));
		this.addElement(lHSL = new ComponentTextInt(172, 40, 206, 60, font, 0, 100, null));
		this.addElement(new ComponentIncrementInt(206, 40, lHSL, 1));
		this.addElement(new ComponentIncrementInt(206, 50, lHSL, -1));
		
		setColor(hsl);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my, deltaTime);
		if (hslPicker.hasChanged)
		{
			HSL hsl = hslPicker.getColor().getHSL();
			hHSL.setVal(Math.round(hsl.h * 360));
			sHSL.setVal(Math.round(hsl.s * 100));
			lHSL.setVal(Math.round(hsl.l * 100));
			this.hasChanged = true;
		}
		else if (hHSL.hasChanged)
		{
			HSL hsl = hslPicker.getColor().getHSL();
			hsl.h = hHSL.getVal() / 360f;
			hslPicker.setFromColor(hsl);
			this.hasChanged = true;
		}
		else if (sHSL.hasChanged)
		{
			HSL hsl = hslPicker.getColor().getHSL();
			hsl.s = sHSL.getVal() / 100f;
			hslPicker.setFromColor(hsl);
			this.hasChanged = true;
		}
		else if (lHSL.hasChanged)
		{
			HSL hsl = hslPicker.getColor().getHSL();
			hsl.l = lHSL.getVal() / 100f;
			hslPicker.setFromColor(hsl);
			this.hasChanged = true;
		}
		hslPicker.hasChanged = hHSL.hasChanged = sHSL.hasChanged = lHSL.hasChanged = false;
	}

	@Override
	public ColorModel getColor()
	{
		return hslPicker.getColor();
	}

	@Override
	public void setColor(ColorModel color)
	{
		HSL hsl = color.getHSL();
		hslPicker.setFromColor(hsl);
		hHSL.setVal(Math.round(hsl.h * 360));
		sHSL.setVal(Math.round(hsl.s * 100));
		lHSL.setVal(Math.round(hsl.l * 100));
	}
}