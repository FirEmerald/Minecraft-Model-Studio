package firemerald.mcms.gui.popups.animation;

import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Pose;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.DropdownButton;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.main.components.animation.ComponentFramesBar;
import firemerald.mcms.gui.main.components.animation.ComponentPoseFrame;
import firemerald.mcms.util.GuiUpdate;

public class GuiPopupNewPoseframe extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentLabel name;
	public final DropdownButton nameOptions;
	public final StandardButton ok, cancel;
	public final ComponentFramesBar framesBar;
	
	public GuiPopupNewPoseframe(ComponentFramesBar framesBar)
	{
		this.framesBar = framesBar;
		Main main = Main.instance;
		Project project = main.project;
		List<String> possible = project.getAllBoneNames();
		String[] names = possible.toArray(new String[possible.size()]);
		String defName;
		if (main.getEditingModel() != null) defName = main.getEditingModel().getName();
		else defName = names[0];
		final int cw = 180;
		final int ch = 60;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentLabel(cx, y, cx + cw - 20, y + 20, Main.instance.fontMsg, defName));
		this.addElement(nameOptions = new DropdownButton(cx + cw - 20, y, cx + cw, y + 20, name, names, (ind, val) -> name.setText(val)));
		y += 20;
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
		name.setSize(cx, cy, cx + cw - 20, cy + 20);
		nameOptions.setSize(cx + cw - 20, y, cx + cw, y + 20);
		y += 20;
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
		Main main = Main.instance;
		Project project = main.project;
		project.onAction();
		Pose anim = (Pose) project.getAnimation();
		Transformation transform = null;
		if (main.getEditing() instanceof ComponentPoseFrame)
		{
			ComponentPoseFrame keyFrame = (ComponentPoseFrame) main.getEditing();
			if (keyFrame.name.equals(name.getText())) transform = keyFrame.transform.copy();
		}
		if (transform == null)
		{
			if (anim.isRelative()) transform = new Transformation();
			else
			{
				if (project.useBackingSkeleton()) transform = project.getSkeleton().getBone(name.getText()).defaultTransform.copy();
				else
				{
					IRigged<?> rigged = project.getRig();
					if (rigged != null) transform = rigged.getBone(name.getText()).defaultTransform.copy();
					if (transform == null) transform = new Transformation();
				}
			}
		}
		final Transformation transformation = transform;
		if (!anim.pose.containsKey(name.getText())) //TODO if already has a keyframe
		{
			anim.pose.put(name.getText(), transformation);
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
			framesBar.keyFrames.get(0f).forEach(keyframe -> {
				if (keyframe.transform == transformation) Main.instance.setEditing(keyframe);
			});
		}
	}
}