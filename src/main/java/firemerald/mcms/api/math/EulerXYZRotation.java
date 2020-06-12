package firemerald.mcms.api.math;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public class EulerXYZRotation extends EulerRotation
{
	public EulerXYZRotation() {}
	
	public EulerXYZRotation(Vector3d vec)
	{
		super(vec);
	}
	
	@Override
	public void setFromQuaternion(Quaterniond q)
	{
		q.getEulerAnglesXYZ(vec).mul(MathUtils.RAD_TO_DEG);
	}

	@Override
	public Quaterniond getQuaternion()
	{
		return new Quaterniond().rotateXYZ(vec.x() * MathUtils.DEG_TO_RAD, vec.y() * MathUtils.DEG_TO_RAD, vec.z() * MathUtils.DEG_TO_RAD);
	}

	@Override
	public IRotation copy()
	{
		return new EulerXYZRotation(vec);
	}
	
	@Override
	public String toString()
	{
		return "Euler XYZ " + vec.toString();
	}
}