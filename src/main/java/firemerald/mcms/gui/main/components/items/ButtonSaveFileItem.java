package firemerald.mcms.gui.main.components.items;

import java.io.File;
import java.util.function.Consumer;

import firemerald.mcms.gui.components.ItemButton16;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.ResourceLocation;

public class ButtonSaveFileItem extends ItemButton16
{
	protected final ResourceLocation texture;
	public String filter, defExt;
	protected String directory = null;
	protected final Consumer<File> action;
	
	public ButtonSaveFileItem(int x, int y, ResourceLocation texture, String filter, String defExt, Consumer<File> action)
	{
		super(x, y);
		this.texture = texture;
		this.filter = filter;
		this.defExt = defExt;
		this.action = action;
	}
	
	@Override
	public void onRelease()
	{
		File file = FileUtils.getSaveFile(directory, filter, defExt);
		if (file != null)
		{
			directory = file.toString();
			action.accept(file);
		}
	}

	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}
}