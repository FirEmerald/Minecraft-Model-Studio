package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.model.IModelEditable;

public abstract class Model<M extends Model<M, T>, T extends Bone<T>> implements IModel<M, T>
{
	private T base;
	public final List<T> bones = new ArrayList<>();
	
	public Model() {}
	
	public Model(T base)
	{
		setBase(base);
	}

	@Override
	public List<T> getRootBones()
	{
		return Collections.singletonList(base);
	}

	@Override
	public Collection<T> getAllBones()
	{
		return bones;
	}
	
	@Override
	public boolean addRootBone(T bone, boolean updateBoneList)
	{
		return false;
	}
	
	public T getBase()
	{
		return base;
	}
	
	public void setBase(T base)
	{
		bones.clear();
		addBone(this.base = base);
	}
	
	protected void addBone(T bone)
	{
		bones.add(bone);
		for (T bone2 : bone.children) addBone(bone2);
	}
	
	@Override
	public void render(Map<String, Matrix4d> map, Runnable defaultTexture)
	{
		if (base instanceof RenderBone) ((RenderBone<?>) base).render(map, defaultTexture);
	}

	@Override
	public void cleanUp()
	{
		base.cleanUp();
	}

	@Override
	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d root)
	{
		if (base instanceof ObjectBone)
		{
			Matrix4d transformation = transformations.get(base.name);
			if (transformation == null) transformation = new Matrix4d();
			transformation = root.mul(transformation, new Matrix4d());
			return ((ObjectBone<?>) base).raytrace(fx, fy, fz, dx, dy, dz, transformations, transformation);
		}
		else return null;
	}

	@Override
	public Collection<T> getChildren()
	{
		return Collections.singleton(base);
	}

	@Override
	public boolean hasChildren()
	{
		return true;
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return false;
	}

	@Override
	public void addChild(IModelEditable child) {}

	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position) {}

	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position) {}

	@Override
	public void removeChild(IModelEditable child) {}

	@Override
	public int getChildIndex(IModelEditable child)
	{
		return -1;
	}

	@Override
	public void addChildAt(IModelEditable child, int index) {}

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
		addBone(base);
	}

	@Override
	public void load(AbstractElement root)
	{
		base = null;
		for (AbstractElement el : root.getChildren())
		{
			base = this.makeNew(el.getString("name", "unnamed bone"), new Transformation(el, 1), null);
			base.loadFromXML(el, 1);
		}
		updateBonesList();
	}

	@Override
	public void save(AbstractElement modelEl)
	{
		base.addToXML(modelEl, 1);
	}

	@Override
	public Skeleton getSkeleton()
	{
		Skeleton skeleton = new Skeleton();
		Bone.Actual root = new Bone.Actual(base.name, base.defaultTransform, null);
		skeleton.addRootBone(root, false);
		processToSkeleton(root, base);
		skeleton.updateBonesList();
		return skeleton;
	}

	@Override
	public void updateTex()
	{
		base.updateTex();
	}

	@Override
	public M cloneObject()
	{
		T newBase = base.cloneObject(null);
		return newModel(newBase);
	}
	
	public abstract M newModel(T base);
}