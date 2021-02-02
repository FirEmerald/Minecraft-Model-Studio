package firemerald.mcms.gui.popups.model;

import java.io.File;

import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ButtonItem20;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.model.ComponentMeshTrue;
import firemerald.mcms.model.ProjectModel;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.history.HistoryAction;
import firemerald.mcms.util.mesh.ModelMesh;

public class GuiPopupAddModel extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentText name;
	public final ComponentText file;
	public final ButtonItem20 browse;
	public final ComponentFloatingLabel scaleLabel;
	public final ComponentTextFloat scale;
	public final StandardButton ok, cancel;
	
	public GuiPopupAddModel()
	{
		Project project = Main.instance.project;
		final int cw = 180;
		final int ch = 100;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, MiscUtil.ensureUnique("Untitled", project.getAnimationNames()), null));
		y += 20;
		this.addElement(file = new ComponentText(cx, y, cx + cw - 20, y + 20, Main.instance.fontMsg, "", text -> {}));
		this.addElement(browse = new ButtonItem20(cx + cw - 20, y, Textures.ITEM_BROWSE, () -> {
			File file = FileUtils.getOpenFile(null, "obj");
			if (file != null) this.file.setText(file.toString());
		}));
		browse.enabled = true;
		y += 20;
		this.addElement(scaleLabel = new ComponentFloatingLabel(cx, y, cx + 36, y + 20, Main.instance.fontMsg, "scale"));
		this.addElement(scale = new ComponentTextFloat(cx + 36, y, cx + cw, y + 20, Main.instance.fontMsg, 1f / project.getScale(), 0, Float.POSITIVE_INFINITY, null));
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "load", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 100;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, cy, cx + cw, cy + 20);
		y += 20;
		file.setSize(cx, y, cx + cw - 20, y + 20);
		browse.setSize(cx + cw - 20, y);
		y += 20;
		scaleLabel.setSize(cx, y, cx + 36, y + 20);
		scale.setSize(cx + 36, y, cx + cw, y + 20);
		y += 20;
		ok.setSize(cx, cy + ch - 20, cx + 80, cy + ch);
		cancel.setSize(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch);
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
		Project project = Main.instance.project;
		try
		{
			ObjData data = new ObjData(new File(file.getText()));
			final ProjectModel model = new ProjectModel();
			RenderObjectComponents.Actual root = new RenderObjectComponents.Actual(name.getText(), new Transformation(), null);
			data.groupObjects.forEach((name, mesh) -> {
				ModelMesh m = RenderUtil.makeMesh(mesh, data, new Matrix4d().scale(scale.getVal()));
				root.addComponent(new ComponentMeshTrue(m, name));
			});
			model.addRootBone(root, true);
			final String name = MiscUtil.ensureUnique(this.name.getText(), project.getModelNames());
			project.addModel(name, model);
			project.onAction(new HistoryAction(() -> project.removeModel(name), () -> project.addModel(name, model)));
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load model file: " + file.getText(), e);
		}
	}
}