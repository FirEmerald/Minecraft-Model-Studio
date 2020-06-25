package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.model.IModelEditable;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.util.IntReference;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.history.HistoryAction;

public class ButtonRemove extends EditableButton
{
	private IEditableParent parent;
	private IModelEditable component;
	
	public ButtonRemove(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_DELETE;
	}
	
	@Override
	public void onRelease() //TODO
	{
		final IEditableParent parent = this.parent;
		final IModelEditable component = this.component;
		final IntReference pos = new IntReference(parent.getChildIndex(component));
		Main.instance.project.onAction(new HistoryAction(() -> {
			parent.addChildAt(component, pos.val);
			Main.instance.project.getRig().updateBonesList();
			Main.instance.editorPanes.selector.updateBase();
		}, () -> {
			pos.val = parent.getChildIndex(component);
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
		}));
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
		this.parent = (parent != null ? parent : Main.instance.project.getRig());
		this.component = component;
	}

	@Override
	public boolean isEnabled()
	{
		return this.component != null;
	}
}