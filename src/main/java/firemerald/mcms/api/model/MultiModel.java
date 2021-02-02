package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.util.RaytraceResult;

public abstract class MultiModel<M extends MultiModel<M, T>, T extends Bone<T>> implements IModel<M, T>
{
	public final List<T> base = new ArrayList<>();
	public final List<T> bones = new ArrayList<>();
	
	public MultiModel() {}
	
	public MultiModel(AbstractElement el)
	{
		load(el);
	}
	
	public MultiModel(List<T> base)
	{
		setBase(base);
	}
	
	@Override
	public List<T> getRootBones()
	{
		return base;
	}

	@Override
	public Collection<T> getAllBones()
	{
		return bones;
	}
	
	@Override
	public boolean addRootBone(T bone, boolean updateBoneList)
	{
		base.add(bone);
		if (updateBoneList) this.updateBonesList();
		return true;
	}
	
	public void setBase(List<T> base)
	{
		bones.clear();
		this.base.clear();
		for (T obj : base)
		{
			this.base.add(obj);
			addBone(obj);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setBase(T... base)
	{
		bones.clear();
		this.base.clear();
		for (T obj : base)
		{
			this.base.add(obj);
			addBone(obj);
		}
	}
	
	protected void addBone(T bone)
	{
		bones.add(bone);
		for (T bone2 : bone.children) addBone(bone2);
	}
	
	public void addBaseBone(T bone)
	{
		this.base.add(bone);
		addBone(bone);
	}
	
	@Override
	public void render(IModelHolder holder, Map<String, Matrix4d> map, Runnable defaultTexture)
	{
		Matrix4d root = new Matrix4d();
		base.stream().filter(base -> base instanceof IRenderBone).forEach(base -> ((IRenderBone<?>) base).render(holder, map, root, defaultTexture));
	}

	@Override
	public void tick(IModelHolder holder, Map<String, Matrix4d> pos, float deltaTime)
	{
		Matrix4d root = new Matrix4d();
		base.stream().filter(base -> base instanceof ITickableBone).forEach(base -> ((ITickableBone<?>) base).tick(holder, pos, root, deltaTime));
	}

	@Override
	public void cleanUp()
	{
		for (T base : this.base) base.cleanUp();
	}

	@Override
	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d root)
	{
		RaytraceResult result = null;
		for (T bone : base) if (bone instanceof IRaytraceBone)
		{
			Matrix4d transformation = transformations.get(bone.name);
			if (transformation == null) transformation = new Matrix4d();
			transformation = root.mul(transformation, new Matrix4d());
			RaytraceResult res = ((IRaytraceBone<?>) bone).raytrace(fx, fy, fz, dx, dy, dz, transformations, transformation);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		return result;
	}

	@Override
	public Collection<? extends IModelEditable> getChildren()
	{
		return base;
	}

	@Override
	public boolean hasChildren()
	{
		return !base.isEmpty();
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return candidate instanceof Bone;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChild(IModelEditable child)
	{
		if (child instanceof Bone && !this.base.contains(child)) this.addBaseBone((T) child);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position);
			if (pos < 0) pos = 0;
			this.base.add(pos, (T) child);
			addBone((T) child);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.base.contains(child))
		{
			int pos = this.base.indexOf(position) + 1;
			if (pos <= 0) pos = this.base.size();
			this.base.add(pos, (T) child);
			addBone((T) child);
		}
	}

	@Override
	public void removeChild(IModelEditable child)
	{
		if (child instanceof Bone) this.base.remove(child);
	}

	@Override
	public int getChildIndex(IModelEditable child)
	{
		if (child instanceof Bone && this.base.contains(child)) return this.base.indexOf(child);
		else return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildAt(IModelEditable child, int index)
	{
		if (child instanceof Bone && !this.base.contains(child))
		{
			if (index < 0) index = 0;
			this.base.add(index, (T) child);
			addBone((T) child);
		}
	}

	@Override
	public boolean isNameUsed(String name)
	{
		for (T bone : this.bones) if (bone.name.equals(name)) return true;
		return false;
	}

	@Override
	public void updateBonesList()
	{
		this.bones.clear();
		for (T base : this.base) addBone(base);
	}

	@Override
	public void load(AbstractElement root)
	{
		base.clear();
		for (AbstractElement el : root.getChildren())
		{
			T base = this.makeNew(el.getString("name", "unnamed bone"), new Transformation(el, 1), null);
			base.loadFromXML(el, 1);
			this.base.add(base);
		}
		updateBonesList();
	}

	@Override
	public void save(AbstractElement modelEl)
	{
		base.forEach(bone -> bone.addToXML(modelEl, 1));
	}

	@Override
	public void updateTex()
	{
		base.forEach(bone -> bone.updateTex());
	}

	@Override
	public M cloneObject()
	{
		List<T> newBase = new ArrayList<>(this.base.size());
		this.base.forEach(oldBase -> newBase.add(oldBase.cloneObject(null)));
		return newModel(newBase);
	}
	
	public abstract M newModel(List<T> base);
}