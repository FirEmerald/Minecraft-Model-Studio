package firemerald.mcms.gui.main.components;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.scrolling.ScrollBar;
import firemerald.mcms.gui.components.scrolling.ScrollBarH;
import firemerald.mcms.gui.components.scrolling.ScrollDown;
import firemerald.mcms.gui.components.scrolling.ScrollLeft;
import firemerald.mcms.gui.components.scrolling.ScrollRight;
import firemerald.mcms.gui.components.scrolling.ScrollUp;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.main.components.items.ButtonAction;
import firemerald.mcms.gui.main.components.texture.TextureViewer;
import firemerald.mcms.texture.space.EnumTextureSpace;
import firemerald.mcms.util.ApplicationState.EnumLayout;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.hotkey.Action;

public class ComponentTexturePanel extends ComponentPanelMain
{
	public final SelectorButton textureSpaceSelector;
	public final TextureViewer viewer;
	public final ScrollBar scrollBar;
	public final ScrollUp scrollUp;
	public final ScrollDown scrollDown;
	public final ScrollBarH scrollBarH;
	public final ScrollLeft scrollLeft;
	public final ScrollRight scrollRight;
	public final ButtonAction newTexture;
	public final ButtonAction addTexture;
	public final ButtonAction removeSpace;
	public final ButtonAction loadTexture;
	public final ButtonAction cloneTexture;
	public final ButtonAction saveTexture;
	public final ButtonAction editTexture;
	public final ButtonAction removeTexture;
	public final SelectorButton textureSelector;
	
	public ComponentTexturePanel(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
		int w = x2 - x1, h = y2 - y1;
		this.addElement(textureSpaceSelector = new SelectorButton(0, 0, w, 16, Main.instance.activeSpace, EnumTextureSpace.values(), this::setActiveSpace));
		this.addElement(viewer = new TextureViewer(0, 0, w - 16, h - 32));
		this.addElement(scrollBar = new ScrollBar(w - 16, 16, w, h - 48, viewer));
		this.addElement(scrollUp = new ScrollUp(w - 16, 0, w, 16, viewer));
		this.addElement(scrollDown = new ScrollDown(w - 16, h - 48, w, h - 32, viewer));
		viewer.setScrollBar(scrollBar);
		this.addElement(scrollBarH = new ScrollBarH(16, h - 32, w - 32, h - 16, viewer));
		this.addElement(scrollLeft = new ScrollLeft(0, h - 32, 16, h - 16, viewer));
		this.addElement(scrollRight = new ScrollRight(w - 32, h - 32, w - 16, h - 16, viewer));
		viewer.setScrollBarH(scrollBarH);
		this.addElement(newTexture = new ButtonAction(0, h - 16, Textures.ITEM_NEW, Action.NEW_TEXTURE));
		this.addElement(addTexture = new ButtonAction(16, h - 16, Textures.ITEM_ADD, Action.ADD_TEXTURE));
		removeSpace = new ButtonAction(16, h - 16, Textures.ITEM_ADD, Action.ADD_TEXTURE);
		this.addElement(loadTexture = new ButtonAction(32, h - 16, Textures.ITEM_LOAD, Action.LOAD_TEXTURE));
		this.addElement(cloneTexture = new ButtonAction(48, h - 16, Textures.ITEM_COPY, Action.CLONE_TEXTURE));
		this.addElement(saveTexture = new ButtonAction(64, h - 16, Textures.ITEM_SAVE, Action.SAVE_TEXTURE));
		this.addElement(editTexture = new ButtonAction(80, h - 16, Textures.ITEM_EDIT, Action.EDIT_TEXTURE));
		this.addElement(removeTexture = new ButtonAction(96, h - 16, Textures.ITEM_REMOVE, Action.REMOVE_TEXTURE));
		newTexture.enabled = addTexture.enabled = true;
		this.addElement(textureSelector = new SelectorButton(112, h - 16, w, h, Main.instance.project.getTextureNames().isEmpty() ? "no textures available" : Main.instance.project.getTextureName() == null ? "no texture selected" : Main.instance.project.getTextureName(), MiscUtil.array("none", Main.instance.project.getTextureNames()), (ind, value) -> {
			//Main.instance.project.onAction(); TODO undo?
			if (ind == 0)
			{
				Main.instance.project.setTexture((String) null);
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
	
	public void setActiveSpace(EnumTextureSpace texSpace)
	{
		if (Main.instance.activeSpace == EnumTextureSpace.DIFFUSE)
		{
			if (texSpace != EnumTextureSpace.DIFFUSE)
			{
				this.removeElement(addTexture);
				this.addElement(removeSpace);
				addTexture.enabled = false;
				removeSpace.enabled = true;
			}
		}
		else if (texSpace == EnumTextureSpace.DIFFUSE)
		{
			this.removeElement(addTexture);
			this.addElement(addTexture);
			removeSpace.enabled = false;
			addTexture.enabled = true;
		}
		Main.instance.activeSpace = texSpace;
	}
	
	@Override
	public void onSize(int w, int h)
	{
		// TODO components
		textureSpaceSelector.setSize(0, 0, w, 16);
		if (Main.instance.state.getLayout() == EnumLayout.LAYOUT_A)
		{
			//new layout
			viewer.setSize(0, 32, w - 16, h - 16);
			scrollBar.setSize(w - 16, 48, w, h - 32);
			scrollUp.setSize(w - 16, 32, w, 48);
			scrollDown.setSize(w - 16, h - 32, w, h - 16);
			scrollBarH.setSize(16, h - 16, w - 32, h);
			scrollLeft.setSize(0, h - 16, 16, h);
			scrollRight.setSize(w - 32, h - 16, w - 16, h);
			newTexture.setSize(0, 16);
			addTexture.setSize(16, 16);
			removeSpace.setSize(16, 16);
			loadTexture.setSize(32, 16);
			cloneTexture.setSize(48, 16);
			saveTexture.setSize(64, 16);
			editTexture.setSize(80, 16);
			removeTexture.setSize(96, 16);
			textureSelector.setSize(112, 16, w, 32);
		}
		else
		{
			//old layout
			viewer.setSize(0, 16, w - 16, h - 32);
			scrollBar.setSize(w - 16, 32, w, h - 48);
			scrollUp.setSize(w - 16, 16, w, 32);
			scrollDown.setSize(w - 16, h - 48, w, h - 32);
			scrollBarH.setSize(16, h - 32, w - 32, h - 16);
			scrollLeft.setSize(0, h - 32, 16, h - 16);
			scrollRight.setSize(w - 32, h - 32, w - 16, h - 16);
			newTexture.setSize(0, h - 16);
			addTexture.setSize(16, h - 16);
			removeSpace.setSize(16, h - 16);
			loadTexture.setSize(32, h - 16);
			cloneTexture.setSize(48, h - 16);
			saveTexture.setSize(64, h - 16);
			editTexture.setSize(80, h - 16);
			removeTexture.setSize(96, h - 16);
			textureSelector.setSize(112, h - 16, w, h);
		}
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