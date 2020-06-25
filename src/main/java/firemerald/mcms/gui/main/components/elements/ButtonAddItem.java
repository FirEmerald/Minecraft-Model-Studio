package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.gui.popups.model.GuiPopupItem;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class ButtonAddItem<T extends RenderBone<T>> extends EditableButton
{
	private T bone;
	
	public ButtonAddItem(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_ADD_ITEM;
	}
	
	@Override
	public void onRelease()
	{
		new GuiPopupItem<>(bone).activate();
	}
	
	public void setBone(T bone)
	{
		this.bone = bone;
	}

	@Override
	public boolean isEnabled()
	{
		return bone != null && Main.instance.project.getModel() != null;
	}

	@SuppressWarnings("unchecked")
	public void setBone(Bone<?> bone)
	{
		if (bone instanceof RenderBone) setBone((T) bone);
	}
}