package firemerald.mcms.gui.main.components;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.scrolling.ScrollBar;
import firemerald.mcms.gui.components.scrolling.ScrollBarH;
import firemerald.mcms.gui.components.scrolling.ScrollDown;
import firemerald.mcms.gui.components.scrolling.ScrollLeft;
import firemerald.mcms.gui.components.scrolling.ScrollRight;
import firemerald.mcms.gui.components.scrolling.ScrollUp;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.main.components.animation.ButtonAddKeyframe;
import firemerald.mcms.gui.main.components.animation.ButtonMoveKeyframe;
import firemerald.mcms.gui.main.components.animation.ButtonRemoveKeyframe;
import firemerald.mcms.gui.main.components.animation.ComponentFramesBar;
import firemerald.mcms.gui.main.components.animation.PlaybackEndButton;
import firemerald.mcms.gui.main.components.animation.PlaybackForwardButton;
import firemerald.mcms.gui.main.components.animation.PlaybackLockButton;
import firemerald.mcms.gui.main.components.animation.PlaybackPauseButton;
import firemerald.mcms.gui.main.components.animation.PlaybackReverseButton;
import firemerald.mcms.gui.main.components.animation.PlaybackStartButton;
import firemerald.mcms.gui.main.components.items.ButtonAction;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.hotkey.Action;

public class ComponentAnimationBar extends ComponentPanelMain
{
	public final ButtonAction newAnimation;
	public final ButtonAction addAnimation;
	public final ButtonAction loadAnimation;
	public final ButtonAction cloneAnimation;
	public final ButtonAction saveAnimation;
	public final ButtonAction editAnimation;
	public final ButtonAction reverseAnimation;
	public final ButtonAction removeAnimation;
	public final SelectorButton animationSelector;
	public final ComponentFramesBar framesBar;
	public final ScrollBar scrollBar;
	public final ScrollUp scrollUp;
	public final ScrollDown scrollDown;
	public final ScrollBarH scrollBarH;
	public final ScrollLeft scrollLeft;
	public final ScrollRight scrollRight;
	public final ButtonAddKeyframe addKeyFrame;
	public final ButtonMoveKeyframe moveKeyFrame;
	public final ButtonRemoveKeyframe removeKeyFrame;
	
	public ComponentAnimationBar(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2, gui);
		int w = x2 - x1, h = y2 - y1;
		this.addElement(newAnimation = new ButtonAction(0, 16, Textures.ITEM_NEW, Action.NEW_ANIMATION));
		this.addElement(addAnimation = new ButtonAction(16, 16, Textures.ITEM_ADD, Action.ADD_ANIMATION));
		this.addElement(loadAnimation = new ButtonAction(32, 16, Textures.ITEM_LOAD, Action.LOAD_ANIMATION));
		this.addElement(cloneAnimation = new ButtonAction(48, 16, Textures.ITEM_COPY, Action.CLONE_ANIMATION));
		this.addElement(saveAnimation = new ButtonAction(64, 16, Textures.ITEM_SAVE, Action.SAVE_ANIMATION));
		this.addElement(editAnimation = new ButtonAction(80, 16, Textures.ITEM_EDIT, Action.EDIT_ANIMATION));
		this.addElement(reverseAnimation = new ButtonAction(96, 16, Textures.ITEM_REVERSE, Action.REVERSE_ANIMATION));
		this.addElement(removeAnimation = new ButtonAction(112, 16, Textures.ITEM_REMOVE, Action.REMOVE_ANIMATION));
		newAnimation.enabled = addAnimation.enabled = true;
		this.addElement(animationSelector = new SelectorButton(0, 0, 160, 16, Main.instance.project.getAnimationNames().isEmpty() ? "no animations available" : Main.instance.project.getAnimationName() == null ? "no animation" : Main.instance.project.getAnimationName(), Main.instance.project.getAnimationNames().isEmpty() ? new String[0] : MiscUtil.array("no animation", Main.instance.project.getAnimationNames()), (ind, value) -> {
			//TODO undo?
			if (ind == 0)
			{
				Main.instance.project.setAnimation(null);
				return "no animation selected";
			}
			else
			{
				Main.instance.project.setAnimation(value);
				return value;
			}
		}));
		this.addElement(framesBar = new ComponentFramesBar(192, 0, w - 16, h - 16));
		this.addElement(scrollBar = new ScrollBar(w - 16, 16, w, h - 16, framesBar));
		this.addElement(scrollUp = new ScrollUp(w - 16, 0, w, 16, framesBar));
		this.addElement(scrollDown = new ScrollDown(w - 16, h - 16, w, h, framesBar));
		framesBar.setScrollBar(scrollBar);
		this.addElement(scrollBarH = new ScrollBarH(208, h - 16, w - 16, h, framesBar));
		this.addElement(scrollLeft = new ScrollLeft(192, h - 16, 208, h, framesBar));
		this.addElement(scrollRight = new ScrollRight(w - 16, h - 16, w, h, framesBar));
		framesBar.setScrollBarH(scrollBarH);
		this.addElement(addKeyFrame = new ButtonAddKeyframe(32, 64, framesBar));
		this.addElement(moveKeyFrame = new ButtonMoveKeyframe(64, 64, framesBar));
		this.addElement(removeKeyFrame = new ButtonRemoveKeyframe(96, 64));
		this.addElement(new PlaybackStartButton(0, 32));
		this.addElement(new PlaybackReverseButton(32, 32));
		this.addElement(new PlaybackPauseButton(64, 32));
		this.addElement(new PlaybackForwardButton(96, 32));
		this.addElement(new PlaybackEndButton(128, 32));
		this.addElement(new PlaybackLockButton(0, 64));
	}
	
	@Override
	public void onSize(int w, int h)
	{
		// TODO components
		framesBar.setSize(160, 0, w - 16, h - 16);
		scrollBar.setSize(w - 16, 16, w, h - 32);
		scrollUp.setSize(w - 16, 0, w, 16);
		scrollDown.setSize(w - 16, h - 32, w, h - 16);
		scrollBarH.setSize(176, h - 16, w - 32, h);
		scrollLeft.setSize(160, h - 16, 176, h);
		scrollRight.setSize(w - 32, h - 16, w - 16, h);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		Main main = Main.instance;
		main.project.tickAnims(deltaTime);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (reason == GuiUpdate.PROJECT || reason == GuiUpdate.ANIMATION)
		{
			Project project = Main.instance.project;
			if (project.getAnimationName() == null) loadAnimation.enabled = cloneAnimation.enabled = saveAnimation.enabled = editAnimation.enabled = reverseAnimation.enabled = removeAnimation.enabled = false;
			else loadAnimation.enabled = cloneAnimation.enabled = saveAnimation.enabled = editAnimation.enabled = reverseAnimation.enabled = removeAnimation.enabled = true;
			animationSelector.setValues(Main.instance.project.getAnimationNames().isEmpty() ? new String[0] : MiscUtil.array("none", project.getAnimationNames()));
			animationSelector.setText(project.getAnimationNames().isEmpty() ? "no animations available" : project.getAnimationName() == null ? "no animation selected" : project.getAnimationName());
		}
	}
}