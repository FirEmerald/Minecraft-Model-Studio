package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSL;
import firemerald.mcms.util.FontRenderer;

public class ComponentPaneHSL extends ComponentPaneColorPicker
{
	public final ColorPickerHSL hslPicker;
	public final ComponentTextInt hHSL, sHSL, lHSL;
	
	public ComponentPaneHSL(float x1, float y1, float x2, float y2, ColorModel color)
	{
		super(x1, y1, x2, y2);
		HSL hsl = color.getHSL();
		guiElements.add(hslPicker = new ColorPickerHSL(0, 0, hsl));
		FontRenderer font = Main.instance.fontMsg;
		guiElements.add(new ComponentLabel(160, 00, 172, 20, font, "H"));
		guiElements.add(hHSL = ComponentTextInt.makeIntControl(this, 172, 0, 44, 0, 0, 360, 1));
		guiElements.add(new ComponentLabel(160, 20, 172, 40, font, "S"));
		guiElements.add(sHSL = ComponentTextInt.makeIntControl(this, 172, 20, 44, 0, 0, 100, 1));
		guiElements.add(new ComponentLabel(160, 40, 172, 60, font, "L"));
		guiElements.add(lHSL = ComponentTextInt.makeIntControl(this, 172, 40, 44, 0, 0, 100, 1));
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