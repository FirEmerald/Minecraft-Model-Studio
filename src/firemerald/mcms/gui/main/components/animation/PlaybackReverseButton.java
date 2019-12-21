package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.Textures;

public class PlaybackReverseButton extends PlaybackButton
{
	public PlaybackReverseButton(int x, int y)
	{
		super(x, y, Textures.PLAYBACK_REVERSE);
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimation() != null && Main.instance.animMode != EnumPlaybackMode.REVERSE;
	}

	@Override
	public void onRelease()
	{
		Main.instance.animMode = EnumPlaybackMode.REVERSE;
		if (Main.instance.animTime == 0) Main.instance.animTime = Main.instance.project.getAnimation().getLength();
	}
}