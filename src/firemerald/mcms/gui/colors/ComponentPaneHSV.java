package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSV;
import firemerald.mcms.util.FontRenderer;

public class ComponentPaneHSV extends ComponentPaneColorPicker
{
	public final ColorPickerHSV hsvPicker;
	public final ComponentTextInt hHSV, sHSV, vHSV;
	
	public ComponentPaneHSV(float x1, float y1, float x2, float y2, ColorModel color)
	{
		super(x1, y1, x2, y2);
		HSV hsv = color.getHSV();
		guiElements.add(hsvPicker = new ColorPickerHSV(0, 0, hsv));
		FontRenderer font = Main.instance.fontMsg;
		guiElements.add(new ComponentLabel(160, 00, 172, 20, font, "H"));
		guiElements.add(hHSV = ComponentTextInt.makeIntControl(this, 172, 0, 44, 0, 0, 360, 1));
		guiElements.add(new ComponentLabel(160, 20, 172, 40, font, "S"));
		guiElements.add(sHSV = ComponentTextInt.makeIntControl(this, 172, 20, 44, 0, 0, 100, 1));
		guiElements.add(new ComponentLabel(160, 40, 172, 60, font, "V"));
		guiElements.add(vHSV = ComponentTextInt.makeIntControl(this, 172, 40, 44, 0, 0, 100, 1));
		setColor(hsv);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my, deltaTime);
		if (hsvPicker.hasChanged)
		{
			HSV hsv = hsvPicker.getColor().getHSV();
			hHSV.setVal(Math.round(hsv.h * 360));
			sHSV.setVal(Math.round(hsv.s * 100));
			vHSV.setVal(Math.round(hsv.v * 100));
			this.hasChanged = true;
		}
		else if (hHSV.hasChanged)
		{
			HSV hsv = hsvPicker.getColor().getHSV();
			hsv.h = hHSV.getVal() / 360f;
			hsvPicker.setFromColor(hsv);
			this.hasChanged = true;
		}
		else if (sHSV.hasChanged)
		{
			HSV hsv = hsvPicker.getColor().getHSV();
			hsv.s = sHSV.getVal() / 100f;
			hsvPicker.setFromColor(hsv);
			this.hasChanged = true;
		}
		else if (vHSV.hasChanged)
		{
			HSV hsv = hsvPicker.getColor().getHSV();
			hsv.v = vHSV.getVal() / 100f;
			hsvPicker.setFromColor(hsv);
			this.hasChanged = true;
		}
		hsvPicker.hasChanged = hHSV.hasChanged = sHSV.hasChanged = vHSV.hasChanged = false;
	}

	@Override
	public ColorModel getColor()
	{
		return hsvPicker.getColor();
	}

	@Override
	public void setColor(ColorModel color)
	{
		HSV hsv = color.getHSV();
		hsvPicker.setFromColor(hsv);
		hHSV.setVal(Math.round(hsv.h * 360));
		sHSV.setVal(Math.round(hsv.s * 100));
		vHSV.setVal(Math.round(hsv.v * 100));
	}
}