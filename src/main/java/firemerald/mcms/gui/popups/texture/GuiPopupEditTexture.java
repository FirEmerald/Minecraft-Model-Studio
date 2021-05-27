package firemerald.mcms.gui.popups.texture;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.texture.space.Material;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.history.HistoryActionMaterialEdit;

public class GuiPopupEditTexture extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentText name;
	public final ComponentFloatingLabel labelWidth;
	public final ComponentTextInt width;
	public final ComponentIncrementInt widthUp, widthDown;
	public final ComponentFloatingLabel labelHeight;
	public final ComponentTextInt height;
	public final ComponentIncrementInt heightUp, heightDown;
	public final StandardButton ok, cancel;
	
	public GuiPopupEditTexture()
	{
		Project project = Main.instance.project;
		final int cw = 180;
		final int ch = 100;
		final int cx = 20;
		final int cy = 20;
		final int lSize = 44;
		Material tex = project.getTexture();
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, project.getTextureName(), null));
		y += 20;
		this.addElement(labelWidth = new ComponentFloatingLabel(cx, y, cx + lSize, y + 20, Main.instance.fontMsg, "width"));
		this.addElement(width = new ComponentTextInt(cx + lSize, y, cx + cw - 10, y + 20, Main.instance.fontMsg, tex.getDiffuse().w, 1, Integer.MAX_VALUE, null, "default"));
		this.addElement(widthUp = new ComponentIncrementInt(cx + cw - 10, y, width, 1));
		this.addElement(widthDown = new ComponentIncrementInt(cx + cw - 10, y + 10, width, -1));
		y += 20;
		this.addElement(labelHeight = new ComponentFloatingLabel(cx, y, cx + lSize, y + 20, Main.instance.fontMsg, "height"));
		this.addElement(height = new ComponentTextInt(cx + lSize, y, cx + cw - 10, y + 20, Main.instance.fontMsg, tex.getDiffuse().h, 1, Integer.MAX_VALUE, null, "default"));
		this.addElement(heightUp = new ComponentIncrementInt(cx + cw - 10, y, height, 1));
		this.addElement(heightDown = new ComponentIncrementInt(cx + cw - 10, y + 10, height, -1));
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "apply", this::apply));
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
		final int lSize = 44;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, cy, cx + cw, cy + 20);
		y += 20;
		labelWidth.setSize(cx, y, cx + lSize, y + 20);
		width.setSize(cx + lSize, y, cx + cw - 10, y + 20);
		widthUp.setPosition(cx + cw - 10, y);
		widthDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		labelHeight.setSize(cx, y, cx + lSize, y + 20);
		height.setSize(cx + lSize, y, cx + cw - 10, y + 20);
		heightUp.setPosition(cx + cw - 10, y);
		heightDown.setPosition(cx + cw - 10, y + 10);
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
		final String oldName = project.getTextureName();
		final String newName;
		String name = this.name.getText();
		if (!name.equals(oldName)) newName = MiscUtil.ensureUnique(name, project.getTextureNames());
		else newName = name;
		final Material tex = project.getTexture();
		final int oldWidth = tex.getDiffuse().w;
		final int oldHeight = tex.getDiffuse().h;
		final int w = width.getText().length() == 0 ? project.getTextureWidth() : width.getVal();
		final int h = height.getText().length() == 0 ? project.getTextureHeight() : height.getVal();
		project.onAction(new HistoryActionMaterialEdit(tex, oldName, newName, oldWidth, oldHeight));
		if (tex.getDiffuse().w != w || tex.getDiffuse().h != h) tex.resize(w, h);
		if (!newName.equals(oldName)) project.setTextureName(newName);
	}
}