package firemerald.mcms.gui;

import firemerald.mcms.Main;
import firemerald.mcms.util.GuiUpdate;

public abstract class GuiPopup extends GuiScreen
{
	public GuiScreen under;
	
	public void activate()
	{
		under = Main.instance.gui;
		Main.instance.gui = this;
		this.setSize(Main.instance.sizeW, Main.instance.sizeH);
	}
	
	@Override
	public void setSize(int w, int h)
	{
		under.setSize(w, h);
	}
	
	public void deactivate()
	{
		Main.instance.gui = under;
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