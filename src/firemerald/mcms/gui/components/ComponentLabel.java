package firemerald.mcms.gui.components;

import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.EnumTextAlignment;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.font.FontRenderer;
import firemerald.mcms.util.mesh.Mesh;

public class ComponentLabel extends ComponentFloatingLabel
{
	protected ThemeElement rectangle;
	protected final Mesh mesh;
	
	public ComponentLabel(int x1, int y1, int x2, int y2, FontRenderer font, String text, EnumTextAlignment alignment)
	{
		super(x1, y1, x2, y2, font, text, alignment);
		mesh = new Mesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	public ComponentLabel(int x1, int y1, int x2, int y2, FontRenderer font, String text)
	{
		super(x1, y1, x2, y2, font, text);
		mesh = new Mesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	public ComponentLabel(int x1, int y1, int x2, int y2, FontRenderer font)
	{
		super(x1, y1, x2, y2, font);
		mesh = new Mesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rectangle != null)
			{
				rectangle.release();
				rectangle = null;
			}
			rectangle = getTheme().genTextBox(x2 - x1, y2 - y1, 1);
		}
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		rectangle.bind();
		mesh.render();
		super.render(mx, my, canHover);
	}
}