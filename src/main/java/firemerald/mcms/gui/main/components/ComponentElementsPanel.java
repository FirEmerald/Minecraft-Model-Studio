package firemerald.mcms.gui.main.components;

import java.util.Set;

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
import firemerald.mcms.gui.main.components.elements.ComponentEditSelector;
import firemerald.mcms.gui.main.components.items.ButtonAction;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.hotkey.Action;

public class ComponentElementsPanel extends ComponentPanelMain
{
	public final ComponentEditSelector selector;
	public final ScrollBar scrollBar;
	public final ScrollUp scrollUp;
	public final ScrollDown scrollDown;
	public final ScrollBarH scrollBarH;
	public final ScrollLeft scrollLeft;
	public final ScrollRight scrollRight;
	public final ButtonAction newModel;
	public final ButtonAction addModel;
	public final ButtonAction loadModel;
	public final ButtonAction cloneModel;
	public final ButtonAction saveModel;
	public final ButtonAction editModel;
	public final ButtonAction removeModel;
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
		this.addElement(newModel = new ButtonAction(0, h - 16, Textures.ITEM_NEW, Action.NEW_MODEL));
		this.addElement(addModel = new ButtonAction(16, h - 16, Textures.ITEM_ADD, Action.ADD_MODEL));
		this.addElement(loadModel = new ButtonAction(32, h - 16, Textures.ITEM_LOAD, Action.LOAD_MODEL));
		this.addElement(cloneModel = new ButtonAction(48, h - 16, Textures.ITEM_COPY, Action.CLONE_MODEL));
		this.addElement(saveModel = new ButtonAction(64, h - 16, Textures.ITEM_SAVE, Action.EXPORT_MODEL));
		this.addElement(editModel = new ButtonAction(80, h - 16, Textures.ITEM_EDIT, Action.EDIT_MODEL));
		this.addElement(removeModel = new ButtonAction(96, h - 16, Textures.ITEM_REMOVE, Action.REMOVE_MODEL));
		newModel.enabled = addModel.enabled = true;
		Project project = Main.instance.project;
		this.addElement(modelSelector = new SelectorButton(112, h - 16, w, h, project.getModelName() == null ? project.useBackingSkeleton() ? "model skeleton" : "no model selected" : project.getModelName(), allModelNames(project), (ind, value) -> {
			Project proj = Main.instance.project;
			//proj.onAction(); TODO undo?
			if (proj.useBackingSkeleton() && ind == 0) proj.setModel((String) null);
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