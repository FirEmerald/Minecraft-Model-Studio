package firemerald.mcms.api.animation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.ITransformsProvider;
import firemerald.mcms.gui.main.components.animation.ComponentKeyFrame;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.util.GuiUpdate;

import java.util.Map.Entry;

public class Animation implements IAnimation
{
	public final Map<String, NavigableMap<Float, TweeningFrame>> animation;
	protected float length;
	public float loopStart;
	protected boolean relative;
	
	public Animation(float length, float loopStart, boolean relative)
	{
		this(new HashMap<>(), length, loopStart, relative);
	}
	
	public Animation(Map<String, NavigableMap<Float, TweeningFrame>> animation, float length, float loopStart, boolean relative)
	{
		this.animation = animation;
		this.length = length;
		this.loopStart = loopStart;
		this.relative = relative;
	}
	
	public Animation(Map<String, NavigableMap<Float, TweeningFrame>> animation, float length)
	{
		this(animation, length, 0, false);
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
	
	@Override
	public float getAnimTime(float timestamp)
	{
		if (timestamp < 0) return 0;
		else if (loopStart < 0) return timestamp > length ? length : timestamp;
		else return timestamp < loopStart ? timestamp : loopStart + ((timestamp - loopStart) % (length - loopStart));
	}
	
	public void setLength(float length, Collection<? extends Bone<?>> bones)
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
				Bone<?> bone = null;
				for (Bone<?> b : bones) if (b.getName().equals(name))
				{
					bone = b;
					break;
				}
				def = bone == null ? new Transformation() : bone.defaultTransform;
			}
			TweeningFrame tran = anim.get(length);
			TweeningFrame res;
			if (tran != null) //exact keyframe
			{
				res = new TweeningFrame(tran);
			}
			else
			{
				Entry<Float, TweeningFrame> lower = anim.lowerEntry(length);
				Entry<Float, TweeningFrame> higher = anim.higherEntry(length);
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
						TweeningFrame higherTrans = higher.getValue();
						float part = length / higherFrame;
						res = new TweeningFrame(Transformation.tween(lowerTrans, higherTrans.transformation, higherTrans.apply(part)), higherTrans.tweening, higherTrans.factor);
					}
				}
				else
				{
					if (higher == null)
					{
						res = new TweeningFrame(lower.getValue());
					}
					else //interpolate between frames
					{
						float lowerFrame = lower.getKey();
						Transformation lowerTrans = lower.getValue().transformation;
						float higherFrame = higher.getKey();
						TweeningFrame higherTrans = higher.getValue();
						float part = (length - lowerFrame) / (higherFrame - lowerFrame);
						res = new TweeningFrame(Transformation.tween(lowerTrans, higherTrans.transformation, higherTrans.apply(part)), higherTrans.tweening, higherTrans.factor);
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
			NavigableMap<Float, TweeningFrame> newMap = new TreeMap<>();
			animation.put(bone, newMap);
			anim.forEach((time, transform) -> newMap.put(time * mul, transform));
		});
	}
	
	@Override
	public Map<String, Matrix4d> getBones(Map<String, Matrix4d> map, float frame, Collection<? extends Bone<?>> bones)
	{
		frame = getAnimTime(frame);
		for (Bone<?> bone : bones)
		{
			NavigableMap<Float, TweeningFrame> anim = animation.get(bone.getName());
			if (anim != null)
			{
				TweeningFrame tran = anim.get(frame);
				Transformation res;
				if (tran != null)
				{
					res = tran.transformation.copy();
				}
				else
				{
					Transformation def = relative ? new Transformation() : bone.defaultTransform;
					Entry<Float, TweeningFrame> lower = anim.lowerEntry(frame);
					Entry<Float, TweeningFrame> higher = anim.higherEntry(frame);
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
							Transformation higherTrans = higher.getValue().transformation;
							float part = higher.getValue().apply(frame / higherFrame);
							res = Transformation.tween(lowerTrans, higherTrans, part);
						}
					}
					else
					{
						if (higher == null)
						{
							res = lower.getValue().transformation;
						}
						else //interpolate between frames
						{
							float lowerFrame = lower.getKey();
							Transformation lowerTrans = lower.getValue().transformation;
							float higherFrame = higher.getKey();
							Transformation higherTrans = higher.getValue().transformation;
							float part = higher.getValue().apply((frame - lowerFrame) / (higherFrame - lowerFrame));
							res = Transformation.tween(lowerTrans, higherTrans, part);
						}
					}
				}
				Matrix4d mat = res.getTransformation();
				//Matrix4d mat = new Matrix4d().scale(MathUtils.toVector3d(res.scaling)).translate(res.translation).mul(res.rotation.getQuaternion().get(new Matrix4d()));
				if (relative) map.get(bone.getName()).mul(mat);
				else map.put(bone.getName(), mat);
			}
		}
		return map;
	}

	@Override
	public void load(AbstractElement root, float scale)
	{
		length = root.getFloat("length", 1);
		if (root.hasAttribute("loopStart")) loopStart = root.getFloat("loopStart", -1);
		else loopStart = root.getBoolean("loop", true) ? 0 : -.05f;
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
						NavigableMap<Float, TweeningFrame> boneMap = animation.get(boneName);
						if (boneMap == null) animation.put(boneName, boneMap = new TreeMap<>());
						TweeningFrame t = new TweeningFrame(el, scale);
						boneMap.put(time, t);
					}
					catch (Exception e)
					{
						GuiPopupException.onException("Couldn't parse bone", e);
					}
				}
			}
			catch (NumberFormatException e)
			{
				GuiPopupException.onException("Couldn't parse frame time", e);
			}
		}
	}

	@Override
	public void save(AbstractElement root, float scale)
	{
		NavigableMap<Float, Map<String, TweeningFrame>> map = new TreeMap<>();
		for (Entry<String, NavigableMap<Float, TweeningFrame>> entry : this.animation.entrySet())
		{
			String bone = entry.getKey();
			for (Entry<Float, TweeningFrame> entry2 : entry.getValue().entrySet())
			{
				Float time = entry2.getKey();
				TweeningFrame trans = entry2.getValue();
				Map<String, TweeningFrame> map2 = map.get(time);
				if (map2 == null) map.put(time, map2 = new HashMap<>());
				map2.put(bone, trans);
			}
		}
		root.setDouble("length", length);
		root.setFloat("loopStart", loopStart);
		root.setBoolean("relative", relative);
		for (Entry<Float, Map<String, TweeningFrame>> entry : map.entrySet())
		{
			AbstractElement frameEl = root.addChild("frame");
			frameEl.setFloat("frameTime", entry.getKey());
			for (Entry<String, TweeningFrame> entry2 : entry.getValue().entrySet())
			{
				AbstractElement el = frameEl.addChild("bone");
				el.setString("boneName", entry2.getKey());
				TweeningFrame t = entry2.getValue();
				t.save(el, scale);
			}
		}
	}

	@Override
	public String getElementName()
	{
		return "animation";
	}
	
	@Override
	public boolean isRelative()
	{
		return relative;
	}
	
	@Override
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
					Matrix4d cur = transform.transformation.getTransformation();
					cur = mul.mul(cur, cur);
					transform.transformation.setFromMatrix(cur);
				});
			});
			main.onGuiUpdate(GuiUpdate.ANIMATION);
			if (main.getEditing() instanceof ComponentKeyFrame) main.setEditing(main.getEditing());
		}
	}
	
	@Override
	public Animation cloneObject()
	{
		Map<String, NavigableMap<Float, TweeningFrame>> newAnimation = new HashMap<>();
		this.animation.forEach((bone, anim) -> {
			NavigableMap<Float, TweeningFrame> newAnim = new TreeMap<>();
			newAnimation.put(bone, newAnim);
			anim.forEach((time, transform) -> {
				newAnim.put(time, new TweeningFrame(transform));
			});
		});
		return new Animation(newAnimation, this.length, this.loopStart, this.relative);
	}

	@Override
	public void reverseAnimation(IRigged<?, ?> rig)
	{
		this.animation.forEach((name, anim) -> {
			if (!anim.isEmpty())
			{
				NavigableMap<Float, TweeningFrame> newAnim = new TreeMap<>();
				Iterator<Entry<Float, TweeningFrame>> it = anim.entrySet().iterator();
				Entry<Float, TweeningFrame> cur, next = it.next();
				do
				{
					cur = next;
					TweeningFrame frame = new TweeningFrame(cur.getValue());
					if (it.hasNext())
					{
						next = it.next();
						frame.tweening = next.getValue().tweening.inverse();
						frame.factor = next.getValue().factor;
					}
					else break;
					newAnim.put(length - cur.getKey(), frame);
				}
				while (true);
				if (rig != null)
				{
					Bone<?> bone = rig.getBone(name);
					if (bone != null)
					{
						Transformation def = this.relative ? new Transformation() : bone.defaultTransform;
						if (anim.get(length) == null) //lacks final keyframe
						{
							Transformation current = anim.lowerEntry(length).getValue().transformation;
							if (!current.equals(def)) //needs initial set
							{
								newAnim.put(0f, new TweeningFrame(current.copy()));
							}
						}
						if (anim.get(0f) == null) //lacks initial keyframe
						{
							TweeningFrame current = anim.higherEntry(0f).getValue();
							if (!current.transformation.equals(def)) //needs final set
							{
								newAnim.put(length, new TweeningFrame(def.copy(), current.tweening, current.factor));
							}
						}
						Float curF = -1f;
						Entry<Float, TweeningFrame> entry;
						while ((entry = newAnim.higherEntry(curF)) != null && entry.getValue().transformation.equals(def)) newAnim.remove(curF = entry.getKey()); //remove redundant starting frames
					}
				}
				if (!newAnim.isEmpty())
				{
					Entry<Float, TweeningFrame> lastEntry = newAnim.lastEntry();
					Float curF = lastEntry.getKey();
					Entry<Float, TweeningFrame> entry;
					while ((entry = newAnim.lowerEntry(curF)) != null && entry.getValue().transformation.equals(lastEntry.getValue().transformation)) //remove redundant ending frames
					{
						newAnim.remove(curF);
						curF = (lastEntry = entry).getKey();
					}
				}
				anim.clear();
				anim.putAll(newAnim);
			}
		});
	}
}