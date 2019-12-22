package firemerald.mcms.gui.main.components;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Supplier;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.popups.GuiPopupMenu;
import firemerald.mcms.gui.popups.GuiPopupMessageOK;
import firemerald.mcms.gui.themes.GuiThemes;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.GuiUpdate;

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
        	menu.add(newProject);
        	MenuItem openProject = new MenuItem("Open Project...");
        	openProject.addActionListener(action -> {
        		Main.instance.project.loadFrom();
        		Main.instance.gui.onGuiUpdate(GuiUpdate.PROJECT);
        		((GuiPopup) Main.instance.gui).deactivate();
        	});
        	menu.add(openProject);
        	MenuItem editProject = new MenuItem("Edit Project");
        	menu.add(editProject);
        	MenuItem saveProject = new MenuItem("Save Project");
        	saveProject.addActionListener(action -> {
        		Main.instance.project.save();
        		((GuiPopup) Main.instance.gui).deactivate();
        	});
        	menu.add(saveProject);
        	MenuItem saveProjectAs = new MenuItem("Save Project as...");
        	saveProjectAs.addActionListener(action -> {
        		Main.instance.project.saveAs();
        		((GuiPopup) Main.instance.gui).deactivate();
        	});
        	menu.add(saveProjectAs);
        	MenuItem exportProject = new MenuItem("Export Project");
        	exportProject.addActionListener(action -> {
        		Main.instance.project.export(); //TODO
        		((GuiPopup) Main.instance.gui).deactivate();
        	});
        	menu.add(exportProject);
        	menu.add("-");
        	MenuItem importSkeleton = new MenuItem("Import Skeleton");
        	menu.add(importSkeleton);
        	MenuItem exportSkeleton = new MenuItem("Export Skeleton");
        	menu.add(exportSkeleton);
        	//TODO overwrite last skeleton
        	menu.add("-");
        	MenuItem exit = new MenuItem("Exit");
        	exit.addActionListener(action -> Main.instance.window.close());
        	menu.add(exit);
            return menu;
        }));
        this.addElement(editMenu = new TitleButton(64, 0, 128, 16, 1, 0, "Edit", () -> {
            PopupMenu menu = new PopupMenu();
            return menu;
        }));
        this.addElement(modelMenu = new TitleButton(128, 0, 192, 16, 1, 0, "Model", () -> {
            PopupMenu menu = new PopupMenu();
        	MenuItem importModel = new MenuItem("Import Model");
        	menu.add(importModel);
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
        				Main.LOGGER.warn("Couldn't export model to " + file, e);
        			}
        			FileUtil.closeSafe(writer);
        		}
        		((GuiPopup) Main.instance.gui).deactivate();
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
        				AnimationState[] anims;
        				if (project.getAnimation() != null) anims = new AnimationState[] {new AnimationState(project.getAnimation(), main.animTime)};
        				else anims = new AnimationState[0];
        				writer.write(RenderObjectComponents.createObj(project.getModel(), project.getModel().getPose(anims)).toString());
        			}
        			catch (IOException e)
        			{
        				Main.LOGGER.warn("Couldn't export model to " + file, e);
        			}
        			FileUtil.closeSafe(writer);
        		}
        		((GuiPopup) Main.instance.gui).deactivate();
        	});
        	menu.add(exportPosedModel);
            return menu;
        }));
        this.addElement(textureMenu = new TitleButton(192, 0, 256, 16, 1, 0, "Texture", () -> {
            PopupMenu menu = new PopupMenu();
            return menu;
        }));
        this.addElement(optionsMenu = new TitleButton(256, 0, 320, 16, 1, 0, "Options", () -> {
            PopupMenu menu = new PopupMenu();
            if (!Main.instance.state.showNodes())
            {
                MenuItem showNodes = new MenuItem("Show Nodes");
                showNodes.addActionListener(action -> {
                	Main.instance.state.setShowNodes(true);
            		((GuiPopup) Main.instance.gui).deactivate();
                });
                menu.add(showNodes);
            }
            else
            {
                MenuItem hideNodes = new MenuItem("Hide Nodes");
                hideNodes.addActionListener(action -> {
                	Main.instance.state.setShowNodes(false);
            		((GuiPopup) Main.instance.gui).deactivate();
                });
                menu.add(hideNodes);
            }
            if (!Main.instance.state.showBones())
            {
                MenuItem showBones = new MenuItem("Show Bones");
                showBones.addActionListener(action -> {
                	Main.instance.state.setShowBones(true);
            		((GuiPopup) Main.instance.gui).deactivate();
                });
                menu.add(showBones);
            }
            else
            {
                MenuItem hideBones = new MenuItem("Hide Bones");
                hideBones.addActionListener(action -> {
                	Main.instance.state.setShowBones(false);
            		((GuiPopup) Main.instance.gui).deactivate();
                });
                menu.add(hideBones);
            }
        	MenuItem theme = new MenuItem("Change Theme");
        	theme.addActionListener(action -> {
        		((GuiPopup) Main.instance.gui).deactivate();
        		new GuiThemes().activate();
        	});
        	menu.add(theme);
            return menu;
        }));
        this.addElement(helpMenu = new TitleButton(320, 0, 384, 16, 1, 0, "Help", () -> {
            PopupMenu menu = new PopupMenu();
        	MenuItem theme = new MenuItem("About MCMS");
        	theme.addActionListener(action -> {
        		((GuiPopup) Main.instance.gui).deactivate();
        		new GuiPopupMessageOK("Minecraft Model Studio, by §cFF0000Fir§cFFFF00E§c00FF00merald§r.\n\nBuild " + Main.VERSION + ", on " + Main.BUILD_DATE).activate();
        	});
        	menu.add(theme);
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