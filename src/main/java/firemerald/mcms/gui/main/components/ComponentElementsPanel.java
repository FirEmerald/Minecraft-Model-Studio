package firemerald.mcms.gui.main.components;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.components.ButtonItem16;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.scrolling.ScrollBar;
import firemerald.mcms.gui.components.scrolling.ScrollBarH;
import firemerald.mcms.gui.components.scrolling.ScrollDown;
import firemerald.mcms.gui.components.scrolling.ScrollLeft;
import firemerald.mcms.gui.components.scrolling.ScrollRight;
import firemerald.mcms.gui.components.scrolling.ScrollUp;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.main.components.elements.ComponentEditSelector;
import firemerald.mcms.gui.main.components.items.ButtonSaveFileItem;
import firemerald.mcms.gui.popups.GuiPopupCopy;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.gui.popups.model.GuiPopupAddModel;
import firemerald.mcms.gui.popups.model.GuiPopupLoadModel;
import firemerald.mcms.gui.popups.model.GuiPopupModel;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Textures;

public class ComponentElementsPanel extends ComponentPanelMain
{
	public final ComponentEditSelector selector;
	public final ScrollBar scrollBar;
	public final ScrollUp scrollUp;
	public final ScrollDown scrollDown;
	public final ScrollBarH scrollBarH;
	public final ScrollLeft scrollLeft;
	public final ScrollRight scrollRight;
	public final ButtonItem16 newModel;
	public final ButtonItem16 addModel;
	public final ButtonItem16 loadModel;
	public final ButtonItem16 cloneModel;
	public final ButtonSaveFileItem saveModel;
	public final ButtonItem16 editModel;
	public final ButtonItem16 removeModel;
	public final SelectorButton modelSelector;
	
	public ComponentElementsPanel(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
		int w = x2 - x1, h = y2 - y1;
		this.addElement(selector = new ComponentEditSelector(0, 32, w - 16, h - 32, gui));
		this.addElement(scrollBar = new ScrollBar(w - 16, 48, w, h - 48, selector));
		this.addElement(scrollUp = new ScrollUp(w - 16, 32, w, 48, selector));
		this.addElement(scrollDown = new ScrollDown(w - 16, h - 48, w, h - 32, selector));
		selector.setScrollBar(scrollBar);
		this.addElement(scrollBarH = new ScrollBarH(16, h - 32, w - 32, h - 16, selector));
		this.addElement(scrollLeft = new ScrollLeft(0, h - 32, 16, h - 16, selector));
		this.addElement(scrollRight = new ScrollRight(w - 32, h - 32, w - 16, h - 16, selector));
		selector.setScrollBarH(scrollBarH);
		this.addElement(newModel = new ButtonItem16(0, h - 16, Textures.ITEM_NEW, () -> new GuiPopupModel(false).activate()));
		this.addElement(addModel = new ButtonItem16(16, h - 16, Textures.ITEM_ADD, () -> new GuiPopupAddModel().activate()));
		this.addElement(loadModel = new ButtonItem16(32, h - 16, Textures.ITEM_LOAD, () -> new GuiPopupLoadModel().activate()));
		this.addElement(cloneModel = new ButtonItem16(48, h - 16, Textures.ITEM_COPY, () -> {
			Project project = Main.instance.project;
			new GuiPopupCopy(MiscUtil.ensureUnique(project.getModelName(), project.getModelNames()), (name) -> project.addModel(name, project.getModel().cloneObject())).activate();
		}));
		this.addElement(saveModel = new ButtonSaveFileItem(64, h - 16, Textures.ITEM_SAVE, "obj", (file) -> {
			Writer writer = null;
			try
			{
				writer = new FileWriter(file);
				writer.write(RenderObjectComponents.createObj(Main.instance.project.getModel(), Main.instance.project.getModel().getPose()).toString());
			}
			catch (IOException e)
			{
				GuiPopupException.onException("Couldn't export model to " + file, e);
			}
			FileUtil.closeSafe(writer);
		}));
		this.addElement(editModel = new ButtonItem16(80, h - 16, Textures.ITEM_EDIT, () -> new GuiPopupModel(true).activate()));
		this.addElement(removeModel = new ButtonItem16(96, h - 16, Textures.ITEM_REMOVE, () -> {
			Main.instance.project.removeModel();
			Main.instance.project.onAction();
		}));
		newModel.enabled = addModel.enabled = true;
		Project project = Main.instance.project;
		this.addElement(modelSelector = new SelectorButton(112, h - 16, w, h, project.getModelName() == null ? project.useBackingSkeleton() ? "model skeleton" : "no model selected" : project.getModelName(), allModelNames(project), (ind, value) -> {
			Project proj = Main.instance.project;
			proj.onAction();
			if (proj.useBackingSkeleton() && ind == 0) proj.setModel(null);
			else proj.setModel(value);
		}));
		// TODO components
	}
	
	@Override
	public void onSize(int w, int h)
	{
		selector.setSize(0, 32, w - 16, h - 32);
		scrollBar.setSize(w - 16, 48, w, h - 48);
		scrollUp.setSize(w - 16, 32, w, 48);
		scrollDown.setSize(w - 16, h - 48, w, h - 32);
		scrollBarH.setSize(16, h - 32, w - 32, h - 16);
		scrollLeft.setSize(0, h - 32, 16, h - 16);
		scrollRight.setSize(w - 32, h - 32, w - 16, h - 16);
		newModel.setSize(0, h - 16);
		addModel.setSize(16, h - 16);
		loadModel.setSize(32, h - 16);
		cloneModel.setSize(48, h - 16);
		saveModel.setSize(64, h - 16);
		editModel.setSize(80, h - 16);
		removeModel.setSize(96, h - 16);
		modelSelector.setSize(112, h - 16, w, h);
		// TODO components
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (reason == GuiUpdate.PROJECT || reason == GuiUpdate.MODEL)
		{
			Project project = Main.instance.project;
			if (project.getModelName() == null) loadModel.enabled = cloneModel.enabled = saveModel.enabled = editModel.enabled = removeModel.enabled = false;
			else loadModel.enabled = cloneModel.enabled = saveModel.enabled = editModel.enabled = removeModel.enabled = true;
			modelSelector.setValues(allModelNames(project));
			modelSelector.setText(project.getModelName() == null ? project.useBackingSkeleton() ? "model skeleton" : "no model selected" : project.getModelName());
		}
	}
	
	public static String[] allModelNames(Project project)
	{
		Set<String> modelNames = project.getModelNames();
		String[] a2 = modelNames.toArray(new String[modelNames.size()]);
		if (!project.useBackingSkeleton()) return a2;
		String[] array = new String[a2.length + 1];
		array[0] = "model skeleton";
		System.arraycopy(a2, 0, array, 1, a2.length);
		return array;
	}
}