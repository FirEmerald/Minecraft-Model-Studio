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
		if (Modifier.SHIFT.isDown(Main.instance.window))
		{
			state.animLoop = true;
			state.animProgrammed = false;
		}
		else if (Modifier.CONTROL.isDown(Main.instance.window))
		{
			state.animLoop = false;
			state.animProgrammed = true;
		}
		else
		{
			state.animLoop = false;
			state.animProgrammed = false;
		}
	}

	@Override
	public ResourceLocation getIcon()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		if (state == null || (isEnabled() && state.animMode != EnumPlaybackMode.PLAYING)) //from buttons
		{
			return 
					Modifier.SHIFT.isDown(Main.instance.window) ? Textures.PLAYBACK_FORWARD_REPEAT : 
						Modifier.CONTROL.isDown(Main.instance.window) ? Textures.PLAYBACK_FORWARD_PROGRAMMED : 
							Textures.PLAYBACK_FORWARD;
		}
		else
		{
			return 
					state.animLoop ? Textures.PLAYBACK_FORWARD_REPEAT : 
						state.animProgrammed ? Textures.PLAYBACK_FORWARD_PROGRAMMED : 
							Textures.PLAYBACK_FORWARD;
		}
	}
}