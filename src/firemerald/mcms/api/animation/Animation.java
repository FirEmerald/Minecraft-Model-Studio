package firemerald.mcms.api.animation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.API;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.ITransformsProvider;
import firemerald.mcms.gui.main.components.animation.ComponentKeyFrame;
import firemerald.mcms.util.GuiUpdate;

import java.util.Map.Entry;

public class Animation implements IAnimation
{
	public final Map<String, NavigableMap<Float, Transformation>> animation;
	protected float length;
	public boolean loop;
	protected boolean relative;
	
	public Animation(float length, boolean loop, boolean relative)
	{
		this(new HashMap<>(), length, loop, relative);
	}
	
	public Animation(Map<String, NavigableMap<Float, Transformation>> animation, float length, boolean loop, boolean relative)
	{
		this.animation = animation;
		this.length = length;
		this.loop = loop;
		this.relative = relative;
	}
	
	public Animation(Map<String, NavigableMap<Float, Transformation>> animation, float length)
	{
		this(animation, length, true, false);
	}
	
	public Animation(AbstractElement el)
	{
		this(new HashMap<>(), 1);
		load(el);
	}
	
	@Override
	public float getLength()
	{
		return length;
	}
	
	public void setLength(float length, Collection<Bone> bones)
	{
		this.length = length;
		animation.forEach((name, anim) -> {
			Transformation def;
			if (relative)
			{
				def = new Transformation();
			}
			else
			{
				Bone bone = null;
				for (Bone b : bones) if (b.name.equals(name))
				{
					bone = b;
					break;
				}
				def = bone == null ? new Transformation() : bone.defaultTransform;
			}
			Transformation tran = anim.get(length);
			Transformation res;
			if (tran != null) //exact keyframe
			{
				res = tran.copy();
			}
			else
			{
				Entry<Float, Transformation> lower = anim.lowerEntry(length);
				Entry<Float, Transformation> higher = anim.higherEntry(length);
				if (lower == null)
				{
					if (higher == null) //no animation at all
					{
						return;
					}
					else //assume start to be the default
					{
						Transformation lowerTrans = def;
						float higherFrame = higher.getKey();
						Transformation higherTrans = higher.getValue();
						float part = length / higherFrame;
						res = Transformation.tween(lowerTrans, higherTrans, part);
					}
				}
				else
				{
					if (higher == null)
					{
						res = lower.getValue().copy();
						/*
						if (!loop) //end of animation
						{
							Transformation trans = lower.getValue();
							q = trans.rotation.getQuaternion();
							vec = trans.translation;
						}
						else
						{
							Transformation higherTrans = anim.get(0f); //interpolate between lower and start
							if (higherTrans == null) higherTrans = def;
							float lowerFrame = lower.getKey();
							Transformation lowerTrans = lower.getValue();
							float part = (length - lowerFrame) / (length - lowerFrame);
							q = lowerTrans.rotation.getQuaternion().slerp(higherTrans.rotation.getQuaternion(), part);
							Vector3f last = lowerTrans.translation, next = higherTrans.translation;
							vec = new Vector3f(last.x() + (next.x() - last.x()) * part, last.y() + (next.y() - last.y()) * part, last.z() + (next.z() - last.z()) * part);
						}
						*/
					}
					else //interpolate between frames
					{
						float lowerFrame = lower.getKey();
						Transformation lowerTrans = lower.getValue();
						float higherFrame = higher.getKey();
						Transformation higherTrans = higher.getValue();
						float part = (length - lowerFrame) / (higherFrame - lowerFrame);
						res = Transformation.tween(lowerTrans, higherTrans, part);
					}
				}
			}
			anim.put(length, res);
			Float key;
			while ((key = anim.higherKey(length)) != null) anim.remove(key); //remove all higher keys
		});
	}
	
	public void scaleTo(float length)
	{
		float mul = length / this.length;
		this.length = length;
		this.animation.forEach((bone, anim) -> {
			NavigableMap<Float, Transformation> newMap = new TreeMap<>();
			animation.put(bone, newMap);
			anim.forEach((time, transform) -> newMap.put(time * mul, transform));
		});
	}
	
	@Override
	public Map<String, Matrix4d> getBones(Map<String, Matrix4d> map, float frame, Collection<Bone> bones)
	{
		if (frame > length && loop) frame %= length;
		for (Bone bone : bones)
		{
			NavigableMap<Float, Transformation> anim = animation.get(bone.name);
			if (anim != null)
			{
				Transformation tran = anim.get(frame);
				Transformation res;
				if (tran != null)
				{
					res = tran.copy();
				}
				else
				{
					Transformation def = relative ? new Transformation() : bone.defaultTransform;
					Entry<Float, Transformation> lower = anim.lowerEntry(frame);
					Entry<Float, Transformation> higher = anim.higherEntry(frame);
					if (lower == null)
					{
						if (higher == null) //no animation at all
						{
							break;
						}
						else //assume start to be the default
						{
							Transformation lowerTrans = def;
							float higherFrame = higher.getKey();
							Transformation higherTrans = higher.getValue();
							float part = frame / higherFrame;
							res = Transformation.tween(lowerTrans, higherTrans, part);
						}
					}
					else
					{
						if (higher == null)
						{
							res = lower.getValue().copy();
							/*
							if (!loop) //end of animation
							{
								Transformation trans = lower.getValue();
								q = trans.rotation.getQuaternion();
								vec = trans.translation;
							}
							else
							{
								Transformation higherTrans = anim.get(0f); //interpolate between lower and start
								if (higherTrans == null) higherTrans = bone.defaultTransform;
								float lowerFrame = lower.getKey();
								Transformation lowerTrans = lower.getValue();
								float part = (frame - lowerFrame) / (length - lowerFrame);
								q = lowerTrans.rotation.getQuaternion().slerp(higherTrans.rotation.getQuaternion(), part);
								Vector3f last = lowerTrans.translation, next = higherTrans.translation;
								vec = new Vector3f(last.x() + (next.x() - last.x()) * part, last.y() + (next.y() - last.y()) * part, last.z() + (next.z() - last.z()) * part);
							}
							*/
						}
						else //interpolate between frames
						{
							float lowerFrame = lower.getKey();
							Transformation lowerTrans = lower.getValue();
							float higherFrame = higher.getKey();
							Transformation higherTrans = higher.getValue();
							float part = (frame - lowerFrame) / (higherFrame - lowerFrame);
							res = Transformation.tween(lowerTrans, higherTrans, part);
						}
					}
				}
				Matrix4d mat = new Matrix4d().translate(res.translation);
				mat.mul(res.rotation.getQuaternion().get(new Matrix4d()));
				if (relative) map.get(bone.name).mul(mat);
				else map.put(bone.name, mat);
			}
		}
		return map;
	}

	@Override
	public void load(AbstractElement root)
	{
		length = root.getFloat("length", 1);
		loop = root.getBoolean("loop", true);
		relative = root.getBoolean("relative", false);
		animation.clear();
		for (AbstractElement frameEl : root.getChildren()) if (frameEl.getName().equals("frame"))
		{
			try
			{
				float time = frameEl.getFloat("frameTime", 0);
				for (AbstractElement el : frameEl.getChildren()) if (el.getName().equals("bone"))
				{
					try
					{
						String boneName = el.getString("boneName");
						NavigableMap<Float, Transformation> boneMap = animation.get(boneName);
						if (boneMap == null) animation.put(boneName, boneMap = new TreeMap<>());
						boneMap.put(time, new Transformation(el));
					}
					catch (Exception e)
					{
						API.LOGGER.log(Level.WARN, "Couldn't parse bone", e);
					}
				}
			}
			catch (NumberFormatException e)
			{
				API.LOGGER.log(Level.WARN, "Couldn't parse frame time", e);
			}
		}
	}

	@Override
	public void save(AbstractElement root)
	{
		NavigableMap<Float, Map<String, Transformation>> map = new TreeMap<>();
		for (Entry<String, NavigableMap<Float, Transformation>> entry : this.animation.entrySet())
		{
			String bone = entry.getKey();
			for (Entry<Float, Transformation> entry2 : entry.getValue().entrySet())
			{
				Float time = entry2.getKey();
				Transformation trans = entry2.getValue();
				Map<String, Transformation> map2 = map.get(time);
				if (map2 == null) map.put(time, map2 = new HashMap<>());
				map2.put(bone, trans);
			}
		}
		root.setDouble("length", length);
		root.setBoolean("loop", loop);
		root.setBoolean("relative", relative);
		for (Entry<Float, Map<String, Transformation>> entry : map.entrySet())
		{
			AbstractElement frameEl = root.addChild("frame");
			frameEl.setFloat("frameTime", entry.getKey());
			for (Entry<String, Transformation> entry2 : entry.getValue().entrySet())
			{
				AbstractElement el = frameEl.addChild("bone");
				el.setString("boneName", entry2.getKey());
				Transformation t = entry2.getValue();
				t.save(el);
			}
		}
	}

	@Override
	public String getElementName()
	{
		return "animation";
	}
	
	public boolean isRelative()
	{
		return relative;
	}
	
	public void setRelative(boolean relative)
	{
		if (relative != this.relative)
		{
			this.relative = relative;
			Main main = Main.instance;
			Project project = main.project;
			final ITransformsProvider transforms;
			if (project.useBackingSkeleton()) transforms = project.getSkeleton();
			else
			{
				if (project.getRig() != null) transforms = project.getRig();
				else transforms = ITransformsProvider.NONE;
			}
			animation.forEach((name, map) -> {
				Transformation orig = transforms.get(name);
				final Matrix4d mul = orig.getTransformation();
				if (relative) mul.invert();
				map.values().forEach(transform -> {
					Matrix4d cur = transform.getTransformation();
					cur = mul.mul(cur, cur);
					transform.setFromMatrix(cur);
				});
			});
			main.gui.onGuiUpdate(GuiUpdate.ANIMATION);
			if (main.getEditing() instanceof ComponentKeyFrame) main.setEditing(main.getEditing());
		}
	}
	
	@Override
	public Animation cloneObject()
	{
		Map<String, NavigableMap<Float, Transformation>> newAnimation = new HashMap<>();
		this.animation.forEach((bone, anim) -> {
			NavigableMap<Float, Transformation> newAnim = new TreeMap<>();
			newAnimation.put(bone, newAnim);
			anim.forEach((time, transform) -> {
				newAnim.put(time, transform.copy());
			});
		});
		return new Animation(newAnimation, this.length, this.loop, this.relative);
	}

	@Override
	public void reverseAnimation(IRigged<?> rig)
	{
		this.animation.forEach((name, anim) -> {
			if (!anim.isEmpty())
			{
				NavigableMap<Float, Transformation> newAnim = new TreeMap<>();
				anim.forEach((time, transform) -> {
					newAnim.put(length - time, transform.copy());
				});
				if (rig != null)
				{
					Bone bone = rig.getBone(name);
					if (bone != null)
					{
						Transformation def = this.relative ? new Transformation() : bone.defaultTransform;
						if (anim.get(length) == null) //lacks final keyframe
						{
							Transformation current = anim.lowerEntry(length).getValue();
							if (!current.equals(def)) //needs initial set
							{
								newAnim.put(0f, current.copy());
							}
						}
						if (anim.get(0f) == null) //lacks initial keyframe
						{
							Transformation current = anim.higherEntry(0f).getValue();
							if (!current.equals(def)) //needs final set
							{
								newAnim.put(length, def.copy());
							}
						}
						Float cur = -1f;
						Entry<Float, Transformation> entry;
						while ((entry = newAnim.higherEntry(cur)) != null && entry.getValue().equals(def)) newAnim.remove(cur = entry.getKey()); //remove redundant starting frames
					}
				}
				if (!newAnim.isEmpty())
				{
					Entry<Float, Transformation> lastEntry = newAnim.lastEntry();
					Float cur = lastEntry.getKey();
					Entry<Float, Transformation> entry;
					while ((entry = newAnim.lowerEntry(cur)) != null && entry.getValue().equals(lastEntry.getValue())) //remove redundant ending frames
					{
						newAnim.remove(cur);
						cur = (lastEntry = entry).getKey();
					}
				}
				anim.clear();
				anim.putAll(newAnim);
			}
		});
	}
}