package firemerald.mcms.texture.tools;

import static org.lwjgl.glfw.GLFW.*;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.texture.BlendMode;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.Texture;

public abstract class AbstractToolBrush implements ITool
{
	@Override
	public void onMouseClick(Texture tex, double u, double v, int button)
	{
		if (button == GLFW_MOUSE_BUTTON_LEFT) draw(tex, u, v, Main.instance.toolHolder.getColor1());
		else if (button == GLFW_MOUSE_BUTTON_RIGHT) draw(tex, u, v, Main.instance.toolHolder.getColor2());
	}

	@Override
	public void onMouseDrag(Texture tex, double prevU, double prevV, double u, double v, int button) //TODO drag = line
	{
		if (button == GLFW_MOUSE_BUTTON_LEFT) drawLine(tex, prevU, prevV, u, v, Main.instance.toolHolder.getColor1());
		else if (button == GLFW_MOUSE_BUTTON_RIGHT) drawLine(tex, prevU, prevV, u, v, Main.instance.toolHolder.getColor2());
	}

	@Override
	public void onMouseRelease(Texture tex, double u, double v, int button) {}
	
	public static final Color OVERLAY = new Color(0, 0, 0, .5f);
	
	@Override
	public void drawOnOverlay(Texture tex, double u, double v)
	{
		draw(tex, u, v, OVERLAY);
	}
	
	public abstract void draw(Texture tex, double u, double v, Color color);
	
	public abstract void drawLine(Texture tex, double u1, double v1, double u2, double v2, Color color);

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
		String[] values = new String[BlendMode.values().length];
		for (int i = 0; i < values.length; i++) values[i] = BlendMode.values()[i].name();
		container.addElement(labelBlendMode  = new ComponentFloatingLabel(minX, minY     , maxX, minY + 20, Main.instance.fontMsg, "Blend Mode"));
		container.addElement(buttonBlendMode = new SelectorButton(minX, minY + 20, maxX, minY + 40, Main.instance.toolHolder.getBlendMode().name(), values, (ind, str) -> {Main.instance.toolHolder.setBlendMode(BlendMode.values()[ind]);}));
	}

	@Override
	public void onDeselect(GuiSection options)
	{
		GuiElementContainer container = options.container;
		container.removeElement(labelBlendMode);
		container.removeElement(buttonBlendMode);
	}
}