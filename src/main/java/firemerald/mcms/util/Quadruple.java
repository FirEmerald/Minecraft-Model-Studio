package firemerald.mcms.util;

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
}