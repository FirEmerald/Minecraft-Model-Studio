package firemerald.mcms.gui.plugins;

import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.gui.decoration.DecoIcon;
import firemerald.mcms.gui.decoration.DecoText;
import firemerald.mcms.plugin.AbstractPluginWrapper;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.font.Formatting;
import firemerald.mcms.util.mesh.Mesh;

public class PluginItem extends ComponentPane
{
	public final ComponentPanePluginItems pane;
	public final AbstractPluginWrapper plugin;
	public final Mesh mesh = new Mesh();
	public ThemeElement rect = null;
	public final int minSize = 28, maxSize;
	public float size;
	public boolean expanded = false;
	
	public PluginItem(ComponentPanePluginItems pane, AbstractPluginWrapper plugin, int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2, 6);
		this.pane = pane;
		size = y2 - y1;
		this.plugin = plugin;
		String thumb = plugin.icon;
		String resetColor = Formatting.color(getTheme().getTextColor());
		if (thumb != null)
		{
			this.addElement(new DecoIcon(0, 0, 16, 16, new ResourceLocation(thumb)));
			this.addElement(new DecoText(18, -1, x2 - 12, 41, plugin.name));
			StringBuilder builder = new StringBuilder("Version: ");
			builder.append(plugin.version);
			builder.append(resetColor);
			builder.append("\nAuthor: ");
			builder.append(plugin.author);
			builder.append("\n");
			builder.append(resetColor);
			String[] credits = plugin.credits;
			if (credits.length > 0)
			{
				builder.append("Credits: ");
				builder.append(String.join(", " + resetColor, credits));
				builder.append('\n');
			}
			builder.append(resetColor);
			String desc = plugin.description;
			if (desc != null) builder.append(desc);
			else builder.append("No mod description provided.");
			String str = builder.toString();
			maxSize = 34 + str.split("\n").length * 12;
			this.addElement(new DecoText(0, 15, x2 - 12, y1 + maxSize - 12, str));
		}
		else
		{
			StringBuilder builder = new StringBuilder(plugin.name);
			builder.append(resetColor);
			builder.append("\nVersion: ");
			builder.append(plugin.version);
			builder.append(resetColor);
			builder.append("\nAuthor: ");
			builder.append(plugin.author);
			builder.append("\n");
			builder.append(resetColor);
			String[] credits = plugin.credits;
			if (credits.length > 0)
			{
				builder.append("Credits: ");
				builder.append(String.join(", " + resetColor, credits));
				builder.append('\n');
			}
			builder.append(resetColor);
			String desc = plugin.description;
			if (desc != null) builder.append(desc);
			else builder.append("No mod description provided.");
			String str = builder.toString();
			maxSize = 18 + str.split("\n").length * 12;
			this.addElement(new DecoText(0, -1, x2 - 12, y1 + maxSize - 12, str));
		}
	}

	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genRoundedBox(x2 - x1, y2 - y1, 2, 6);
		}
	}
	
	private static float EXPAND_SPEED = 120;
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		if (expanded)
		{
			if (size < maxSize)
			{
				if ((size += deltaTime * EXPAND_SPEED) >= maxSize) size = maxSize;
				pane.updateItemSizes();
			}
		}
		else
		{
			if (size > minSize)
			{
				if ((size -= deltaTime * EXPAND_SPEED) <= minSize) size = minSize;
				pane.updateItemSizes();
			}
		}
		super.tick(mx, my, deltaTime);
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		rect.bind();
		mesh.render();
		super.render(mx, my, canHover);
	}
	
	@Override
	public void onFocus()
	{
		super.onFocus();
		expanded = true;
	}
	
	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		expanded = false;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		this.onGuiUpdate(GuiUpdate.THEME);
	}
}