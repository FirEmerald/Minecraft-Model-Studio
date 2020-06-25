package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Bone.Actual;
import firemerald.mcms.model.IModelEditable;

public class Skeleton implements IRigged<Skeleton, Bone.Actual>
{
	public final List<Actual> base = new ArrayList<>();
	public final Map<String, Actual> bones = new HashMap<>();
	public final Map<String, Matrix4d> inverse = new HashMap<>();
	
	public Skeleton() {}
	
	public Skeleton(AbstractElement el)
	{
		load(el);
	}
	
	public Skeleton(List<Actual> base)
	{
		setBase(base);
	}

	@Override
	public List<Actual> getRootBones()
	{
		return base;
	}

	@Override
	public Collection<Actual> getAllBones()
	{
		return bones.values();
	}

	@Override
	public boolean isNameUsed(String name)
	{
		return bones.containsKey(name);
	}
	
	public void setBase(List<Actual> bones)
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
	
	public void updateBone(Actual bone)
	{
		bones.put(bone.name, bone);
		inverse.put(bone.name, bone.getTransformation().invert(new Matrix4d()));
		bone.children.forEach(child -> updateBone(child));
	}

	public Map<String, Matrix4d> getInverseTransforms()
	{
		return inverse;
	}

	public void load(AbstractElement root, float scale)
	{
		this.base.clear();
		for (AbstractElement el : root.getChildren())
		{
			Actual bone = new Actual(el.getString("name", "unnamed bone"), new Transformation(el, scale), null);
			base.add(bone);
			bone.loadChildrenFromXML(el, scale);
		}
		updateBonesList();
	}
	
	@Override
	public boolean addRootBone(Actual bone, boolean updateBoneList)
	{
		base.add(bone);
		if (updateBoneList) this.updateBonesList();
		return true;
	}

	public void save(AbstractElement skeletonEl, float scale)
	{
		base.forEach(bone -> bone.addToXML(skeletonEl, scale));
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
		if (child instanceof Actual && !this.base.contains(child)) this.addRootBone((Actual) child, true);
	}

	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Actual && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position);
			if (pos < 0) pos = 0;
			this.base.add(pos, (Actual) child);
			this.updateBonesList();
		}
	}

	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Actual && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position) + 1;
			if (pos <= 0) pos = this.base.size();
			this.base.add(pos, (Actual) child);
			this.updateBonesList();
		}
	}

	@Override
	public void removeChild(IModelEditable child)
	{
		if (child instanceof Bone && base.remove(child)) updateBonesList();
	}

	@Override
	public int getChildIndex(IModelEditable child)
	{
		if (child instanceof Bone && this.base.contains(child)) return base.indexOf(child);
		else return -1;
	}

	@Override
	public void addChildAt(IModelEditable child, int index)
	{
		if (child instanceof Actual && !this.base.contains(child))
		{
			if (index < 0) index = 0;
			this.base.add(index, (Actual) child);
			this.updateBonesList();
		}
	}

	@Override
	public Actual makeNew(String name, Transformation transformation, @Nullable Actual parent)
	{
		return new Actual(name, transformation, parent);
	}

	public Skeleton getSkeleton()
	{
		return this;
	}
	
	public String getElementName()
	{
		return "skeleton";
	}
	
	@Override
	public void save(AbstractElement el)
	{
		save(el, 1);
	}
	
	@Override
	public void load(AbstractElement el)
	{
		load(el, 1);
	}
}