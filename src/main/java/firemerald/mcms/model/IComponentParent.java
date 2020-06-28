package firemerald.mcms.model;

import java.util.List;

import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.ITransformed;

public interface IComponentParent extends IModelEditable, ITransformed
{
	public void updateTex();
	
	public List<ModelComponent> getChildrenComponents();
}