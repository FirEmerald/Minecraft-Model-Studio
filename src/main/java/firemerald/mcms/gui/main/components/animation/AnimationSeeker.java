package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.theme.EnumDirection;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.GuiMesh;

public class AnimationSeeker extends ComponentButton
{
	public float scale;
	public ThemeElement el;
	public final GuiMesh mesh = new GuiMesh();
	
	public AnimationSeeker(int x, int y, int h, float scale)
	{
		super(x, y, x + h * 2, y + h);
		this.scale = scale;
		setSize(x1, y1, x2, y2);
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
		el = this.getTheme().genArrow(y2 - y1, EnumDirection.DOWN);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (el != null) el.release();
			el = this.getTheme().genArrow(y2 - y1, EnumDirection.DOWN);
		}
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
	
	private float px = 0;

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (Main.instance.project.getAnimationState() != null) px = mx - Main.instance.project.getAnimationState().time * scale;
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		if (state != null)
		{
			state.time = (mx - px) / scale;
			if (state.time < 0) state.time = 0;
			else if (state.time > state.anim.get().getLength()) state.time = state.anim.get().getLength();
		}
	}
	
	@Override
	public int getX1()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return super.getX1() + (state == null ? 0 : (int) Math.floor(state.time * scale));
	}
	
	@Override
	public int getX2()
	{
		ExtendedAnimationState state = Main.instance.project.getAnimationState();
		return super.getX2() + (state == null ? 0 : (int) Math.floor(state.time * scale));
	}

	@Override
	public void render(ButtonState state)
	{
		ExtendedAnimationState animState = Main.instance.project.getAnimationState();
		if (animState != null)
		{
			GuiShader.MODEL.push();
			GuiShader.MODEL.matrix().translate(animState.time * scale, 0, 0);
			Main.instance.guiShader.updateModel();
		}
		state.applyButtonEffects();
		el.bind();
		mesh.render();
		state.removeButtonEffects();
		if (animState != null)
		{
			GuiShader.MODEL.pop();
			Main.instance.guiShader.updateModel();
		}
	}
}