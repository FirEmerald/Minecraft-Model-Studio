package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.api.animation.TweenType;
import firemerald.mcms.api.animation.TweeningFrame;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.IEditable;
import firemerald.mcms.util.history.HistoryActionChangeFloatValue;

public class ComponentKeyFrame extends ComponentPoseFrame implements IEditable
{
	public final float time;
	public final TweeningFrame keyFrame;
	
	public ComponentKeyFrame(int x1, int y1, int x2, int y2, int outline, int radius, String name, Bone<?> bone, float time, TweeningFrame keyFrame)
	{
		super(x1, y1, x2, y2, outline, radius, name, bone, keyFrame.transformation);
		this.time = time;
		this.keyFrame = keyFrame;
		this.onRelease = () -> {
			Main.instance.setEditing(this);
			ExtendedAnimationState state = Main.instance.project.getAnimationState();
			state.time = this.time;
			state.animMode = EnumPlaybackMode.PAUSED;
		};
	}

	private ComponentFloatingLabel labelTween;
	private SelectorButton selectorTween;
	private ComponentTextFloat smoothT;
	private ComponentIncrementFloat smoothP, smoothS;
	private ComponentFloatingLabel labelTime;

	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		editorY = super.onSelect(editorPanes, editorY);
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelTween = new ComponentFloatingLabel(editorX      , editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, "Tweening"));
		editorY += 20;
		editor.addElement(selectorTween = new SelectorButton(     editorX      , editorY, editorX + 150, editorY + 20, keyFrame.tweening, TweenType.values(), tween -> keyFrame.tweening = tween));
		editor.addElement(smoothT       = new ComponentTextFloat( editorX + 150, editorY, editorX + 290, editorY + 20 , Main.instance.fontMsg, keyFrame.factor, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> {
			Main.instance.project.onAction(new HistoryActionChangeFloatValue(keyFrame.factor, () -> keyFrame.factor, val -> keyFrame.factor = val));
			keyFrame.factor = value;
		}));
		editor.addElement(smoothP     = new ComponentIncrementFloat(editorX + 290, editorY     , smoothT, 1f));
		editor.addElement(smoothS     = new ComponentIncrementFloat(editorX + 290, editorY + 10, smoothT, -1f));
		editorY += 20;
		editor.addElement(labelTime = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, Float.toString(time)));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelTween);
		editor.removeElement(selectorTween);
		editor.removeElement(smoothT);
		editor.removeElement(smoothP);
		editor.removeElement(smoothS);
		editor.removeElement(labelTime);
		labelTween    = null;
		selectorTween = null;
		smoothT       = null;
		smoothP       = null;
		smoothS       = null;
		labelTime     = null;
	}
}