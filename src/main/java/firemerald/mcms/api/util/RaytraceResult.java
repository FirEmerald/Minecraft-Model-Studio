package firemerald.mcms.api.util;

import firemerald.mcms.api.model.IRaytraceTarget;

public class RaytraceResult
{
	public final IRaytraceTarget hit;
	public final double m;
	
	public RaytraceResult(IRaytraceTarget hit, double m)
	{
		this.hit = hit;
		this.m = m;
	}
}