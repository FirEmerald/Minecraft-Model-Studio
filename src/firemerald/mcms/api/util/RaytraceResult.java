package firemerald.mcms.api.util;

import firemerald.mcms.api.model.IRaytraceTarget;

public class RaytraceResult
{
	public final IRaytraceTarget hit;
	public final float m;
	
	public RaytraceResult(IRaytraceTarget hit, float m)
	{
		this.hit = hit;
		this.m = m;
	}
}