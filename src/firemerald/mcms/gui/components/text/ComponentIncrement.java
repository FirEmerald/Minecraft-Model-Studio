package firemerald.mcms.gui.components.text;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.EnumDirection;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.Mesh;

public abstract class ComponentIncrement extends ComponentButton
{
	public static final int ARROW_W = 10;
	public static final int ARROW_H = 10;
	public static final Mesh ARROW = new Mesh(0, 0, ARROW_W, ARROW_H, 0, 0, 0, 1, 1);
	
	public ThemeElement id;
	public boolean isNegative;

	public ComponentIncrement(int x, int y, boolean isNegative)
	{
		super(x, y, x + ARROW_W, y + ARROW_H);
		setIsNegative(isNegative);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	public void setIsNegative(boolean isNegative)
	{
		this.isNegative = isNegative;
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (id != null) id.release();
			id = getTheme().genDirectionButton(ARROW_W, ARROW_H, 1, 0, isNegative ? EnumDirection.DOWN : EnumDirection.UP);
		}
	}
	
	public void setPosition(int x, int y)
	{
		setSize(x, y, x + ARROW_W, y + ARROW_H);
	}
	
	public abstract void increment();
	
	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		this.held = false;
	}

	@Override
	public void onPress()
	{
		increment();
	}

	@Override
	public void onRepeat()
	{
		increment();
	}
	
	@Override
	public void render(ButtonState state)
	{
		Shader s = Main.instance.shader;
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		state.applyButtonEffects();
		id.bind();
		ARROW.render();
		state.removeButtonEffects();
		Shader.MODEL.pop();
		s.updateModel();
	}
}