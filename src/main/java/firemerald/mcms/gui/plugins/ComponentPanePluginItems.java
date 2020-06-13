package firemerald.mcms.gui.plugins;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.gui.components.scrolling.ScrollableComponentPaneVertical;
import firemerald.mcms.plugin.PluginLoader;
import firemerald.mcms.util.IntReference;

public class ComponentPanePluginItems extends ScrollableComponentPaneVertical
{
	public final List<PluginItem> pluginItems = new ArrayList<>();
	
	public ComponentPanePluginItems(int x1, int y1, int x2, int y2, int border)
	{
		super(x1, y1, x2, y2, border);
		final int sizeX = x2 - x1 - border * 2;
		final IntReference y = new IntReference(border);
		PluginLoader.INSTANCE.loadedPlugins.values().forEach(plugin -> {
			int size = plugin.thumbnail() == null ? 48 : 60;
			PluginItem item = new PluginItem(this, plugin, border, y.val, sizeX, y.val + size);
			this.addElement(item);
			pluginItems.add(item);
			y.val += size;
		});
		updateComponentSize();
		updateScrollSize();
	}
	
	public void updateItemSizes()
	{
		final IntReference y = new IntReference(this.margin);
		pluginItems.forEach(item -> {
			int size = Math.round(item.size);
			item.setSize(item.x1, y.val, item.x2, y.val + size);
			y.val += size;
		});
		updateComponentSize();
		updateScrollSize();
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		final int sizeX = x2 - x1 - margin * 2;
		if (pluginItems != null) pluginItems.forEach(item -> item.setSize(margin, item.y1, sizeX, item.y2));
		updateComponentSize();
		updateScrollSize();
	}
}