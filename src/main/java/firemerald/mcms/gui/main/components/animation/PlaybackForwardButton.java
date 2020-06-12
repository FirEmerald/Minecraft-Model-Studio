package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.Textures;

public class PlaybackForwardButton extends PlaybackButton
{
	public PlaybackForwardButton(int x, int y)
	{
		super(x, y, Textures.PLAYBACK_FORWARD);
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimation() != null && Main.instance.animMode != EnumPlaybackMode.PLAYING;
	}

	@Override
	public void onRelease()
	{
		Main.instance.animMode = EnumPlaybackMode.PLAYING;
		if (Main.instance.animState.time == Main.instance.project.getAnimation().getLength()) Main.instance.animState.time = 0;
	}
}