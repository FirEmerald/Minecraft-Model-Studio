package firemerald.mcms.util;

import java.util.Objects;

public class Quadruple<L, ML, MR, R> extends Pair<L, R>
{
	public final ML middleLeft;
	public final MR middleRight;
	
	public Quadruple(L left, ML middleLeft, MR middleRight, R right)
	{
		super(left, right);
		this.middleLeft = middleLeft;
		this.middleRight = middleRight;
	}
	
	@Override
	public int hashCode()
	{
		return (left == null ? 0 : left.hashCode()) ^ (middleLeft == null ? 0 : middleLeft.hashCode()) ^ (middleRight == null ? 0 : middleRight.hashCode()) ^ (right == null ? 0 : right.hashCode());
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o == null) return false;
		else if (o.getClass() == this.getClass())
		{
			Quadruple<?, ?, ?, ?> q = (Quadruple<?, ?, ?, ?>) o;
			return Objects.equals(left, q.left) && Objects.equals(middleLeft, q.middleLeft) && Objects.equals(middleRight, q.middleRight) && Objects.equals(right, q.right);
		}
		else return false;
	}
	
	@Override
	public Quadruple<R, MR, ML, L> reverse()
	{
		return new Quadruple<>(right, middleRight, middleLeft, left);
	}
}