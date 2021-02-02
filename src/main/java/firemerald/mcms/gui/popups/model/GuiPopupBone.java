package firemerald.mcms.gui.popups.model;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.DropdownButton;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.IntReference;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupBone<T extends Bone<T>> extends GuiPopup
{
	public final T parent;
	public final DecoPane pane;
	public final ComponentText name;
	public final DropdownButton nameOptions;
	public final StandardButton ok, cancel;
	
	public GuiPopupBone(T parent)
	{
		this.parent = parent;
		Project project = Main.instance.project;
		final int cw = 180;
		final int ch = 60;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		String[] names;
		{
			List<String> possible = new ArrayList<>();
			possible.add("new bone");
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
		this.addElement(nameOptions = new DropdownButton(cx + cw - 20, y, cx + cy, y + 20, name, names, (ind, val) -> name.setText(val)));
		
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
		int y = cy;
		name.setSize(cx, y, cx + cw - 20, y + 20);
		nameOptions.setSize(cx + cw - 20, y, cx + cw, y + 20);
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
		final T parent = this.parent;
		@SuppressWarnings("unchecked")
		final IRigged<?, T> rigged = (IRigged<?, T>) Main.instance.project.getRig();
		final T newBone = rigged.makeNew(name.getText(), new Transformation(), parent);
		final IntReference index = new IntReference();
		Main.instance.project.onAction(new HistoryAction(() -> {
			IEditableParent p = (parent == null ? rigged : parent);
			index.val = p.getChildIndex(newBone);
			p.removeChild(newBone);
			Main main = Main.instance;
			main.project.updateSkeletonLocalAlt();
			//main.setEditing(newBone);
			Main.instance.editorPanes.selector.updateBase();
		}, () -> {
			if (parent != null)
			{
				parent.addChildAt(newBone, index.val);
				rigged.updateBonesList();
			}
			else rigged.addChild(newBone);
			Main main = Main.instance;
			main.project.updateSkeletonLocalAlt();
			//main.setEditing(newBone);
			Main.instance.editorPanes.selector.updateBase();
		}));
		if (parent != null) rigged.updateBonesList();
		else rigged.addChild(newBone);
		Main main = Main.instance;
		main.project.updateSkeletonLocalAlt();
		main.setEditing(newBone);
		Main.instance.editorPanes.selector.updateBase();
	}
}