package firemerald.mcms.gui.components.menu;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentPanel;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.MouseButtons;

public class ComponentMenu extends ComponentPanel
{
	public final Mesh mesh = new Mesh();
	public ThemeElement format;
	public ComponentMenu expanded = null;
	public final GuiElementContainer container;
	public final Menu menu;
	
	public ComponentMenu(int x, int y, Menu menu, GuiElementContainer container)
	{
		super(x, y, x + 202, y + 2);
		this.menu = menu;
		this.container = container;
		//FontRenderer font = Main.instance.fontMsg;
		int size = menu.getItemCount();
		int cy = 1, h = 2;
		for (int i = 0; i < size; i++)
		{
			MenuItem item = menu.getItem(i);
			String label = item.getLabel();
			float inc;
			if (item instanceof Menu)
			{
				//TODO nested menus
				final int my = cy;
				MenuExpandButton button = new MenuExpandButton(1, cy, 201, cy + 20, 0, 0, label, this, (Menu) item, x + 201, y + my - 1);
				button.textCentered = false;
				this.addElement(button);
				inc = 20;
			}
			else if (label.equals("-"))
			{
				this.addElement(new DecoSeperator(1, cy, 201, cy + 10, 1, 1));
				inc = 10;
			}
			else
			{
				StandardButton button = new StandardButton(1, cy, 201, cy + 20, 0, 0, label, () -> 
				{
					Main.instance.closePopup();
					for (ActionListener listener: item.getActionListeners()) listener.actionPerformed(new ActionEvent(item, ActionEvent.ACTION_PERFORMED, item.getActionCommand()));
				});
				button.textCentered = false;
				button.enabled = item.isEnabled();
				this.addElement(button);
				inc = 20;
			}
			h += inc;
			cy += inc;
		}
		this.setSize(x, y, x + 202, y + h);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (format != null) format.release();
			format = getTheme().genBox(x2 - x1, y2 - y1, 1);
		}
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my, button, mods);
		if (focused != null && button == MouseButtons.LEFT && expanded != null) expanded.retract();
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		if (enabled)
		{
			format.bind();
			mesh.render();
		}
		super.render(mx, my, canHover);
	}
	
	public void retract()
	{
		if (expanded != null)
		{
			expanded.retract();
			expanded = null;
		}
		container.removeElement(this);
	}
	
	public void expand(Menu menu, int x, int y)
	{
		if (expanded != null) expanded.retract();
		if (expanded == null || expanded.menu != menu) container.addElement(expanded = new ComponentMenu(x, y, menu, container));
		else expanded = null;
	}
}