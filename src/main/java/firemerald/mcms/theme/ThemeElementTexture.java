package firemerald.mcms.theme;

import static org.lwjgl.opengl.GL11.*;

import firemerald.mcms.Main;

public class ThemeElementTexture extends ThemeElement
{
	public final int tex;
	
	ThemeElementTexture(GuiTheme theme, int tex)
	{
		super(theme);
		this.tex = tex;
	}

	@Override
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, tex);
		//Main.listGLErrors("test: " + Thread.currentThread().getStackTrace()[2] + " \n" + Thread.currentThread().getStackTrace()[3] + " \n" + Thread.currentThread().getStackTrace()[4] + " \n" + Thread.currentThread().getStackTrace()[5]);
	}

	@Override
	public void releaseElement()
	{
		Main.CLEANUP_ACTIONS.add(() -> glDeleteTextures(tex));
	}
}
