package firemerald.mcms.events.gui;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import firemerald.mcms.events.Event;
import firemerald.mcms.gui.popups.GuiPopupException;

public class TitlebarInitEvent extends Event
{
	private final List<String> ordering = new ArrayList<>();
	private final Map<String, Menu> titlebar = new LinkedHashMap<>();
	
	public TitlebarInitEvent() {}

	public Function<MenuItem, MenuItem> getOrMakeCategoryBefore(String category, String before)
	{
		Menu menu = titlebar.get(category);
		if (menu == null)
		{
			titlebar.put(category, menu = new Menu(category));
			int i = ordering.indexOf(before);
			if (i >= 0) ordering.add(i, category);
			else ordering.add(category);
		}
		return addItemFunction(menu, category);
	}

	public Function<MenuItem, MenuItem> getOrMakeCategoryAfter(String category, String after)
	{
		Menu menu = titlebar.get(category);
		if (menu == null)
		{
			titlebar.put(category, menu = new Menu(category));
			int i = ordering.indexOf(after);
			if (i >= 0) ordering.add(i + 1, category);
			else ordering.add(category);
		}
		return addItemFunction(menu, category);
	}
	
	public Function<MenuItem, MenuItem> getOrMakeCategory(String category)
	{
		Menu menu = titlebar.get(category);
		if (menu == null)
		{
			titlebar.put(category, menu = new Menu(category));
			ordering.add(category);
		}
		return addItemFunction(menu, category);
	}
	
	private Function<MenuItem, MenuItem> addItemFunction(Menu menu, String category)
	{
		return item -> {
			if (item instanceof CheckboxMenuItem)
			{
				GuiPopupException.onException(new IllegalArgumentException("Tried to add an unsupported menu item to titlebar: CheckboxMenuItem is not currently supported. Item being added: " + item.toString() + " in category " + category));
				return null;
			}
			menu.add(item);
			return item;
		};
	}
	
	public Map<String, Menu> getTitlebar()
	{
		Map<String, Menu> map = new LinkedHashMap<>();
		ordering.forEach(category -> map.put(category, titlebar.get(category)));
		return map;
	}
}