package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.theme.RoundedBoxFormat;
import firemerald.mcms.util.Action;
import firemerald.mcms.util.RenderUtil;

public class StandardButton extends ComponentButton
{
	public Action onRelease;
	public boolean enabled = true;
	private String text;
	public RoundedBoxFormat rect;
	public final Mesh mesh = new Mesh(), inner = new Mesh();
	public int outline = 1, radius = 0;
	
	public StandardButton(float x1, float y1, float x2, float y2, String text, Action onRelease)
	{
		this(x1, y1, x2, y2, 1, 0, text, onRelease);
	}
	
	public StandardButton(float x1, float y1, float x2, float y2, int outline, int radius, String text, Action onRelease)
	{
		super(x1, y1, x2, y2);
		this.outline = outline;
		this.radius = radius;
		setText(text);
		this.onRelease = onRelease;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		final float border = 1;
		rect = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1), outline, radius);
		mesh.setMesh(0, 0, x2 - x1, y2 - y1, 0, 0, 0, 1, 1);
		inner.setMesh(border, border, x2 - x1 - border, y2 - y1 - border, 0, 0, 0, 1, 1);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void onRelease()
	{
		onRelease.apply();
	}

	@Override
	public void render(ButtonState state)
	{
		getTheme().bindRoundedBox(rect);
		state.applyButtonEffects();
		mesh.render();
		state.removeButtonEffects();
		RenderUtil.pushStencil();
		RenderUtil.startStencil(false);
		inner.render();
		RenderUtil.endStencil();
		getTheme().bindRoundedBox(rect);
		state.applyButtonEffects();
		//mesh.render();
		state.removeButtonEffects();
		Main.instance.fontMsg.drawTextLineCentered(text, (x2 - x1) * 0.5f, ((y2 - y1) - Main.instance.fontMsg.height) * 0.5f, state.getColor(getTheme().getTextColor()));
		RenderUtil.popStencil();
		Main.instance.shader.setColor(1, 1, 1, 1);
	}
}