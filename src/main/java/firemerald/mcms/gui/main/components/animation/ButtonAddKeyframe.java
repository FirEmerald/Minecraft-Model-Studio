package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.Pose;
import firemerald.mcms.gui.main.components.elements.EditableButton;
import firemerald.mcms.gui.popups.animation.GuiPopupNewKeyframe;
import firemerald.mcms.gui.popups.animation.GuiPopupNewPoseframe;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class ButtonAddKeyframe extends EditableButton
{
	public final ComponentFramesBar framesBar;
	
	public ButtonAddKeyframe(int x, int y, ComponentFramesBar framesBar)
	{
		super(x, y);
		this.framesBar = framesBar;
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_ADD_FRAME;
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimation() instanceof Animation || Main.instance.project.getAnimation() instanceof Pose;
	}

	@Override
	public void onRelease()
	{
		if (Main.instance.project.getAnimation() instanceof Animation) new GuiPopupNewKeyframe(framesBar).activate();
		else if (Main.instance.project.getAnimation() instanceof Pose) new GuiPopupNewPoseframe(framesBar).activate();
	}
}