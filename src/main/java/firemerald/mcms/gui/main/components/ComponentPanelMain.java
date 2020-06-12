package firemerald.mcms.gui.main.components;

import firemerald.mcms.gui.components.ComponentPanel;
import firemerald.mcms.gui.main.GuiMain;

public abstract class ComponentPanelMain extends ComponentPanel
{
	public final GuiMain gui;
	
	public ComponentPanelMain(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2);
		this.gui = gui;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		onSize(x2 - x1, y2 - y1);
	}
	
	public abstract void onSize(int w, int h);
}