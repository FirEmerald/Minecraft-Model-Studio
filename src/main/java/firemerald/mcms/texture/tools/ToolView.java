package firemerald.mcms.texture.tools;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.IUVMovable;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.main.components.model.ComponentModelViewer;
import firemerald.mcms.gui.main.components.texture.TextureViewer;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.Material;
import firemerald.mcms.window.api.Modifier;
import firemerald.mcms.window.api.MouseButtons;

public class ToolView implements ITool
{
	public static final ToolView INSTANCE = new ToolView();

	public boolean isDraggingTexture = false;
	public double texClickU, texClickV, texMoveU, texMoveV;
	public float dU, dV;
	
	@Override
	public boolean onModelViewClick(ComponentModelViewer viewer, float mx, float my, int button, int mods)
	{
		return (button == MouseButtons.MIDDLE || button == MouseButtons.RIGHT) && viewer.processModelViewClick(mx, my, button, mods);
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
	public boolean onTextureViewClick(TextureViewer viewer, float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
		{
			texClickU = viewer.getTexU(mx);
			texClickV = viewer.getTexV(my);
			isDraggingTexture = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean onTextureViewDrag(TextureViewer viewer, float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT && isDraggingTexture)
		{
			double texClickU = viewer.getTexU(mx);
			double texClickV = viewer.getTexV(my);
			texMoveU += texClickU - this.texClickU;
			texMoveV += texClickV - this.texClickV;
			if (Main.instance.getEditing() instanceof IUVMovable)
			{
				float pU = dU, pV = dV;
				float texW = Main.instance.project.getTexture() == null ? Main.instance.project.getTextureWidth() : Main.instance.project.getTexture().getDiffuse().w;
				float texH = Main.instance.project.getTexture() == null ? Main.instance.project.getTextureHeight() : Main.instance.project.getTexture().getDiffuse().h;
				if (Modifier.ALT.isDown(Main.instance.window)) //free
				{
					dU = (float) texMoveU;
					dV = (float) texMoveV;
				}
				else
				{
					dU = (float) (Math.round(texMoveU * texW) / texW);
					dV = (float) (Math.round(texMoveV * texH) / texH);
				}
				if (dU != pU || dV != pV) ((IUVMovable) Main.instance.getEditing()).move((dU - pU) * texW, (dV - pV) * texH);
			}
			this.texClickU = texClickU;
			this.texClickV = texClickV;
			return true;
		}
		else return false;
	}

	@Override
	public boolean onTextureViewRelease(TextureViewer viewer, float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT && isDraggingTexture)
		{
			isDraggingTexture = false;
			texMoveU = texMoveV = dU = dV = 0;
			return true;
		}
		else return false;
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