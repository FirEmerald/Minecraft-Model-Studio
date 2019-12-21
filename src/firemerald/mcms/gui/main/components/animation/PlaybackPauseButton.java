package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.Textures;

public class PlaybackPauseButton extends PlaybackButton
{
	public PlaybackPauseButton(int x, int y)
	{
		super(x, y, Textures.PLAYBACK_PAUSE);
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimation() != null && Main.instance.animMode != EnumPlaybackMode.PAUSED;
	}

	@Override
	public void onRelease()
	{
		Main.instance.animMode = EnumPlaybackMode.PAUSED;
	}
}