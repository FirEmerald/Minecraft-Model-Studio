package firemerald.mcms.api.animation;

public class AnimationState
{
	public final IAnimation anim;
	public float time;
	
	public AnimationState(IAnimation anim, float time)
	{
		this.anim = anim;
		this.time = time;
	}
	
	public AnimationState(IAnimation anim)
	{
		this(anim, 0);
	}
}