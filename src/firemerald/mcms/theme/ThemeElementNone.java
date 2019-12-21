package firemerald.mcms.theme;

import static org.lwjgl.opengl.GL11.*;

import firemerald.mcms.Main;

public class ThemeElementNone extends ThemeElement
{
	final Main main;
	
	ThemeElementNone(GuiTheme theme, Main main)
	{
		super(theme);
		this.main = main;
	}

	@Override
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, main.textureManager.missingTex);
	}

	@Override
	public void releaseElement() {}
}
