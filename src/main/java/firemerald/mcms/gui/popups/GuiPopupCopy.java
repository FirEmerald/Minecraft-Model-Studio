package firemerald.mcms.gui.popups;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import firemerald.mcms.Main;
import firemerald.mcms.api.util.IClonableObject;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupCopy<T> extends GuiPopup
{	
	public final DecoPane pane;
	public final ComponentText name;
	public final StandardButton ok, cancel;
	public final IClonableObject<T> object;
	public final BiConsumer<String, T> add;
	public final Consumer<String> remove;
	
	public GuiPopupCopy(String curName, IClonableObject<T> object, BiConsumer<String, T> add, Consumer<String> remove)
	{
		final int cw = 180;
		final int ch = 60;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, curName, null));
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
		this.object = object;
		this.add = add;
		this.remove = remove;
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 100;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, y, cx + cw, y + 20);
		y += 20;
		ok.setSize(cx, cy + ch - 20, cx + 80, cy + ch);
		cancel.setSize(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch);
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.guiShader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.guiShader.setColor(1, 1, 1, 1);
	}
	
	public void apply()
	{
		deactivate();
		final String name = this.name.getText();
		final T object = this.object.cloneObject();
		Main.instance.project.onAction(new HistoryAction(() -> remove.accept(name), () -> add.accept(name, object)));
		add.accept(name, object);
	}
}