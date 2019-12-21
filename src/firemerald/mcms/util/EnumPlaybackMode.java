package firemerald.mcms.util;

public enum EnumPlaybackMode
{
	PAUSED(0),
	PLAYING(1),
	REVERSE(-1);
	
	public final float step;
	
	EnumPlaybackMode(float step)
	{
		this.step = step;
	}
}