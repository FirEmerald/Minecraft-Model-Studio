package firemerald.mcms.gui.main.components;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.util.function.Supplier;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.plugins.GuiPopupPlugins;
import firemerald.mcms.gui.popups.GuiPopupMenu;
import firemerald.mcms.gui.popups.GuiPopupMessageOK;
import firemerald.mcms.gui.popups.hotkeys.GuiPopupHotkeys;
import firemerald.mcms.gui.themes.GuiThemes;
import firemerald.mcms.plugin.PluginLoader;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.ApplicationState.EnumLayout;
import firemerald.mcms.util.font.FormattedText;
import firemerald.mcms.util.hotkey.Action;

public class ComponentTitleBar extends ComponentPanelMain
{
	public final TitleButton filesMenu, editMenu, modelMenu, textureMenu, animationMenu, optionsMenu, helpMenu;
	
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
		final int w = 68;
		int x = 0;
        this.addElement(filesMenu = new TitleButton(x, 0, x += w, 16, 1, 0, "File", () ->  {
            PopupMenu menu = new PopupMenu();
            addAction(menu, "New project", Action.NEW_PROJECT);
            addAction(menu, "Open project...", Action.LOAD_PROJECT);
            addAction(menu, "Edit Project", Action.EDIT_PROJECT);
            addAction(menu, "Save project", Action.SAVE_PROJECT);
            addAction(menu, "Save project as...", Action.SAVE_PROJECT_AS);
            addAction(menu, "Export project", Action.EXPORT_PROJECT);
        	MenuItem exit = new MenuItem("Exit");
        	exit.addActionListener(action -> Main.instance.tryClose());
        	menu.add(exit);
            return menu;
        }));
        this.addElement(editMenu = new TitleButton(x, 0, x += w, 16, 1, 0, "Edit", () -> {
            PopupMenu menu = new PopupMenu();
            addAction(menu, "Undo", Action.UNDO);
            addAction(menu, "Redo", Action.REDO);
            return menu;
        }));
        this.addElement(modelMenu = new TitleButton(x, 0, x += w, 16, 1, 0, "Model", () -> {
            PopupMenu menu = new PopupMenu();
            addAction(menu, "New model", Action.NEW_MODEL);
            addAction(menu, "New model from OBJ data", Action.ADD_MODEL);
            addAction(menu, "Import OBJ data", Action.LOAD_MODEL);
            addAction(menu, "Clone model", Action.CLONE_MODEL);
            addAction(menu, "Export posed model", Action.EXPORT_MODEL);
            addAction(menu, "Export unposed model", Action.EXPORT_UNPOSED_MODEL);
            addAction(menu, "Edit model", Action.EDIT_MODEL);
            addAction(menu, "Remove model", Action.REMOVE_MODEL);
            addAction(menu, "Import skeleton", Action.IMPORT_SKELETON);
            addAction(menu, "Export skeleton", Action.EXPORT_SKELETON);
            return menu;
        }));
        this.addElement(textureMenu = new TitleButton(x, 0, x += w, 16, 1, 0, "Texture", () -> {
            PopupMenu menu = new PopupMenu();
            addAction(menu, "New texture", Action.NEW_TEXTURE);
            addAction(menu, "Add texture from file", Action.ADD_TEXTURE);
            addAction(menu, "Load texture", Action.LOAD_TEXTURE);
            addAction(menu, "Clone texture", Action.CLONE_TEXTURE);
            addAction(menu, "Save texture", Action.SAVE_TEXTURE);
            addAction(menu, "Edit texture", Action.EDIT_TEXTURE);
            addAction(menu, "Remove texture", Action.REMOVE_TEXTURE);
            return menu;
        }));
        this.addElement(animationMenu = new TitleButton(x, 0, x += w, 16, 1, 0, "Animation", () -> {
            PopupMenu menu = new PopupMenu();
            addAction(menu, "New animation", Action.NEW_ANIMATION);
            addAction(menu, "Add animation from file", Action.ADD_ANIMATION);
            addAction(menu, "Load animation", Action.LOAD_ANIMATION);
            addAction(menu, "Clone animation", Action.CLONE_ANIMATION);
            addAction(menu, "Save animation", Action.SAVE_ANIMATION);
            addAction(menu, "Edit animation", Action.EDIT_ANIMATION);
            addAction(menu, "Reverse animation", Action.REVERSE_ANIMATION);
            addAction(menu, "Remove animation", Action.REMOVE_ANIMATION);
            return menu;
        }));
        this.addElement(optionsMenu = new TitleButton(x, 0, x += w, 16, 1, 0, "Options", () -> {
            PopupMenu menu = new PopupMenu();
            if (!Main.instance.state.showNodes())
            {
                MenuItem showNodes = new MenuItem("Show nodes");
                showNodes.addActionListener(action -> {
                	Main.instance.state.setShowNodes(true);
                });
                menu.add(showNodes);
            }
            else
            {
                MenuItem hideNodes = new MenuItem("Hide nodes");
                hideNodes.addActionListener(action -> {
                	Main.instance.state.setShowNodes(false);
                });
                menu.add(hideNodes);
            }
            if (!Main.instance.state.showBones())
            {
                MenuItem showBones = new MenuItem("Show bones");
                showBones.addActionListener(action -> {
                	Main.instance.state.setShowBones(true);
                });
                menu.add(showBones);
            }
            else
            {
                MenuItem hideBones = new MenuItem("Hide bones");
                hideBones.addActionListener(action -> {
                	Main.instance.state.setShowBones(false);
                });
                menu.add(hideBones);
            }
        	MenuItem theme = new MenuItem("Change theme");
        	theme.addActionListener(action -> {
        		new GuiThemes().activate();
        	});
        	menu.add(theme);
        	MenuItem hotkeys = new MenuItem("Hotkeys");
        	hotkeys.addActionListener(action -> {
        		new GuiPopupHotkeys().activate();
        	});
        	menu.add(hotkeys);
        	Menu layout = new Menu("Layout");
        	for (EnumLayout l : EnumLayout.values())
        	{
                MenuItem li = new MenuItem(l.displayName);
                li.addActionListener(action -> {
                	Main.instance.state.setLayout(l);
                });
                if (Main.instance.state.getLayout() == l) li.setEnabled(false);
                layout.add(li);
        	}
        	menu.add(layout);
            return menu;
        }));
        this.addElement(helpMenu = new TitleButton(x, 0, x += w, 16, 1, 0, "Help", () -> {
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
	
	private static void addAction(PopupMenu menu, String name, Action action)
	{
		MenuItem item = new MenuItem(name);
		item.addActionListener(a -> action.action.run());
		item.setEnabled(action.canRun.getAsBoolean());
		menu.add(item);
	}
	
	@Override
	public void onSize(int w, int h) {}
}