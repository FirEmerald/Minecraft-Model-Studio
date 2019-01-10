package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.RenderObjectComponents;

public class MultiModel implements IModel
{
	public final List<Bone> base = new ArrayList<>();
	public final List<Bone> bones = new ArrayList<>();
	
	public MultiModel() {}
	
	public MultiModel(List<Bone> base)
	{
		setBase(base);
	}
	
	public List<Bone> getBase()
	{
		return base;
	}
	
	public void setBase(List<Bone> base)
	{
		bones.clear();
		this.base.clear();
		for (Bone obj : base)
		{
			this.base.add(obj);
			addBone(obj);
		}
	}
	
	public void setBase(Bone... base)
	{
		bones.clear();
		this.base.clear();
		for (Bone obj : base)
		{
			this.base.add(obj);
			addBone(obj);
		}
	}
	
	protected void addBone(Bone bone)
	{
		bones.add(bone);
		for (Bone bone2 : bone.children) addBone(bone2);
	}
	
	public void addBaseBone(Bone bone)
	{
		this.base.add(bone);
		addBone(bone);
	}
	
	@Override
	public Map<String, Matrix4> getPose(AnimationState... anims)
	{
		Map<String, Matrix4> map = new HashMap<>();
		for (Bone base : this.base) base.setDefTransform(map);
		for (AnimationState state : anims) map = state.anim.getBones(map, state.time, bones);
		return map;
	}
	
	@Override
	public void render(Map<String, Matrix4> map)
	{
		for (Bone base : this.base) base.render(map);
	}

	@Override
	public void cleanUp()
	{
		for (Bone base : this.base) base.cleanUp();
	}

	@Override
	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4> transformations)
	{
		RaytraceResult result = null;
		for (Bone bone : base)
		{
			Matrix4 transformation = transformations.get(bone.name);
			if (transformation == null) transformation = new Matrix4();
			RaytraceResult res = bone.raytrace(fx, fy, fz, dx, dy, dz, transformations, transformation);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		return result;
	}

	@Override
	public Collection<? extends IEditable> getChildren()
	{
		return base;
	}

	@Override
	public boolean hasChildren()
	{
		return !base.isEmpty();
	}

	@Override
	public boolean canBeChild(IEditable candidate)
	{
		return candidate instanceof Bone;
	}

	@Override
	public void addChild(IEditable child)
	{
		if (child instanceof Bone && !this.base.contains(child)) this.addBaseBone((Bone) child);
	}

	@Override
	public void addChildBefore(IEditable child, IEditable position)
	{
		if (child instanceof Bone && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position);
			if (pos < 0) pos = 0;
			this.base.add(pos, (Bone) child);
			addBone((Bone) child);
		}
	}

	@Override
	public void addChildAfter(IEditable child, IEditable position)
	{
		if (child instanceof Bone && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position) + 1;
			if (pos <= 0) pos = this.base.size();
			this.base.add(pos, (Bone) child);
			addBone((Bone) child);
		}
	}

	@Override
	public void removeChild(IEditable child)
	{
		if (child instanceof Bone) this.base.remove(child);
	}

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
		for (Bone base : this.base) addBone(base);
	}

	@Override
	public void loadFromXML(AbstractElement root)
	{
		base.clear();
		for (AbstractElement el : root.getChildren())
		{
			Bone base;
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
			default: base = null;
			}
			if (base != null)
			{
				base.loadFromXML(el);
				this.base.add(base);
			}
		}
		updateBonesList();
	}
}