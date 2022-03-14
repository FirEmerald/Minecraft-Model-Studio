package firemerald.mcms.texture.tools;

import static org.lwjgl.glfw.GLFW.*;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.main.components.model.ComponentModelViewer;
import firemerald.mcms.gui.main.components.texture.TextureViewer;
import firemerald.mcms.texture.BlendMode;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.window.api.MouseButtons;

public class ToolDropper implements ITextureTool
{
	public static final ToolDropper INSTANCE = new ToolDropper();
	
	@Override
	public boolean onModelViewClick(ComponentModelViewer viewer, float mx, float my, int button, int mods)
	{
		return button == MouseButtons.MIDDLE && viewer.processModelViewClick(mx, my, button, mods);
	}

	@Override
	public boolean onModelViewDrag(ComponentModelViewer viewer, float mx, float my, int button)
	{
		return false;
	}

	@Override
	public boolean onModelViewRelease(ComponentModelViewer viewer, float mx, float my, int button)
	{
		return false;
	}

	@Override
	public boolean onTextureViewClick(TextureViewer viewer, float mx, float my, int button, int mods)
	{
		return false;
	}

	@Override
	public boolean onTextureViewDrag(TextureViewer viewer, float mx, float my, int button)
	{
		return false;
	}

	@Override
	public boolean onTextureViewRelease(TextureViewer viewer, float mx, float my, int button)
	{
		return false;
	}
	
	@Override
	public void onMouseClick(Texture tex, double u, double v, int button)
	{
		if (button == GLFW_MOUSE_BUTTON_LEFT) Main.instance.toolHolder.setColor1(pick(tex, u, v));
		else if (button == GLFW_MOUSE_BUTTON_RIGHT) Main.instance.toolHolder.setColor2(pick(tex, u, v));
	}

	@Override
	public void onMouseDrag(Texture tex, double prevU, double prevV, double u, double v, int button, boolean isNewObject) //TODO drag = line
	{
		if (button == GLFW_MOUSE_BUTTON_LEFT) Main.instance.toolHolder.setColor1(pick(tex, u, v));
		else if (button == GLFW_MOUSE_BUTTON_RIGHT) Main.instance.toolHolder.setColor2(pick(tex, u, v));
	}

	@Override
	public void onMouseRelease(Texture tex, double u, double v, int button) {}
	
	public ColorModel pick(Texture tex, double u, double v)
	{
		return tex.getPixel((int) Math.round(u * tex.w - 0.5f), (int) Math.round(v * tex.h - 0.5f)).c;
	}

	private ComponentFloatingLabel labelBlendMode;
	private SelectorButton buttonBlendMode;

	@Override
	public void onSelect(GuiSection options)
	{
		GuiElementContainer container = options.container;
		int minX = options.minX;
		int minY = options.minY;
		int maxX = options.maxX;
		//float maxY = options.maxY;
		container.addElement(labelBlendMode  = new ComponentFloatingLabel(minX, minY     , maxX, minY + 20, Main.instance.fontMsg, "Blend Mode"));
		container.addElement(buttonBlendMode = new SelectorButton(minX, minY + 20, maxX, minY + 40, Main.instance.toolHolder.getBlendMode(), BlendMode.values(), Main.instance.toolHolder::setBlendMode));
	}

	@Override
	public void onDeselect(GuiSection options)
	{
		GuiElementContainer container = options.container;
		container.removeElement(labelBlendMode);
		container.removeElement(buttonBlendMode);
	}

	@Override
	public void drawOnOverlay(Texture tex, double u, double v)
	{
		// TODO Auto-generated method stub
	}
}