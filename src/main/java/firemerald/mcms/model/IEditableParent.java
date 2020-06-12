package firemerald.mcms.model;

import java.util.Collection;

public interface IEditableParent
{
	public Collection<? extends IModelEditable> getChildren();
	
	public boolean hasChildren();
	
	public boolean canBeChild(IModelEditable candidate);
	
	public void addChild(IModelEditable child);
	
	public void addChildBefore(IModelEditable child, IModelEditable position);
	
	public void addChildAfter(IModelEditable child, IModelEditable position);
	
	public void removeChild(IModelEditable child);
}