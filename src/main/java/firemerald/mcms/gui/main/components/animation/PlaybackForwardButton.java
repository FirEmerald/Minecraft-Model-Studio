package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.window.api.Modifier;

public class PlaybackForwardButton extends PlaybackButton
{
	public PlaybackForwardButton(int x, int y)
	{
		super(x, y);
	}

	@Override
	public boolean isEnabled()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return state != null && state.animMode != EnumPlaybackMode.PLAYING;
	}

	@Override
	public void onRelease()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		state.animMode = EnumPlaybackMode.PLAYING;
		if (state.time == Main.instance.project.getAnimation().getLength()) state.time = 0;
		state.animLoop = Modifier.SHIFT.isDown(Main.instance.window);
	}

	@Override
	public ResourceLocation getIcon()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return (!isEnabled() ? state != null && state.animLoop : Modifier.SHIFT.isDown(Main.instance.window)) ? Textures.PLAYBACK_FORWARD_REPEAT : Textures.PLAYBACK_FORWARD;
	}
}