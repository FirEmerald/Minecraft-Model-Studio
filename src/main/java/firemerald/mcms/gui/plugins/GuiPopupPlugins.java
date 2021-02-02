package firemerald.mcms.gui.plugins;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.scrolling.ScrollBar;
import firemerald.mcms.gui.components.scrolling.ScrollDown;
import firemerald.mcms.gui.components.scrolling.ScrollUp;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.MouseButtons;

public class GuiPopupPlugins extends GuiPopup
{
	public final ComponentPanePluginItems pane;
	public final ScrollBar scrollBar;
	public final ScrollDown scrollDown;
	public final ScrollUp scrollUp;
	
	public GuiPopupPlugins()
	{
		this.addElement(pane = new ComponentPanePluginItems(0, 0, 620, 480, 2));
		this.addElement(scrollBar = new ScrollBar(620, 20, 640, 460, pane));
		pane.setScrollBar(scrollBar);
		this.addElement(scrollDown = new ScrollDown(620, 460, 640, 480, pane));
		this.addElement(scrollUp = new ScrollUp(620, 0, 640, 20, pane));
	}
	
	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		pane.setSize((w / 2) - 320, (h / 2) - 240, (w / 2) + 300, (h / 2) + 240);
		scrollBar.setSize((w / 2) + 300, (h / 2) - 220, (w / 2) + 320, (h / 2) + 220);
		scrollDown.setSize((w / 2) + 300, (h / 2) + 220, (w / 2) + 320, (h / 2) + 240);
		scrollUp.setSize((w / 2) + 300, (h / 2) - 240, (w / 2) + 320, (h / 2) - 220);
	}
	
	@Override
	public boolean onKeyPressed(Key key, int scancode, int mods)
	{
		if (!super.onKeyPressed(key, scancode, mods))
		{
			if (key == Key.ESCAPE)
			{
				this.deactivate();
				return true;
			}
			else return false;
		}
		else return true;
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.guiShader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.guiShader.setColor(1, 1, 1, 1);
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my, button, mods);
		if (button == MouseButtons.LEFT && this.focused == null) deactivate();
	}
}