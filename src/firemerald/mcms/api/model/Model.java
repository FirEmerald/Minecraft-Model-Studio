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

	@Override
	public List<Bone> getRootBones()
	{
		return Collections.singletonList(base);
	}

	@Override
	public Collection<Bone> getAllBones()
	{
		return bones;
	}
	
	@Override
	public boolean addRootBone(Bone bone, boolean updateBoneList)
	{
		return false;
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
	public void render(Map<String, Matrix4d> map)
	{
		base.render(map);
	}

	@Override
	public void cleanUp()
	{
		base.cleanUp();
	}

	@Override
	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations)
	{
		Matrix4d transformation = transformations.get(base.name);
		if (transformation == null) transformation = new Matrix4d();
		return base.raytrace(fx, fy, fz, dx, dy, dz, transformations, transformation);
	}

	@Override
	public Collection<? extends IModelEditable> getChildren()
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
	public void load(AbstractElement root)
	{
		for (AbstractElement el : root.getChildren())
		{
			boolean flag = true;
			switch (el.getName())
			{
			case "bone":
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

	@Override
	public void save(AbstractElement modelEl)
	{
		base.addToXML(modelEl);
	}

	@Override
	public ISkeleton getSkeleton()
	{
		Skeleton skeleton = new Skeleton();
		Bone root = new Bone(base.name, base.defaultTransform);
		skeleton.addRootBone(root, false);
		processToSkeleton(root, base);
		skeleton.updateBonesList();
		return skeleton;
	}
	
	@Override
	public void processToSkeleton(Bone addTo, Bone addFrom)
	{
		addFrom.children.forEach(bone -> processToSkeleton(new Bone(bone.name, bone.defaultTransform, addTo), bone));
	}

	@Override
	public void updateTex()
	{
		base.updateTex();
	}
}