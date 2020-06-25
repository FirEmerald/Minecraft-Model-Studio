package firemerald.mcms.util.hotkey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.popups.GuiPopupCopy;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.gui.popups.model.GuiPopupAddModel;
import firemerald.mcms.gui.popups.model.GuiPopupLoadModel;
import firemerald.mcms.gui.popups.model.GuiPopupModel;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;

public class Action
{
	public static final Map<String, Action> ACTIONS = new LinkedHashMap<>();
	
	public static final Action SAVE = new Action("save", "save project", () -> Main.instance.project.save(), new HotKey(Key.S, Modifier.CONTROL));
	public static final Action SAVE_AS = new Action("save_as", "save project as", () -> Main.instance.project.saveAs(), new HotKey(Key.S, Modifier.SHIFT, Modifier.CONTROL));
	public static final Action LOAD = new Action("load", "open project", () -> Main.instance.project.loadFrom(), new HotKey(Key.O, Modifier.CONTROL));
	public static final Action EXPORT = new Action("export", "export project", () -> Main.instance.project.export(), new HotKey(Key.E, Modifier.CONTROL));
	public static final Action UNDO = new Action("undo", "undo last action", () -> Main.instance.project.undo(), new HotKey(Key.Z, Modifier.CONTROL));
	public static final Action REDO = new Action("redo", "redo last undid action", () -> Main.instance.project.redo(), new HotKey(Key.Y, Modifier.CONTROL));
	public static final Action NEW_MODEL = new Action("new_model", "add a new model", () -> new GuiPopupModel(false).activate(), new HotKey(Key.UNKNOWN));
	public static final Action ADD_MODEL = new Action("add_model", "add a new model from an OBJ model", () -> new GuiPopupAddModel().activate(), new HotKey(Key.UNKNOWN));
	public static final Action LOAD_MODEL = new Action("load_model", "loads mesh components from an OBJ model", () -> new GuiPopupLoadModel().activate(), new HotKey(Key.UNKNOWN));
	public static final Action CLONE_MODEL = new Action("clone_model", "Creates a copy of a model", () -> {
		Project project = Main.instance.project;
		new GuiPopupCopy<>(MiscUtil.ensureUnique(project.getModelName(), project.getModelNames()), project.getModel(), (name, copy) -> project.addModel(name, copy), (name) -> project.removeModel(name)).activate();
	}, new HotKey(Key.UNKNOWN));
	public static final Action EXPORT_MODEL = new Action("export_model", "Exports the model to an OBJ file using it's current pose", () -> {
		File file = FileUtils.getSaveFile("obj", "");
		if (file != null)
		{
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
		}
	}, new HotKey(Key.UNKNOWN));
	public static final Action DEBUG_STENCIL = new Action("debug_stencil", "debug the stencil buffer (EXTREME LAG!) by saving the contents at each update", () -> RenderUtil.save = !RenderUtil.save, new HotKey(Key.UNKNOWN));

	public final String id;
	public final String display_name;
	public final Runnable action;
	public final HotKey def;
	
	public Action(String name, Runnable action, HotKey def)
	{
		this(name, name, action, def);
	}
	
	public Action(String id, String display_name, Runnable action, HotKey def)
	{
		this.id = id;
		this.display_name = display_name;
		this.action = action;
		this.def = def;
		ACTIONS.put(id, this);
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}