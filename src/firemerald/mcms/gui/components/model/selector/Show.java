package firemerald.mcms.gui.components.model.selector;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.util.Meshes;
import firemerald.mcms.util.Textures;

public class Show extends ComponentButton
{
	public final SelectorEntry entry;
	
	public Show(SelectorEntry entry)
	{
		super(entry.x + 16, entry.y, entry.x + 32, entry.y + 16);
		this.entry = entry;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public void onRelease()
	{
		entry.editable.setVisible(!entry.editable.isVisible());
	}

	@Override
	public void render(ButtonState state)
	{
		Main main = Main.instance;
		main.textureManager.bindTexture(entry.editable.isVisible() ? Textures.EDITABLE_VISIBLE : Textures.EDITABLE_HIDDEN);
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