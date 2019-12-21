package firemerald.mcms.model;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.util.IEditable;

public interface IModelEditable extends IEditableParent, IEditable
{	
	public String getDisplayIcon();
	
	public String getName();
	
	public void movedTo(IEditableParent oldParent, IEditableParent newParent);
	
	public boolean isVisible();
	
	public void setVisible(boolean visible);
	
	public IModelEditable copy(IEditableParent newParent, IModel model);
	
	default public void drawOnTexture(float x, float y, float sizeX, float sizeY) {}
	
	public Transformation getDefaultTransformation();
	
	@Override
	default public IModelEditable getRenderComponent()
	{
		return this;
	}
}