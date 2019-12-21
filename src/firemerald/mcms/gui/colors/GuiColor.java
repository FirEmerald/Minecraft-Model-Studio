package firemerald.mcms.gui.colors;

import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.window.api.Key;

public class GuiColor extends GuiPopup
{
	public final ComponentColorPicker picker;
	
	public GuiColor(ColorModel color, Consumer<ColorModel> onColor)
	{
		this.addElement(picker = new ComponentColorPicker(0, 0, color, onColor));
		onGuiUpdate(GuiUpdate.THEME);
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		picker.setCenter(w / 2, h / 2);
	}
	
	@Override
	public void onKeyPressed(Key key, int scancode, int mods)
	{
		if (key == Key.ESCAPE)
		{
			Main.instance.state.addToColorHistory(picker.color);
			this.deactivate();
		}
		else super.onKeyPressed(key, scancode, mods);
	}
}