package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.mesh.Mesh;

public class StandardFloatingButton extends ComponentButton
{
	public Runnable onRelease;
	public boolean enabled = true;
	public boolean textCentered = true;
	protected String text;
	public final Mesh inner = new Mesh();
	public int outline = 1;
	
	public StandardFloatingButton(int x1, int y1, int x2, int y2, String text, Runnable onRelease)
	{
		this(x1, y1, x2, y2, 1, text, onRelease);
	}
	
	public StandardFloatingButton(int x1, int y1, int x2, int y2, int outline, String text, Runnable onRelease)
	{
		super(x1, y1, x2, y2);
		this.outline = outline;
		setText(text);
		this.onRelease = onRelease;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		inner.setMesh(x1 + outline, y1 + outline, x2 - outline, y2 - outline, 0, 0, 0, 1, 1);
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
		onRelease.run();
	}

	@Override
	public void render(ButtonState state)
	{
		RenderUtil.pushStencil();
		RenderUtil.startStencil(false);
		inner.render();
		RenderUtil.endStencil();
		//TODO button effects
		if (textCentered) Main.instance.fontMsg.drawTextLineCentered(text, (x1 + x2) * 0.5f, ((y1 + y2) - Main.instance.fontMsg.height) * 0.5f, state.getColor(getTheme().getTextColor()));
		else Main.instance.fontMsg.drawTextLine(text, x1 + outline + 1, ((y1 + y2) - Main.instance.fontMsg.height) * 0.5f, state.getColor(getTheme().getTextColor()));
		RenderUtil.popStencil();
		Main.instance.shader.setColor(1, 1, 1, 1);
	}
}