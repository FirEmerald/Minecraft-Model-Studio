package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.Textures;

public class PlaybackEndButton extends PlaybackButton
{
	public PlaybackEndButton(int x, int y)
	{
		super(x, y, Textures.PLAYBACK_END);
	}

	@Override
	public boolean isEnabled()
	{
		Main main = Main.instance;
		return main.project.getAnimation() != null && main.animState.time < main.project.getAnimation().getLength();
	}

	@Override
	public void onRelease()
	{
		Main.instance.animMode = EnumPlaybackMode.PAUSED;
		Main.instance.animState.time = Main.instance.project.getAnimation().getLength();
	}
}