package firemerald.mcms.theme;

import static org.lwjgl.opengl.GL11.*;

import firemerald.mcms.Main;

public abstract class CommonGuiTheme extends GuiTheme
{
	public final ThemeElementNone NONE = new ThemeElementNone(this, Main.instance);
	
	public CommonGuiTheme(String name, String origin)
	{
		super(name, origin);
	}
	
	@Override
	public void cleanUp() {}
	
	@Override
	public void finalize()
	{
		if (Main.glActive) this.cleanUp();
	}
	
	@Override
	public ThemeElement genRoundedBox(int w, int h, int outline, int radius)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genRoundedBox(w, h, outline, radius, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genRoundedBox(int w, int h, int outline, int radius, int tex);
	
	@Override
	public ThemeElement genTextBox(int w, int h, int outline)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genTextBox(w, h, outline, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genTextBox(int w, int h, int outline, int tex);
	
	@Override
	public ThemeElement genScrollBar(int w, int h, int outline)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genScrollBar(w, h, outline, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genScrollBar(int w, int h, int outline, int tex);
	
	@Override
	public ThemeElement genScrollButton(int w, int h, int outline, EnumDirection direction)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genScrollButton(w, h, outline, direction, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genScrollButton(int w, int h, int outline, EnumDirection direction, int tex);
	
	@Override
	public ThemeElement genDirectionButton(int w, int h, int outline, int radius, EnumDirection direction)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genDirectionButton(w, h, outline, radius, direction, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genDirectionButton(int w, int h, int outline, int radius, EnumDirection direction, int tex);
	
	@Override
	public ThemeElement genArrowedButton(int w, int h, int outline, int radius, float x1, float y1, float x2, float y2, EnumDirection direction)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genArrowedButton(w, h, outline, radius, x1, y1, x2, y2, direction, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genArrowedButton(int w, int h, int outline, int radius, float x1, float y1, float x2, float y2, EnumDirection direction, int tex);
	
	@Override
	public ThemeElement genArrow(int h, int outline, EnumDirection direction)
	{
		if (h <= 0) return NONE;
		int tex;
		genArrow(h, outline, direction, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genArrow(int h, int outline, EnumDirection direction, int tex);
	
	@Override
	public ThemeElement genMenuSeperator(int w, int h, int thickness, int offset)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genMenuSeperator(w, h, thickness, offset, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genMenuSeperator(int w, int h, int thickness, int offset, int tex);

	@Override
	public ThemeElement genTab(int w, int h, int outline, int radius, EnumDirection direction, boolean connectLeft, boolean connectRight)
	{
		if (w <= 0 || h <= 0) return NONE;
		int tex;
		genTab(w, h, outline, radius, direction, connectLeft, connectRight, tex = glGenTextures());
		return new ThemeElementTexture(this, tex);
	}
	
	public abstract void genTab(int w, int h, int outline, int radius, EnumDirection direction, boolean connectLeft, boolean connectRight, int tex);
}