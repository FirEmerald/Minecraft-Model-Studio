package firemerald.mcms.gui.popups;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.decoration.DecoText;
import firemerald.mcms.util.font.FormattedText;

public class GuiPopupMessageOK extends GuiPopup
{
	public final DecoPane pane;
	public final DecoText message;
	public final StandardButton ok;
	
	public GuiPopupMessageOK(String message)
	{
		this(message, "ok");
	}
	
	public GuiPopupMessageOK(String message, String button)
	{
		this(FormattedText.parse(message, Main.instance.fontMsg, Main.instance.getTheme().getTextColor(), false, false), button);
	}
	
	public GuiPopupMessageOK(FormattedText message)
	{
		this(message, "ok");
	}
	
	public GuiPopupMessageOK(FormattedText message, String button)
	{
		//this.message = message;
		this.addElement(pane = new DecoPane(0, 0, 320, 160, 2, 16));
		this.addElement(this.message = new DecoText(0, 0, 0, 0, message));
		this.addElement(ok = new StandardButton(0, 0, 80, 20, 1, 4, button, this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		pane.setSize((w / 2) - 160, (h / 2) - 80, (w / 2) + 160, (h / 2) + 80);
		message.setSize((w / 2) - 150, (h / 2) - 70, (w / 2) + 150, (h / 2) + 80 - 40);
		ok.setSize((w / 2) - 40, (h / 2) + 80 - 30, (w / 2) + 40, (h / 2) + 80 - 10);
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.shader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.shader.setColor(1, 1, 1, 1);
	}
}