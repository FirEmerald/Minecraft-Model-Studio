package firemerald.mcms.util;

import java.util.Objects;

public class Pair<L, R>
{
	public final L left;
	public final R right;
	
	public Pair(L left, R right)
	{
		this.left = left;
		this.right = right;
	}
	
	@Override
	public int hashCode()
	{
		return (left == null ? 0 : left.hashCode()) ^ (right == null ? 0 : right.hashCode());
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o == null) return false;
		else if (o.getClass() == this.getClass())
		{
			Pair<?, ?> p = (Pair<?, ?>) o;
			return Objects.equals(left, p.left) && Objects.equals(right, p.right);
		}
		else return false;
	}
	
	public Pair<R, L> reverse()
	{
		return new Pair<>(right, left);
	}
}