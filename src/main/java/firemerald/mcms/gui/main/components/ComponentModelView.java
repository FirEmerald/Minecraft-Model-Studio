package firemerald.mcms.gui.main.components;

import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.main.components.model.ComponentModelViewer;

public class ComponentModelView extends ComponentPanelMain
{
	public final ComponentModelViewer modelViewer;
	
	public ComponentModelView(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
		this.addElement(modelViewer = new ComponentModelViewer(0, 0, x2 - x1, y2 - y1, gui));
	}
	
	@Override
	public void onSize(int w, int h)
	{
		modelViewer.setSize(0, 0, w, h);
	}
}