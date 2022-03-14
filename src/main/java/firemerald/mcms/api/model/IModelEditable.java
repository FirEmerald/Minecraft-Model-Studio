package firemerald.mcms.api.model;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.util.IEditable;
import firemerald.mcms.util.ResourceLocation;

public interface IModelEditable extends IEditableParent, IEditable
{	
	public IModelEditable getParent();
	
	public ResourceLocation getDisplayIcon();
	
	public String getName();
	
	public String getBoneName();
	
	public void movedTo(IEditableParent oldParent, IEditableParent newParent);
	
	public boolean isVisible();
	
	public void setVisible(boolean visible);
	
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> iRigged);
	
	default public void drawOnTexture(float x, float y, float sizeX, float sizeY) {}
	
	public Transformation getDefaultTransformation();
	
	@Override
	default public IModelEditable getRenderComponent()
	{
		return this;
	}
}