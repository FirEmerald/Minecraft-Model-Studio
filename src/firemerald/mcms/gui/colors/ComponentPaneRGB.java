package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentPaneRGB extends ComponentPaneColorPicker
{
	public final ColorPickerRGB rgbPicker;
	public final ComponentTextInt rRGB, gRGB, bRGB;
	
	public ComponentPaneRGB(int x1, int y1, int x2, int y2, ColorModel color)
	{
		super(x1, y1, x2, y2);
		RGB rgb = color.getRGB();
		this.addElement(rgbPicker = new ColorPickerRGB(0, 0, rgb));
		FontRenderer font = Main.instance.fontMsg;
		this.addElement(new ComponentFloatingLabel(160, 00, 172, 20, font, "R"));
		this.addElement(rRGB = new ComponentTextInt(172, 0, 206, 20, font, 0, 255));
		this.addElement(new ComponentIncrementInt(206, 0, rRGB, 1));
		this.addElement(new ComponentIncrementInt(206, 10, rRGB, -1));
		this.addElement(new ComponentFloatingLabel(160, 20, 172, 40, font, "G"));
		this.addElement(gRGB = new ComponentTextInt(172, 20, 206, 40, font, 0, 255));
		this.addElement(new ComponentIncrementInt(206, 20, gRGB, 1));
		this.addElement(new ComponentIncrementInt(206, 30, gRGB, -1));
		this.addElement(new ComponentFloatingLabel(160, 40, 172, 60, font, "B"));
		this.addElement(bRGB = new ComponentTextInt(172, 40, 206, 60, font, 0, 255));
		this.addElement(new ComponentIncrementInt(206, 40, bRGB, 1));
		this.addElement(new ComponentIncrementInt(206, 50, bRGB, -1));
		setColor(rgb);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my, deltaTime);
		if (rgbPicker.hasChanged)
		{
			RGB rgb = rgbPicker.getColor().getRGB();
			rRGB.setVal(Math.round(rgb.r * 255));
			gRGB.setVal(Math.round(rgb.g * 255));
			bRGB.setVal(Math.round(rgb.b * 255));
			this.hasChanged = true;
		}
		else if (rRGB.hasChanged)
		{
			RGB rgb = rgbPicker.getColor().getRGB();
			rgb.r = rRGB.getVal() / 255f;
			rgbPicker.setFromColor(rgb);
			this.hasChanged = true;
		}
		else if (gRGB.hasChanged)
		{
			RGB rgb = rgbPicker.getColor().getRGB();
			rgb.g = gRGB.getVal() / 255f;
			rgbPicker.setFromColor(rgb);
			this.hasChanged = true;
		}
		else if (bRGB.hasChanged)
		{
			RGB rgb = rgbPicker.getColor().getRGB();
			rgb.b = bRGB.getVal() / 255f;
			rgbPicker.setFromColor(rgb);
			this.hasChanged = true;
		}
		rgbPicker.hasChanged = rRGB.hasChanged = gRGB.hasChanged = bRGB.hasChanged = false;
	}

	@Override
	public ColorModel getColor()
	{
		return rgbPicker.getColor();
	}

	@Override
	public void setColor(ColorModel color)
	{
		RGB rgb = color.getRGB();
		rgbPicker.setFromColor(rgb);
		rRGB.setVal(Math.round(rgb.r * 255));
		gRGB.setVal(Math.round(rgb.g * 255));
		bRGB.setVal(Math.round(rgb.b * 255));
	}
}