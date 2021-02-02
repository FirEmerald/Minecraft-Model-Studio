package firemerald.mcms.gui.popups.project;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ComponentToggle;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupEditProject extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentText name;
	public final ComponentFloatingLabel labelWidth;
	public final ComponentTextInt width;
	public final ComponentIncrementInt widthUp, widthDown;
	public final ComponentFloatingLabel labelHeight;
	public final ComponentTextInt height;
	public final ComponentIncrementInt heightUp, heightDown;
	public final ComponentFloatingLabel labelScale;
	public final ComponentTextFloat scale;
	public final ComponentIncrementFloat scaleUp, scaleDown;
	public final ComponentFloatingLabel labelSkeleton;
	public final ComponentToggle skeleton;
	public final StandardButton ok, cancel;
	
	public GuiPopupEditProject()
	{
		final int cw = 180;
		final int ch = 140;
		final int cx = 20;
		final int cy = 20;
		Project project = Main.instance.project;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, project.getName(), null));
		y += 20;
		this.addElement(labelWidth = new ComponentFloatingLabel(cx, y, cx + cw - 70, y + 20, Main.instance.fontMsg, "texture width"));
		this.addElement(width = new ComponentTextInt(cx + cw - 70, y, cx + cw - 10, y + 20, Main.instance.fontMsg, project.getProjectTextureWidth(), 1, Integer.MAX_VALUE, null));
		this.addElement(widthUp = new ComponentIncrementInt(cx + cw - 10, y, width, 1));
		this.addElement(widthDown = new ComponentIncrementInt(cx + cw - 10, y + 10, width, -1));
		y += 20;
		this.addElement(labelHeight = new ComponentFloatingLabel(cx, y, cx + cw - 70, y + 20, Main.instance.fontMsg, "texture height"));
		this.addElement(height = new ComponentTextInt(cx + cw - 70, y, cx + cw - 10, y + 20, Main.instance.fontMsg, project.getProjectTextureHeight(), 1, Integer.MAX_VALUE, null));
		this.addElement(heightUp = new ComponentIncrementInt(cx + cw - 10, y, height, 1));
		this.addElement(heightDown = new ComponentIncrementInt(cx + cw - 10, y + 10, height, -1));
		y += 20;
		this.addElement(labelScale = new ComponentFloatingLabel(cx, y, cx + cw - 70, y + 20, Main.instance.fontMsg, "pixels per block"));
		this.addElement(scale = new ComponentTextFloat(cx + cw - 70, y, cx + cw - 10, y + 20, Main.instance.fontMsg, 1f / project.getScale(), Float.MIN_VALUE, Float.MAX_VALUE, null));
		this.addElement(scaleUp = new ComponentIncrementFloat(cx + cw - 10, y, scale, 1));
		this.addElement(scaleDown = new ComponentIncrementFloat(cx + cw - 10, y + 10, scale, -1));
		y += 20;
		this.addElement(labelSkeleton = new ComponentFloatingLabel(cx, y, cx + cw - 20, y + 20, Main.instance.fontMsg, "use backing skeleton"));
		this.addElement(skeleton = new ComponentToggle(cx + cw - 15, y + 5, cx + cw - 5, y + 15, project.useBackingSkeleton(), null));
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "ok", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 140;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, cy, cx + cw, cy + 20);
		y += 20;
		labelWidth.setSize(cx, y, cx + cw - 70, y + 20);
		width.setSize(cx + cw - 70, y, cx + cw - 10, y + 20);
		widthUp.setPosition(cx + cw - 10, y);
		widthDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		labelHeight.setSize(cx, y, cx + cw - 70, y + 20);
		height.setSize(cx + cw - 70, y, cx + cw - 10, y + 20);
		heightUp.setPosition(cx + cw - 10, y);
		heightDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		labelScale.setSize(cx, y, cx + cw - 70, y + 20);
		scale.setSize(cx + cw - 70, y, cx + cw - 10, y + 20);
		scaleUp.setPosition(cx + cw - 10, y);
		scaleDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		labelSkeleton.setSize(cx, y, cx + cw - 20, y + 20);
		skeleton.setSize(cx + cw - 15, y + 5, cx + cw - 5, y + 15);
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
	
	public void apply() //TODO undo
	{
		deactivate();
		Project project = Main.instance.project;
		final String oldName = project.getName();
		final String newName = name.getText();
		final int oldTexW = project.getProjectTextureWidth();
		final int newTexW = width.getVal();
		final int oldTexH = project.getProjectTextureHeight();
		final int newTexH = height.getVal();
		final float oldScale = project.getScale();
		final float newScale = 1 / scale.getVal();
		final boolean oldSkel = project.useBackingSkeleton();
		final boolean newSkel  = skeleton.state;
		if (
				!oldName.equals(newName) || 
				oldTexW != newTexW || 
				oldTexH != newTexH || 
				oldScale != newScale ||
				oldSkel != newSkel
				)
		{
			if (!oldName.equals(newName)) project.setName(newName);
			if (oldTexW != newTexW) project.setTextureWidth(newTexW);
			if (oldTexH != newTexH) project.setTextureHeight(newTexH);
			if (oldScale != newScale) project.setScale(newScale);
			if (oldSkel != newSkel) project.setBackingSkeleton(newSkel);
			Main.instance.onGuiUpdate(GuiUpdate.PROJECT);
			project.onAction(new HistoryAction(() -> {
				if (!oldName.equals(newName)) project.setName(oldName);
				if (oldTexW != newTexW) project.setTextureWidth(oldTexW);
				if (oldTexH != newTexH) project.setTextureHeight(oldTexH);
				if (oldScale != newScale) project.setScale(oldScale);
				if (oldSkel != newSkel) project.setBackingSkeleton(oldSkel);
				Main.instance.onGuiUpdate(GuiUpdate.PROJECT);
			}, () -> {
				if (!oldName.equals(newName)) project.setName(newName);
				if (oldTexW != newTexW) project.setTextureWidth(newTexW);
				if (oldTexH != newTexH) project.setTextureHeight(newTexH);
				if (oldScale != newScale) project.setScale(newScale);
				if (oldSkel != newSkel) project.setBackingSkeleton(newSkel);
				Main.instance.onGuiUpdate(GuiUpdate.PROJECT);
			}));
		}
	}
}