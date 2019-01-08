package firemerald.mcms.gui.components.model;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.Textures;

public class ButtonAddBone extends EditableButton
{
	private Bone bone;
	
	public ButtonAddBone(float x, float y, EditorPanes editorPanes)
	{
		super(x, y, editorPanes);
		enabled = true;
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_ADD_BONE;
	}
	
	@Override
	public void onRelease()
	{
		Bone newBone = null;
		if (bone != null)
		{
			newBone = new RenderObjectComponents("new bone", Transformation.NONE, bone);
			editorPanes.base.updateBonesList();
		}
		else
		{
			newBone = new RenderObjectComponents("new bone", Transformation.NONE);
			editorPanes.base.addChild(newBone);
		}
		Main main = Main.instance;
		if (main.editing != null) main.editing.onDeselect(editorPanes);
		(main.editing = newBone).onSelect(editorPanes);
		editorPanes.selector.updateBase();
	}
	
	public void setBone(Bone bone)
	{
		this.bone = bone;
	}
}