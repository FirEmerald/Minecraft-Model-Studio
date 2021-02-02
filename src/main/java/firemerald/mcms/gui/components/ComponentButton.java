package firemerald.mcms.gui.components;

import static firemerald.mcms.gui.components.ComponentButton.ButtonState.*;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.window.api.MouseButtons;

public abstract class ComponentButton extends Component
{	
	public float repeatTime;
	public boolean held;

	public ComponentButton(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
	}

	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		if (held)
		{
			if (!isEnabled()) held = false;
			else if (contains(mx, my))
			{
				repeatTime += deltaTime;
				while (repeatTime > .5f)
				{
					if (isEnabled()) onRepeat();
					else
					{
						held = false;
						break;
					}
					repeatTime -= .05f;
				}
			}
			else repeatTime = .5f;
		}
	}
	
	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT && isEnabled())
		{
			held = true;
			repeatTime = 0;
			onPress();
		}
	}
	
	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT && held)
		{
			held = false;
			if (isEnabled() && contains(mx, my)) onRelease();
		}
	}
	
	public abstract boolean isEnabled();
	
	public void onPress() {}
	
	public void onRepeat() {}
	
	public void onRelease() {}
	
	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		this.held = false;
	}
	
	public ButtonState getState(float mx, float my, boolean canHover)
	{
		return isEnabled() ? canHover && contains(mx, my) ? Main.instance.window.isMouseDown(MouseButtons.LEFT) ? held ? PUSH : getIdleState() : HOVER : getIdleState() : DISABLED;
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		render(getState(mx, my, canHover));
		Main.instance.guiShader.setTexOffset(0, 0);
	}
	
	public abstract void render(ButtonState state);
	
	public ButtonState getIdleState()
	{
		return ButtonState.NONE;
	}
	
	public static enum ButtonState
	{
		//TODO theme colors
		NONE(Color.WHITE, false),
		INVERT(Color.WHITE, true),
		HOVER(new Color(.75f, .75f, 1, 1), false),
		PUSH(new Color(.75f, .75f, .75f, 1), true),
		DISABLED(new Color(.5f, .5f, .5f, 1), false);

		public final Color color;
		public final boolean invert;
		
		ButtonState(Color color, boolean invert)
		{
			this.color = color;
			this.invert = invert;
		}
		
		public void applyButtonEffects()
		{
			Main.instance.guiShader.setColor2(color);
			Main.instance.guiShader.setInvert(invert);
		}
		
		public void removeButtonEffects()
		{
			Main.instance.guiShader.setColor2(1, 1, 1, 1);
			Main.instance.guiShader.setInvert(false);
		}
		
		public Color getColor(Color c)
		{
			RGB rgb = c.c.getRGB();
			RGB col = color.c.getRGB();
			return new Color((invert ? 1 - rgb.r : rgb.r) * col.r, (invert ? 1 - rgb.g : rgb.g) * col.g, (invert ? 1 - rgb.b : rgb.b) * col.b, c.a * color.a);
		}
	}
}