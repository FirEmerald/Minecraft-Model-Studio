package firemerald.mcms.theme;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import firemerald.mcms.Main;

public abstract class CommonGuiTheme extends GuiTheme
{
	protected final Map<RoundedBoxFormat, Integer> boxes = new HashMap<>();
	protected final Map<BoxFormat, Integer> textBoxes = new HashMap<>();
	protected final Map<BoxFormat, Integer> scrollBars = new HashMap<>();
	protected final Map<DirectionButtonFormat, Integer> scrollButtons = new HashMap<>();
	protected final Map<DirectionButtonFormat, Integer> directionButtons = new HashMap<>();

	public CommonGuiTheme(String name, String origin)
	{
		super(name, origin);
	}
	
	@Override
	public void cleanUp()
	{
		for (Integer tex : boxes.values()) Main.CLEANUP_ACTIONS.add(() -> GL11.glDeleteTextures(tex));
		for (Integer tex : textBoxes.values()) Main.CLEANUP_ACTIONS.add(() -> GL11.glDeleteTextures(tex));
		for (Integer tex : scrollBars.values()) Main.CLEANUP_ACTIONS.add(() -> GL11.glDeleteTextures(tex));
		for (Integer tex : scrollButtons.values()) Main.CLEANUP_ACTIONS.add(() -> GL11.glDeleteTextures(tex));
		for (Integer tex : directionButtons.values()) Main.CLEANUP_ACTIONS.add(() -> GL11.glDeleteTextures(tex));
		boxes.clear();
		textBoxes.clear();
		scrollBars.clear();
		scrollButtons.clear();
		directionButtons.clear();
	}
	
	@Override
	public void finalize()
	{
		if (Main.glActive) this.cleanUp();
	}

	@Override
	public void bindRoundedBox(RoundedBoxFormat box)
	{
		Integer tex = boxes.get(box);
		if (tex == null) generateRoundedBox(box);
		else GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
	}
	
	public abstract void generateRoundedBox(RoundedBoxFormat box);

	@Override
	public void bindTextBox(BoxFormat textBox)
	{
		Integer tex = textBoxes.get(textBox);
		if (tex == null) generateTextBox(textBox);
		else GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
	}
	
	public abstract void generateTextBox(BoxFormat textBox);

	@Override
	public void bindScrollBar(BoxFormat scrollBar)
	{
		Integer tex = scrollBars.get(scrollBar);
		if (tex == null) generateScrollBar(scrollBar);
		else GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
	}
	
	public abstract void generateScrollBar(BoxFormat scrollBar);

	@Override
	public void bindScrollButton(DirectionButtonFormat scrollButton)
	{
		Integer tex = scrollButtons.get(scrollButton);
		if (tex == null) generateScrollButton(scrollButton);
		else GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
	}
	
	public abstract void generateScrollButton(DirectionButtonFormat scrollButton);

	@Override
	public void bindDirectionButton(DirectionButtonFormat directionButton)
	{
		Integer tex = directionButtons.get(directionButton);
		if (tex == null) generateDirectionButton(directionButton);
		else GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
	}
	
	public abstract void generateDirectionButton(DirectionButtonFormat directionButton);
}