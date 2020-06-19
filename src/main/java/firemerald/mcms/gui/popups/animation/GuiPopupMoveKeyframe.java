package firemerald.mcms.gui.popups.animation;

import java.util.NavigableMap;
import java.util.TreeMap;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.main.components.animation.ComponentFramesBar;
import firemerald.mcms.gui.main.components.animation.ComponentKeyFrame;
import firemerald.mcms.util.GuiUpdate;

public class GuiPopupMoveKeyframe extends GuiPopup
{
	public final DecoPane pane;
	public final ComponentFloatingLabel labelTime;
	public final ComponentTextFloat time;
	public final ComponentIncrementFloat timeUp, timeDown;
	public final StandardButton ok, cancel;
	public final ComponentFramesBar framesBar;
	public final ComponentKeyFrame keyFrame;
	
	public GuiPopupMoveKeyframe(ComponentKeyFrame keyFrame, ComponentFramesBar framesBar)
	{
		this.keyFrame = keyFrame;
		this.framesBar = framesBar;
		Animation anim = (Animation) Main.instance.project.getAnimation();
		final int cw = 180;
		final int ch = 60;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(labelTime = new ComponentFloatingLabel(cx, y, cx + 50, y + 20, Main.instance.fontMsg, "time"));
		this.addElement(time = new ComponentTextFloat(cx + 50, y, cx + cw - 10, y + 20, Main.instance.fontMsg, keyFrame.time, 0, anim.getLength()));
		this.addElement(timeUp = new ComponentIncrementFloat(cx + cw - 10, y, time, .05f));
		this.addElement(timeDown = new ComponentIncrementFloat(cx + cw - 10, y + 10, time, -.05f));
		y += 20;
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "move", this::apply));
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
		main.shader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.shader.setColor(1, 1, 1, 1);
	}
	
	public void apply()
	{
		deactivate();
		Project project = Main.instance.project;
		project.onAction();
		Animation anim = (Animation) project.getAnimation();
		NavigableMap<Float, Transformation> map = anim.animation.get(keyFrame.name);
		if (map == null) anim.animation.put(keyFrame.name, map = new TreeMap<>());
		else map.remove(keyFrame.time);
		Float time = this.time.getVal();
		if (!map.containsKey(time)) //TODO if already has a keyframe
		{
			map.put(time, keyFrame.transform);
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
			framesBar.keyFrames.get(time).forEach(keyframe -> {
				if (keyframe.transform == keyFrame.transform) Main.instance.setEditing(keyframe);
			});
		}
	}
}