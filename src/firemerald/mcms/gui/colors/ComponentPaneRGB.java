package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.FontRenderer;

public class ComponentPaneRGB extends ComponentPaneColorPicker
{
	public final ColorPickerRGB rgbPicker;
	public final ComponentTextInt rRGB, gRGB, bRGB;
	
	public ComponentPaneRGB(float x1, float y1, float x2, float y2, ColorModel color)
	{
		super(x1, y1, x2, y2);
		RGB rgb = color.getRGB();
		guiElements.add(rgbPicker = new ColorPickerRGB(0, 0, rgb));
		FontRenderer font = Main.instance.fontMsg;
		guiElements.add(new ComponentLabel(160, 00, 172, 20, font, "R"));
		guiElements.add(rRGB = ComponentTextInt.makeIntControl(this, 172, 0, 44, 0, 0, 255, 1));
		guiElements.add(new ComponentLabel(160, 20, 172, 40, font, "G"));
		guiElements.add(gRGB = ComponentTextInt.makeIntControl(this, 172, 20, 44, 0, 0, 255, 1));
		guiElements.add(new ComponentLabel(160, 40, 172, 60, font, "B"));
		guiElements.add(bRGB = ComponentTextInt.makeIntControl(this, 172, 40, 44, 0, 0, 255, 1));
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