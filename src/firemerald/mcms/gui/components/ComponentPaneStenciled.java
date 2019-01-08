package firemerald.mcms.gui.components;

import firemerald.mcms.model.Mesh;
import firemerald.mcms.util.RenderUtil;

public class ComponentPaneStenciled extends ComponentPane
{
	public final Mesh stencil = new Mesh();
	public final float border;
	
	public ComponentPaneStenciled(float x1, float y1, float x2, float y2, float border)
	{
		super(x1, y1, x2, y2);
		this.border = border;
		updateStencilMesh();
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		updateStencilMesh();
	}
	
	public void updateStencilMesh()
	{
		stencil.setMesh(x1 + border, y1 + border, x2 - border, y2 - border, 0, 0, 0, 1, 1);
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		RenderUtil.pushStencil();
		RenderUtil.startStencil(false);
		stencil.render();
		RenderUtil.endStencil();
		super.render(mx, my, canHover);
		RenderUtil.popStencil();
	}
}