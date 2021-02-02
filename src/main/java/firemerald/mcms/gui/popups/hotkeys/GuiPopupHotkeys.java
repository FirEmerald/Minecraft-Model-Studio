package firemerald.mcms.gui.popups.hotkeys;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.scrolling.ScrollBar;
import firemerald.mcms.gui.components.scrolling.ScrollDown;
import firemerald.mcms.gui.components.scrolling.ScrollUp;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.MouseButtons;

public class GuiPopupHotkeys extends GuiPopup
{
	public final ComponentPaneHotkeys pane;
	public final ScrollBar scrollBar;
	public final ScrollDown scrollDown;
	public final ScrollUp scrollUp;
	
	public GuiPopupHotkeys()
	{
		int w = 900, h = 480;
		int hW = w / 2, hH = h / 2, sizeW = w, sizeH = h, sizeHW = sizeW / 2, sizeHH = sizeH / 2, sizeScroll = 16;
		this.addElement(pane = new ComponentPaneHotkeys(hW - sizeHW, hH - sizeHH, hW + sizeHW - sizeScroll, hH + sizeHH, 2));
		this.addElement(scrollBar = new ScrollBar(hW + sizeHW - sizeScroll, hH - sizeHH + sizeScroll, hW + sizeHW, hH + sizeHH - sizeScroll, pane));
		pane.setScrollBar(scrollBar);
		this.addElement(scrollDown = new ScrollDown(hW + sizeHW - sizeScroll, hH + sizeHH - sizeScroll, hW + sizeHW, hH + sizeHH, pane));
		this.addElement(scrollUp = new ScrollUp(hW + sizeHW - sizeScroll, hH - sizeHH, hW + sizeHW, hH - sizeHH + sizeScroll, pane));
	}
	
	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		int hW = w / 2, hH = h / 2, sizeW = 900, sizeH = 480, sizeHW = sizeW / 2, sizeHH = sizeH / 2, sizeScroll = 16;
		pane.setSize(hW - sizeHW, hH - sizeHH, hW + sizeHW - sizeScroll, hH + sizeHH);
		scrollBar.setSize(hW + sizeHW - sizeScroll, hH - sizeHH + sizeScroll, hW + sizeHW, hH + sizeHH - sizeScroll);
		scrollDown.setSize(hW + sizeHW - sizeScroll, hH + sizeHH - sizeScroll, hW + sizeHW, hH + sizeHH);
		scrollUp.setSize(hW + sizeHW - sizeScroll, hH - sizeHH, hW + sizeHW, hH - sizeHH + sizeScroll);
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