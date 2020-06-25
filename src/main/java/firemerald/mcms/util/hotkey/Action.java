package firemerald.mcms.util.hotkey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.IAnimation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.Skeleton;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.api.util.FileUtil.DataType;
import firemerald.mcms.gui.popups.GuiPopupCopy;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.gui.popups.GuiPopupUnsavedChanges;
import firemerald.mcms.gui.popups.animation.GuiPopupEditAnimation;
import firemerald.mcms.gui.popups.animation.GuiPopupLoadAnimation;
import firemerald.mcms.gui.popups.animation.GuiPopupNewAnimation;
import firemerald.mcms.gui.popups.model.GuiPopupAddModel;
import firemerald.mcms.gui.popups.model.GuiPopupLoadModel;
import firemerald.mcms.gui.popups.model.GuiPopupModel;
import firemerald.mcms.gui.popups.project.GuiPopupNewProject;
import firemerald.mcms.gui.popups.texture.GuiPopupEditTexture;
import firemerald.mcms.gui.popups.texture.GuiPopupLoadTexture;
import firemerald.mcms.gui.popups.texture.GuiPopupNewTexture;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.texture.ReloadingTexture;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.history.HistoryAction;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;

public class Action
{
	public static final Map<String, Action> ACTIONS = new LinkedHashMap<>();
	private static final BooleanSupplier CHECK_MODEL = () -> Main.instance.project.getModel() != null;
	private static final BooleanSupplier CHECK_RIG = () -> Main.instance.project.getRig() != null;
	private static final BooleanSupplier CHECK_TEXTURE = () -> Main.instance.project.getTexture() != null;
	private static final BooleanSupplier CHECK_ANIMATION = () -> Main.instance.project.getAnimation() != null;

	public static final Action NEW_PROJECT = new Action("new_project", "new project", () -> {
		if (Main.instance.project.needsSave()) new GuiPopupUnsavedChanges(() -> new GuiPopupNewProject().activate()).activate();
		else new GuiPopupNewProject().activate();
	}, new HotKey(Key.N, Modifier.CONTROL));
	public static final Action SAVE_PROJECT = new Action("save_project", "save project", () -> Main.instance.project.save(), new HotKey(Key.S, Modifier.CONTROL));
	public static final Action SAVE_PROJECT_AS = new Action("save_project_as", "save project as", () -> Main.instance.project.saveAs(), new HotKey(Key.S, Modifier.SHIFT, Modifier.CONTROL));
	public static final Action LOAD_PROJECT = new Action("load_project", "open project", () -> Main.instance.project.loadFrom(), new HotKey(Key.O, Modifier.CONTROL));
	public static final Action EXPORT_PROJECT = new Action("export_project", "export project", () -> Main.instance.project.export(), new HotKey(Key.E, Modifier.CONTROL));
	
	public static final Action UNDO = new Action("undo", "undo last action", () -> Main.instance.project.canUndo(), () -> Main.instance.project.undo(), new HotKey(Key.Z, Modifier.CONTROL));
	public static final Action REDO = new Action("redo", "redo last undid action", () -> Main.instance.project.canRedo(), () -> Main.instance.project.redo(), new HotKey(Key.Y, Modifier.CONTROL));
	
	public static final Action NEW_MODEL = new Action("new_model", "add a new model", () -> new GuiPopupModel(false).activate(), new HotKey(Key.UNKNOWN));
	public static final Action ADD_MODEL = new Action("add_model", "add a new model from an OBJ model", () -> new GuiPopupAddModel().activate(), new HotKey(Key.UNKNOWN));
	public static final Action LOAD_MODEL = new Action("load_model", "loads mesh components from an OBJ model to the active model", CHECK_MODEL, () -> new GuiPopupLoadModel().activate(), new HotKey(Key.UNKNOWN));
	public static final Action CLONE_MODEL = new Action("clone_model", "Creates a copy of the active model", CHECK_MODEL, () -> {
		Project project = Main.instance.project;
		new GuiPopupCopy<>(MiscUtil.ensureUnique(project.getModelName(), project.getModelNames()), project.getModel(), (name, copy) -> project.addModel(name, copy), (name) -> project.removeModel(name)).activate();
	}, new HotKey(Key.UNKNOWN));
	public static final Action EXPORT_MODEL = new Action("export_model", "Exports the active model to an OBJ file using it's current pose", CHECK_MODEL, () -> {
		File file = FileUtils.getSaveFile("obj", "");
		if (file != null)
		{
			Writer writer = null;
			try
			{
				writer = new FileWriter(file);
				writer.write(RenderObjectComponents.createObj(Main.instance.project.getModel(), Main.instance.project.getModel().getPose(Main.instance.project.getStates())).toString());
			}
			catch (IOException e)
			{
				GuiPopupException.onException("Couldn't export model to " + file, e);
			}
			FileUtil.closeSafe(writer);
		}
	}, new HotKey(Key.UNKNOWN));
	public static final Action EXPORT_UNPOSED_MODEL = new Action("export_unposed_model", "Exports the active model to an OBJ file without any pose data", CHECK_MODEL, () -> {
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
	public static final Action EDIT_MODEL = new Action("edit_model", "Edits the active model's properties", CHECK_MODEL, () -> new GuiPopupModel(true).activate(), new HotKey(Key.UNKNOWN));
	public static final Action REMOVE_MODEL = new Action("remove_model", "Removes the active model", CHECK_MODEL, () -> {
		final String name = Main.instance.project.getModelName();
		final IModel<?, ? extends RenderObjectComponents<?>> model = Main.instance.project.getModel();
		Main.instance.project.onAction(new HistoryAction(() -> Main.instance.project.addModel(name, model), () -> Main.instance.project.removeModel()));
		Main.instance.project.removeModel();
	}, new HotKey(Key.UNKNOWN));
	
	public static final Action IMPORT_SKELETON = new Action("import_skeleton", "Imports a skeleton to the active model or skeleton", CHECK_RIG, () -> {
		File loadFile = FileUtils.getOpenFile("skel;xml;json;bin", "");
		if (loadFile != null)
		{
			try
			{
				AbstractElement el = FileUtil.readFile(loadFile);
				Skeleton skel = new Skeleton(el);
				Main.instance.project.getRig().applySkeleton(skel);
				Main.instance.onGuiUpdate(GuiUpdate.MODEL);
			}
			catch (IOException e)
			{
				GuiPopupException.onException("Couldn't import skeleton from " + loadFile, e);
			}
		}
	}, new HotKey(Key.UNKNOWN));
	public static final Action EXPORT_SKELETON = new Action("exports_skeleton", "Exports the skeleton of the active model or skeleton", CHECK_RIG, () -> {
		File saveFile = FileUtils.getSaveFile("skel;xml;json;bin", "");
		if (saveFile != null)
		{
			FileUtil.DataType dataType = FileUtil.getAppropriateDataType(saveFile.toString());
			AbstractElement root = dataType.newElement("project");
			Main.instance.project.getRig().getSkeleton().save(root);
			try
			{
				dataType.saveElement(root, saveFile);
			}
			catch (Exception e)
			{
				GuiPopupException.onException("Couldn't export skeleton to " + saveFile, e);
			}
		}
	}, new HotKey(Key.UNKNOWN));
	
	public static final Action NEW_TEXTURE = new Action("new_texture", "Add a new texture", () -> new GuiPopupNewTexture().activate(), new HotKey(Key.UNKNOWN));
	public static final Action ADD_TEXTURE = new Action("add_texture", "Add a new texture from a file", () -> new GuiPopupLoadTexture().activate(), new HotKey(Key.UNKNOWN));
	public static final Action LOAD_TEXTURE = new Action("load_texture", "Load texture from a file", CHECK_TEXTURE, () -> {
		File file = FileUtils.getOpenFile(FileUtils.getLoadImageFilter(), "");
		if (file != null) try
		{
			final String name = Main.instance.project.getTextureName();
			final Texture prevTex = Main.instance.project.getTexture();
			final Texture newTex = new ReloadingTexture(file);
			Main.instance.project.addTexture(name, newTex);
			Main.instance.project.onAction(new HistoryAction(() -> Main.instance.project.addTexture(name, prevTex), () -> Main.instance.project.addTexture(name, newTex)));
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't load texture file: " + file.getAbsolutePath().toString(), e, Level.WARN);
		}
	}, new HotKey(Key.UNKNOWN));
	public static final Action CLONE_TEXTURE = new Action("clone_texture", "Clone the active texture", CHECK_TEXTURE, () -> {
		Project project = Main.instance.project;
		new GuiPopupCopy<>(MiscUtil.ensureUnique(project.getTextureName(), project.getTextureNames()), project.getTexture(), (name, copy) -> project.addTexture(name, copy), (name) -> project.removeTexture(name)).activate();
	}, new HotKey(Key.UNKNOWN));
	public static final Action SAVE_TEXTURE = new Action("save_texture", "Save the active texture", CHECK_TEXTURE, () -> {
		File file = FileUtils.getSaveFile(FileUtils.getLoadImageFilter(), "");
		if (file != null) Main.instance.project.getTexture().saveTexture(file);
	}, new HotKey(Key.UNKNOWN));
	public static final Action EDIT_TEXTURE = new Action("edit_texture", "Edit the active texture", CHECK_TEXTURE, () -> new GuiPopupEditTexture().activate(), new HotKey(Key.UNKNOWN));
	public static final Action REMOVE_TEXTURE = new Action("remove_texture", "Remove the active texture", CHECK_TEXTURE, () -> {
		final String name = Main.instance.project.getTextureName();
		final Texture tex = Main.instance.project.getTexture();
		Main.instance.project.onAction(new HistoryAction(() -> Main.instance.project.addTexture(name, tex), () -> Main.instance.project.removeTexture(name)));
		Main.instance.project.removeTexture();
	}, new HotKey(Key.UNKNOWN));

	public static final Action NEW_ANIMATION = new Action("new_animation", "Add a new animation", () -> new GuiPopupNewAnimation().activate(), new HotKey(Key.UNKNOWN));
	public static final Action ADD_ANIMATION = new Action("add_animation", "Add a new animation from a file", () -> new GuiPopupLoadAnimation().activate(), new HotKey(Key.UNKNOWN)); //TODO scale?
	public static final Action LOAD_ANIMATION = new Action("load_animation", "Load animation from a file", CHECK_ANIMATION, () -> {
		File file = FileUtils.getOpenFile("anim;xml;json;bin", "");
		if (file != null) try
		{
			final String name = Main.instance.project.getAnimationName();
			final IAnimation animation = Main.instance.project.getAnimation();
			final IAnimation prev = animation.cloneObject();
			AbstractElement el = FileUtil.readFile(file);
			animation.load(el); //TODO scale?
			final IAnimation cur = animation.cloneObject();
			Main.instance.project.onAction(new HistoryAction(() -> Main.instance.project.addAnimation(name, prev), () -> Main.instance.project.addAnimation(name, cur)));
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't load animation file: " + file, e, Level.WARN);
		}
	}, new HotKey(Key.UNKNOWN)); //TODO scale?
	public static final Action CLONE_ANIMATION = new Action("clone_animation", "Clone the active animation", CHECK_ANIMATION, () -> {
		Project project = Main.instance.project;
		new GuiPopupCopy<>(MiscUtil.ensureUnique(project.getAnimationName(), project.getAnimationNames()), project.getAnimation(), (name, copy) -> project.addAnimation(name, copy), (name) -> project.removeAnimation(name)).activate();
	}, new HotKey(Key.UNKNOWN));
	public static final Action SAVE_ANIMATION = new Action("save_animation", "Save the active animation", CHECK_ANIMATION, () -> {
		File file = FileUtils.getSaveFile("anim;xml;json;bin", "");
		if (file != null) {
			DataType dataType = FileUtil.getAppropriateDataType(file.toString());
			AbstractElement root = dataType.newElement("animtion");
			Main.instance.project.getAnimation().save(root); //TODO scale?
			try
			{
				dataType.saveElement(root, file);
			}
			catch (Exception e)
			{
				GuiPopupException.onException("Couldn't save animation to " + file, e);
			}
		}
	}, new HotKey(Key.UNKNOWN));
	public static final Action EDIT_ANIMATION = new Action("edit_animation", "Edit the active animation", CHECK_ANIMATION, () -> new GuiPopupEditAnimation().activate(), new HotKey(Key.UNKNOWN));
	public static final Action REVERSE_ANIMATION = new Action("reverse_animation", "Reverse the active animation", CHECK_ANIMATION, () -> {
		final String name = Main.instance.project.getAnimationName();
		final IAnimation animation = Main.instance.project.getAnimation();
		final IAnimation prev = animation.cloneObject();
		animation.reverseAnimation(Main.instance.project.getCompletestRig());
		final IAnimation cur = animation.cloneObject();
		Main.instance.project.onAction(new HistoryAction(() -> Main.instance.project.addAnimation(name, prev), () -> Main.instance.project.addAnimation(name, cur)));
		Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
	}, new HotKey(Key.UNKNOWN));
	public static final Action REMOVE_ANIMATION = new Action("remove_animation", "Remove the active animation", CHECK_ANIMATION, () -> {
		final String name = Main.instance.project.getAnimationName();
		final IAnimation animation = Main.instance.project.getAnimation();
		Main.instance.project.onAction(new HistoryAction(() -> Main.instance.project.addAnimation(name, animation), () -> Main.instance.project.removeAnimation(name)));
		Main.instance.project.removeAnimation();
	}, new HotKey(Key.UNKNOWN));
	
	public static final Action DEBUG_STENCIL = new Action("debug_stencil", "debug the stencil buffer (EXTREME LAG!) by saving the contents at each update", () -> RenderUtil.save = !RenderUtil.save, new HotKey(Key.UNKNOWN));

	public final String id;
	public final String display_name;
	public final BooleanSupplier canRun;
	public final Runnable action;
	public final HotKey def;
	
	public Action(String name, Runnable action, HotKey def)
	{
		this(name, name, action, def);
	}
	
	public Action(String id, String display_name, Runnable action, HotKey def)
	{
		this(id, display_name, () -> true, action, def);
	}
	
	public Action(String name, BooleanSupplier canRun, Runnable action, HotKey def)
	{
		this(name, name, canRun, action, def);
	}
	
	public Action(String id, String display_name, BooleanSupplier canRun, Runnable action, HotKey def)
	{
		this.id = id;
		this.display_name = display_name;
		this.canRun = canRun;
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