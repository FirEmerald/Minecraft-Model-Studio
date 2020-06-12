package firemerald.mcms.util.hotkey;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;
import firemerald.mcms.window.api.Window;

public class HotKey
{
	public final int modifiers;
	public final Key key;
	private final int hashCode;
	
	public HotKey(Key key, Modifier... modifiers)
	{
		this.key = key;
		int temp = 0;
		for (Modifier modifier : modifiers) temp |= modifier.flag;
		this.modifiers = temp;
		this.hashCode = key.ordinal() | (this.modifiers << 12);
	}
	
	public HotKey(AbstractElement el)
	{
		Key key = el.getEnum("key", Key.values(), Key.UNKNOWN);
		int modifiers = 0;
		if (el.hasAttribute("modifiers")) for (String mod : el.getString("modifiers", "").split("\\*"))
		{
			boolean flag = false;
			for (Modifier modifier : Modifier.values()) if (modifier.name().equalsIgnoreCase(mod))
			{
				flag = true;
				modifiers |= modifier.flag;
				break;
			}
			if (!flag)
			{
				GuiPopupException.onException("Couldn't load HotKey with invalid modifier " + mod);
				key = Key.UNKNOWN;
				modifiers = 0;
				break;
			}
		}
		this.key = key;
		this.modifiers = modifiers;
		this.hashCode = key.ordinal() | (modifiers << 12);
	}
	
	public boolean isPressed(Window window, Key key, int modifiers)
	{
		return key == this.key && (modifiers & this.modifiers) == this.modifiers;
	}
	
	@Override
	public int hashCode()
	{
		return hashCode;
	}
	
	public void writeToElement(AbstractElement el)
	{
		List<String> modifiers = new ArrayList<>();
		int modsCheck = 0;
		for (Modifier modifier : Modifier.values())
		{
			if ((this.modifiers & modifier.flag) == modifier.flag)
			{
				modsCheck |= modifier.flag;
				modifiers.add(modifier.name().toLowerCase(Locale.ENGLISH));
			}
		}
		if (modsCheck != this.modifiers)
		{
			GuiPopupException.onException("Couldn't load HotKey with invalid modifier flags " + Integer.toBinaryString(this.modifiers - modsCheck));
			return;
		}
		el.setEnum("key", key);
		if (!modifiers.isEmpty())
		{
			StringJoiner joiner = new StringJoiner("*");
			modifiers.forEach(modifier -> joiner.add(modifier));
			el.setString("modifiers", joiner.toString());
		}
	}
}