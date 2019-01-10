package firemerald.mcms.api.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.data.W3CElement;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Quaternion;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.util.FileUtil;

public class Skeleton
{
	public final List<Bone> base = new ArrayList<>();
	public final Map<String, Bone> bones = new HashMap<>();
	public final Map<String, Matrix4> inverts = new HashMap<>();
	
	public Skeleton() {}
	
	public Skeleton(List<Bone> base)
	{
		setBase(base);
	}
	
	public void setBase(List<Bone> bones)
	{
		base.clear();
		this.bones.clear();
		inverts.clear();
		for (Bone bone : bones)
		{
			base.add(bone);
			addBones(bone, new Matrix4());
		}
	}
	
	public void addBones(Bone bone, Matrix4 currentTransform)
	{
		bones.put(bone.name, bone);
		Matrix4 mat = new Matrix4(currentTransform);
		Vec3 v = bone.defaultTransform.translation;
		mat.translate(v);
		mat.mul(bone.defaultTransform.rotation.getMatrix4());
		inverts.put(bone.name, mat.invert(new Matrix4()));
		for (Bone child : bone.children) addBones(child, mat);
	}
	/*
	public static Skeleton tryLoadXML(ResourceLocation file)
	{
		InputStream in = null;
		Skeleton skel = null;
		try
		{
			skel = new Skeleton(in = Minecraft.getMinecraft().getResourceManager().getResource(file).getInputStream());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (in != null) try
		{
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return skel;
	}
	*/
	public static Skeleton tryLoadXML(File file)
	{
		Skeleton skel = null;
		try
		{
			skel = new Skeleton(file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return skel;
	}
	
	public Skeleton(File file) throws SAXException, IOException
	{
		List<Bone> base = iterate(FileUtil.readFile(file), null);
		setBase(base);
	}
	
	public static Skeleton tryLoadXML(InputStream in)
	{
		Skeleton skel = null;
		try
		{
			skel = new Skeleton(in);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return skel;
	}
	
	public Skeleton(InputStream in) throws SAXException, IOException
	{
		Document doc = FileUtil.readXML(in);
		List<Bone> base = iterate(new W3CElement(doc), null);
		setBase(base);
	}
	
	private static List<Bone> iterate(AbstractElement element, Bone parent)
	{
		List<Bone> bones = new ArrayList<Bone>();
		for (AbstractElement e : element.getChildren()) if (e.getName().equals("bone"))
		{
			try
			{
				String name = e.getString("boneName");
				float x, y, z;
				double qX, qY, qZ, qW;
				x = e.getFloat("x", 0);
				y = e.getFloat("y", 0);
				z = e.getFloat("z", 0);
				qX = e.getDouble("qX", 0);
				qY = e.getDouble("qY", 0);
				qZ = e.getDouble("qZ", 0);
				qW = e.getDouble("qW", 1);
				Transformation transform = new Transformation(new Quaternion(qX, qY, qZ, qW), new Vec3(x, y, z));
				Bone bone = new Bone(name, transform, parent);
				iterate(e, bone);
				bones.add(bone);
			}
			catch (Exception e1)
			{
				new Exception("Missing bone name", e1).printStackTrace();
			}
		}
		return bones;
	}
}