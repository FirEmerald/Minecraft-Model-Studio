package firemerald.mcms.model;

import firemerald.mcms.api.model.IModel;

public interface IEditable extends IEditableParent
{
	public void onSelect(EditorPanes editorPanes);
	
	public void onDeselect(EditorPanes editorPanes);
	
	public String getDisplayIcon();
	
	public String getName();
	
	public void movedTo(IEditableParent oldParent, IEditableParent newParent);
	
	public boolean isVisible();
	
	public void setVisible(boolean visible);
	
	public IEditable copy(IEditableParent newParent, IModel model);
}