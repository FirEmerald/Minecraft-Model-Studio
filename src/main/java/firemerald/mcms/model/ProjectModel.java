package firemerald.mcms.model;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.MultiModel;
import firemerald.mcms.model.RenderObjectComponents.Actual;

public class ProjectModel extends MultiModel<ProjectModel, Actual>
{
	public ProjectModel() {}
	
	public ProjectModel(AbstractElement el)
	{
		super(el);
	}
	
	public ProjectModel(List<Actual> base)
	{
		super(base);
	}
	
	@Override
	public Actual makeNew(String name, Transformation transformation, @Nullable Actual parent)
	{
		return new Actual(name, transformation, parent);
	}

	@Override
	public ProjectModel newModel(List<Actual> base)
	{
		return new ProjectModel(base);
	}
}