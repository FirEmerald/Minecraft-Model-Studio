package firemerald.mcms.gui.main.components.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.IAnimation;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.scrolling.ScrollableComponentPane;
import firemerald.mcms.util.EnumTextAlignment;
import firemerald.mcms.util.GuiUpdate;

public class ComponentFramesBar extends ScrollableComponentPane //TODO bones not scrolling horizontally
{
	public final List<ComponentLabel> boneNames = new ArrayList<>();
	public final List<ComponentFloatingLabel> timeStamps = new ArrayList<>();
	public final Map<Float, List<ComponentKeyFrame>> keyFrames = new HashMap<>();
	final int nameSize = 100;
	final float zoomSize = 100;
	public AnimationSeeker seeker;
	
	public ComponentFramesBar(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
		setup();
	}
	
	public void setup()
	{
		for (IGuiElement element : this.getElementsCopy()) this.removeElement(element);
		this.boneNames.clear();
		this.timeStamps.clear();
		this.keyFrames.clear();
		IAnimation animI = Main.instance.project.getAnimation();
		if (animI instanceof Animation)
		{
			Animation anim = (Animation) animI;
			List<String> bones = Main.instance.project.getAllBoneNames();
			anim.animation.forEach((bone, frames) -> {
				int y = bones.indexOf(bone) * 16 + 16;
				frames.forEach((time, transform) -> {
					int x = (int) (nameSize + (time * zoomSize)); //TODO zoom
					ComponentKeyFrame keyFrame = new ComponentKeyFrame(x - 4, y, x + 4, y + 16, 1, 4, bone, Main.instance.project.getBone(bone), time, transform);
					this.addElement(keyFrame);
					List<ComponentKeyFrame> keyFrames = this.keyFrames.get(time);
					if (keyFrames == null) this.keyFrames.put(time, keyFrames = new ArrayList<>());
					keyFrames.add(keyFrame);
				});
			});
			{
				int x = nameSize;
				for (int i = 0; i <= Math.ceil(anim.getLength()); i++)
				{
					ComponentFloatingLabel label = new ComponentFloatingLabel(x - 32, 0, x + 32, 16, Main.instance.fontMsg, Integer.toString(i), EnumTextAlignment.CENTER);
					timeStamps.add(label);
					this.addElement(label);
					x += zoomSize; //TODO zoom
				}
			}
			{
				int y = 16;
				for (String bone : bones)
				{
					ComponentLabel label = new ComponentLabel(0, y, nameSize, y + 16, Main.instance.fontMsg, bone);
					this.addElement(label);
					boneNames.add(label);
					y += 16;
				}
			}
		}
		this.addElement(seeker = new AnimationSeeker(nameSize - 16, 0, 16, zoomSize));
		updateComponentSize();
		updateScrollSize();
	}
	
	public void updateZoom()
	{
		keyFrames.forEach((time, keyFrames) -> {
			int x = (int) (nameSize + (time * zoomSize)); //TODO zoom
			keyFrames.forEach(frame -> {
				frame.setSize(x - 4, frame.y1, x + 4, frame.y2);
			});
		});
		seeker.scale = zoomSize;
	}

	@Override
	public void onScrolledH()
	{
		boneNames.forEach(boneName -> {
			boneName.setSize((int) scrollH, boneName.y1, (int) (scrollH + nameSize), boneName.y2);
		});
	}

	@Override
	public void onScrolled()
	{
		int scroll = (int) Math.floor(this.scroll);
		timeStamps.forEach(timeStamp -> {
			timeStamp.setSize(timeStamp.x1, scroll, timeStamp.x2, scroll + 16);
		});
		seeker.setSize(seeker.x1, scroll, seeker.x2, scroll + 16);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.ANIMATION || reason == GuiUpdate.PROJECT || reason == GuiUpdate.MODEL)
		{
			setup();
		}
		super.onGuiUpdate(reason);
	}
}