package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.window.api.Modifier;

public class PlaybackReverseButton extends PlaybackButton
{
	public PlaybackReverseButton(int x, int y)
	{
		super(x, y);
	}

	@Override
	public boolean isEnabled()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return state != null && state.animMode != EnumPlaybackMode.REVERSE;
	}

	@Override
	public void onRelease()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		state.animMode = EnumPlaybackMode.REVERSE;
		if (state.time == 0) state.time = Main.instance.project.getAnimation().getLength();
		state.animLoop = Modifier.SHIFT.isDown(Main.instance.window);
		state.animProgrammed = false;
	}

	@Override
	public ResourceLocation getIcon()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return (!isEnabled() ? state != null && state.animLoop : Modifier.SHIFT.isDown(Main.instance.window)) ? Textures.PLAYBACK_REVERSE_REPEAT : Textures.PLAYBACK_REVERSE;
	}
}