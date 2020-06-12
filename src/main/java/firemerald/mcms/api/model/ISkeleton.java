package firemerald.mcms.api.model;

import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.data.AbstractElement;

public interface ISkeleton extends IRigged<ISkeleton>
{
	public Map<String, Matrix4d> getInverseTransforms();

	@Override
	public default ISkeleton getSkeleton()
	{
		return this;
	}
	
	@Override
	public default String getElementName()
	{
		return "skeleton";
	}
	
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
}