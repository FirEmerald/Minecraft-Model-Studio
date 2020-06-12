package firemerald.mcms.texture.tools;

import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.texture.Texture;

public interface ITool
{
	public void onMouseClick(Texture tex, double u, double v, int button);

	public void onMouseDrag(Texture tex, double prevU, double prevV, double u, double v, int button, boolean isNewObject);

	public void onMouseRelease(Texture tex, double u, double v, int button);
	
	public void drawOnOverlay(Texture tex, double u, double v);
	
	public void onSelect(GuiSection options);
	
	public void onDeselect(GuiSection options);
}