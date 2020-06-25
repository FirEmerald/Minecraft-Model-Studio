package firemerald.mcms.api.animation;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.EulerXYZRotation;
import firemerald.mcms.api.math.EulerZYXRotation;
import firemerald.mcms.api.math.IRotation;
import firemerald.mcms.api.math.QuaternionRotation;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.util.IEditable;
import firemerald.mcms.util.history.IHistoryAction;

public class Transformation
{
	public IRotation rotation = IRotation.NONE;
	public final Vector3f translation = new Vector3f();
	
	public static Transformation getFromChild(AbstractElement el, String name, float scale)
	{
		for (AbstractElement child : el.getChildren()) if (child.getName().equals(name)) return new Transformation(child, scale);
		return new Transformation();
	}
	
	public Transformation(AbstractElement el, float scale)
	{
		load(el, scale);
	}
	
	public void set(Transformation trans)
	{
		this.translation.set(trans.translation);
		this.rotation = trans.rotation.copy();
	}
	/*
	public void loadFromChild(AbstractElement el, String name)
	{
		for (AbstractElement child : el.getChildren()) if (child.getName().equals(name))
		{
			load(child);
			break;
		}
	}
	*/

	public void load(AbstractElement el, float scale)
	{
		translation.set(el.getFloat("tX", 0) * scale, el.getFloat("tY", 0) * scale, el.getFloat("tZ", 0) * scale);
		String rotationType = el.getString("rotation", null);
		if (rotationType != null) switch (rotationType)
		{
		case "quaternion":
			rotation = new QuaternionRotation();
			break;
		case "euler_xyz":
			rotation = new EulerXYZRotation();
			break;
		case "euler_zyx":
			rotation = new EulerZYXRotation();
			break;
		default:
			rotation = IRotation.NONE;
			GuiPopupException.onException("Could not load unknown rotation type " + rotationType);
			break;
		}
		else rotation = IRotation.NONE;
		rotation.load(el);
	}
	/*
	public void saveAsChild(AbstractElement el, String name)
	{
		AbstractElement child = el.addChild(name);
		save(child);
	}
	*/
	public void save(AbstractElement el, float scale)
	{
		if (translation.x() != 0) el.setFloat("tX", translation.x() * scale);
		if (translation.y() != 0) el.setFloat("tY", translation.y() * scale);
		if (translation.z() != 0) el.setFloat("tZ", translation.z() * scale);
		IRotation saveRot = rotation;
		if (!(rotation == IRotation.NONE || rotation.getQuaternion().normalize().w() == 1.0))
		{
			if (rotation instanceof QuaternionRotation) el.setString("rotation", "quaternion");
			else if (rotation instanceof EulerXYZRotation) el.setString("rotation", "euler_xyz");
			else if (rotation instanceof EulerZYXRotation) el.setString("rotation", "euler_zyx");
			else
			{
				saveRot = new QuaternionRotation(rotation.getQuaternion());
				el.setString("rotation", "quaternion");
				GuiPopupException.onException("Could not save unknown rotation type " + rotation.getClass().toString(), " saving it as a quaternion.");
			}
		}
		saveRot.save(el);
	}
	
	public Transformation(Transformation t)
	{
		translation.set(t.translation);
		rotation = t.rotation.copy();
	}
	
	public Transformation(Quaterniond rotation, Vector3f translation)
	{
		this.translation.set(translation);
		this.rotation = new QuaternionRotation(rotation);
	}
	
	public Transformation(Quaterniond rotation)
	{
		this.rotation = new QuaternionRotation(rotation);
	}
	
	public Transformation(Vector3f translation)
	{
		this.translation.set(translation);
	}
	
	public Transformation() {}
	
	public void setQuaternion(Quaterniond q)
	{
		if (q.x == 0 && q.y == 0 && q.z == 0) this.rotation = IRotation.NONE; //set to null
		else if (this.rotation == IRotation.NONE) this.rotation = new QuaternionRotation(q); //set to quaternion
		else this.rotation.setFromQuaternion(q); //set current rotation values
	}
	
	public Matrix4d getTransformation()
	{
		return new Matrix4d().translate(translation).rotate(rotation.getQuaternion());
	}
	
	public void setFromMatrix(Matrix4d matrix)
	{
		setQuaternion(new Quaterniond().setFromUnnormalized(matrix));
		this.translation.set(matrix.getTranslation(new Vector3d()));
	}
	
	@Override
	public String toString()
	{
		return "translation: " + translation.toString() + ", rotation: " + rotation.toString();
	}
	
	public Transformation copy()
	{
		return new Transformation(this);
	}
	
	public static Transformation tween(Transformation a, Transformation b, float mix)
	{
		Quaterniond q = a.rotation.getQuaternion().slerp(b.rotation.getQuaternion(), mix);
		Vector3f vec = new Vector3f(a.translation.x() + (b.translation.x() - a.translation.x()) * mix, a.translation.y() + (b.translation.y() - a.translation.y()) * mix, a.translation.z() + (b.translation.z() - a.translation.z()) * mix);
		return new Transformation(q, vec);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Transformation)
		{
			Transformation t = (Transformation) o;
			return t.translation.equals(translation) && t.rotation.getQuaternion().equals(rotation.getQuaternion());
		}
		else return false;
	}
	
	public static class ChangeRotationAction implements IHistoryAction<ChangeRotationAction>
	{
		public final Transformation transform;
		public final IRotation rotation;
		public final ChangeRotationAction opposite;
		public final IEditable editable;
		
		public ChangeRotationAction(IEditable editable, Transformation transform, IRotation rotation)
		{
			this.editable = editable;
			this.transform = transform;
			this.rotation = rotation;
			this.opposite = new ChangeRotationAction(this);
		}
		
		private ChangeRotationAction(ChangeRotationAction opposite)
		{
			this.editable = opposite.editable;
			this.transform = opposite.transform;
			this.rotation = transform.rotation;
			this.opposite = opposite;
		}
		
		@Override
		public ChangeRotationAction perform()
		{
			if (Main.instance.getEditing() == editable) Main.instance.setEditing(null);
			this.transform.rotation = rotation;
			Main.instance.setEditing(editable);
			return opposite;
		}
	}
	
	public void setRotationTo(IEditable editable, IRotation rotation)
	{
		IRotation old = this.rotation;
		(this.rotation = rotation).setFromQuaternion(old.getQuaternion());
		Main.instance.project.onAction(new ChangeRotationAction(editable, this, old));
	}
}