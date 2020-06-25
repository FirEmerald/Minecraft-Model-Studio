package firemerald.mcms.gui.main.components.items;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ItemButton16;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.hotkey.Action;

public class ButtonAction extends ItemButton16
{
	protected final ResourceLocation texture;
	protected final Action action;
	
	public ButtonAction(int x, int y, ResourceLocation texture, Action action)
	{
		super(x, y);
		this.texture = texture;
		this.action = action;
	}
	
	@Override
	public void onRelease()
	{
		Main.instance.doAction(action);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}

	/*
	@Override
	public boolean isEnabled()
	{
		return super.isEnabled() && action.canRun.getAsBoolean();
	}
	*/
}