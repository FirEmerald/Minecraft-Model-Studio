package firemerald.mcms.util;

import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IModelEditable;

public interface IEditable
{
	public int onSelect(EditorPanes editorPanes, int editorY);
	
	public void onDeselect(EditorPanes editorPanes);

	public IModelEditable getRenderComponent();
}