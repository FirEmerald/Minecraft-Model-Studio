package firemerald.mcms.api.animation;

import java.util.Collection;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.util.ISaveable;

public interface IAnimation extends ISaveable
{
	/**
	 * gets the transformation matrices to apply to the given bones.
	 * By using an animation this way, an animated object need only track the frame time for it's animations.
	 * 
	 * @param map a list of already computed transformations, generally from other animations
	 * @param frame the current frame time
	 * @param bones the model's bones
	 * @return a map paring bones to transformation matrices
	 */
	public Map<String, Matrix4d> getBones(Map<String, Matrix4d> map, float frame, Collection<Bone> bones);
	
	/**
	 * gets the animation's running length, or loop length for looping animations. This can be used to detect when an animation has completed.
	 * @return the animation's total length (use {@link Float#POSITIVE_INFINITY} for custom animations that have no specified length)
	 */
	public float getLength();
} 