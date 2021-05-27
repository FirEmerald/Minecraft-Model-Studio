package firemerald.mcms.texture.tools;

import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.main.components.model.ComponentModelViewer;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.Material;
import firemerald.mcms.window.api.MouseButtons;

public class ToolLight implements ITool
{
	public static final ToolLight INSTANCE = new ToolLight();
	
	@Override
	public boolean onModelViewClick(ComponentModelViewer viewer, float mx, float my, int button, int mods)
	{
		return button == MouseButtons.MIDDLE ? viewer.processModelViewClick(mx, my, button, mods) : button == MouseButtons.RIGHT && viewer.processLightViewClick(mx, my, button, mods);
	}

	@Override
	public boolean onModelViewDrag(ComponentModelViewer viewer, float mx, float my, int button)
	{
		return false;
	}

	@Override
	public boolean onModelViewRelease(ComponentModelViewer viewer, float mx, float my, int button)
	{
		return false;
	}
	
	@Override
	public void onMouseClick(Material mat, double u, double v, int button) {}

	@Override
	public void onMouseDrag(Material mat, double prevU, double prevV, double u, double v, int button, boolean isNewObject) {}

	@Override
	public void onMouseRelease(Material mat, double u, double v, int button) {}
	
	@Override
	public void drawOnOverlay(Texture tex, double u, double v) {}

	@Override
	public void onSelect(GuiSection options) {}

	@Override
	public void onDeselect(GuiSection options) {}
}