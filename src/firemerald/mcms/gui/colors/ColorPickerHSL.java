package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSL;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.MouseButtons;

public class ColorPickerHSL extends ColorPicker
{
	public static final float SQRT3 = (float) Math.sqrt(3);
	public static final float IRR = 31.25f * SQRT3; 
	public static final Mesh H_CIRCLE = new Mesh(0, 0, 155, 155, 0);
	public static final Mesh SL_TRIANGLE = new Mesh(new float[] {
			62.5f, 0, 0,
			-31.25f, -IRR, 0,
			-31.25f, IRR, 0
	}, new float[] {
			1, .5f,
			.25f, 0.0669872981078f,
			.25f, 0.9330127018922f
	}, new float[] {
			0, 0, 1,
			0, 0, 1,
			0, 0, 1
	}, new int[] {
			0, 1, 2
	});
	public static final Mesh H_LINE = new Mesh(62.5f, -1, 77.5f, 1, 0);
	public static final Mesh SL_DOT = new Mesh(-1.5f, -1.5f, 1.5f, 1.5f, 0);
	
	private HSL hsl;
	private int selected = -1;
	
	public ColorPickerHSL(int x, int y, ColorModel color)
	{
		super(x, y);
		hsl = color.getHSL();
	}
	
	@Override
	public void setFromColor(ColorModel color)
	{
		HSL hsl = color.getHSL();
		if (hsl.s == 0) //don't change H
		{
			this.hsl.s = 0;
			this.hsl.l = hsl.l;
		}
		else this.hsl = hsl;
	}

	@Override
	public ColorModel getColor()
	{
		return hsl;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
		{
			mx -= x1;
			my -= y1;
			float ax = mx - 77.5f;
			float ay = my - 77.5f;
			float r = ax * ax + ay * ay;
			if (r >= (62.5f * 62.5f) && r < (77.5f * 77.5f)) selected = 0; //h
			else
			{
				double angle = - (hsl.h * 2 * Math.PI);
				float cs = (float) Math.cos(angle);
				float sn = (float) Math.sin(angle);
				float tx = ax * cs + ay * sn; //de-rotated x
				float ty = ay * cs - ax * sn; //de-rotated y
				if (tx >= -31.25f && tx <= 62.5f && tx <= 62.5f - Math.abs(ty * SQRT3)) selected = 1;
				else selected = -1; //none
			}
			update(mx, my);
		}
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT)
		{
			mx -= x1;
			my -= y1;
			update(mx, my);
		}
	}
	
	private void update(float mx, float my)
	{
		if (selected != -1)
		{
			switch (selected)
			{
			case 0:
			{
				float ay = 77.5f - my;
				if (ay < 0) hsl.h = (float) (Math.atan2(ay, mx - 77.5) / (2 * Math.PI)) + 1;
				else hsl.h = (float) (Math.atan2(ay, mx - 77.5) / (2 * Math.PI));
				break;
			}
			case 1:
			{
				float ax = mx - 77.5f;
				float ay = my - 77.5f;
				double angle = - (hsl.h * 2 * Math.PI);
				float cs = (float) Math.cos(angle);
				float sn = (float) Math.sin(angle);
				float tx = ax * cs + ay * sn; //de-rotated x
				float ty = ay * cs - ax * sn; //de-rotated y
				if (tx <= -31.25f) //the easy one
				{
					tx = -31.25f;
					if (ty < -IRR) ty = -IRR;
					else if (ty > IRR) ty = IRR;
				}
				else if (ty <= 0)
				{
					if (tx > 62.5f + ty * SQRT3)
					{
						float t = .5f - (tx / 125) - (ty * SQRT3 / 375);
						if (t <= 0)
						{
							tx = 62.5f;
							ty = 0;
						}
						else if (t >= 1)
						{
							tx = -31.25f;
							ty = -IRR;
						}
						else
						{
							float ny = -IRR * t;
							if (ny > ty)
							{
								tx = 62.5f - 93.75f * t;
								ty = ny;
							}
						}
					}
				}
				else
				{
					if (tx > 62.5f - ty * SQRT3)
					{
						float t = .5f - (tx / 125) + (ty * SQRT3 / 375);
						if (t <= 0)
						{
							tx = 62.5f;
							ty = 0;
						}
						else if (t >= 1)
						{
							tx = -31.25f;
							ty = IRR;
						}
						else
						{
							float ny = IRR * t;
							if (ny < ty)
							{
								tx = 62.5f - 93.75f * t;
								ty = ny;
							}
						}
					}
				}
				ty = ty / IRR;
				hsl.l = ty <= -1 ? 1 : ty >= 1 ? 0 : (1 - ty) * .5f;
				float l = 1 - Math.abs(ty);
				if (l > 0)
				{
					hsl.s = (tx + 32.5f) / (l * 93.75f);
					if (hsl.s < 0) hsl.s = 0;
					else if (hsl.s > 1) hsl.s = 1;
				}
				break;
			}
			}
			hasChanged = true;
		}
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x1, y1, 0);
		Main.instance.shader.updateModel();
		Shader s = Main.instance.shader;
		TextureManager t = Main.instance.textureManager;
		t.bindTexture("color_pickers/hsl/h.png");
		H_CIRCLE.render();
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(77.5f, 77.5f, 0);
		Shader.MODEL.matrix().rotateZ(hsl.h * (float) (-2 * Math.PI));
		s.updateModel();
		s.setHueSet(true);
		s.setHue(hsl.h, 1);
		t.bindTexture("color_pickers/hsl/sl.png");
		SL_TRIANGLE.render();
		s.setHueSet(false);
		t.unbindTexture();
		t.unbindTexture();
		s.setColor(0, 0, 0, .75f);
		H_LINE.render();
		s.setColor(1, 1, 1, .75f);
		t.bindTexture("color_pickers/dot.png");
		Shader.MODEL.push();
		float l = 1 - 2 * hsl.l;
		float sl = hsl.s * (1 - Math.abs(l));
		Shader.MODEL.matrix().translate(sl * 93.75f - 31.25f, l * 54.1265877365274f, 0);
		Shader.MODEL.matrix().rotateZ(hsl.h * (float) (2 * Math.PI));
		s.updateModel();
		SL_DOT.render();
		Shader.MODEL.pop();
		Shader.MODEL.pop();
		s.updateModel();
		s.setColor(1, 1, 1, 1);
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
}