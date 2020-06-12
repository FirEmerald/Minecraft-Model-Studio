package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.model.IModelEditable;

public class Skeleton implements ISkeleton
{
	public final List<Bone> base = new ArrayList<>();
	public final Map<String, Bone> bones = new HashMap<>();
	public final Map<String, Matrix4d> inverse = new HashMap<>();
	
	public Skeleton() {}
	
	public Skeleton(AbstractElement el)
	{
		load(el);
	}
	
	public Skeleton(List<Bone> base)
	{
		setBase(base);
	}

	@Override
	public List<Bone> getRootBones()
	{
		return base;
	}

	@Override
	public Collection<Bone> getAllBones()
	{
		return bones.values();
	}

	@Override
	public boolean isNameUsed(String name)
	{
		return bones.containsKey(name);
	}
	
	public void setBase(List<Bone> bones)
	{
		base.clear();
		base.addAll(bones);
		updateBonesList();
	}
	
	@Override
	public void updateBonesList()
	{
		bones.clear();
		inverse.clear();
		base.forEach(bone -> updateBone(bone));
	}
	
	public void updateBone(Bone bone)
	{
		bones.put(bone.name, bone);
		inverse.put(bone.name, bone.getTransformation().invert(new Matrix4d()));
		bone.children.forEach(child -> updateBone(child));
	}

	@Override
	public Map<String, Matrix4d> getInverseTransforms()
	{
		return inverse;
	}

	@Override
	public void load(AbstractElement root, float scale)
	{
		this.base.clear();
		for (AbstractElement el : root.getChildren())
		{
			Bone bone = Bone.construct(el.getName(), null, el, scale);
			if (bone != null) base.add(bone);
		}
		updateBonesList();
	}
	
	@Override
	public boolean addRootBone(Bone bone, boolean updateBoneList)
	{
		base.add(bone);
		if (updateBoneList) this.updateBonesList();
		return true;
	}

	@Override
	public void save(AbstractElement skeletonEl, float scale)
	{
		base.forEach(bone -> bone.addToSkeleton(skeletonEl, scale));
	}

	@Override
	public Collection<? extends IModelEditable> getChildren()
	{
		return this.getRootBones();
	}

	@Override
	public boolean hasChildren()
	{
		return !base.isEmpty();
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return candidate instanceof Bone && !base.contains(candidate);
	}

	@Override
	public void addChild(IModelEditable child)
	{
		if (child instanceof Bone && !this.base.contains(child)) this.addRootBone((Bone) child, true);
	}

	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position);
			if (pos < 0) pos = 0;
			this.base.add(pos, (Bone) child);
			this.updateBonesList();
		}
	}

	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position) + 1;
			if (pos <= 0) pos = this.base.size();
			this.base.add(pos, (Bone) child);
			this.updateBonesList();
		}
	}

	@Override
	public void removeChild(IModelEditable child)
	{
		if (child instanceof Bone && base.remove(child)) updateBonesList();
	}
}