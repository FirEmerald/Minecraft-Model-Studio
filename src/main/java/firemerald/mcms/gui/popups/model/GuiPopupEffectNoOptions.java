package firemerald.mcms.gui.popups.model;

import java.util.function.BiFunction;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.api.model.effects.BoneEffect;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupEffectNoOptions<T extends RenderBone<?>> extends GuiPopup
{
	public final T parent;
	public final BiFunction<String, T, ? extends BoneEffect> constructor;
	public final DecoPane pane;
	public final ComponentText name;
	public final StandardButton ok, cancel;
	
	public GuiPopupEffectNoOptions(T parent, BiFunction<String, T, ? extends BoneEffect> constructor, String initialName)
	{
		this.parent = parent;
		this.constructor = constructor;
		final int cw = 180;
		final int ch = 60;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		this.addElement(name = new ComponentText(cx, cy, cx + cw, cy + 20, Main.instance.fontMsg, initialName, text -> {}) {
			@Override
			public boolean shouldUndo()
			{
				return false;
			}
		});
		
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 60;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		name.setSize(cx, cy, cx + cw, cy + 20);
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
		final BoneEffect newEffect = constructor.apply(name.getText(), parent);
		Main main = Main.instance;
		main.project.updateSkeletonLocalAlt();
		main.setEditing(newEffect);
		Main.instance.editorPanes.selector.updateBase();
		Main.instance.project.onAction(new HistoryAction(() -> {
			parent.removeEffect(newEffect);
			main.project.updateSkeletonLocalAlt();
			if (Main.instance.getEditing() == newEffect) Main.instance.setEditing(null);
			Main.instance.editorPanes.selector.updateBase();
		}, () -> {
			parent.addEffect(newEffect);
			main.project.updateSkeletonLocalAlt();
			main.setEditing(newEffect);
			Main.instance.editorPanes.selector.updateBase();
		}));
	}
}