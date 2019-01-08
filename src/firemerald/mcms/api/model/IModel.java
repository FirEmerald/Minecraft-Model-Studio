package firemerald.mcms.api.model;

import java.util.Map;

import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.data.Element;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.model.IEditableParent;

public interface IModel extends IRaytraceTarget, IEditableParent
{
	public Map<String, Matrix4> getPose(AnimationState... anims);
	
	public void render(Map<String, Matrix4> pos);
	
	public void cleanUp();
	
	public RaytraceResult rayTrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4> map);
	
	public boolean isNameUsed(String name);
	
	public void updateBonesList();
	
	public void loadFromXML(Element root);
}