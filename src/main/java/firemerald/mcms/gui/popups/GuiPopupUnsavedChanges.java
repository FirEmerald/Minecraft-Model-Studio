package firemerald.mcms.gui.popups;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.decoration.DecoText;

public class GuiPopupUnsavedChanges extends GuiPopup
{
	public final DecoPane pane;
	public final DecoText message;
	public final StandardButton save, saveAs, dontSave, cancel;
	
	public GuiPopupUnsavedChanges(Runnable onClose)
	{
		//this.message = message;
		this.addElement(pane = new DecoPane(0, 0, 400, 160, 2, 16));
		this.addElement(this.message = new DecoText(0, 0, 0, 0, "You have unsaved changes!"));
		this.addElement(save = new StandardButton(0, 0, 80, 20, 1, 4, "save", () -> {
			this.deactivate();
			if (Main.instance.project.save()) onClose.run();
		}));
		this.addElement(saveAs = new StandardButton(80, 0, 160, 20, 1, 4, "save as", () -> {
			this.deactivate();
			if (Main.instance.project.saveAs()) onClose.run();
		}));
		this.addElement(dontSave = new StandardButton(160, 0, 240, 20, 1, 4, "don't save", () -> {
			this.deactivate();
			onClose.run();
		}));
		this.addElement(cancel = new StandardButton(240, 0, 320, 20, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		pane.setSize((w / 2) - 200, (h / 2) - 40, (w / 2) + 200, (h / 2) + 40);
		message.setSize((w / 2) - 90, (h / 2) - 30, (w / 2) + 150, (h / 2) - 10);
		save.setSize((w / 2) - 190, (h / 2) + 10, (w / 2) - 110, (h / 2) + 30);
		saveAs.setSize((w / 2) - 90, (h / 2) + 10, (w / 2) - 10, (h / 2) + 30);
		dontSave.setSize((w / 2) + 10, (h / 2) + 10, (w / 2) + 90, (h / 2) + 30);
		cancel.setSize((w / 2) + 110, (h / 2) + 10, (w / 2) + 190, (h / 2) + 30);
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
}