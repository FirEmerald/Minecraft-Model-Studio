package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.gui.main.components.elements.EditableButton;
import firemerald.mcms.gui.popups.animation.GuiPopupNewKeyframe;
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
	public String getTexture()
	{
		return Textures.EDITABLE_ADD_FRAME;
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimation() instanceof Animation;
	}

	@Override
	public void onRelease()
	{
		new GuiPopupNewKeyframe(framesBar).activate();
	}
}