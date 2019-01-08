package firemerald.mcms.api.animation;

import java.util.List;
import java.util.Map;

import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.model.Bone;

public interface IAnimation
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
	public Map<String, Matrix4> getBones(Map<String, Matrix4> map, float frame, List<Bone> bones);
}