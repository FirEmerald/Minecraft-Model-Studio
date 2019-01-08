package firemerald.mcms.gui.components.model;

import firemerald.mcms.Main;
import firemerald.mcms.model.ComponentBox;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IComponentParent;
import firemerald.mcms.util.Textures;

public class ButtonAddBox extends EditableButton
{
	private IComponentParent parent;
	
	public ButtonAddBox(float x, float y, EditorPanes editorPanes)
	{
		super(x, y, editorPanes);
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_ADD_BOX;
	}
	
	@Override
	public void onRelease()
	{
		ComponentBox box = new ComponentBox(parent, "new box");
		Main main = Main.instance;
		if (main.editing != null) main.editing.onDeselect(editorPanes);
		(main.editing = box).onSelect(editorPanes);
		editorPanes.selector.updateBase();
	}
	
	public void setParent(IComponentParent parent)
	{
		enabled = (this.parent = parent) != null;
	}
}