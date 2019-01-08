package firemerald.mcms.gui.components.text;

import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.theme.DirectionButtonFormat;

public abstract class ComponentIncrement extends ComponentButton
{
	public static final int ARROW_W = 10;
	public static final int ARROW_H = 10;
	public static final Mesh ARROW = new Mesh(0, 0, ARROW_W, ARROW_H, 0, 0, 0, 1, 1);
	public static final DirectionButtonFormat UP = new DirectionButtonFormat(ARROW_W, ARROW_H, 1, 0);
	public static final DirectionButtonFormat DOWN = new DirectionButtonFormat(ARROW_W, ARROW_H, 1, 2);
	public static final DirectionButtonFormat RIGHT = new DirectionButtonFormat(ARROW_W, ARROW_H, 1, 1);
	public static final DirectionButtonFormat LEFT = new DirectionButtonFormat(ARROW_W, ARROW_H, 1, 3);
	
	public DirectionButtonFormat id;

	public ComponentIncrement(float x, float y, boolean isNegative)
	{
		super(x, y, x + ARROW_W, y + ARROW_H);
		id = isNegative ? DOWN : UP;
	}
	
	public void setPosition(float x, float y)
	{
		setSize(x, y, x + ARROW_W, y + ARROW_H);
	}
	
	public abstract void increment();
	
	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		this.held = false;
	}

	@Override
	public void onPress()
	{
		increment();
	}

	@Override
	public void onRepeat()
	{
		increment();
	}
	
	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		getTheme().bindDirectionButton(id);
		ARROW.render();
		state.removeButtonEffects();
	}
}