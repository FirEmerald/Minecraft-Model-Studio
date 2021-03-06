package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.gui.popups.model.GuiPopupBone;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class ButtonAddBone extends EditableButton
{
	private Bone<?> bone;
	
	public ButtonAddBone(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_ADD_BONE;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onRelease()
	{
		new GuiPopupBone(bone).activate();
	}
	
	public void setBone(Bone<?> bone)
	{
		this.bone = bone;
	}

	@Override
	public boolean isEnabled()
	{
		return (bone != null || Main.instance.getEditing() == null) && Main.instance.project.getRig() != null;
	}
}