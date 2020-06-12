package firemerald.mcms.window.awt;

import java.util.HashMap;
import java.util.Map;

public class CursorConverter
{
	public static final Map<firemerald.mcms.window.api.Cursor, java.awt.Cursor> MAP = new HashMap<>();
	
	static
	{
		MAP.put(firemerald.mcms.window.api.Cursor.STANDARD, java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
		MAP.put(firemerald.mcms.window.api.Cursor.TEXT, java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.TEXT_CURSOR));
		MAP.put(firemerald.mcms.window.api.Cursor.CROSSHAIR, java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		MAP.put(firemerald.mcms.window.api.Cursor.HAND, java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		MAP.put(firemerald.mcms.window.api.Cursor.HORIZONTAL_RESIZE, java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		MAP.put(firemerald.mcms.window.api.Cursor.VERTICAL_RESIZE, java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
	}
	
	public static java.awt.Cursor getCursor(firemerald.mcms.window.api.Cursor cursor)
	{
		java.awt.Cursor val = MAP.get(cursor);
		return val != null ? val : java.awt.Cursor.getDefaultCursor();
	}
}