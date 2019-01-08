package firemerald.mcms.gui.colors;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSV;
import firemerald.mcms.util.TextureManager;

public class ColorPickerHSV extends ColorPicker
{
	public static final Mesh H_SLIDER = new Mesh(0, 0, 155, 15, 0);
	public static final Mesh SV_RECT = new Mesh(0, 20, 155, 155, 0);
	public static final Mesh H_LINE = new Mesh(0, 0, 1, 15, 0);
	public static final Mesh SV_DOT = new Mesh(-1, 19, 2, 22, 0);
	
	private HSV hsv;
	private int selected = -1;
	
	public ColorPickerHSV(float x, float y, ColorModel color)
	{
		super(x, y);
		hsv = color.getHSV();
	}
	
	@Override
	public void setFromColor(ColorModel color)
	{
		HSV hsv = color.getHSV();
		if (hsv.v == 0) this.hsv.v = 0; //don't change H or S
		else if (hsv.s == 0) //don't change H
		{
			this.hsv.s = 0;
			this.hsv.v = hsv.v;
		}
		else this.hsv = hsv;
	}

	@Override
	public ColorModel getColor()
	{
		return hsv;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			mx -= x1;
			my -= y1;
			if (my < 15) selected = 0; //h
			else if (my >= 20) selected = 1; //sv
			else selected = -1; //none
			update(mx, my);
		}
	}

	@Override
	public void onDrag(float mx, float my)
	{
		mx -= x1;
		my -= y1;
		update(mx, my);
	}
	
	private void update(float mx, float my)
	{
		if (selected != -1)
		{
			float valx = mx < 0 ? 0 : mx >= 155 ? 1 : mx / 155f;
			switch (selected)
			{
			case 0: hsv.h = valx; break;
			case 1:
				hsv.s = valx;
				hsv.v = my < 20 ? 1 : my >= 155 ? 0 : 1 - ((my - 20) / 135f);
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
		t.bindTexture("color_pickers/hsv/h.png");
		H_SLIDER.render();
		s.setHueSet(true);
		s.setHue(hsv.h, 1);
		t.bindTexture("color_pickers/hsv/sv.png");
		SV_RECT.render();
		s.setHueSet(false);
		t.unbindTexture();
		s.setColor(0, 0, 0, .75f);
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(hsv.h * 154, 0, 0);
		s.updateModel();
		H_LINE.render();
		Shader.MODEL.pop();
		s.setColor(1, 1, 1, .75f);
		t.bindTexture("color_pickers/dot.png");
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(hsv.s * 154, (1 - hsv.v) * 134, 0);
		s.updateModel();
		SV_DOT.render();
		Shader.MODEL.pop();
		s.updateModel();
		s.setColor(1, 1, 1, 1);
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
}