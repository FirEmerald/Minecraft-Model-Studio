package firemerald.mcms.gui.main.components;

import java.io.IOException;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.gui.components.ButtonItem16;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.scrolling.ScrollBar;
import firemerald.mcms.gui.components.scrolling.ScrollBarH;
import firemerald.mcms.gui.components.scrolling.ScrollDown;
import firemerald.mcms.gui.components.scrolling.ScrollLeft;
import firemerald.mcms.gui.components.scrolling.ScrollRight;
import firemerald.mcms.gui.components.scrolling.ScrollUp;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.main.components.items.ButtonOpenFileItem;
import firemerald.mcms.gui.main.components.items.ButtonSaveFileItem;
import firemerald.mcms.gui.main.components.texture.TextureViewer;
import firemerald.mcms.gui.popups.GuiPopupCopy;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.gui.popups.texture.GuiPopupEditTexture;
import firemerald.mcms.gui.popups.texture.GuiPopupLoadTexture;
import firemerald.mcms.gui.popups.texture.GuiPopupNewTexture;
import firemerald.mcms.texture.ReloadingTexture;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Textures;

public class ComponentTexturePanel extends ComponentPanelMain
{
	public final TextureViewer viewer;
	public final ScrollBar scrollBar;
	public final ScrollUp scrollUp;
	public final ScrollDown scrollDown;
	public final ScrollBarH scrollBarH;
	public final ScrollLeft scrollLeft;
	public final ScrollRight scrollRight;
	public final ButtonItem16 newTexture;
	public final ButtonItem16 addTexture;
	public final ButtonOpenFileItem loadTexture;
	public final ButtonItem16 cloneTexture;
	public final ButtonSaveFileItem saveTexture;
	public final ButtonItem16 editTexture;
	public final ButtonItem16 removeTexture;
	public final SelectorButton textureSelector;
	
	public ComponentTexturePanel(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
		int w = x2 - x1, h = y2 - y1;
		this.addElement(viewer = new TextureViewer(0, 0, w - 16, h - 32));
		this.addElement(scrollBar = new ScrollBar(w - 16, 16, w, h - 48, viewer));
		this.addElement(scrollUp = new ScrollUp(w - 16, 0, w, 16, viewer));
		this.addElement(scrollDown = new ScrollDown(w - 16, h - 48, w, h - 32, viewer));
		viewer.setScrollBar(scrollBar);
		this.addElement(scrollBarH = new ScrollBarH(16, h - 32, w - 32, h - 16, viewer));
		this.addElement(scrollLeft = new ScrollLeft(0, h - 32, 16, h - 16, viewer));
		this.addElement(scrollRight = new ScrollRight(w - 32, h - 32, w - 16, h - 16, viewer));
		viewer.setScrollBarH(scrollBarH);
		this.addElement(newTexture = new ButtonItem16(0, h - 16, Textures.ITEM_NEW, () -> new GuiPopupNewTexture().activate()));
		this.addElement(addTexture = new ButtonItem16(16, h - 16, Textures.ITEM_ADD, () -> new GuiPopupLoadTexture().activate()));
		this.addElement(loadTexture = new ButtonOpenFileItem(32, h - 16, Textures.ITEM_LOAD, FileUtils.getLoadImageFilter(), (file) -> //TODO
		{
			try
			{
				Main.instance.project.onAction();
				Main.instance.project.addTexture(Main.instance.project.getTextureName(), new ReloadingTexture(file));
			}
			catch (IOException e)
			{
				GuiPopupException.onException("Couldn't load texture file: " + file.getAbsolutePath().toString(), e, Level.WARN);
			}
			//Main.instance.project.getTexture().load(file);
			//Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
		}));
		this.addElement(cloneTexture = new ButtonItem16(48, h - 16, Textures.ITEM_COPY, () -> {
			Project project = Main.instance.project;
			new GuiPopupCopy(MiscUtil.ensureUnique(project.getTextureName(), project.getTextureNames()), (name) -> project.addTexture(name, project.getTexture().cloneObject())).activate();
		}));
		this.addElement(saveTexture = new ButtonSaveFileItem(64, h - 16, Textures.ITEM_SAVE, FileUtils.getSaveImageFilter(), (file) -> Main.instance.project.getTexture().saveTexture(file)));
		this.addElement(editTexture = new ButtonItem16(80, h - 16, Textures.ITEM_EDIT, () -> new GuiPopupEditTexture().activate()));
		this.addElement(removeTexture = new ButtonItem16(96, h - 16, Textures.ITEM_REMOVE, () -> {
			Main.instance.project.onAction();
			Main.instance.project.removeTexture();
		}));
		newTexture.enabled = addTexture.enabled = true;
		this.addElement(textureSelector = new SelectorButton(112, h - 16, w, h, Main.instance.project.getTextureNames().isEmpty() ? "no textures available" : Main.instance.project.getTextureName() == null ? "no texture selected" : Main.instance.project.getTextureName(), MiscUtil.array("none", Main.instance.project.getTextureNames()), (ind, value) -> {
			Main.instance.project.onAction();
			if (ind == 0)
			{
				Main.instance.project.setTexture(null);
				return "no texture selected";
			}
			else
			{
				Main.instance.project.setTexture(value);
				return value;
			}
		}));
		// TODO components
	}
	
	@Override
	public void onSize(int w, int h)
	{
		// TODO components
		viewer.setSize(0, 0, w - 16, h - 32);
		scrollBar.setSize(w - 16, 16, w, h - 48);
		scrollUp.setSize(w - 16, 0, w, 16);
		scrollDown.setSize(w - 16, h - 48, w, h - 32);
		scrollBarH.setSize(16, h - 32, w - 32, h - 16);
		scrollLeft.setSize(0, h - 32, 16, h - 16);
		scrollRight.setSize(w - 32, h - 32, w - 16, h - 16);
		newTexture.setSize(0, h - 16);
		addTexture.setSize(16, h - 16);
		loadTexture.setSize(32, h - 16);
		cloneTexture.setSize(48, h - 16);
		saveTexture.setSize(64, h - 16);
		editTexture.setSize(80, h - 16);
		removeTexture.setSize(96, h - 16);
		textureSelector.setSize(112, h - 16, w, h);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (reason == GuiUpdate.PROJECT || reason == GuiUpdate.TEXTURE)
		{
			Project project = Main.instance.project;
			if (project.getTextureName() == null) loadTexture.enabled = cloneTexture.enabled = saveTexture.enabled = editTexture.enabled = removeTexture.enabled = false;
			else
			{
				loadTexture.enabled = cloneTexture.enabled = saveTexture.enabled = editTexture.enabled = removeTexture.enabled = true;
				//TODO set load/save filter
			}
			textureSelector.setValues(MiscUtil.array("none", Main.instance.project.getTextureNames()));
			textureSelector.setText(Main.instance.project.getTextureNames().isEmpty() ? "no textures available" : project.getTextureName() == null ? "no texture selected" : project.getTextureName());
		}
	}
}