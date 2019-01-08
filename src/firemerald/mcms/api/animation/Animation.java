package firemerald.mcms.api.animation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import firemerald.mcms.api.data.Element;
import firemerald.mcms.api.data.W3CElement;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Quaternion;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.util.DataUtil;

import java.util.Map.Entry;

public class Animation implements IAnimation
{
	public final Map<String, NavigableMap<Float, Transformation>> animation;
	public final float length;
	public final boolean loop;
	
	public Animation(Map<String, NavigableMap<Float, Transformation>> animation, float length, boolean loop)
	{
		this.animation = animation;
		this.length = length;
		this.loop = loop;
	}
	
	public Animation(Map<String, NavigableMap<Float, Transformation>> animation, float length)
	{
		this(animation, length, false);
	}
	
	@Override
	public Map<String, Matrix4> getBones(Map<String, Matrix4> map, float frame, List<Bone> bones)
	{
		if (frame > length)
		{
			if (loop) frame %= length;
			else return map;
		}
		for (Bone bone : bones)
		{
			Transformation def = Transformation.NONE;
			NavigableMap<Float, Transformation> anim = animation.get(bone.name);
			if (anim != null)
			{
				Quaternion q;
				Vec3 vec;
				Transformation tran = anim.get(frame);
				if (tran != null)
				{
					q = tran.rotation;
					vec = tran.translation;
				}
				else
				{
					Entry<Float, Transformation> lower = anim.lowerEntry(frame);
					Entry<Float, Transformation> higher = anim.higherEntry(frame);
					if (lower == null)
					{
						if (higher == null) //no animation at all
						{
							q = def.rotation;
							vec = def.translation;
						}
						else //assume start to be the default
						{
							Transformation lowerTrans = def;
							float higherFrame = higher.getKey();
							Transformation higherTrans = higher.getValue();
							float part = frame / higherFrame;
							q = Quaternion.slerp(lowerTrans.rotation, higherTrans.rotation, part);
							Vec3 last = lowerTrans.translation, next = higherTrans.translation;
							vec = new Vec3(last.x() + (next.x() - last.x()) * part, last.y() + (next.y() - last.y()) * part, last.z() + (next.z() - last.z()) * part);
						}
					}
					else
					{
						if (higher == null)
						{
							if (!loop) //end of animation
							{
								Transformation trans = lower.getValue();
								q = trans.rotation;
								vec = trans.translation;
							}
							else
							{
								Transformation higherTrans = anim.get(0f); //interpolate between lower and start
								if (higherTrans == null) higherTrans = def;
								float lowerFrame = lower.getKey();
								Transformation lowerTrans = lower.getValue();
								float part = (frame - lowerFrame) / (length - lowerFrame);
								q = Quaternion.slerp(lowerTrans.rotation, higherTrans.rotation, part);
								Vec3 last = lowerTrans.translation, next = higherTrans.translation;
								vec = new Vec3(last.x() + (next.x() - last.x()) * part, last.y() + (next.y() - last.y()) * part, last.z() + (next.z() - last.z()) * part);
							}
						}
						else //interpolate between frames
						{
							float lowerFrame = lower.getKey();
							Transformation lowerTrans = lower.getValue();
							float higherFrame = higher.getKey();
							Transformation higherTrans = higher.getValue();
							float part = (frame - lowerFrame) / (higherFrame - lowerFrame);
							q = Quaternion.slerp(lowerTrans.rotation, higherTrans.rotation, part);
							Vec3 last = lowerTrans.translation, next = higherTrans.translation;
							vec = new Vec3(last.x() + (next.x() - last.x()) * part, last.y() + (next.y() - last.y()) * part, last.z() + (next.z() - last.z()) * part);
						}
					}
				}
				Matrix4 mat = new Matrix4().translate(vec);
				mat.mul(q.getMatrix4());
				map.get(bone.name).mul(mat);
			}
		}
		return map;
	}
	
	public static Animation loadAnim(File file)
	{
		try
		{
			Element root = DataUtil.readFile(file);
			float length = root.getFloat("length");
			boolean loop = root.getBoolean("loop", false);
			Map<String, NavigableMap<Float, Transformation>> anim = new HashMap<>();
			for (Element frameEl : root.getChildren()) if (frameEl.getName().equals("frame"))
			{
				try
				{
					float time = frameEl.getFloat("frameTime");
					for (Element el : frameEl.getChildren()) if (el.getName().equals("bone"))
					{
						try
						{
							String boneName = el.getString("boneName");
							float x = el.getFloat("x");
							float y = el.getFloat("y");
							float z = el.getFloat("z");
							double qX = el.getDouble("qZ");
							double qY = el.getDouble("qY");
							double qZ = el.getDouble("qZ");
							double qW = el.getDouble("qW");
							NavigableMap<Float, Transformation> boneMap = anim.get(boneName);
							if (boneMap == null) anim.put(boneName, boneMap = new TreeMap<>());
							boneMap.put(time, new Transformation(new Quaternion(qX, qY, qZ, qW), new Vec3(x, y, z)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			return new Animation(anim, length, loop);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void writeToXML(Element root)
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
		root.setFloat("length", length);
		root.setBoolean("loop", loop);
		for (Entry<Float, Map<String, Transformation>> entry : map.entrySet())
		{
			Element frameEl = root.addChild("frame");
			frameEl.setFloat("frameTime", entry.getKey());
			for (Entry<String, Transformation> entry2 : entry.getValue().entrySet())
			{
				Element el = frameEl.addChild("bone");
				el.setString("boneName", entry2.getKey());
				Transformation t = entry2.getValue();
				Vec3 vec = t.translation;
				Quaternion q = t.rotation;
				el.setFloat("x", vec.x());
				el.setFloat("y", vec.y());
				el.setFloat("z", vec.z());
				el.setDouble("qX", q.x());
				el.setDouble("qY", q.y());
				el.setDouble("qZ", q.z());
				el.setDouble("qW", q.w());
			}
		}
	}
	
	public void saveToDocument(File toSave)
	{
		Document doc = DataUtil.createXML();
		org.w3c.dom.Element rootEl = doc.createElement("animation");
		doc.appendChild(rootEl);
		Element root = new W3CElement(rootEl);
		writeToXML(root);
		try
		{
			DataUtil.saveXML(doc, toSave);
		}
		catch (IOException | TransformerException e)
		{
			e.printStackTrace();
		}
	}
}