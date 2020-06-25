package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.gui.popups.model.GuiPopupFluid;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class ButtonAddFluid<T extends RenderBone<T>> extends EditableButton
{
	private T bone;
	
	public ButtonAddFluid(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_ADD_FLUID;
	}
	
	@Override
	public void onRelease()
	{
		new GuiPopupFluid<>(bone).activate();
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