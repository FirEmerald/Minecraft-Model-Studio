package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.model.IModelEditable;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.util.Textures;

public class ButtonRemove extends EditableButton
{
	private IEditableParent parent;
	private IModelEditable component;
	
	public ButtonRemove(int x, int y)
	{
		super(x, y);
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
		if (Main.instance.getEditing() == component)
		{
			if (parent instanceof IModelEditable)
			{
				Main.instance.setEditing((IModelEditable) parent);
			}
			else Main.instance.setEditing(null);
		}
		Main.instance.project.getRig().updateBonesList();
		Main.instance.editorPanes.selector.updateBase();
	}
	
	public void setEditable(IEditableParent parent, IModelEditable component)
	{
		this.parent = (parent != null ? parent : Main.instance.project.getModel());
		this.component = component;
	}

	@Override
	public boolean isEnabled()
	{
		return this.component != null;
	}
}