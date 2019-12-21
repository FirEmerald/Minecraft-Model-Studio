package firemerald.mcms.gui.main.components;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.main.components.tools.ColorButtonPrimary;
import firemerald.mcms.gui.main.components.tools.ColorButtonSecondary;
import firemerald.mcms.gui.main.components.tools.ColorDefaultButton;
import firemerald.mcms.gui.main.components.tools.ColorSwapButton;
import firemerald.mcms.gui.main.components.tools.ToolButton;
import firemerald.mcms.texture.tools.ToolBrush;
import firemerald.mcms.texture.tools.ToolDropper;
import firemerald.mcms.util.Textures;

public class ComponentToolsPanel extends ComponentPanelMain
{
	public final ToolButton model, pencil, dropper;
	public final ColorButtonPrimary colorButtonPrimary;
	public final ColorButtonSecondary colorButtonSecondary;
	public final ColorDefaultButton colorDefault;
	public final ColorSwapButton colorSwap;
	public final GuiSection section = new GuiSection(this, 0, 32, 300, 300);
	
	public ComponentToolsPanel(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
		this.addElement(model = new ToolButton(0, 0, section, Textures.TOOL_MODEL, null));
		this.addElement(pencil = new ToolButton(32, 0, section, Textures.TOOL_PENCIL, new ToolBrush()));
		this.addElement(dropper = new ToolButton(64, 0, section, Textures.TOOL_DROPPER, new ToolDropper()));
		this.addElement(colorButtonSecondary = new ColorButtonSecondary(12, 12, 32, 32));
		this.addElement(colorButtonPrimary = new ColorButtonPrimary(0, 0, 20, 20));
		this.addElement(colorDefault = new ColorDefaultButton(0, 20));
		this.addElement(colorSwap = new ColorSwapButton(20, 0));
		// TODO components
	}
	
	@Override
	public void onSize(int w, int h)
	{
		if (Main.instance.tool != null) Main.instance.tool.onDeselect(section);
		model.setSize(0, 0);
		pencil.setSize(32, 0);
		dropper.setSize(64, 0);
		section.setBounds(0, 32, w, h);
		colorButtonPrimary.setSize(w - 32, 0, w - 12, 20);
		colorButtonSecondary.setSize(w - 20, 12, w, 32);
		colorDefault.setSize(w - 32, 20);
		colorSwap.setSize(w - 12, 0);
		if (Main.instance.tool != null) Main.instance.tool.onSelect(section);
	}
}