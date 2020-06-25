package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.model.ComponentBox;
import firemerald.mcms.model.IComponentParent;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.history.HistoryAction;

public class ButtonAddBox extends EditableButton
{
	private IComponentParent parent;
	
	public ButtonAddBox(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_ADD_BOX;
	}
	
	@Override
	public void onRelease()
	{
		ComponentBox box = new ComponentBox(parent, "new box");
		Main.instance.project.onAction(new HistoryAction(() -> parent.removeChild(box), () -> parent.addChild(box)));
		Main main = Main.instance;
		main.setEditing(box);
		Main.instance.editorPanes.selector.updateBase();
	}
	
	public void setParent(IComponentParent parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean isEnabled()
	{
		return this.parent != null;
	}
}