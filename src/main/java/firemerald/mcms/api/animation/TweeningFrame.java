package firemerald.mcms.api.animation;

import firemerald.mcms.api.data.AbstractElement;

public class TweeningFrame
{
	public final Transformation transformation;
	public TweenType tweening = TweenType.SLERP;
	public float factor = 1;
	
	public TweeningFrame(AbstractElement el, float scale)
	{
		transformation = new Transformation();
		load(el, scale);
	}
	
	public TweeningFrame(TweeningFrame from)
	{
		this.transformation = from.transformation.copy();
		this.tweening = from.tweening;
		this.factor = from.factor;
	}
	
	public TweeningFrame(Transformation transformation, TweenType tweening, float smoothing)
	{
		this.transformation = transformation;
		this.tweening = tweening;
		this.factor = smoothing;
	}
	
	public TweeningFrame(Transformation transformation, TweenType tweening)
	{
		this.transformation = transformation;
		this.tweening = tweening;
	}
	
	public TweeningFrame(Transformation transformation, float smoothing)
	{
		this.transformation = transformation;
		this.factor = smoothing;
	}
	
	public TweeningFrame(Transformation transformation)
	{
		this.transformation = transformation;
	}

	public void load(AbstractElement el, float scale)
	{
		transformation.load(el, scale);
		tweening = el.getEnum("tweening", TweenType.values(), TweenType.SLERP);
		factor = el.getFloat("factor", 1);
	}
	
	public void save(AbstractElement el, float scale)
	{
		transformation.save(el, scale);
		if (tweening != TweenType.SLERP) el.setEnum("tweening", tweening);
		if (factor != 1) el.setFloat("factor", factor);
	}
	
	public float apply(float v)
	{
		return tweening.apply(v, factor);
	}
}