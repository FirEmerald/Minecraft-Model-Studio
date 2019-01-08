package firemerald.mcms.api.model;

import org.lwjgl.opengl.GL11;

import firemerald.mcms.api.animation.Transformation;

public class RenderObjectDisplayList extends Bone
{
	public final int list;
	
	public RenderObjectDisplayList(String name, Transformation defaultTransform, int list)
	{
		super(name, defaultTransform);
		this.list = list;
	}
	
	public RenderObjectDisplayList(String name, Transformation defaultTransform, Bone parent, int list)
	{
		super(name, defaultTransform, parent);
		this.list = list;
	}

	@Override
	public void doRender()
	{
		GL11.glCallList(list);
	}

	@Override
	public void doCleanUp()
	{
		GL11.glDeleteLists(list, 1);
	}
}