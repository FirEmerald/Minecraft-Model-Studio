package firemerald.mcms.util;

import java.util.Objects;

public class Triple<L, M, R> extends Pair<L, R>
{
	public final M middle;
	
	public Triple(L left, M middle, R right)
	{
		super(left, right);
		this.middle = middle;
	}
	
	@Override
	public int hashCode()
	{
		return (left == null ? 0 : left.hashCode()) ^ (middle == null ? 0 : middle.hashCode()) ^ (right == null ? 0 : right.hashCode());
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o == null) return false;
		else if (o.getClass() == this.getClass())
		{
			Triple<?, ?, ?> p = (Triple<?, ?, ?>) o;
			return Objects.equals(left, p.left) && Objects.equals(middle, p.middle) && Objects.equals(right, p.right);
		}
		else return false;
	}
	
	@Override
	public Triple<R, M, L> reverse()
	{
		return new Triple<>(right, middle, left);
	}
}