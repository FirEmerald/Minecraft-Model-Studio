package firemerald.mcms.api.model;

import java.util.Map;

import org.joml.Matrix4d;

public interface IRenderBone<T extends IRenderBone<T>>
{
	public void render(IModelHolder holder, Map<String, Matrix4d> transformations, Matrix4d parentTransform, Runnable defaultTexture);
}