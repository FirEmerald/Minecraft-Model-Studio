package firemerald.mcms.window.awt;

import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import firemerald.mcms.window.api.Modifier;

public class ModifierConverter
{
	public static final Map<Integer, Integer> MAP = new HashMap<>();
	
	static
	{
		MAP.put(InputEvent.CTRL_DOWN_MASK, Modifier.CONTROL.flag);
		MAP.put(InputEvent.SHIFT_DOWN_MASK, Modifier.SHIFT.flag);
		MAP.put(InputEvent.ALT_DOWN_MASK, Modifier.ALT.flag);
		MAP.put(InputEvent.META_DOWN_MASK, Modifier.SUPER.flag);
	}
	
	public static int getModifiers(int mask)
	{
		int val = 0;
		for (Entry<Integer, Integer> entry : MAP.entrySet()) if ((mask & entry.getKey()) != 0) val |= entry.getValue();
		return val;
	}
}