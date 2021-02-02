package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSV;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.window.api.MouseButtons;

public class ColorPickerHSV extends ColorPicker
{
	public static final GuiMesh H_SLIDER = new GuiMesh(0, 0, 155, 15);
	public static final GuiMesh SV_RECT = new GuiMesh(0, 20, 155, 155);
	public static final GuiMesh H_LINE = new GuiMesh(0, 0, 1, 15);
	public static final GuiMesh SV_DOT = new GuiMesh(-1, 19, 2, 22);
	
	private HSV hsv;
	private int selected = -1;
	
	public ColorPickerHSV(int x, int y, ColorModel color)
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
		if (button == MouseButtons.LEFT)
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

	public static final ResourceLocation TEX_H = new ResourceLocation(Main.ID, "color_pickers/hsv/h.png");
	public static final ResourceLocation TEX_SV = new ResourceLocation(Main.ID, "color_pickers/hsv/sv.png");
	public static final ResourceLocation TEX_DOT = new ResourceLocation(Main.ID, "color_pickers/dot.png");

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		Main.instance.guiShader.updateModel();
		GuiShader s = Main.instance.guiShader;
		TextureManager t = Main.instance.textureManager;
		t.bindTexture(TEX_H);
		H_SLIDER.render();
		s.setHueSet(true);
		s.setHue(hsv.h, 1);
		t.bindTexture(TEX_SV);
		SV_RECT.render();
		s.setHueSet(false);
		t.unbindTexture();
		s.setColor(0, 0, 0, .75f);
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(hsv.h * 154, 0, 0);
		s.updateModel();
		H_LINE.render();
		GuiShader.MODEL.pop();
		s.setColor(1, 1, 1, .75f);
		t.bindTexture(TEX_DOT);
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(hsv.s * 154, (1 - hsv.v) * 134, 0);
		s.updateModel();
		SV_DOT.render();
		GuiShader.MODEL.pop();
		s.updateModel();
		s.setColor(1, 1, 1, 1);
		GuiShader.MODEL.pop();
		Main.instance.guiShader.updateModel();
	}
}