package firemerald.mcms.gui.popups.hotkeys;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.util.hotkey.HotKey;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;

public class ComponentKeyGetter extends StandardButton
{
	public HotKey hotKey;
	private final Consumer<HotKey> onChanged;
	public boolean isSetting = false;
	
	public ComponentKeyGetter(int x1, int y1, int x2, int y2, HotKey currentKey, Consumer<HotKey> onChanged)
	{
		super(x1, y1, x2, y2, "", () -> {});
		this.onRelease = () -> isSetting = true;
		this.hotKey = currentKey;
		this.onChanged = onChanged;
		getString();
		
	}
	
	public void getString()
	{
		if (isSetting)
		{
			StringJoiner joiner = new StringJoiner(" + ");
			for (Modifier mod : Modifier.values()) if (mod.isDown(Main.instance.window)) joiner.add(mod.name());
			joiner.add("...");
			text = joiner.toString();
		}
		else if (hotKey == null) text = "(none)";
		else
		{
			StringJoiner joiner = new StringJoiner(" + ");
			for (Modifier mod : Modifier.values()) if ((mod.flag & hotKey.modifiers) != 0) joiner.add(mod.name());
			joiner.add(hotKey.key.name());
			text = joiner.toString();
		}
	}

	@Override
	public boolean onKeyPressed(Key key, int scancode, int mods)
	{
		if (isSetting)
		{
			if (key == Key.ESCAPE)
			{
				hotKey = null;
				isSetting = false;
				onChanged.accept(null);
			}
			else if (!key.isModifier)
			{
				List<Modifier> modifiers = new ArrayList<>();
				for (Modifier mod : Modifier.values()) if (mod.isDown(Main.instance.window)) modifiers.add(mod);
				hotKey = new HotKey(key, modifiers.toArray(new Modifier[modifiers.size()]));
				isSetting = false;
				onChanged.accept(hotKey);
			}
			getString();
			return true;
		}
		else return false;
	}

	@Override
	public boolean onKeyReleased(Key key, int scancode, int mods)
	{
		if (isSetting)
		{
			if (key.isModifier) getString();
			return true;
		}
		else return false;
	}

	@Override
	public boolean onKeyRepeat(Key key, int scancode, int mods)
	{
		return isSetting;
	}

	@Override
	public boolean isEnabled()
	{
		return !isSetting;
	}

	/*
	@Override
	public ButtonState getState(float mx, float my, boolean canHover)
	{
		return isSetting ? ButtonState.PUSH : super.getState(mx, my, canHover);
	}
	*/
	
	public void onUnfocus()
	{
		this.isSetting = false;
	}
}