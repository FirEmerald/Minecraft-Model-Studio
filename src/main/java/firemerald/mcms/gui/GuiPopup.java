package firemerald.mcms.gui;

import firemerald.mcms.Main;
import firemerald.mcms.util.GuiUpdate;

public abstract class GuiPopup extends GuiScreen
{
	public GuiScreen under;
	
	public void activate()
	{
		Main.instance.openGui(this);
	}
	
	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		under.setSize(w, h);
	}
	
	public void deactivate()
	{
		Main.instance.closePopup();
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		under.render(mx, my, false);
		doRender(mx, my, canHover);
		super.render(mx, my, canHover);
	}
	
	public void doRender(float mx, float my, boolean canHover) {}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (under != null) under.onGuiUpdate(reason);
	}
}