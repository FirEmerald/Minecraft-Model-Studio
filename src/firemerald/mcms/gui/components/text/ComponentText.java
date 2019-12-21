package firemerald.mcms.gui.components.text;

import java.util.function.Consumer;

import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.font.FontRenderer;

public class ComponentText extends ComponentFloatingText
{
	protected ThemeElement rectangle;
	
	public ComponentText(int x1, int y1, int x2, int y2, FontRenderer font, String text, Consumer<String> onTextChange)
	{
		super(x1, y1, x2, y2, font, text, onTextChange);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	public ComponentText(int x1, int y1, int x2, int y2, FontRenderer font, Consumer<String> onTextChange)
	{
		super(x1, y1, x2, y2, font, onTextChange);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rectangle != null) rectangle.release();
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