package firemerald.mcms.gui.components.model;

import firemerald.mcms.Main;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.util.Textures;

public class ButtonCopy extends EditableButton
{
	private IEditableParent parent;
	private IEditable component;
	
	public ButtonCopy(float x, float y, EditorPanes editorPanes)
	{
		super(x, y, editorPanes);
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_COPY;
	}
	
	@Override
	public void onRelease()
	{
		IEditable copy = component.copy(parent, editorPanes.base);
		Main main = Main.instance;
		if (main.editing != null) main.editing.onDeselect(editorPanes);
		(main.editing = copy).onSelect(editorPanes);
		editorPanes.selector.updateBase();
	}
	
	public void setEditable(IEditableParent parent, IEditable component)
	{
		this.parent = (parent != null ? parent : editorPanes.base);
		enabled = (this.component = component) != null;
	}
}