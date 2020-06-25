package firemerald.mcms.gui.popups.model;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.api.model.effects.ItemRenderEffect;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.DropdownButton;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.TransformType;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupItem<T extends RenderBone<T>> extends GuiPopup
{
	public final T parent;
	public final DecoPane pane;
	public final ComponentText name;
	public final DropdownButton nameOptions;
	public final ComponentFloatingLabel slotLabel;
	public final ComponentTextInt slot;
	public final ComponentIncrementInt slotUp, slotDown;
	public final ComponentFloatingLabel scaleLabel;
	public final ComponentTextFloat scale;
	public final ComponentIncrementFloat scaleUp, scaleDown;
	public final ComponentFloatingLabel transformTypeLabel;
	public final SelectorButton transformTypeButton;
	private TransformType transformType = TransformType.NONE;
	public final StandardButton ok, cancel;
	
	public GuiPopupItem(T parent)
	{
		this.parent = parent;
		Project project = Main.instance.project;
		final int cw = 180;
		final int ch = 120;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		final String[] names;
		{
			List<String> possible = new ArrayList<>();
			possible.add("new item");
			project.getDisplayBoneNames(parent, possible);
			names = possible.toArray(new String[possible.size()]);
		}
		this.addElement(name = new ComponentText(cx, y, cx + cw - 20, y + 20, Main.instance.fontMsg, names[0], text -> names[0] = text) {
			@Override
			public boolean shouldUndo()
			{
				return false;
			}
		});
		this.addElement(nameOptions = new DropdownButton(cx + cw - 20, y, cx + cw, y + 20, name, names, (ind, val) -> name.setText(val)));
		y += 20;
		this.addElement(slotLabel = new ComponentFloatingLabel(cx, y, cx + 27, y + 20, Main.instance.fontMsg, "slot"));
		this.addElement(slot = new ComponentTextInt(cx + 27, y, cx + 54, y + 20, Main.instance.fontMsg, 0, 0, Integer.MAX_VALUE, null));
		this.addElement(slotUp = new ComponentIncrementInt(cx + 54, y, slot, 1));
		this.addElement(slotDown = new ComponentIncrementInt(cx + 54, y + 10, slot, -1));
		this.addElement(scaleLabel = new ComponentFloatingLabel(cx + 64, y, cx + 100, y + 20, Main.instance.fontMsg, "scale"));
		this.addElement(scale = new ComponentTextFloat(cx + 100, y, cx + cw - 10, y + 20, Main.instance.fontMsg, 1, 0, Float.POSITIVE_INFINITY, null));
		this.addElement(scaleUp = new ComponentIncrementFloat(cx + cw - 10, y, scale, .0625f));
		this.addElement(scaleDown = new ComponentIncrementFloat(cx + cw - 10, y + 10, scale, -.0625f));
		y += 20;
		this.addElement(transformTypeLabel = new ComponentFloatingLabel(cx, y, cx + cw, y + 20, Main.instance.fontMsg, "transformation type"));
		y += 20;
		this.addElement(transformTypeButton = new SelectorButton(cx, y, cx + cw, y + 20, transformType, TransformType.values(), type -> transformType = type));
		
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 120;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, y, cx + cw - 20, y + 20);
		nameOptions.setSize(cx + cw - 20, y, cx + cw, y + 20);
		y += 20;
		slotLabel.setSize(cx, y, cx + 27, y + 20);
		slot.setSize(cx + 27, y, cx + 54, y + 20);
		slotUp.setPosition(cx + 54, y);
		slotDown.setPosition(cx + 54, y + 10);
		scaleLabel.setSize(cx + 64, y, cx + 100, y + 20);
		scale.setSize(cx + 100, y, cx + cw - 10, y + 20);
		scaleUp.setPosition(cx + cw - 10, y);
		scaleDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		transformTypeLabel.setSize(cx, y, cx + cw, y + 20);
		y += 20;
		transformTypeButton.setSize(cx, y, cx + cw, y + 20);
		ok.setSize(cx, cy + ch - 20, cx + 80, cy + ch);
		cancel.setSize(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch);
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.shader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.shader.setColor(1, 1, 1, 1);
	}
	
	public void apply()
	{
		deactivate();
		final ItemRenderEffect newBone = new ItemRenderEffect(name.getText(), parent, new Transformation(), slot.getVal(), scale.getVal(), transformType);
		Main main = Main.instance;
		main.project.updateSkeletonLocalAlt();
		main.setEditing(newBone);
		Main.instance.editorPanes.selector.updateBase();
		Main.instance.project.onAction(new HistoryAction(() -> {
			parent.removeEffect(newBone);
			main.project.updateSkeletonLocalAlt();
			if (Main.instance.getEditing() == newBone) Main.instance.setEditing(null);
			Main.instance.editorPanes.selector.updateBase();
		}, () -> {
			parent.addEffect(newBone);
			main.project.updateSkeletonLocalAlt();
			main.setEditing(newBone);
			Main.instance.editorPanes.selector.updateBase();
		}));
	}
}