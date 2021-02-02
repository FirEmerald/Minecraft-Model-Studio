package firemerald.mcms.api.animation;

import firemerald.mcms.api.math.MathUtils;
import firemerald.mcms.api.util.FloatBinaryOperator;

public enum TweenType
{
	SLERP(MathUtils::smoothLerp), 
	FALLIN(MathUtils::intoCurve), 
	FALLOUT(MathUtils::fromCurve);
	
	static
	{
		SLERP.inverse = SLERP;
		FALLIN.inverse = FALLOUT;
		FALLOUT.inverse = FALLIN;
	}
	
	protected TweenType inverse;
	public final FloatBinaryOperator function;
	
	TweenType(FloatBinaryOperator function)
	{
		this.function = function;
	}
	
	public TweenType inverse()
	{
		return inverse;
	}
	
	public float apply(float factor, float smoothing)
	{
		return function.applyAsFloat(factor, smoothing);
	}
}