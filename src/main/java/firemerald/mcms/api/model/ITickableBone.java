package firemerald.mcms.api.model;

import java.util.Map;

import org.joml.Matrix4d;

public interface ITickableBone<T extends ITickableBone<T>>
{
	public void tick(IModelHolder holder, Map<String, Matrix4d> transformations, Matrix4d parentTransform, float deltaTime);
}