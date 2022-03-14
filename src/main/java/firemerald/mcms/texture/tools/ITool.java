package firemerald.mcms.texture.tools;

import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.main.components.model.ComponentModelViewer;
import firemerald.mcms.gui.main.components.texture.TextureViewer;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.Material;

public interface ITool
{
	public boolean onModelViewClick(ComponentModelViewer viewer, float mx, float my, int button, int mods);
	
	public boolean onModelViewDrag(ComponentModelViewer viewer, float mx, float my, int button);
	
	public boolean onModelViewRelease(ComponentModelViewer viewer, float mx, float my, int button);
	
	public boolean onTextureViewClick(TextureViewer viewer, float mx, float my, int button, int mods);
	
	public boolean onTextureViewDrag(TextureViewer viewer, float mx, float my, int button);
	
	public boolean onTextureViewRelease(TextureViewer viewer, float mx, float my, int button);
	
	public void onMouseClick(Material tex, double u, double v, int button);

	public void onMouseDrag(Material tex, double prevU, double prevV, double u, double v, int button, boolean isNewObject);

	public void onMouseRelease(Material tex, double u, double v, int button);
	
	public void drawOnOverlay(Texture tex, double u, double v);
	
	public void onSelect(GuiSection options);
	
	public void onDeselect(GuiSection options);
}