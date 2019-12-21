package firemerald.mcms.model;

import java.util.List;

public interface IComponentParent extends IModelEditable, ITransformed
{
	public void updateTex();
	
	public List<ModelComponent> getChildrenComponents();
}