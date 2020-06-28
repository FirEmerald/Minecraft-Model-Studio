package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.history.HistoryAction;

public class ButtonCopy extends EditableButton
{
	private IEditableParent parent;
	private IModelEditable component;
	
	public ButtonCopy(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_COPY;
	}
	
	@Override
	public void onRelease()
	{
		IModelEditable copy = component.copy(parent, Main.instance.project.getRig());
		Main.instance.project.onAction(new HistoryAction(() -> parent.removeChild(copy), () -> parent.addChild(copy)));
		Main main = Main.instance;
		main.setEditing(copy);
		Main.instance.editorPanes.selector.updateBase();
	}
	
	public void setEditable(IEditableParent parent, IModelEditable component)
	{
		this.parent = (parent != null ? parent : Main.instance.project.getRig());
		this.component = component;
	}

	@Override
	public boolean isEnabled()
	{
		return this.component != null;
	}
}