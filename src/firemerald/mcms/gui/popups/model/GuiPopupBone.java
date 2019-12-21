package firemerald.mcms.gui.popups.model;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.DropdownButton;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.decoration.DecoPane;

public class GuiPopupBone extends GuiPopup
{
	public final Bone parent;
	public final DecoPane pane;
	public final ComponentText name;
	public final DropdownButton nameOptions;
	public final StandardButton ok, cancel;
	
	public GuiPopupBone(Bone parent)
	{
		this.parent = parent;
		Project project = Main.instance.project;
		this.addElement(pane = new DecoPane(0, 0, 320, 160, 2, 16));
		String[] names;
		{
			List<String> possible = new ArrayList<>();
			possible.add("new bone");
			project.getDisplayBoneNames(parent, possible);
			names = possible.toArray(new String[possible.size()]);
		}
		this.addElement(name = new ComponentText(0, 10, 220, 30, Main.instance.fontMsg, names[0], text -> names[0] = text));
		this.addElement(nameOptions = new DropdownButton(220, 10, 240, 30, 0, 10, 220, 30, names, (ind, val) -> name.setText(val)));
		this.addElement(ok = new StandardButton(0, 0, 80, 20, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(0, 0, 80, 20, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		pane.setSize((w / 2) - 110, (h / 2) - 50, (w / 2) + 110, (h / 2) + 50);
		name.setSize((w / 2) - 90, (h / 2) - 30, (w / 2) + 70, (h / 2) - 10);
		nameOptions.setSize((w / 2) + 70, (h / 2) - 30, (w / 2) + 90, (h / 2) - 10, (w / 2) - 90, (h / 2) - 30, (w / 2) + 70, (h / 2) - 10);
		ok.setSize((w / 2) - 90, (h / 2) + 10, (w / 2) - 10, (h / 2) + 30);
		cancel.setSize((w / 2) + 10, (h / 2) + 10, (w / 2) + 90, (h / 2) + 30);
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
		IRigged<?> rigged = Main.instance.project.getRig();
		Bone newBone = rigged.makeNew(name.getText(), new Transformation(), parent);
		if (parent != null) rigged.updateBonesList();
		else rigged.addChild(newBone);
		Main main = Main.instance;
		main.project.updateSkeletonLocalAlt();
		main.setEditing(newBone);
		Main.instance.editorPanes.selector.updateBase();
		this.deactivate();
	}
}