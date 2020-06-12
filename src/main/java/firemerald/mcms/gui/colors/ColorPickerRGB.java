package firemerald.mcms.gui.colors;

import firemerald.mcms.Main;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.MouseButtons;

public class ColorPickerRGB extends ColorPicker
{
	public static final Mesh R_SLIDER = new Mesh(0, 0, 155, 15, 0);
	public static final Mesh G_SLIDER = new Mesh(0, 20, 155, 35, 0);
	public static final Mesh B_SLIDER = new Mesh(0, 40, 155, 55, 0);
	public static final Mesh R_LINE = new Mesh(0, 0, 1, 15, 0);
	public static final Mesh G_LINE = new Mesh(0, 20, 1, 35, 0);
	public static final Mesh B_LINE = new Mesh(0, 40, 1, 55, 0);
	
	private RGB rgb;
	private int selected = -1;
	
	public ColorPickerRGB(int x, int y, ColorModel color)
	{
		super(x, y);
		rgb = color.getRGB();
	}
	
	@Override
	public void setFromColor(ColorModel color)
	{
		rgb = color.getRGB();
	}

	@Override
	public ColorModel getColor()
	{
		return rgb;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
		{
			mx -= x1;
			my -= y1;
			if (my < 15) selected = 0; //r
			else if (my >= 20 && my < 35) selected = 1; //g
			else if (my >= 40 && my < 55) selected = 2; //b
			else selected = -1; //none
			update(mx);
		}
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT)
		{
			mx -= x1;
			my -= y1;
			update(mx);
		}
	}
	
	private void update(float mx)
	{
		if (selected != -1)
		{
			float val = mx < 0 ? 0 : mx >= 155 ? 1 : mx / 155f;
			switch (selected)
			{
			case 0: rgb.r = val; break;
			case 1: rgb.g = val; break;
			case 2: rgb.b = val; break;
			}
			hasChanged = true;
		}
	}

	public static final ResourceLocation TEX_R = new ResourceLocation(Main.ID, "color_pickers/rgb/r.png");
	public static final ResourceLocation TEX_G = new ResourceLocation(Main.ID, "color_pickers/rgb/g.png");
	public static final ResourceLocation TEX_B = new ResourceLocation(Main.ID, "color_pickers/rgb/b.png");

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x1, y1, 0);
		Main.instance.shader.updateModel();
		Shader s = Main.instance.shader;
		TextureManager t = Main.instance.textureManager;
		s.setColor(1, rgb.g, rgb.b, 1);
		t.bindTexture(TEX_R);
		R_SLIDER.render();
		s.setColor(rgb.r, 1, rgb.b, 1);
		t.bindTexture(TEX_G);
		G_SLIDER.render();
		s.setColor(rgb.r, rgb.g, 1, 1);
		t.bindTexture(TEX_B);
		B_SLIDER.render();
		t.unbindTexture();
		s.setColor(0, 0, 0, .75f);
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(rgb.r * 154, 0, 0);
		s.updateModel();
		R_LINE.render();
		Shader.MODEL.pop();
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(rgb.g * 154, 0, 0);
		s.updateModel();
		G_LINE.render();
		Shader.MODEL.pop();
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(rgb.b * 154, 0, 0);
		s.updateModel();
		B_LINE.render();
		Shader.MODEL.pop();
		s.updateModel();
		s.setColor(1, 1, 1, 1);
		Shader.MODEL.pop();
		s.updateModel();
	}
}