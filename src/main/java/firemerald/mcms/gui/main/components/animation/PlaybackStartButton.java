package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.Textures;

public class PlaybackStartButton extends PlaybackButton
{
	public PlaybackStartButton(int x, int y)
	{
		super(x, y, Textures.PLAYBACK_START);
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.animState.time > 0;
	}

	@Override
	public void onRelease()
	{
		Main.instance.animMode = EnumPlaybackMode.PAUSED;
		Main.instance.animState.time = 0;
	}
}