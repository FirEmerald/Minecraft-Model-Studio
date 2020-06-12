package firemerald.mcms.theme;

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
		main.textureManager.missingTex.bind();;
	}

	@Override
	public void releaseElement() {}
}
