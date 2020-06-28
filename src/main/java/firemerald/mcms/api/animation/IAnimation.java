package firemerald.mcms.api.animation;

import java.util.Collection;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.util.IClonableObject;
import firemerald.mcms.api.util.ISaveable;

public interface IAnimation extends ISaveable, IClonableObject<IAnimation>
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
	public Map<String, Matrix4d> getBones(Map<String, Matrix4d> map, float frame, Collection<? extends Bone<?>> bones);
	
	/**
	 * gets the animation's running length, or loop length for looping animations. This can be used to detect when an animation has completed.
	 * @return the animation's total length (use {@link Float#POSITIVE_INFINITY} for custom animations that have no specified length)
	 */
	public float getLength();
	
	public void reverseAnimation(IRigged<?, ?> rig);
	
	@Override
	public default void save(AbstractElement el)
	{
		save(el, 1);
	}
	
	public void save(AbstractElement el, float scale);
	
	@Override
	public default void load(AbstractElement el)
	{
		load(el, 1);
	}
	
	public void load(AbstractElement el, float scale);
	
	public boolean isRelative();
	
	public void setRelative(boolean relative);
} 