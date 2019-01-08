package firemerald.mcms.model;

import java.util.List;

public interface IComponentParent extends IEditable, ITransformed
{
	public List<ModelComponent> getChildrenComponents();
}