package firemerald.mcms.api.animation;

import java.util.function.Supplier;

public class AnimationState
{
	public final Supplier<IAnimation> anim;
	public float time;
	
	public AnimationState(Supplier<IAnimation> anim, float time)
	{
		this.anim = anim;
		this.time = time;
	}
	
	public AnimationState(Supplier<IAnimation> anim)
	{
		this(anim, 0);
	}
}