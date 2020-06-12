package firemerald.mcms.util;

public class Triple<L, M, R> extends Pair<L, R>
{
	public final M middle;
	
	public Triple(L left, M middle, R right)
	{
		super(left, right);
		this.middle = middle;
	}
}