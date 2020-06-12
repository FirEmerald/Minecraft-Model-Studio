package firemerald.mcms.gui.components;

import java.io.File;
import java.util.function.Consumer;

import firemerald.mcms.util.FileUtils;

public class ComponentFileOpen extends StandardButton
{
	public boolean state = false;
	public CharSequence directory;
	
	public ComponentFileOpen(int x1, int y1, int x2, int y2, String text, CharSequence filter, CharSequence directory, Consumer<File> onFile)
	{
		super(x1, y1, x2, y2, text, null);
		this.directory = directory;
		this.onRelease = () -> {
			File file = FileUtils.getOpenFile(filter, directory);
			if (file != null)
			{
				this.directory = file.getParent();
				onFile.accept(file);
			}
		};
	}
}