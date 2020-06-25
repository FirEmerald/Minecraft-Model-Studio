package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.animation.IAnimation;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone.Actual;
import firemerald.mcms.api.util.ISaveable;
import firemerald.mcms.api.util.ISelfTyped;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.util.GuiUpdate;

public interface IRigged<T extends IRigged<T, B>, B extends Bone<B>> extends ISelfTyped<T>, ISaveable, IEditableParent, ITransformsProvider
{	
	public default T removeNonSkeleton(Skeleton skeleton)
	{
		List<B> roots = this.getRootBones();
		for (B bone : new ArrayList<>(roots))
		{
			boolean flag = false;
			for (Bone.Actual root : skeleton.getRootBones()) if (root.name.endsWith(bone.name))
			{
				flag = true;
				removeNotFromSkeleton(root, bone);
				break;
			}
			if (!flag) this.removeChild(bone);
		}
		this.updateBonesList();
		return self();
	}
	
	public default <C extends Bone<C>> void removeNotFromSkeleton(Bone.Actual from, B to)
	{
		List<B> roots = to.children;
		for (B bone : new ArrayList<>(roots))
		{
			boolean flag = false;
			for (Bone.Actual root : from.children) if (root.name.endsWith(bone.name))
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
		for (B base : this.getRootBones()) iteratePose(base, map);
		for (AnimationState state : anims)
		{
			IAnimation anim = state.anim.get();
			if (anim != null) map = anim.getBones(map, state.time, getAllBones());
		}
		return map;
	}
	
	public default void iteratePose(B bone, Map<String, Matrix4d> pose)
	{
		pose.put(bone.name, bone.defaultTransform.getTransformation());
		bone.children.forEach(child -> iteratePose(child, pose));
	}
	
	public List<B> getRootBones();
	
	public Collection<B> getAllBones();
	
	public default Collection<String> getAllBoneNames()
	{
		Collection<B> bones = getAllBones();
		List<String> names = new ArrayList<>(bones.size());
		bones.forEach(bone -> names.add(bone.name));
		return names;
	}
	
	public default B getBone(String name)
	{
		for (B root : getRootBones())
		{
			B res = getBone(root, name);
			if (res != null) return res;
		}
		return null;
	}
	
	default B getBone(B bone, String name)
	{
		if (bone.name.equals(name)) return bone;
		else for (B child : bone.children)
		{
			B res = getBone(child, name);
			if (res != null) return res;
		}
		return null;
	}
	
	public boolean addRootBone(B bone, boolean updateBoneList);
	
	public void updateBonesList();
	
	public boolean isNameUsed(String name);
	
	public default Skeleton getSkeleton()
	{
		Skeleton skeleton = new Skeleton();
		this.getRootBones().forEach(bone -> {
			Bone.Actual root = bone.cloneToSkeleton(null);
			skeleton.addRootBone(root, false);
			processToSkeleton(root, bone);
		});
		skeleton.updateBonesList();
		return skeleton;
	}
	
	public default void processToSkeleton(Bone.Actual addTo, B addFrom)
	{
		addFrom.children.forEach(bone -> processToSkeleton(bone.cloneToSkeleton(addTo), bone));
	}
	
	public default T applySkeleton(Skeleton skeleton)
	{
		for (Bone.Actual bone : skeleton.getRootBones())
		{
			boolean flag = false;
			B root = null;
			for (B root2 : this.getRootBones()) if (root2.name.equals(bone.name))
			{
				root = root2;
				flag = true;
				root.cloneProperties(bone);
				break;
			}
			if (!flag)
			{
				if (this.isNameUsed(bone.name)) continue;
				root = this.makeNew(bone.name, bone.defaultTransform.copy(), null);
				if (!this.addRootBone(root, false)) continue;
			}
			applyFromSkeleton(bone, root);
		}
		this.updateBonesList();
		return self();
	}
	
	public abstract B makeNew(String name, Transformation transformation, @Nullable B parent);

	public default void applyFromSkeleton(Bone.Actual from, B to)
	{
		for (Actual bone : from.children)
		{
			boolean flag = false;
			B root = null;
			for (B root2 : to.children) if (root2.name.equals(bone.name))
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
	public default T applySkeletonTransforms(Skeleton skeleton)
	{
		for (Bone.Actual bone : skeleton.getRootBones())
		{
			for (B root : this.getRootBones()) if (root.name.equals(bone.name))
			{
				root.defaultTransform.set(bone.defaultTransform);
				applyFromSkeletonTransforms(bone, root);
				break;
			}
		}
		return (T) this;
	}
	
	public default void applyFromSkeletonTransforms(Bone.Actual from, B to)
	{
		for (Bone.Actual bone : from.children)
		{
			for (B root : to.children) if (root.name.equals(bone.name))
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
		B bone = getBone(name);
		return bone == null ? new Transformation() : bone.defaultTransform;
	}
}