package firemerald.mcms.gui.components;

public abstract class ButtonItem extends ItemButton
{
	protected final String texture;
	protected final Runnable action;
	
	public ButtonItem(int x, int y, int size, String texture, Runnable action)
	{
		super(x, y, size);
		this.texture = texture;
		this.action = action;
	}
	
	@Override
	public void onRelease()
	{
		action.run();
	}

	@Override
	public String getTexture()
	{
		return texture;
	}
}