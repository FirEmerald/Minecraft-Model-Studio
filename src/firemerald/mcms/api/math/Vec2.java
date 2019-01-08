package firemerald.mcms.api.math;

public class Vec2
{
	private float x, y;

	public static final Vec2 ZERO = new Vec2();
	public static final Vec2 ONE = new Vec2(1);
	
	public Vec2()
	{
		x = y = 0;
	}
	
	public Vec2(float v)
	{
		x = y = v;
	}
	
	public Vec2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float x()
	{
		return x;
	}
	
	public float y()
	{
		return y;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (!(o instanceof Vec2)) return false;
		else
		{
			Vec2 v = (Vec2) o;
			return (v.x == x && v.y == y);
		}
	}
	
	@Override
	public String toString()
	{
		return "{" + x + ", " + y + "}";
	}
}