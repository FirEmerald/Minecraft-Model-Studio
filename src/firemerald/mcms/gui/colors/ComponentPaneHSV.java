package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSV;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentPaneHSV extends ComponentPaneColorPicker
{
	public final ColorPickerHSV hsvPicker;
	public final ComponentTextInt hHSV, sHSV, vHSV;
	
	public ComponentPaneHSV(int x1, int y1, int x2, int y2, ColorModel color)
	{
		super(x1, y1, x2, y2);
		HSV hsv = color.getHSV();
		this.addElement(hsvPicker = new ColorPickerHSV(0, 0, hsv));
		FontRenderer font = Main.instance.fontMsg;
		this.addElement(new ComponentFloatingLabel(160, 00, 172, 20, font, "H"));
		this.addElement(hHSV = new ComponentTextInt(172, 0, 206, 20, font, 0, 360));
		this.addElement(new ComponentIncrementInt(206, 0, hHSV, 1));
		this.addElement(new ComponentIncrementInt(206, 10, hHSV, -1));
		this.addElement(new ComponentFloatingLabel(160, 20, 172, 40, font, "S"));
		this.addElement(sHSV = new ComponentTextInt(172, 20, 206, 40, font, 0, 100));
		this.addElement(new ComponentIncrementInt(206, 20, sHSV, 1));
		this.addElement(new ComponentIncrementInt(206, 30, sHSV, -1));
		this.addElement(new ComponentFloatingLabel(160, 40, 172, 60, font, "V"));
		this.addElement(vHSV = new ComponentTextInt(172, 40, 206, 60, font, 0, 100));
		this.addElement(new ComponentIncrementInt(206, 40, vHSV, 1));
		this.addElement(new ComponentIncrementInt(206, 50, vHSV, -1));
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