package firemerald.mcms.model;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.ITransformed;
import firemerald.mcms.texture.space.Material;

public interface IComponentParent extends IModelEditable, ITransformed
{
	public void updateTex();
	
	public List<ModelComponent> getChildrenComponents();
	
	public @Nullable Material getTexture();
}