package firemerald.mcms.gui.components.model;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.theme.RoundedBoxFormat;
import firemerald.mcms.util.Meshes;

public abstract class EditableButton extends ComponentButton
{
	public final EditorPanes editorPanes;
	public static final RoundedBoxFormat RECTANGLE = new RoundedBoxFormat(32, 32);
	public boolean enabled = false;
	
	public EditableButton(float x, float y, EditorPanes editorPanes)
	{
		super(x, y, x + 32, y + 32);
		this.editorPanes = editorPanes;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void render(ButtonState state)
	{
		Main main = Main.instance;
		state.applyButtonEffects();
		getTheme().bindRoundedBox(RECTANGLE);
		Meshes.X32.render();
		main.textureManager.bindTexture(getTexture());
		Meshes.X32.render();
		state.removeButtonEffects();
	}
	
	public abstract String getTexture();
}