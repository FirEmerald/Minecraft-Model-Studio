package firemerald.mcms.gui.popups.texture;

import java.io.File;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ButtonItem20;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.texture.ReloadingTexture;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Textures;

public class GuiPopupLoadTexture extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentText name;
	public final ComponentText file;
	public final ButtonItem20 browse;
	public final StandardButton ok, cancel;
	
	public GuiPopupLoadTexture()
	{
		Project project = Main.instance.project;
		final int cw = 180;
		final int ch = 80;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, MiscUtil.ensureUnique("Untitled", project.getTextureNames()), text -> {}));
		y += 20;
		this.addElement(file = new ComponentText(cx, y, cx + cw - 20, y + 20, Main.instance.fontMsg, "", text -> {}));
		this.addElement(browse = new ButtonItem20(cx + cw - 20, y, Textures.ITEM_BROWSE, () -> {
			File file = FileUtils.getOpenFile(FileUtils.getLoadImageFilter(), "");
			if (file != null) this.file.setText(file.toString());
		}));
		browse.enabled = true;
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "load", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 80;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, cy, cx + cw, cy + 20);
		y += 20;
		file.setSize(cx, y, cx + cw - 20, y + 20);
		browse.setSize(cx + cw - 20, y);
		y += 20;
		ok.setSize(cx, cy + ch - 20, cx + 80, cy + ch);
		cancel.setSize(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch);
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
		try
		{
			Texture tex = new ReloadingTexture(new File(file.getText()));
			String name = MiscUtil.ensureUnique(this.name.getText(), project.getTextureNames());
			project.onAction();
			project.addTexture(name, tex);
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load texture file: " + file.getText(), e, Level.WARN);
		}
	}
}