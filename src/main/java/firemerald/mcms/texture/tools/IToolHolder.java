package firemerald.mcms.texture.tools;

import firemerald.mcms.texture.BlendMode;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.ColorModel;

public interface IToolHolder
{
	public Color getColor1();
	
	public Color getColor2();

	public void setColor2(ColorModel color);
	
	public void setColor1(ColorModel color);
	
	public void setAlpha(float alpha);
	
	public BlendMode getBlendMode();
	
	public void setBlendMode(BlendMode mode);
}