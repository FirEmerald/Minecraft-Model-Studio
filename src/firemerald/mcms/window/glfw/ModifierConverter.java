package firemerald.mcms.window.glfw;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.window.api.Modifier;

public class ModifierConverter
{
	public static final Map<Integer, Integer> MAP = new HashMap<>();
	
	static
	{
		MAP.put(GLFW.GLFW_MOD_CONTROL, Modifier.CONTROL.flag);
		MAP.put(GLFW.GLFW_MOD_SHIFT, Modifier.SHIFT.flag);
		MAP.put(GLFW.GLFW_MOD_ALT, Modifier.ALT.flag);
		MAP.put(GLFW.GLFW_MOD_SUPER, Modifier.SUPER.flag);
	}
	
	public static int getModifiers(int mask)
	{
		int val = 0;
		for (Entry<Integer, Integer> entry : MAP.entrySet()) if ((mask & entry.getKey()) != 0) val |= entry.getValue();
		return val;
	}
}