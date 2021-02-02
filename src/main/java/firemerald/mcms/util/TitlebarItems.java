package firemerald.mcms.util;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import firemerald.mcms.Main;
import firemerald.mcms.gui.plugins.GuiPopupPlugins;
import firemerald.mcms.gui.popups.GuiPopupMessageOK;
import firemerald.mcms.gui.popups.hotkeys.GuiPopupHotkeys;
import firemerald.mcms.gui.themes.GuiThemes;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.ApplicationState.EnumLayout;
import firemerald.mcms.util.font.FormattedText;
import firemerald.mcms.util.hotkey.Action;

public class TitlebarItems
{
	public static final Map<EnumLayout, MenuItem> LAYOUT_ITEMS = new LinkedHashMap<>();
	static
	{
		for (EnumLayout layout : EnumLayout.values()) LAYOUT_ITEMS.put(layout, setAction(new MenuItem(layout.displayName), action -> Main.instance.state.setLayout(layout)));
	}
	
	public static final MenuItem 
	//file
	NEW_PROJECT = makeAction(Action.NEW_PROJECT),
	LOAD_PROJECT = makeAction(Action.LOAD_PROJECT),
	EDIT_PROJECT = makeAction(Action.EDIT_PROJECT),
	SAVE_PROJECT = makeAction(Action.SAVE_PROJECT),
	SAVE_PROJECT_AS = makeAction(Action.SAVE_PROJECT_AS),
	EXPORT_PROJECT = makeAction(Action.EXPORT_PROJECT),
	EXIT = setAction(new MenuItem("Exit"), action -> Main.instance.tryClose()),
	//edit
	UNDO = makeAction(Action.UNDO),
	REDO = makeAction(Action.REDO),
	//model
	NEW_MODEL = makeAction(Action.NEW_MODEL),
	ADD_MODEL = makeAction(Action.ADD_MODEL),
	LOAD_MODEL = makeAction(Action.LOAD_MODEL),
	CLONE_MODEL = makeAction(Action.CLONE_MODEL),
	EXPORT_MODEL = makeAction(Action.EXPORT_MODEL),
	EXPORT_UNPOSED_MODEL = makeAction(Action.EXPORT_UNPOSED_MODEL),
	EDIT_MODEL = makeAction(Action.EDIT_MODEL),
	REMOVE_MODEL = makeAction(Action.REMOVE_MODEL),
	IMPORT_SKELETON = makeAction(Action.IMPORT_SKELETON),
	EXPORT_SKELETON = makeAction(Action.EXPORT_SKELETON),
	//texture
	NEW_TEXTURE = makeAction(Action.NEW_TEXTURE),
	ADD_TEXTURE = makeAction(Action.ADD_TEXTURE),
	LOAD_TEXTURE = makeAction(Action.LOAD_TEXTURE),
	CLONE_TEXTURE = makeAction(Action.CLONE_TEXTURE),
	SAVE_TEXTURE = makeAction(Action.SAVE_TEXTURE),
	EDIT_TEXTURE = makeAction(Action.EDIT_TEXTURE),
	REMOVE_TEXTURE = makeAction(Action.REMOVE_TEXTURE),
	//animation
	NEW_ANIMATION = makeAction(Action.NEW_ANIMATION),
	ADD_ANIMATION = makeAction(Action.ADD_ANIMATION),
	LOAD_ANIMATION = makeAction(Action.LOAD_ANIMATION),
	CLONE_ANIMATION = makeAction(Action.CLONE_ANIMATION),
	SAVE_ANIMATION = makeAction(Action.SAVE_ANIMATION),
	EDIT_ANIMATION = makeAction(Action.EDIT_ANIMATION),
	REVERSE_ANIMATION = makeAction(Action.REVERSE_ANIMATION),
	REMOVE_ANIMATION = makeAction(Action.REMOVE_ANIMATION),
	//options
	TOGGLE_NODES = setAction(new MenuItem("Toggle nodes"), action -> Main.instance.state.setShowNodes(!Main.instance.state.showNodes())),
	TOGGLE_BONES = setAction(new MenuItem("Toggle bones"), action -> Main.instance.state.setShowBones(!Main.instance.state.showBones())),
	TOGGLE_SHADOWS = setAction(new MenuItem("Toggle shadows"), action -> Main.instance.state.setEnableShadows(!Main.instance.state.enableShadows())),
	CHANGE_THEME = setAction(new MenuItem("Change theme"), action -> new GuiThemes().activate()),
	HOTKEYS = setAction(new MenuItem("Hotkeys"), action -> new GuiPopupHotkeys().activate()),
	LAYOUT_MENU = makeMenu("Layout", LAYOUT_ITEMS.values().stream()),
	//help
	ABOUT_MCMS = setAction(new MenuItem("About MCMS"), action -> new GuiPopupMessageOK(new FormattedText("Minecraft Model Studio, by ", Main.instance.getTheme().getTextColor(), Main.instance.fontMsg).append("Fir", Color.RED).append("E", Color.YELLOW).append("merald", Color.GREEN).append(".\n\nBuild ", Main.instance.getTheme().getTextColor()).append(Main.VERSION, Color.BLUE).append(", on ", Main.instance.getTheme().getTextColor()).append(Main.BUILD_DATE, Color.BLUE)).activate()),
	PLUGINS = setAction(new MenuItem("Plugins"), action -> new GuiPopupPlugins().activate());
	
	public static MenuItem makeAction(Action action)
	{
		MenuItem item = new MenuItem(action.menuName);
		item.addActionListener(a -> action.action.run());
		item.setEnabled(action.canRun.getAsBoolean());
		return item;
	}
	
	public static MenuItem setAction(MenuItem item, ActionListener action)
	{
		item.addActionListener(action);
		return item;
	}
	
	public static Menu makeMenu(String name, Stream<MenuItem> items)
	{
		Menu menu = new Menu(name);
		items.forEach(menu::add);
		return menu;
	}
}