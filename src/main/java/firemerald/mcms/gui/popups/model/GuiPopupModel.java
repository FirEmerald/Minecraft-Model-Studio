package firemerald.mcms.gui.popups.model;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.model.ProjectModel;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.history.HistoryAction;

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
		this.addElement(name = new ComponentText(0, 10, 240, 30, Main.instance.fontMsg, isEdit ? project.getModelName() : "new model", null));
		this.addElement(textureWidth = new ComponentTextInt(0, 40, 100, 60, Main.instance.fontMsg, project.hasModelTextureWidth() ? project.getTextureWidth() : null, 1, Integer.MAX_VALUE, null, "default")); //TODO if already has override
		this.addElement(textureHeight = new ComponentTextInt(140, 40, 240, 60, Main.instance.fontMsg, project.hasModelTextureHeight() ? project.getTextureHeight() : null, 1, Integer.MAX_VALUE, null, "default")); //TODO if already has override
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
		main.guiShader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.guiShader.setColor(1, 1, 1, 1);
	}
	
	public void apply()
	{
		deactivate();
		final Project project = Main.instance.project;
		String name2 = this.name.getText();
		if (!name2.equals(project.getModelName())) name2 = MiscUtil.ensureUnique(name2, project.getModelNames());
		final String name = name2;
		final Integer width = textureWidth.getText().length() == 0 ? null : textureWidth.getVal();
		final Integer height = textureHeight.getText().length() == 0 ? null : textureHeight.getVal();
		final IModel<?, ? extends RenderObjectComponents<?>> model;
		if (isEdit)
		{
			model = project.getModel();
			final String oldName = project.getModelName();
			final Integer oldWidth = project.getModelTextureWidth();
			final Integer oldHeight = project.getModelTextureHeight();
			project.setModelName(name);
			if (textureWidth.getText().length() == 0) project.removeModelTextureWidth();
			else project.setModelTextureWidth(textureWidth.getVal());
			if (textureHeight.getText().length() == 0) project.removeModelTextureHeight();
			else project.setModelTextureHeight(textureHeight.getVal());
			project.onAction(new HistoryAction(() -> {
				project.setModelName(oldName, name);
				if (oldWidth == null) project.removeModelTextureWidth(name);
				else project.setModelTextureWidth(name, oldWidth);
				if (oldHeight == null) project.removeModelTextureHeight(name);
				else project.setModelTextureHeight(name, oldHeight);
			}, () -> {
				project.setModelName(name, oldName);
				if (width == null) project.removeModelTextureWidth(oldName);
				else project.setModelTextureWidth(oldName, width);
				if (height == null) project.removeModelTextureHeight(oldName);
				else project.setModelTextureHeight(oldName, height);
			}));
			//TODO
		}
		else
		{
			model = new ProjectModel();
			project.addModel(name, model);
			if (textureWidth.getText().length() != 0) project.setModelTextureWidth(textureWidth.getVal());
			if (textureHeight.getText().length() != 0) project.setModelTextureHeight(textureHeight.getVal());
			project.onAction(new HistoryAction(() -> project.removeModel(name), () -> {
				project.addModel(name, model);
				if (width != null) project.setModelTextureWidth(name, width);
				if (height != null) project.setModelTextureHeight(name, height);
			}));
			//TODO
		}
	}
}