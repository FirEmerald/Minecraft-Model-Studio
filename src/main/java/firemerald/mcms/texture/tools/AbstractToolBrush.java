package firemerald.mcms.texture.tools;

import static org.lwjgl.glfw.GLFW.*;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.main.components.model.ComponentModelViewer;
import firemerald.mcms.texture.BlendMode;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.history.HistoryActionTextureDraw;
import firemerald.mcms.window.api.MouseButtons;

public abstract class AbstractToolBrush implements ITextureTool
{
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
	public void onMouseClick(Texture tex, double u, double v, int button)
	{
		//action = new TextureDrawAction(tex);
		Main.instance.project.onAction(new HistoryActionTextureDraw(tex));
		if (button == GLFW_MOUSE_BUTTON_LEFT) draw(tex, u, v, Main.instance.toolHolder.getColor1());
		else if (button == GLFW_MOUSE_BUTTON_RIGHT) draw(tex, u, v, Main.instance.toolHolder.getColor2());
	}

	@Override
	public void onMouseDrag(Texture tex, double prevU, double prevV, double u, double v, int button, boolean isNewObject) //TODO drag = line
	{
		if (isNewObject)
		{
			onMouseRelease(tex, prevU, prevV, button);
			onMouseClick(tex, u, v, button);
		}
		else if (button == GLFW_MOUSE_BUTTON_LEFT) drawLine(tex, prevU, prevV, u, v, Main.instance.toolHolder.getColor1());
		else if (button == GLFW_MOUSE_BUTTON_RIGHT) drawLine(tex, prevU, prevV, u, v, Main.instance.toolHolder.getColor2());
	}

	@Override
	public void onMouseRelease(Texture tex, double u, double v, int button)
	{
		//if (action != null) Main.instance.onAction(action);
		//action = null;
	}
	
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
}