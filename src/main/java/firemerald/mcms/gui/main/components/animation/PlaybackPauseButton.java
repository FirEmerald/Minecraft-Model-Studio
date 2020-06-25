package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class PlaybackPauseButton extends PlaybackButton
{
	public PlaybackPauseButton(int x, int y)
	{
		super(x, y);
	}

	@Override
	public boolean isEnabled()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return state != null && state.animMode != EnumPlaybackMode.PAUSED;
	}

	@Override
	public void onRelease()
	{
		Main.instance.project.getAnimationState().animMode = EnumPlaybackMode.PAUSED;
	}

	@Override
	public ResourceLocation getIcon()
	{
		return Textures.PLAYBACK_PAUSE;
	}
}