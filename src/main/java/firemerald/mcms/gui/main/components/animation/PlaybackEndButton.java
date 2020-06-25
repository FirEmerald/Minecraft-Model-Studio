package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class PlaybackEndButton extends PlaybackButton
{
	public PlaybackEndButton(int x, int y)
	{
		super(x, y);
	}

	@Override
	public boolean isEnabled()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return state != null && state.time < state.anim.get().getLength();
	}

	@Override
	public void onRelease()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		state.animMode = EnumPlaybackMode.PAUSED;
		state.time = state.anim.get().getLength();
	}

	@Override
	public ResourceLocation getIcon()
	{
		return Textures.PLAYBACK_END;
	}
}