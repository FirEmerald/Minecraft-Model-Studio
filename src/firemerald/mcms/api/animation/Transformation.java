package firemerald.mcms.api.animation;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Quaternion;
import firemerald.mcms.api.math.Vec3;

public class Transformation
{
	public Quaternion rotation;
	public Vec3 translation;
	public double rX, rY, rZ;
	
	public static final Transformation NONE = new Transformation();
	
	public static enum SaveType
	{
		NONE(false, false, false),
		TRANSLATION(true, false, false),
		EULER_XZY(false, true, false),
		TRANSLATION_EULER_XZY(true, true, false),
		QUATERNION(false, false, true),
		TRANSLATION_QUATERNION(true, false, true),
		EULER_XZY_QUATERNION(false, true, true),
		FULL(true, true, true);
		
		public final boolean hasTranslation, hasEuler, hasQuaternion;
		
		SaveType(boolean hasTranslation, boolean hasEuler, boolean hasQuaternion)
		{
			this.hasTranslation = hasTranslation;
			this.hasEuler = hasEuler;
			this.hasQuaternion = hasQuaternion;
		}
		
		public static SaveType getType(boolean hasTranslation, boolean hasEuler, boolean hasQuaternion)
		{
			if (hasTranslation)
			{
				if (hasEuler)
				{
					if (hasQuaternion) return FULL;
					else return TRANSLATION_EULER_XZY;
				}
				else
				{
					if (hasQuaternion) return TRANSLATION_QUATERNION;
					else return TRANSLATION;
				}
			}
			else
			{
				if (hasEuler)
				{
					if (hasQuaternion) return EULER_XZY_QUATERNION;
					else return EULER_XZY;
				}
				else
				{
					if (hasQuaternion) return QUATERNION;
					else return NONE;
				}
			}
		}
	}
	
	public static Transformation getFromChild(AbstractElement el, String name)
	{
		for (AbstractElement child : el.getChildren()) if (child.getName().equals(name)) return new Transformation(child);
		return new Transformation();
	}
	
	public Transformation(AbstractElement el)
	{
		SaveType type = el.getEnum("type", SaveType.values(), SaveType.NONE);
		if (type.hasTranslation) loadTranslation(el);
		if (type.hasEuler)
		{
			loadEuler(el);
			if (type.hasQuaternion) loadQuaternion(el);
			else updateFromEuler();
		}
		else if (type.hasQuaternion)
		{
			loadQuaternion(el);
			Vec3 rot = rotation.toEulerXZY();
			rX = rot.x();
			rY = rot.y();
			rZ = rot.z();
		}
	}
	
	public void loadFromChild(AbstractElement el, String name)
	{
		for (AbstractElement child : el.getChildren()) if (child.getName().equals(name))
		{
			load(child);
			break;
		}
	}
	
	public void load(AbstractElement el)
	{
		SaveType type = el.getEnum("type", SaveType.values(), SaveType.NONE);
		if (type.hasTranslation) loadTranslation(el);
		else
		{
			translation.x(0);
			translation.y(0);
			translation.z(0);
		}
		if (type.hasEuler)
		{
			loadEuler(el);
			if (type.hasQuaternion) loadQuaternion(el);
			else updateFromEuler();
		}
		else if (type.hasQuaternion)
		{
			loadQuaternion(el);
			Vec3 rot = rotation.toEulerXZY();
			rX = rot.x();
			rY = rot.y();
			rZ = rot.z();
		}
		else
		{
			rX = rY = rZ = 0;
			rotation = new Quaternion();
		}
	}
	
	public void saveAsChild(AbstractElement el, String name, boolean hasTranslation, boolean hasEuler, boolean hasQuaternion)
	{
		AbstractElement child = el.addChild(name);
		save(child, hasTranslation, hasEuler, hasQuaternion);
	}
	
	public void save(AbstractElement el, boolean hasTranslation, boolean hasEuler, boolean hasQuaternion)
	{
		hasTranslation &= (translation.x() != 0 || translation.y() != 0 || translation.z() != 0);
		hasEuler &= (rX != 0 || rY != 0 || rZ != 0);
		hasQuaternion &= (rotation.x() != 0 || rotation.y() != 0 || rotation.z() != 0);
		SaveType type = SaveType.getType(hasTranslation, hasEuler, hasQuaternion);
		el.setEnum("type", type);
		if (hasTranslation) saveTranslation(el);
		if (hasEuler) saveEuler(el);
		if (hasQuaternion) saveQuaternion(el);
	}
	
	public void loadTranslation(AbstractElement el)
	{
		translation = new Vec3(el.getFloat("x", 0), el.getFloat("y", 0), el.getFloat("z", 0));
	}
	
	public void saveTranslation(AbstractElement el)
	{
		el.setFloat("x", translation.x());
		el.setFloat("y", translation.y());
		el.setFloat("z", translation.z());
	}
	
	public void loadEuler(AbstractElement el)
	{
		rX = el.getDouble("rX", 0);
		rY = el.getDouble("rY", 0);
		rZ = el.getDouble("rZ", 0);
	}
	
	public void saveEuler(AbstractElement el)
	{
		el.setDouble("rX", rX);
		el.setDouble("rY", rY);
		el.setDouble("rZ", rZ);
	}
	
	public void loadQuaternion(AbstractElement el)
	{
		rotation = new Quaternion(el.getDouble("qX", 0), el.getDouble("qY", 0), el.getDouble("qZ", 0), el.getDouble("qW", 1)).normalize();
	}
	
	public void saveQuaternion(AbstractElement el)
	{
		el.setDouble("qX", rotation.x());
		el.setDouble("qY", rotation.y());
		el.setDouble("qZ", rotation.z());
		el.setDouble("qW", rotation.w());
	}
	
	public Transformation(Transformation t)
	{
		translation = new Vec3(t.translation);
		rotation = new Quaternion(t.rotation);
		rX = t.rX;
		rY = t.rY;
		rZ = t.rZ;
	}
	
	public Transformation(Quaternion rotation, Vec3 translation)
	{
		this.translation = translation;
		setQuaternion(rotation.normalize());
	}
	
	public Transformation(Quaternion rotation)
	{
		this.rotation = rotation;
		setQuaternion(rotation.normalize());
	}
	
	public Transformation(Vec3 translation)
	{
		this.rotation = Quaternion.IDENTITY;
		this.translation = translation;
		rX = rY = rZ = 0;
	}
	
	public Transformation()
	{
		rotation = Quaternion.IDENTITY;
		translation = Vec3.ZERO;
		rX = rY = rZ = 0;
	}
	
	public void setQuaternion(Quaternion q)
	{
		this.rotation = q;
		Vec3 rot = q.toEulerXZY();
		rX = rot.x();
		rY = rot.y();
		rZ = rot.z();
	}
	
	public void updateFromEuler()
	{
		rotation = Quaternion.forEulerXZY(rX, rY, rZ);
	}
	
	public void setRX(double rX)
	{
		this.rX = rX;
		updateFromEuler();
	}
	
	public void setRY(double rY)
	{
		this.rY = rY;
		updateFromEuler();
	}
	
	public void setRZ(double rZ)
	{
		this.rZ = rZ;
		updateFromEuler();
	}
	
	public Matrix4 getTransformation()
	{
		return new Matrix4().translate(translation).mul(rotation.getMatrix4());
	}
	
	public void setFromMatrix(Matrix4 matrix)
	{
		this.rotation.setFromMatrix(matrix);
		setQuaternion(rotation);
		Matrix4 trans = matrix.matrix3().invert().matrix4();
		matrix.mul(trans, trans);
		this.translation = new Vec3(trans.m30(), trans.m31(), trans.m32());
	}
}