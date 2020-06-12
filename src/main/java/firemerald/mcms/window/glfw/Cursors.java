package firemerald.mcms.window.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Map;

import firemerald.mcms.window.api.Cursor;

public class Cursors
{
	public final long standard;
	private final Map<Integer, Long> compiled = new HashMap<>();
	public final Map<Cursor, Long> cursors = new HashMap<>();
	
	public Cursors()
	{
		standard = create(Cursor.STANDARD, GLFW_ARROW_CURSOR);
		create(Cursor.TEXT, GLFW_IBEAM_CURSOR);
		create(Cursor.CROSSHAIR, GLFW_CROSSHAIR_CURSOR);
		create(Cursor.HAND, GLFW_HAND_CURSOR);
		create(Cursor.HORIZONTAL_RESIZE, GLFW_HRESIZE_CURSOR);
		create(Cursor.VERTICAL_RESIZE, GLFW_VRESIZE_CURSOR);
	}
	
	public long create(Cursor cursor, int type)
	{
		Long val = compiled.get(type);
		if (val == null) val = glfwCreateStandardCursor(type);
		cursors.put(cursor, val);
		return val;
	}
	
	public long getCursor(Cursor cursor)
	{
		Long val;
		if ((val = cursors.get(cursor)) != null) return val;
		else return standard;
	}
}