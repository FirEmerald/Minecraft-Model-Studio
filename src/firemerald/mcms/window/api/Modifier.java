package firemerald.mcms.window.api;

public enum Modifier
{
	CONTROL(Key.LEFT_CONTROL, Key.RIGHT_CONTROL),
	SHIFT(Key.LEFT_SHIFT, Key.RIGHT_SHIFT),
	ALT(Key.LEFT_ALT, Key.RIGHT_ALT),
	SUPER(Key.LEFT_SUPER, Key.RIGHT_SUPER);
	
	public final Key left, right;
	public final int flag;
	
	Modifier(Key left, Key right)
	{
		this.left = left;
		this.right = right;
		flag = 1 << ordinal();
	}
	
	public boolean isDown(Window window)
	{
		return window.isKeyDown(left) || window.isKeyDown(right);
	}
}