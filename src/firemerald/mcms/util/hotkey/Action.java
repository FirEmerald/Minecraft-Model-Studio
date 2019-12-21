package firemerald.mcms.util.hotkey;

import java.util.LinkedHashMap;
import java.util.Map;

import firemerald.mcms.Main;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;

public class Action
{
	public static final Map<String, Action> ACTIONS = new LinkedHashMap<>();
	
	public static final Action SAVE = new Action("save", "save project", () -> Main.instance.project.save(), new HotKey(Key.S, Modifier.CONTROL));
	public static final Action SAVE_AS = new Action("save_as", "save project as", () -> Main.instance.project.saveAs(), new HotKey(Key.S, Modifier.SHIFT, Modifier.CONTROL));
	public static final Action LOAD = new Action("load", "open project", () -> Main.instance.project.loadFrom(), new HotKey(Key.O, Modifier.CONTROL));
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