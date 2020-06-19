package firemerald.mcms.gui.main.components.animation;

import java.util.NavigableMap;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.Pose;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.gui.main.components.elements.EditableButton;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class ButtonRemoveKeyframe extends EditableButton
{
	public ButtonRemoveKeyframe(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_DELETE;
	}

	@Override
	public boolean isEnabled()
	{
		return (Main.instance.project.getAnimation() instanceof Animation && Main.instance.getEditing() instanceof ComponentKeyFrame) || (Main.instance.project.getAnimation() instanceof Pose && Main.instance.getEditing() instanceof ComponentPoseFrame);
	}

	@Override
	public void onRelease()
	{
		if (Main.instance.project.getAnimation() instanceof Animation)
		{
			Animation anim = (Animation) Main.instance.project.getAnimation();
			ComponentKeyFrame frame = (ComponentKeyFrame) Main.instance.getEditing();
			NavigableMap<Float, Transformation> map = anim.animation.get(frame.name);
			if (map != null) map.remove(frame.time);
		}
		else if (Main.instance.project.getAnimation() instanceof Pose)
		{
			Pose pose = (Pose) Main.instance.project.getAnimation();
			ComponentPoseFrame frame = (ComponentPoseFrame) Main.instance.getEditing();
			pose.pose.remove(frame.name);
		}
		Main.instance.setEditing(null);
		Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
	}
}