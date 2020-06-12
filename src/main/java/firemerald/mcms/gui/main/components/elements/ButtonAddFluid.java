package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.gui.popups.model.GuiPopupFluid;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class ButtonAddFluid extends EditableButton
{
	private Bone bone;
	
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
		new GuiPopupFluid(bone).activate();
	}
	
	public void setBone(Bone bone)
	{
		this.bone = bone;
	}

	@Override
	public boolean isEnabled()
	{
		return bone != null && Main.instance.project.getModel() != null;
	}
}