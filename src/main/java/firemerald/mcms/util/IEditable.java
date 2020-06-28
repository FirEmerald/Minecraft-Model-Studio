package firemerald.mcms.util;

import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.model.EditorPanes;

public interface IEditable
{
	public int onSelect(EditorPanes editorPanes, int editorY);
	
	public void onDeselect(EditorPanes editorPanes);

	public IModelEditable getRenderComponent();
}