package firemerald.mcms.window.glfw.callbacks;

import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.util.hotkey.HotKey;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Window;
import firemerald.mcms.window.glfw.KeyConverter;
import firemerald.mcms.window.glfw.ModifierConverter;

public class KeyCallback extends GLFWKeyCallback
{
	public final Main main;
	public final Window window;
	
	public KeyCallback(Main main, Window window)
	{
		this.main = main;
		this.window = window;
	}
	
	@Override
	public void invoke(long window, int code, int scancode, int action, int mods)
	{
		Key key = KeyConverter.getKey(code);
		mods = ModifierConverter.getModifiers(mods);
		if (action == GLFW.GLFW_PRESS) for (Entry<Action, HotKey> entry : Main.instance.state.hotkeys.entrySet())
		{
			if (entry.getValue().isPressed(this.window, key, mods))
			{
				entry.getKey().action.run();
				return;
			}
		}
		//if (action == GLFW.GLFW_PRESS && code == GLFW.GLFW_KEY_V && (mods & GLFW.GLFW_MOD_CONTROL) > 0) System.out.println(ClipboardUtil.getRTF());
		//if (action == GLFW.GLFW_PRESS && code == GLFW.GLFW_KEY_T && (mods & GLFW.GLFW_MOD_CONTROL) != 0) System.out.println(FileUtils.encode64(Main.instance.getTexture().getBytes()).length());
		//if (action == GLFW.GLFW_PRESS && code == GLFW.GLFW_KEY_C && (mods & GLFW.GLFW_MOD_CONTROL) != 0 && !(Main.instance.gui instanceof GuiColor)) new GuiColor(10, 10).activate();
		GuiScreen gui;
		if ((gui = main.getGui()) != null)
		{
			switch (action)
			{
			case GLFW.GLFW_PRESS:
				gui.onKeyPressed(key, scancode, mods);
				break;
			case GLFW.GLFW_RELEASE:
				gui.onKeyReleased(key, scancode, mods);
				break;
			case GLFW.GLFW_REPEAT:
				gui.onKeyRepeat(key, scancode, mods);
				break;
			}
		}
	}
}