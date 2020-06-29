package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class PlaybackLockButton extends PlaybackButton
{
	public PlaybackLockButton(int x, int y)
	{
		super(x, y);
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimationState() != null;
	}

	@Override
	public void onRelease()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		state.locked = !state.locked;
		Main.instance.project.setNeedsSave();
	}

	@Override
	public ResourceLocation getIcon()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return state == null || !state.locked ? Textures.EDITABLE_UNLOCKED : Textures.EDITABLE_LOCKED;
	}
}