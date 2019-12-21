package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.gui.main.components.elements.EditableButton;
import firemerald.mcms.gui.popups.animation.GuiPopupMoveKeyframe;
import firemerald.mcms.util.Textures;

public class ButtonMoveKeyframe extends EditableButton
{
	public final ComponentFramesBar framesBar;
	
	public ButtonMoveKeyframe(int x, int y, ComponentFramesBar framesBar)
	{
		super(x, y);
		this.framesBar = framesBar;
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_MOVE_FRAME;
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimation() instanceof Animation && Main.instance.getEditing() instanceof ComponentKeyFrame;
	}

	@Override
	public void onRelease()
	{
		new GuiPopupMoveKeyframe((ComponentKeyFrame) Main.instance.getEditing(), framesBar).activate();
	}
}