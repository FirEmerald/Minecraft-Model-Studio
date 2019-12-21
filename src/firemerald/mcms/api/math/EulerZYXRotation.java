package firemerald.mcms.api.math;

import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class EulerZYXRotation extends EulerRotation
{
	public EulerZYXRotation() {}
	
	public EulerZYXRotation(Vector3d vec)
	{
		super(vec);
	}
	
	@Override
	public void setFromQuaternion(Quaterniond q)
	{
		q.get(new Matrix3d()).getEulerAnglesZYX(vec).mul(MathUtils.RAD_TO_DEG);
	}

	@Override
	public Quaterniond getQuaternion()
	{
		return new Quaterniond().rotateZ(vec.z() * MathUtils.DEG_TO_RAD).rotateY(vec.y() * MathUtils.DEG_TO_RAD).rotateX(vec.x() * MathUtils.DEG_TO_RAD);
	}

	@Override
	public IRotation copy()
	{
		return new EulerZYXRotation(vec);
	}
	
	@Override
	public String toString()
	{
		return "Euler ZYX " + vec.toString();
	}
}