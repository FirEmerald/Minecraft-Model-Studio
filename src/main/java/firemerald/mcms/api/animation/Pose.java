package firemerald.mcms.api.animation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.ITransformsProvider;
import firemerald.mcms.gui.main.components.animation.ComponentPoseFrame;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.util.GuiUpdate;

public class Pose implements IAnimation
{
	public final Map<String, Transformation> pose;
	protected boolean relative;
	
	public Pose(boolean relative)
	{
		this(new HashMap<>(), relative);
	}
	
	public Pose(Map<String, Transformation> pose, boolean relative)
	{
		this.pose = pose;
		this.relative = relative;
	}
	
	public Pose(Map<String, Transformation> pose)
	{
		this(pose, false);
	}
	
	public Pose(AbstractElement el)
	{
		this(new HashMap<>(), false);
		load(el);
	}
	
	@Override
	public float getLength()
	{
		return 0;
	}
	
	@Override
	public Map<String, Matrix4d> getBones(Map<String, Matrix4d> map, float frame, Collection<Bone> bones)
	{
		for (Bone bone : bones)
		{
			Transformation transform = pose.get(bone.getName());
			if (transform != null)
			{
				Matrix4d mat = transform.getTransformation();
				if (relative) map.get(bone.getName()).mul(mat);
				else map.put(bone.getName(), mat);
			}
		}
		return map;
	}

	@Override
	public void load(AbstractElement root, float scale)
	{
		relative = root.getBoolean("relative", false);
		pose.clear();
		for (AbstractElement el : root.getChildren()) if (el.getName().equals("bone"))
		{
			try
			{
				String boneName = el.getString("boneName");
				Transformation t = new Transformation(el, scale);
				pose.put(boneName, t);
			}
			catch (Exception e)
			{
				GuiPopupException.onException("Couldn't parse bone", e);
			}
		}
	}

	@Override
	public void save(AbstractElement root, float scale)
	{
		root.setBoolean("relative", relative);
		pose.forEach((bone, transform) -> {
			AbstractElement el = root.addChild("bone");
			el.setString("boneName", bone);
			transform.save(el, scale);
		});
	}

	@Override
	public String getElementName()
	{
		return "pose";
	}
	
	@Override
	public boolean isRelative()
	{
		return relative;
	}
	
	@Override
	public void setRelative(boolean relative)
	{
		if (relative != this.relative)
		{
			this.relative = relative;
			Main main = Main.instance;
			Project project = main.project;
			final ITransformsProvider transforms;
			if (project.useBackingSkeleton()) transforms = project.getSkeleton();
			else
			{
				if (project.getRig() != null) transforms = project.getRig();
				else transforms = ITransformsProvider.NONE;
			}
			pose.forEach((name, transform) -> {
				Transformation orig = transforms.get(name);
				final Matrix4d mul = orig.getTransformation();
				if (relative) mul.invert();
				Matrix4d cur = transform.getTransformation();
				cur = mul.mul(cur, cur);
				transform.setFromMatrix(cur);
			});
			main.onGuiUpdate(GuiUpdate.ANIMATION);
			if (main.getEditing() instanceof ComponentPoseFrame) main.setEditing(main.getEditing());
		}
	}
	
	@Override
	public Pose cloneObject()
	{
		Map<String, Transformation> newPose = new HashMap<>();
		this.pose.forEach((bone, transform) -> newPose.put(bone, transform.copy()));
		return new Pose(newPose, this.relative);
	}

	@Override
	public void reverseAnimation(IRigged<?> rig) {} //nothing to do
}