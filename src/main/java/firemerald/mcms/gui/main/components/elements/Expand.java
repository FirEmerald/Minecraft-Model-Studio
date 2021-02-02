package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.Meshes;

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
		GuiShader s = main.guiShader;
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		main.textureManager.bindTexture(entry.expanded ? Textures.MODEL_RETRACT : Textures.MODEL_EXPAND);
		Meshes.X16.render();
		main.textureManager.unbindTexture();
		switch (state)
		{
		case DISABLED:
			main.guiShader.setColor(.5f, .5f, .5f, .5f);
			break;
		case HOVER:
			main.guiShader.setColor(0, 0, 1, .25f);
			break;
		case PUSH:
			main.guiShader.setColor(0, 0, 1, .5f);
			break;
		default:
			main.guiShader.setColor(0, 0, 0, 0);
			break;
		}
		Meshes.X16.render();
		main.guiShader.setColor(1, 1, 1, 1);
		GuiShader.MODEL.pop();
		s.updateModel();
	}
}