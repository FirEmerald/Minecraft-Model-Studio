package firemerald.mcms.window.awt.listeners;

import java.awt.event.KeyEvent;
import java.util.BitSet;
import java.util.Map.Entry;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.util.hotkey.HotKey;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.awt.AWTWindow;
import firemerald.mcms.window.awt.KeyConverter;
import firemerald.mcms.window.awt.ModifierConverter;

public class KeyListener implements java.awt.event.KeyListener
{
	final Main main;
	public final AWTWindow window;
	final BitSet state = new BitSet(Key.values().length);
	
	public KeyListener(Main main, AWTWindow window)
	{
		this.main = main;
		this.window = window;
	}
	
	@Override
	public void keyPressed(KeyEvent event)
	{
		Key key = KeyConverter.getKey(event.getKeyCode(), event.getKeyLocation());
		int mods = ModifierConverter.getModifiers(event.getModifiersEx());
		boolean prevState = state.get(key.ordinal());
		state.set(key.ordinal());
		window.actions.add(() -> {
			for (Entry<Action, HotKey> entry : Main.instance.state.hotkeys.entrySet())
			{
				if (entry.getValue() != null && entry.getValue().isPressed(this.window, key, mods))
				{
					if (main.doAction(entry.getKey())) return;
				}
			}
			GuiScreen gui;
			if ((gui = main.getGui()) != null)
			{
				if (prevState) gui.onKeyRepeat(key, event.getExtendedKeyCode(), mods);
				else gui.onKeyPressed(key, event.getExtendedKeyCode(), ModifierConverter.getModifiers(event.getModifiersEx()));
			}
		});
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		Key key = KeyConverter.getKey(event.getKeyCode(), event.getKeyLocation());
		int mods = ModifierConverter.getModifiers(event.getModifiersEx());
		boolean prevState = state.get(key.ordinal());
		state.set(key.ordinal(), false);
		GuiScreen gui;
		if ((gui = main.getGui()) != null)
		{
			if (prevState) window.actions.add(() -> gui.onKeyReleased(key, event.getExtendedKeyCode(), mods)); //prevent release repeats
		}
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		GuiScreen gui;
		if ((gui = main.getGui()) != null) window.actions.add(() -> gui.onCharTyped(event.getKeyChar()));
	}
	
	public boolean keyDown(Key key)
	{
		return state.get(key.ordinal());
	}
}