package firemerald.mcms.gui.popups.animation;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.IAnimation;
import firemerald.mcms.api.animation.Pose;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ComponentToggle;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;

public class GuiPopupEditAnimation extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentText name;
	public final ComponentFloatingLabel labelLength;
	public final ComponentTextFloat length;
	public final ComponentIncrementFloat lengthUp, lengthDown;
	public final ComponentFloatingLabel labelScale;
	public final ComponentToggle scale;
	public final ComponentFloatingLabel labelLoop;
	public final ComponentToggle loop;
	public final ComponentFloatingLabel labelRelative;
	public final ComponentToggle relative;
	public final StandardButton ok, cancel;
	
	public GuiPopupEditAnimation()
	{
		IAnimation anim = Main.instance.project.getAnimation();
		final int cw = 180;
		final int ch = 120;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, Main.instance.project.getAnimationName(), text -> {}));
		y += 20;
		this.addElement(labelLength = new ComponentFloatingLabel(cx, y, cx + 50, y + 20, Main.instance.fontMsg, "length"));
		this.addElement(length = new ComponentTextFloat(cx + 50, y, cx + cw - 10, y + 20, Main.instance.fontMsg, anim.getLength(), 0, Float.MAX_VALUE));
		this.addElement(lengthUp = new ComponentIncrementFloat(cx + cw - 10, y, length, .05f));
		this.addElement(lengthDown = new ComponentIncrementFloat(cx + cw - 10, y + 10, length, -.05f));
		y += 20;
		this.addElement(labelScale = new ComponentFloatingLabel(cx, y, cx + 117, y + 20, Main.instance.fontMsg, "rescale animation"));
		this.addElement(scale = new ComponentToggle(cx + 122, y + 5, cx + 132, y + 15, true, null));
		y += 20;
		this.addElement(labelLoop = new ComponentFloatingLabel(cx, y, cx + 30, y + 20, Main.instance.fontMsg, "loop"));
		this.addElement(loop = new ComponentToggle(cx + 35, y + 5, cx + 45, y + 15, anim instanceof Animation ? ((Animation) anim).loop : true, null));
		this.addElement(labelRelative = new ComponentFloatingLabel(cx + 50, y, cx + 103, y + 20, Main.instance.fontMsg, "relative"));
		this.addElement(relative = new ComponentToggle(cx + 108, y + 5, cx + 118, y + 15, anim.isRelative(), null));
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "ok", this::apply));
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
		name.setSize(cx, cy, cx + cw, cy + 20);
		y += 20;
		labelLength.setSize(cx, y, cx + 50, y + 20);
		length.setSize(cx + 50, y, cx + cw - 10, y + 20);
		lengthUp.setPosition(cx + cw - 10, y);
		lengthDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		labelScale.setSize(cx, y, cx + 117, y + 20);
		scale.setSize(cx + 122, y + 5, cx + 132, y + 15);
		y += 20;
		labelLoop.setSize(cx, y, cx + 30, y + 20);
		loop.setSize(cx + 35, y + 5, cx + 45, y + 15);
		labelRelative.setSize(cx + 50, y, cx + 103, y + 20);
		relative.setSize(cx + 108, y + 5, cx + 118, y + 15);
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
		Project project = Main.instance.project;
		project.onAction();
		project.getAnimation().setRelative(relative.state);
		if (project.getAnimation() instanceof Animation)
		{
			Animation anim = (Animation) project.getAnimation();
			if (length.getVal() == 0) //convert to pose
			{
				Pose pose = new Pose(relative.state);
				anim.animation.forEach((bone, animation) -> {
					Transformation transform = animation.get(0f);
					if (transform != null) pose.pose.put(bone, transform);
				});
				project.addAnimation(project.getAnimationName(), pose);
			}
			else
			{
				anim.loop = loop.state;
				if (length.getVal() != anim.getLength())
				{
					if (scale.state) anim.scaleTo(length.getVal());
					else if (project.useBackingSkeleton()) anim.setLength(length.getVal(), project.getSkeleton().getAllBones()); //from backing skeleton
					else if (project.getRig() != null) anim.setLength(length.getVal(), project.getRig().getAllBones()); //from current rig
					else anim.setLength(length.getVal(), Collections.emptyList()); //from nothing
				}
			}
		}
		else if (project.getAnimation() instanceof Pose)
		{
			Pose pose = (Pose) project.getAnimation();
			if (length.getVal() == 0) pose.setRelative(relative.state);
			else //convert to animation
			{
				Animation anim = new Animation(length.getVal(), loop.state, relative.state);
				pose.pose.forEach((bone, transform) -> {
					NavigableMap<Float, Transformation> map = anim.animation.get(bone);
					if (map == null) anim.animation.put(bone, map = new TreeMap<>());
					map.put(0f, transform);
				});
				project.addAnimation(project.getAnimationName(), anim);
			}
		}
		if (!name.getText().equals(project.getAnimationName())) project.setAnimationName(MiscUtil.ensureUnique(this.name.getText(), project.getAnimationNames()));
		Main.instance.gui.onGuiUpdate(GuiUpdate.ANIMATION);
	}
}