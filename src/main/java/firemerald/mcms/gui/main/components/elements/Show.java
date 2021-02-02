package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.Meshes;

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
		GuiShader s = main.guiShader;
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		main.textureManager.bindTexture(entry.editable.isVisible() ? Textures.MODEL_VISIBLE : Textures.MODEL_HIDDEN);
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