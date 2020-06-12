package firemerald.mcms.gui.components;

import firemerald.mcms.util.ResourceLocation;

public abstract class ButtonItem extends ItemButton
{
	protected final ResourceLocation texture;
	protected final Runnable action;
	
	public ButtonItem(int x, int y, int size, ResourceLocation texture, Runnable action)
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
	public ResourceLocation getTexture()
	{
		return texture;
	}
}