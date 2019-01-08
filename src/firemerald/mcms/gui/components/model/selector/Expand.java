package firemerald.mcms.gui.components.model.selector;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.util.Meshes;
import firemerald.mcms.util.Textures;

public class Expand extends ComponentButton
{
	public final SelectorEntry entry;
	public final ComponentEditSelector selector;
	
	public Expand(SelectorEntry entry, ComponentEditSelector selector)
	{
		super(entry.x, entry.y, entry.x + 16, entry.y + 16);
		this.entry = entry;
		this.selector = selector;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public void onRelease()
	{
		entry.expanded = !entry.expanded;
		selector.updateList();
	}

	@Override
	public void render(ButtonState state)
	{
		Main main = Main.instance;
		main.textureManager.bindTexture(entry.expanded ? Textures.EDITABLE_RETRACT : Textures.EDITABLE_EXPAND);
		Meshes.X16.render();
		main.textureManager.unbindTexture();
		switch (state)
		{
		case DISABLED:
			main.shader.setColor(.5f, .5f, .5f, .5f);
			break;
		case HOVER:
			main.shader.setColor(0, 0, 1, .25f);
			break;
		case PUSH:
			main.shader.setColor(0, 0, 1, .5f);
			break;
		default:
			main.shader.setColor(0, 0, 0, 0);
			break;
		}
		Meshes.X16.render();
		main.shader.setColor(1, 1, 1, 1);
	}
}