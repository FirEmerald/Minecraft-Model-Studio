package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.IEditable;

public class ComponentKeyFrame extends ComponentPoseFrame implements IEditable
{
	public final float time;
	
	public ComponentKeyFrame(int x1, int y1, int x2, int y2, int outline, int radius, String name, Bone bone, float time, Transformation transform)
	{
		super(x1, y1, x2, y2, outline, radius, name, bone, transform);
		this.time = time;
		this.onRelease = () -> {
			Main.instance.setEditing(this);
			Main.instance.animState.time = this.time;
			Main.instance.animMode = EnumPlaybackMode.PAUSED;
		};
	}
	
	private ComponentFloatingLabel labelTime;

	@Override
	public int addExtra(EditorPanes editorPanes, int editorY)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelTime = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, Float.toString(time)));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelTime);
		labelTime = null;
	}
}