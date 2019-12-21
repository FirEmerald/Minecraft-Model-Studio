package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.EnumDirection;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.Mesh;

public class AnimationSeeker extends ComponentButton
{
	public float scale;
	public ThemeElement el;
	public final Mesh mesh = new Mesh();
	
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
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
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
		px = mx - Main.instance.animTime * scale;
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		Main main = Main.instance;
		if (main.project.getAnimation() != null)
		{
			main.animTime = (mx - px) / scale;
			if (main.animTime < 0) main.animTime = 0;
			else if (main.animTime > main.project.getAnimation().getLength()) main.animTime = main.project.getAnimation().getLength();
		}
	}
	
	@Override
	public int getX1()
	{
		return super.getX1() + (int) Math.floor(Main.instance.animTime * scale);
	}
	
	@Override
	public int getX2()
	{
		return super.getX2() + (int) Math.floor(Main.instance.animTime * scale);
	}

	@Override
	public void render(ButtonState state)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(Main.instance.animTime * scale, 0, 0);
		Main.instance.shader.updateModel();
		state.applyButtonEffects();
		el.bind();
		mesh.render();
		state.removeButtonEffects();
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
}