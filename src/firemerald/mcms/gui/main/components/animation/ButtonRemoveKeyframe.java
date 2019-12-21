package firemerald.mcms.gui.main.components.animation;

import java.util.NavigableMap;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.gui.main.components.elements.EditableButton;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.Textures;

public class ButtonRemoveKeyframe extends EditableButton
{
	public ButtonRemoveKeyframe(int x, int y)
	{
		super(x, y);
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_DELETE;
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getAnimation() instanceof Animation && Main.instance.getEditing() instanceof ComponentKeyFrame;
	}

	@Override
	public void onRelease()
	{
		Animation anim = (Animation) Main.instance.project.getAnimation();
		ComponentKeyFrame frame = (ComponentKeyFrame) Main.instance.getEditing();
		NavigableMap<Float, Transformation> map = anim.animation.get(frame.name);
		if (map != null) map.remove(frame.time);
		Main.instance.setEditing(null);
		Main.instance.gui.onGuiUpdate(GuiUpdate.ANIMATION);
	}
}