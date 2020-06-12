package firemerald.mcms.gui.popups.model;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.MultiModel;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.MiscUtil;

public class GuiPopupModel extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentText name;
	public final ComponentTextInt textureWidth, textureHeight;
	public final StandardButton ok, cancel;
	public final boolean isEdit;
	
	public GuiPopupModel(boolean isEdit)
	{
		Project project = Main.instance.project;
		this.addElement(pane = new DecoPane(0, 0, 320, 160, 2, 16));
		this.addElement(name = new ComponentText(0, 10, 240, 30, Main.instance.fontMsg, isEdit ? project.getModelName() : "new model", (text) ->  {}));
		this.addElement(textureWidth = new ComponentTextInt(0, 40, 100, 60, Main.instance.fontMsg, project.hasModelTextureWidth() ? project.getTextureWidth() : null, 1, Integer.MAX_VALUE, "default")); //TODO if already has override
		this.addElement(textureHeight = new ComponentTextInt(140, 40, 240, 60, Main.instance.fontMsg, project.hasModelTextureHeight() ? project.getTextureHeight() : null, 1, Integer.MAX_VALUE, "default")); //TODO if already has override
		this.addElement(ok = new StandardButton(0, 0, 80, 20, 1, 4, isEdit ? "apply" : "create", this::apply) {
			@Override
			public boolean isEnabled()
			{
				return textureWidth.isValid() && super.isEnabled();
			}
		});
		this.addElement(cancel = new StandardButton(0, 0, 80, 20, 1, 4, "cancel", this::deactivate));
		this.isEdit = isEdit;
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		pane.setSize((w / 2) - 160, (h / 2) - 80, (w / 2) + 160, (h / 2) + 80);
		name.setSize((w / 2) - 120, (h / 2) - 70, (w / 2) + 120, (h / 2) - 50);
		textureWidth.setSize((w / 2) - 120, (h / 2) - 40, (w / 2) - 20, (h / 2) - 20);
		textureHeight.setSize((w / 2) + 20, (h / 2) - 40, (w / 2) + 120, (h / 2) - 20);
		ok.setSize((w / 2) - 120, (h / 2) + 80 - 30, (w / 2) - 40, (h / 2) + 80 - 10);
		cancel.setSize((w / 2) + 40, (h / 2) + 80 - 30, (w / 2) + 120, (h / 2) + 80 - 10);
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
	
	public void apply()
	{
		deactivate();
		Project project = Main.instance.project;
		project.onAction();
		String name = this.name.getText();
		if (!name.equals(project.getModelName())) name = MiscUtil.ensureUnique(name, project.getModelNames());
		if (isEdit)
		{
			project.setModelName(name);
			if (textureWidth.getText().length() == 0) project.removeModelTextureWidth();
			else project.setModelTextureWidth(textureWidth.getVal());
			if (textureHeight.getText().length() == 0) project.removeModelTextureHeight();
			else project.setModelTextureHeight(textureHeight.getVal());
			//TODO
		}
		else
		{
			IModel model = new MultiModel();
			project.addModel(name, model);
			if (textureWidth.getText().length() != 0) project.setModelTextureWidth(textureWidth.getVal());
			if (textureHeight.getText().length() != 0) project.setModelTextureHeight(textureHeight.getVal());
			//TODO
		}
	}
}