package firemerald.mcms.gui.components.menu;

import java.awt.Menu;

import firemerald.mcms.gui.components.StandardFloatingButton;
import firemerald.mcms.theme.EnumDirection;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.GuiMesh;

public class MenuExpandButton extends StandardFloatingButton
{
	public ThemeElement rect;
	public final GuiMesh mesh = new GuiMesh();
	public int radius = 0;
	public float hoverTime = 0;
	static final float TARGETHOVER = 0.5f;
	public final Runnable expand;
	
	public MenuExpandButton(int x1, int y1, int x2, int y2, String text, ComponentMenu container, Menu menu, int menuX, int menuY)
	{
		super(x1, y1, x2, y2, text, null);
		setSize(x1, y1, x2, y2);
		this.onRelease = () -> 
		{
			this.hoverTime = TARGETHOVER;
			container.expand(menu, menuX, menuY);
		};
		this.expand = () ->
		{
			if (container.expanded == null || container.expanded.menu != menu) container.expand(menu, menuX, menuY);
		};
	}
	
	public MenuExpandButton(int x1, int y1, int x2, int y2, int outline, int radius, String text, ComponentMenu container, Menu menu, int menuX, int menuY)
	{
		super(x1, y1, x2, y2, outline, text, null);
		this.radius = radius;
		setSize(x1, y1, x2, y2);
		this.onRelease = () -> 
		{
			this.hoverTime = TARGETHOVER;
			container.expand(menu, menuX, menuY);
		};
		this.expand = () ->
		{
			if (container.expanded == null || container.expanded.menu != menu) container.expand(menu, menuX, menuY);
		};
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my, deltaTime);
		if (this.contains(mx, my))
		{
			if (hoverTime < TARGETHOVER && (hoverTime += deltaTime) >= TARGETHOVER) this.expand.run();
		}
		else hoverTime = 0;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		if (mesh != null) mesh.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			int w = x2 - x1;
			int h = y2 - y1;
			float x1 = w - h * .5f, x2 = w - h * .2f;
			float y1 = h * .2f, y2 = h * .8f;
			rect = getTheme().genArrowedButton(w, h, outline, radius, x1, y1, x2, y2, EnumDirection.RIGHT);
		}
	}

	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		rect.bind();
		mesh.render();
		state.removeButtonEffects();
		super.render(state);
	}
}