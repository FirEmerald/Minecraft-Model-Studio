package firemerald.mcms.gui.popups.animation;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.animation.TweeningFrame;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.DropdownButton;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.main.components.animation.ComponentFramesBar;
import firemerald.mcms.gui.main.components.animation.ComponentKeyFrame;
import firemerald.mcms.gui.main.components.animation.ComponentPoseFrame;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupNewKeyframe extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentLabel name;
	public final DropdownButton nameOptions;
	public final ComponentFloatingLabel labelTime;
	public final ComponentTextFloat time;
	public final ComponentIncrementFloat timeUp, timeDown;
	public final StandardButton ok, cancel;
	public final ComponentFramesBar framesBar;
	
	public GuiPopupNewKeyframe(ComponentFramesBar framesBar)
	{
		this.framesBar = framesBar;
		Main main = Main.instance;
		Project project = main.project;
		Animation anim = (Animation) project.getAnimation();
		List<String> possible = project.getAllBoneNames();
		String[] names = possible.toArray(new String[possible.size()]);
		String defName;
		if (main.getEditingModel() != null)
		{
			defName = main.getEditingModel().getBoneName();
			if (defName == null) defName = names[0];
		}
		else defName = names[0];
		float defFrame;
		if (main.getEditing() instanceof ComponentKeyFrame) defFrame = ((ComponentKeyFrame) main.getEditing()).time;
		else defFrame = 0;
		final int cw = 180;
		final int ch = 80;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentLabel(cx, y, cx + cw - 20, y + 20, Main.instance.fontMsg, defName));
		this.addElement(nameOptions = new DropdownButton(cx + cw - 20, y, cx + cw, y + 20, name, names, (ind, val) -> name.setText(val)));
		y += 20;
		this.addElement(labelTime = new ComponentFloatingLabel(cx, y, cx + 50, y + 20, Main.instance.fontMsg, "time"));
		this.addElement(time = new ComponentTextFloat(cx + 50, y, cx + cw - 10, y + 20, Main.instance.fontMsg, defFrame, 0, anim.getLength(), null));
		this.addElement(timeUp = new ComponentIncrementFloat(cx + cw - 10, y, time, .05f));
		this.addElement(timeDown = new ComponentIncrementFloat(cx + cw - 10, y + 10, time, -.05f));
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 80;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, cy, cx + cw - 20, cy + 20);
		nameOptions.setSize(cx + cw - 20, y, cx + cw, y + 20);
		y += 20;
		labelTime.setSize(cx, y, cx + 50, y + 20);
		time.setSize(cx + 50, y, cx + cw - 10, y + 20);
		timeUp.setPosition(cx + cw - 10, y);
		timeDown.setPosition(cx + cw - 10, y + 10);
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
		Main main = Main.instance;
		Project project = main.project;
		Animation anim = (Animation) project.getAnimation();
		NavigableMap<Float, TweeningFrame> map2 = anim.animation.get(name.getText());
		if (map2 == null) anim.animation.put(name.getText(), map2 = new TreeMap<>());
		final NavigableMap<Float, TweeningFrame> map = map2;
		Float time = this.time.getVal();
		Transformation transform = null;
		if (main.getEditing() instanceof ComponentKeyFrame)
		{
			ComponentKeyFrame keyFrame = (ComponentKeyFrame) main.getEditing();
			if (keyFrame.name.equals(name.getText())) transform = keyFrame.transform.copy();
		}
		if (transform == null)
		{
			Entry<Float, TweeningFrame> lowerEntry = map.lowerEntry(time);
			Entry<Float, TweeningFrame> higherEntry = map.higherEntry(time);
			if (lowerEntry == null)
			{
				Transformation lower;
				if (anim.isRelative()) lower = new Transformation();
				else
				{
					TweeningFrame lowerPair = map.get(time);
					if (lowerPair == null)
					{
						if (project.useBackingSkeleton()) lower = project.getSkeleton().getBone(name.getText()).defaultTransform.copy();
						else
						{
							lower = null;
							IRigged<?, ?> rigged = project.getRig();
							if (rigged != null)
							{
								Bone<?> bone = rigged.getBone(name.getText());
								if (bone != null) lower = bone.defaultTransform.copy();
							}
							if (lower == null) lower = new Transformation();
						}
					}
					else lower = lowerPair.transformation;
				}
				if (higherEntry == null)
				{
					transform = lower.copy();
				}
				else
				{
					transform = Transformation.tween(lower, higherEntry.getValue().transformation, higherEntry.getValue().apply(time / higherEntry.getKey()));
				}
			}
			else if (higherEntry == null) transform = lowerEntry.getValue().transformation.copy();
			else transform = Transformation.tween(lowerEntry.getValue().transformation, higherEntry.getValue().transformation, higherEntry.getValue().apply((time - lowerEntry.getKey()) / (higherEntry.getKey() - lowerEntry.getKey())));
		}
		final Transformation transformation = transform;
		if (!map.containsKey(time)) //TODO if already has a keyframe
		{
			project.onAction(new HistoryAction(() -> {
				if (Main.instance.getEditing() instanceof ComponentPoseFrame)
				{
					ComponentPoseFrame frame =  (ComponentPoseFrame) Main.instance.getEditing();
					if (frame.transform == transformation) Main.instance.setEditing(null);
				}
				map.remove(time);
				Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
			}, () -> {
				map.put(time, new TweeningFrame(transformation));
				Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
			}));
			map.put(time, new TweeningFrame(transformation));
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
			framesBar.keyFrames.get(time).forEach(keyframe -> {
				if (keyframe.transform == transformation) Main.instance.setEditing(keyframe);
			});
			Main.instance.project.getAnimationState().time = time;
		}
	}
}