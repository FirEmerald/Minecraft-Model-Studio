package firemerald.mcms.gui.components.model;

import firemerald.mcms.Main;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.util.Textures;

public class ButtonRemove extends EditableButton
{
	private IEditableParent parent;
	private IEditable component;
	
	public ButtonRemove(float x, float y, EditorPanes editorPanes)
	{
		super(x, y, editorPanes);
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_DELETE;
	}
	
	@Override
	public void onRelease()
	{
		IEditableParent parent = this.parent;
		parent.removeChild(component);
		if (Main.instance.editing == component)
		{
			component.onDeselect(editorPanes);
			if (parent instanceof IEditable)
			{
				IEditable parentE;
				Main.instance.editing = parentE = (IEditable) parent;
				parentE.onSelect(editorPanes);
			}
			else Main.instance.editing = null;
		}
		editorPanes.base.updateBonesList();
		editorPanes.selector.updateBase();
	}
	
	public void setEditable(IEditableParent parent, IEditable component)
	{
		this.parent = (parent != null ? parent : editorPanes.base);
		enabled = (this.component = component) != null;
	}
}