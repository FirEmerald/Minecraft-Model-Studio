package firemerald.mcms.gui.main.components;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Skeleton;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.plugins.GuiPopupPlugins;
import firemerald.mcms.gui.popups.GuiPopupCopy;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.gui.popups.GuiPopupMenu;
import firemerald.mcms.gui.popups.GuiPopupMessageOK;
import firemerald.mcms.gui.popups.GuiPopupUnsavedChanges;
import firemerald.mcms.gui.popups.project.GuiPopupNewProject;
import firemerald.mcms.gui.popups.texture.GuiPopupEditTexture;
import firemerald.mcms.gui.popups.texture.GuiPopupLoadTexture;
import firemerald.mcms.gui.popups.texture.GuiPopupNewTexture;
import firemerald.mcms.gui.themes.GuiThemes;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.plugin.PluginLoader;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.ReloadingTexture;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.font.FormattedText;
import firemerald.mcms.util.history.HistoryAction;
import firemerald.mcms.util.hotkey.Action;

public class ComponentTitleBar extends ComponentPanelMain
{
	public final TitleButton filesMenu, editMenu, modelMenu, textureMenu, optionsMenu, helpMenu;
	
	public static class TitleButton extends StandardButton
	{
		public TitleButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, Supplier<Menu> menu)
		{
			super(x1, y1, x2, y2, outline, radius, text, null);
			this.onRelease = () -> new GuiPopupMenu(this.getTrueX1(), this.getTrueY2(), menu.get()).activate();
		}
		
		public TitleButton(int x1, int y1, int x2, int y2, String text, Supplier<Menu> menu)
		{
			super(x1, y1, x2, y2, text, null);
			this.onRelease = () -> new GuiPopupMenu(this.getTrueX1(), this.getTrueY2(), menu.get()).activate();
		}
	}
	
	public ComponentTitleBar(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
        this.addElement(filesMenu = new TitleButton(0, 0, 64, 16, 1, 0, "File", () ->  {
            PopupMenu menu = new PopupMenu();
        	MenuItem newProject = new MenuItem("New Project");
        	newProject.addActionListener(action -> {
        		if (Main.instance.project.needsSave()) new GuiPopupUnsavedChanges(() -> new GuiPopupNewProject().activate()).activate();
        		else new GuiPopupNewProject().activate();
        	});
        	menu.add(newProject);
        	MenuItem openProject = new MenuItem("Open Project...");
        	openProject.addActionListener(action -> {
        		Main.instance.doAction(Action.LOAD);
        	});
        	menu.add(openProject);
        	MenuItem editProject = new MenuItem("Edit Project");
        	menu.add(editProject);
        	MenuItem saveProject = new MenuItem("Save Project");
        	saveProject.addActionListener(action -> {
        		Main.instance.doAction(Action.SAVE);
        	});
        	menu.add(saveProject);
        	MenuItem saveProjectAs = new MenuItem("Save Project as...");
        	saveProjectAs.addActionListener(action -> {
        		Main.instance.doAction(Action.SAVE_AS);
        	});
        	menu.add(saveProjectAs);
        	MenuItem exportProject = new MenuItem("Export Project");
        	exportProject.addActionListener(action -> {
        		Main.instance.doAction(Action.EXPORT);
        	});
        	menu.add(exportProject);
        	MenuItem exit = new MenuItem("Exit");
        	exit.addActionListener(action -> Main.instance.tryClose());
        	menu.add(exit);
            return menu;
        }));
        this.addElement(editMenu = new TitleButton(64, 0, 128, 16, 1, 0, "Edit", () -> {
            PopupMenu menu = new PopupMenu();
        	MenuItem undo = new MenuItem("Undo");
        	undo.addActionListener(action -> Main.instance.doAction(Action.UNDO));
        	if (!Main.instance.project.canUndo()) undo.setEnabled(false);
        	menu.add(undo);
        	MenuItem redo = new MenuItem("Redo");
        	redo.addActionListener(action -> Main.instance.doAction(Action.REDO));
        	if (!Main.instance.project.canRedo()) redo.setEnabled(false);
        	menu.add(redo);
            return menu;
        }));
        this.addElement(modelMenu = new TitleButton(128, 0, 192, 16, 1, 0, "Model", () -> {
            PopupMenu menu = new PopupMenu();
        	MenuItem importModel = new MenuItem("Import Model");
        	menu.add(importModel); //TODO
        	MenuItem exportModel = new MenuItem("Export Model");
        	if (Main.instance.project.getModel() == null) exportModel.setEnabled(false);
        	else exportModel.addActionListener(action -> {
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
        	});
        	menu.add(exportModel);
        	MenuItem exportPosedModel = new MenuItem("Export Posed Model");
        	if (Main.instance.project.getModel() == null) exportPosedModel.setEnabled(false);
        	else exportPosedModel.addActionListener(action -> {
        		File file = FileUtils.getSaveFile("obj", "");
        		if (file != null)
        		{
        			Writer writer = null;
        			try
        			{
        				writer = new FileWriter(file);
        				Main main = Main.instance;
        				Project project = main.project;
        				AnimationState[] anims = project.getStates();
        				writer.write(RenderObjectComponents.createObj(project.getModel(), project.getModel().getPose(anims)).toString());
        			}
        			catch (IOException e)
        			{
        				GuiPopupException.onException("Couldn't export model to " + file, e);
        			}
        			FileUtil.closeSafe(writer);
        		}
        	});
        	menu.add(exportPosedModel);
        	MenuItem importSkeleton = new MenuItem("Import Skeleton");
        	importSkeleton.addActionListener(action -> {
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
        	});
        	importSkeleton.setEnabled(Main.instance.project.getRig() != null);
        	menu.add(importSkeleton);
        	MenuItem exportSkeleton = new MenuItem("Export Skeleton");
        	exportSkeleton.addActionListener(action -> {
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
        	});
        	exportSkeleton.setEnabled(Main.instance.project.getRig() != null);
        	menu.add(exportSkeleton);
            return menu;
        }));
        this.addElement(textureMenu = new TitleButton(192, 0, 256, 16, 1, 0, "Texture", () -> {
            PopupMenu menu = new PopupMenu();
        	MenuItem newTexture = new MenuItem("New Texture");
        	newTexture.addActionListener(action -> new GuiPopupNewTexture().activate());
        	menu.add(newTexture);
        	MenuItem addTexture = new MenuItem("Add Texture From File");
        	addTexture.addActionListener(action -> new GuiPopupLoadTexture().activate());
        	menu.add(addTexture);
        	MenuItem importTexture = new MenuItem("Load Texture");
        	importTexture.addActionListener(action -> {
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
        	});
        	menu.add(importTexture);
        	MenuItem cloneTexture = new MenuItem("Clone Texture");
        	cloneTexture.addActionListener(action -> {
    			Project project = Main.instance.project;
    			new GuiPopupCopy<>(MiscUtil.ensureUnique(project.getTextureName(), project.getTextureNames()), project.getTexture(), (name, copy) -> project.addTexture(name, copy), (name) -> project.removeTexture(name)).activate();
        	});
        	menu.add(cloneTexture);
        	MenuItem exportTexture = new MenuItem("Export Texture");
        	exportTexture.addActionListener(action -> {
        		File file = FileUtils.getSaveFile(FileUtils.getLoadImageFilter(), "");
        		if (file != null) Main.instance.project.getTexture().saveTexture(file);
        	});
        	menu.add(exportTexture);
        	MenuItem editTexture = new MenuItem("Edit Texture");
        	editTexture.addActionListener(action -> new GuiPopupEditTexture().activate());
        	menu.add(editTexture);
        	MenuItem removeTexture = new MenuItem("Remove Texture");
        	removeTexture.addActionListener(action -> {
    			final String name = Main.instance.project.getTextureName();
    			final Texture tex = Main.instance.project.getTexture();
    			Main.instance.project.onAction(new HistoryAction(() -> Main.instance.project.addTexture(name, tex), () -> Main.instance.project.removeTexture(name)));
    			Main.instance.project.removeTexture();
    		});
        	menu.add(removeTexture);
            return menu;
        }));
        this.addElement(optionsMenu = new TitleButton(256, 0, 320, 16, 1, 0, "Options", () -> {
            PopupMenu menu = new PopupMenu();
            if (!Main.instance.state.showNodes())
            {
                MenuItem showNodes = new MenuItem("Show Nodes");
                showNodes.addActionListener(action -> {
                	Main.instance.state.setShowNodes(true);
                });
                menu.add(showNodes);
            }
            else
            {
                MenuItem hideNodes = new MenuItem("Hide Nodes");
                hideNodes.addActionListener(action -> {
                	Main.instance.state.setShowNodes(false);
                });
                menu.add(hideNodes);
            }
            if (!Main.instance.state.showBones())
            {
                MenuItem showBones = new MenuItem("Show Bones");
                showBones.addActionListener(action -> {
                	Main.instance.state.setShowBones(true);
                });
                menu.add(showBones);
            }
            else
            {
                MenuItem hideBones = new MenuItem("Hide Bones");
                hideBones.addActionListener(action -> {
                	Main.instance.state.setShowBones(false);
                });
                menu.add(hideBones);
            }
        	MenuItem theme = new MenuItem("Change Theme");
        	theme.addActionListener(action -> {
        		new GuiThemes().activate();
        	});
        	menu.add(theme);
            return menu;
        }));
        this.addElement(helpMenu = new TitleButton(320, 0, 384, 16, 1, 0, "Help", () -> {
            PopupMenu menu = new PopupMenu();
        	MenuItem theme = new MenuItem("About MCMS");
        	theme.addActionListener(action -> {
        		new GuiPopupMessageOK(new FormattedText("Minecraft Model Studio, by ", getTheme().getTextColor(), Main.instance.fontMsg).append("Fir", Color.RED).append("E", Color.YELLOW).append("merald", Color.GREEN).append(".\n\nBuild ", getTheme().getTextColor()).append(Main.VERSION, Color.BLUE).append(", on ", getTheme().getTextColor()).append(Main.BUILD_DATE, Color.BLUE)).activate();
        	});
        	menu.add(theme);
        	MenuItem plugins = new MenuItem("Plugins");
        	plugins.addActionListener(action -> {
        		new GuiPopupPlugins().activate();
        	});
        	if (PluginLoader.INSTANCE.loadedPlugins.isEmpty()) plugins.setEnabled(false);
        	menu.add(plugins);
            return menu;
        }));
		// TODO components
	}
	
	@Override
	public void onSize(int w, int h)
	{
		filesMenu.setSize(0, 0, 64, 16);
		editMenu.setSize(64, 0, 128, 16);
		modelMenu.setSize(128, 0, 192, 16);
		textureMenu.setSize(192, 0, 256, 16);
		optionsMenu.setSize(256, 0, 320, 16);
		helpMenu.setSize(320, 0, 384, 16);
	}
}