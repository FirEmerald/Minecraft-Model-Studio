package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.Element;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.RenderObjectComponents;

public class Model implements IModel
{
	private Bone base;
	public final List<Bone> bones = new ArrayList<>();
	
	public Model() {}
	
	public Model(Bone base)
	{
		setBase(base);
	}
	
	public Bone getBase()
	{
		return base;
	}
	
	public void setBase(Bone base)
	{
		bones.clear();
		addBone(this.base = base);
	}
	
	protected void addBone(Bone bone)
	{
		bones.add(bone);
		for (Bone bone2 : bone.children) addBone(bone2);
	}
	
	@Override
	public Map<String, Matrix4> getPose(AnimationState... anims)
	{
		Map<String, Matrix4> map = new HashMap<>();
		base.setDefTransform(map);
		for (AnimationState state : anims) state.anim.getBones(map, state.time, bones);
		return map;
	}
	
	@Override
	public void render(Map<String, Matrix4> map)
	{
		base.render(map);
	}

	@Override
	public void cleanUp()
	{
		base.cleanUp();
	}

	@Override
	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4> transformations)
	{
		Matrix4 transformation = transformations.get(base.name);
		if (transformation == null) transformation = new Matrix4();
		return base.raytrace(fx, fy, fz, dx, dy, dz, transformations, transformation);
	}

	@Override
	public Collection<? extends IEditable> getChildren()
	{
		return Collections.singleton(base);
	}

	@Override
	public boolean hasChildren()
	{
		return true;
	}

	@Override
	public boolean canBeChild(IEditable candidate)
	{
		return false;
	}

	@Override
	public void addChild(IEditable child) {}

	@Override
	public void addChildBefore(IEditable child, IEditable position) {}

	@Override
	public void addChildAfter(IEditable child, IEditable position) {}

	@Override
	public void removeChild(IEditable child) {}

	@Override
	public boolean isNameUsed(String name)
	{
		for (Bone bone : this.bones) if (bone.name.equals(name)) return true;
		return false;
	}

	@Override
	public void updateBonesList()
	{
		this.bones.clear();
		addBone(base);
	}

	@Override
	public void loadFromXML(Element root)
	{
		for (Element el : root.getChildren())
		{
			boolean flag = true;
			switch (el.getName())
			{
			case "bone":
			{
				base = new Bone(el.getString("name", "unnamed bone"), new Transformation());
				break;
			}
			case "component_holder":
			{
				base = new RenderObjectComponents(el.getString("name", "unnamed bone"), new Transformation());
				break;
			}
			default: flag = false;
			}
			if (flag)
			{
				base.loadFromXML(el);
				break;
			}
		}
		updateBonesList();
	}
}