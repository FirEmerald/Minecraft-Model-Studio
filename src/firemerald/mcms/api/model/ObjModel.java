package firemerald.mcms.api.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.MultiModel;
import firemerald.mcms.api.model.Skeleton;

public abstract class ObjModel extends MultiModel
{
	public final String modelFile;
	
	public ObjModel(File modelFile, Skeleton skeleton) throws Exception
	{
		super();
		this.modelFile = modelFile.toString();
		initWithClose(new FileInputStream(modelFile), skeleton);
	}
	/*
	public ObjModel(ResourceLocation modelFile, Skeleton skeleton) throws Exception
	{
		super();
		this.modelFile = modelFile.toString();
		initWithClose(Minecraft.getMinecraft().getResourceManager().getResource(modelFile).getInputStream(), skeleton);
	}
	*/
	public ObjModel(InputStream model, String modelName, Skeleton skeleton) throws Exception
	{
		super();
		this.modelFile = modelName;
		init(model, skeleton);
	}
	
	protected void initWithClose(InputStream in, Skeleton skeleton) throws Exception
	{
		Exception ex = null;
		try
		{
			init(in, skeleton);
		}
		catch (Exception e)
		{
			ex = e;
		}
		try
		{
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if (ex != null) throw ex;
	}
	
	protected void init(InputStream in, Skeleton skeleton) throws Exception
	{
		ObjData data = new ObjData(in);
		List<Bone> base = new ArrayList<>();
		List<String> unReg = new ArrayList<>();
		unReg.addAll(data.groupObjects.keySet());
		iterate(skeleton.base, data, unReg, base, null, skeleton);
		for (String name : unReg) base.add(makeObj(name, Transformation.NONE, null, data.groupObjects.get(name), data, skeleton));
		setBase(base);
	}
	
	private void iterate(List<Bone> bones, ObjData data, List<String> unReg, List<Bone> base, Bone parent, Skeleton skeleton)
	{
		for (Bone bone : bones)
		{
			List<int[][]> mesh = data.groupObjects.get(bone.name);
			Bone obj;
			if (mesh != null)
			{
				unReg.remove(bone.name);
				obj = makeObj(bone.name, bone.defaultTransform, parent, mesh, data, skeleton);
				if (base != null) base.add(obj);
			}
			else
			{
				obj = new Bone(bone.name, bone.defaultTransform, parent);
				if (base != null) base.add(obj);
			}
			iterate(bone.children, data, unReg, null, obj, skeleton);
		}
	}
	
	protected abstract Bone makeObj(String name, Transformation transform, Bone parent, List<int[][]> mesh, ObjData obj, Skeleton skeleton);
}