package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.animation.IAnimation;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.util.ISaveable;
import firemerald.mcms.model.IEditableParent;

public interface IRigged<T extends IRigged<?>> extends ISaveable, IEditableParent, ITransformsProvider
{	
	@SuppressWarnings("unchecked")
	public default T removeNonSkeleton(ISkeleton skeleton)
	{
		List<Bone> roots = this.getRootBones();
		for (Bone bone : roots.toArray(new Bone[roots.size()]))
		{
			boolean flag = false;
			for (Bone root : skeleton.getRootBones()) if (root.name.endsWith(bone.name))
			{
				flag = true;
				removeNotFromSkeleton(root, bone);
				break;
			}
			if (!flag) this.removeChild(bone);
		}
		this.updateBonesList();
		return (T) this;
	}
	
	public default void removeNotFromSkeleton(Bone from, Bone to)
	{
		List<Bone> roots = to.children;
		for (Bone bone : roots.toArray(new Bone[roots.size()]))
		{
			boolean flag = false;
			for (Bone root : from.children) if (root.name.endsWith(bone.name))
			{
				flag = true;
				removeNotFromSkeleton(root, bone);
				break;
			}
			if (!flag) to.removeChild(bone);
		}
	}
	
	public default Map<String, Matrix4d> getPose(AnimationState... anims)
	{
		Map<String, Matrix4d> map = new HashMap<>();
		for (Bone base : this.getRootBones()) iteratePose(base, map);
		for (AnimationState state : anims)
		{
			IAnimation anim = state.anim.get();
			if (anim != null) map = anim.getBones(map, state.time, getAllBones());
		}
		return map;
	}
	
	public default void iteratePose(Bone bone, Map<String, Matrix4d> pose)
	{
		pose.put(bone.name, bone.defaultTransform.getTransformation());
		bone.children.forEach(child -> iteratePose(child, pose));
	}
	
	public List<Bone> getRootBones();
	
	public Collection<Bone> getAllBones();
	
	public default Collection<String> getAllBoneNames()
	{
		Collection<Bone> bones = getAllBones();
		List<String> names = new ArrayList<>(bones.size());
		bones.forEach(bone -> names.add(bone.name));
		return names;
	}
	
	public default Bone getBone(String name)
	{
		for (Bone root : getRootBones())
		{
			Bone res = getBone(root, name);
			if (res != null) return res;
		}
		return null;
	}
	
	default Bone getBone(Bone bone, String name)
	{
		if (bone.name.equals(name)) return bone;
		else for (Bone child : bone.children)
		{
			Bone res = getBone(child, name);
			if (res != null) return res;
		}
		return null;
	}
	
	public boolean addRootBone(Bone bone, boolean updateBoneList);
	
	public void updateBonesList();
	
	public boolean isNameUsed(String name);
	
	public default ISkeleton getSkeleton()
	{
		Skeleton skeleton = new Skeleton();
		this.getRootBones().forEach(bone -> {
			Bone root = bone.cloneToSkeleton(null);
			skeleton.addRootBone(root, false);
			processToSkeleton(root, bone);
		});
		skeleton.updateBonesList();
		return skeleton;
	}
	
	public default void processToSkeleton(Bone addTo, Bone addFrom)
	{
		addFrom.children.forEach(bone -> processToSkeleton(bone.cloneToSkeleton(addTo), bone));
	}
	
	@SuppressWarnings("unchecked")
	public default T applySkeleton(ISkeleton skeleton)
	{
		for (Bone bone : skeleton.getRootBones())
		{
			boolean flag = false;
			Bone root = null;
			for (Bone root2 : this.getRootBones()) if (root2.name.equals(bone.name))
			{
				root = root2;
				flag = true;
				root.cloneProperties(bone);
				break;
			}
			if (!flag)
			{
				if (this.isNameUsed(bone.name)) continue;
				root = bone.cloneToModel(null);
				if (!this.addRootBone(root, false)) continue;
			}
			applyFromSkeleton(bone, root);
		}
		this.updateBonesList();
		return (T) this;
	}
	
	public default Bone makeNew(String name, Transformation transformation, @Nullable Bone parent)
	{
		return new Bone(name, transformation, parent);
	}

	public default void applyFromSkeleton(Bone from, Bone to)
	{
		for (Bone bone : from.children)
		{
			boolean flag = false;
			Bone root = null;
			for (Bone root2 : to.children) if (root2.name.equals(bone.name))
			{
				root = root2;
				flag = true;
				root.defaultTransform.set(bone.defaultTransform);
				break;
			}
			if (!flag)
			{
				if (this.isNameUsed(bone.name)) continue;
				root = makeNew(bone.name, new Transformation(bone.defaultTransform), to);
			}
			applyFromSkeleton(bone, root);
		}
	}

	@SuppressWarnings("unchecked")
	public default T applySkeletonTransforms(ISkeleton skeleton)
	{
		for (Bone bone : skeleton.getRootBones())
		{
			for (Bone root : this.getRootBones()) if (root.name.equals(bone.name))
			{
				root.defaultTransform.set(bone.defaultTransform);
				applyFromSkeletonTransforms(bone, root);
				break;
			}
		}
		return (T) this;
	}
	
	public default void applyFromSkeletonTransforms(Bone from, Bone to)
	{
		for (Bone bone : from.children)
		{
			for (Bone root : to.children) if (root.name.equals(bone.name))
			{
				root.defaultTransform.set(bone.defaultTransform);
				applyFromSkeletonTransforms(bone, root);
				break;
			}
		}
	}
	
	@Override
	public default Transformation get(String name)
	{
		Bone bone = getBone(name);
		return bone == null ? new Transformation() : bone.defaultTransform;
	}
}