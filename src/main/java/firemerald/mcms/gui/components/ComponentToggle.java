package firemerald.mcms.gui.components;

import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.GuiMesh;

public class ComponentToggle extends ComponentButton
{
	public boolean state = false;
	public Consumer<Boolean> onToggle;
	public ThemeElement rect;
	public final GuiMesh mesh = new GuiMesh(), mesh2 = new GuiMesh();
	
	public ComponentToggle(int x1, int y1, int x2, int y2, boolean state, Consumer<Boolean> onToggle)
	{
		super(x1, y1, x2, y2);
		this.state = state;
		this.onToggle = onToggle;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void onRelease()
	{
		state = !state;
		if (onToggle != null) onToggle.accept(state);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		if (mesh != null) mesh.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
		if (mesh2 != null) mesh2.setMesh(x1 + 2, y1 + 2, x2 - 2, y2 - 2, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(x2 - x1, y2 - y1, 1);
		}
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		rect.bind();
		mesh.render();
		if (this.state)
		{
			Main.instance.textureManager.unbindTexture();
			Main.instance.guiShader.setColor(getTheme().getOutlineColor());
			mesh2.render();
			Main.instance.guiShader.setColor(1, 1, 1, 1);
		}
		state.removeButtonEffects();
	}
}