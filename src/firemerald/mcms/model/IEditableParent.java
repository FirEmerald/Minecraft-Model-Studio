package firemerald.mcms.model;

import java.util.Collection;

public interface IEditableParent
{
	public Collection<? extends IEditable> getChildren();
	
	public boolean hasChildren();
	
	public boolean canBeChild(IEditable candidate);
	
	public void addChild(IEditable child);
	
	public void addChildBefore(IEditable child, IEditable position);
	
	public void addChildAfter(IEditable child, IEditable position);
	
	public void removeChild(IEditable child);
}