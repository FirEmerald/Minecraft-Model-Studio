package firemerald.mcms.util;

import firemerald.mcms.api.model.IRaytraceTarget;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.texture.space.Material;

public class TextureRaytraceResult extends RaytraceResult
{
	public final Material tex;
	public final double u, v;
	
	public TextureRaytraceResult(IRaytraceTarget hit, double m, Material tex, double u, double v)
	{
		super(hit, m);
		this.tex = tex;
		this.u = u;
		this.v = v;
	}
}