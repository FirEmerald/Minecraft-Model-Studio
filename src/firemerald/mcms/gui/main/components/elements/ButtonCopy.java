package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.model.IModelEditable;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.util.Textures;

public class ButtonCopy extends EditableButton
{
	private IEditableParent parent;
	private IModelEditable component;
	
	public ButtonCopy(int x, int y)
	{
		super(x, y);
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_COPY;
	}
	
	@Override
	public void onRelease()
	{
		IModelEditable copy = component.copy(parent, Main.instance.project.getModel());
		Main main = Main.instance;
		main.setEditing(copy);
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