package firemerald.mcms.api.model;

import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.EulerZYXRotation;
import firemerald.mcms.api.math.IRotation;
import firemerald.mcms.api.math.QuaternionRotation;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditableParent;

public abstract class PosedBoneEffect extends BoneEffect
{
	public final Transformation transform;

	public PosedBoneEffect(String name, Bone parent, Transformation transform)
	{
		super(name, parent);
		this.transform = transform;
	}

	@Override
	public Transformation getDefaultTransformation()
	{
		return this.transform;
	}

	public Matrix4d getTransformation()
	{
		Matrix4d mat = transform.getTransformation();
		if (parent != null) parent.getTransformation().mul(mat, mat);
		return mat;
	}
	
	@Override
	public void loadFromXML(AbstractElement el, float scale)
	{
		super.loadFromXML(el, scale);
		transform.load(el, scale);
	}
	
	@Override
	public void addDataToXML(AbstractElement el, float scale)
	{
		super.addDataToXML(el, scale);
		transform.save(el, scale);
	}

	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent)
	{
		this.parent = oldParent instanceof Bone ? (Bone) oldParent : null;
		Matrix4d targetTransform = getTransformation();
		this.parent = newParent instanceof Bone ? (Bone) newParent : null;
		Matrix4d parentTransform = parent == null ? new Matrix4d() : parent.getTransformation();
		Matrix4d newTransform = parentTransform.invert().mul(targetTransform);
		this.transform.setFromMatrix(newTransform);
	}
	
	public float tX()
	{
		return transform.translation.x();
	}
	
	public void tX(float x)
	{
		Main.instance.project.onAction();
		transform.translation.x = x;
	}
	
	public float tY()
	{
		return transform.translation.y();
	}
	
	public void tY(float y)
	{
		Main.instance.project.onAction();
		transform.translation.y = y;
	}
	
	public float tZ()
	{
		return transform.translation.z();
	}
	
	public void tZ(float z)
	{
		Main.instance.project.onAction();
		transform.translation.z = z;
	}

	private ComponentFloatingLabel labelPos;
	private ComponentTextFloat posXT;
	private ComponentIncrementFloat posXP, posXS;
	private ComponentTextFloat posYT;
	private ComponentIncrementFloat posYP, posYS;
	private ComponentTextFloat posZT;
	private ComponentIncrementFloat posZP, posZS;
	private SelectorButton rotMode;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		final int origY = editorY;
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editorY = super.onSelect(editorPanes, editorY);
		
		editor.addElement(labelPos  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Position"));
		editorY += 20;
		editor.addElement(posXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20 , Main.instance.fontMsg, tX(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, this::tX));
		editor.addElement(posXP     = new ComponentIncrementFloat(editorX + 90 , editorY                              , posXT, 1));
		editor.addElement(posXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                         , posXT, -1));
		editor.addElement(posYT     = new ComponentTextFloat(     editorX + 100, editorY , editorX + 190, editorY + 20, Main.instance.fontMsg, tY(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, this::tY));
		editor.addElement(posYP     = new ComponentIncrementFloat(editorX + 190, editorY                              , posYT, 1));
		editor.addElement(posYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                         , posYT, -1));
		editor.addElement(posZT     = new ComponentTextFloat(     editorX + 200, editorY , editorX + 290, editorY + 20, Main.instance.fontMsg, tZ(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, this::tZ));
		editor.addElement(posZP     = new ComponentIncrementFloat(editorX + 290, editorY                              , posZT, 1));
		editor.addElement(posZS     = new ComponentIncrementFloat(editorX + 290, editorY + 10                         , posZT, -1));
		editorY += 20;
		
		String[] names = new String[] {
				"No rotation",
				"Euler ZYX rotation",
				//"Euler XYZ rotation",
				"Quaternion rotation"
		};
		editor.addElement(rotMode = new SelectorButton(editorX, editorY, editorX + 300, editorY + 20, 
				this.transform.rotation == IRotation.NONE ? names[0] :  
				this.transform.rotation instanceof EulerZYXRotation ? names[1] :  
				//this.defaultTransform.rotation instanceof EulerXYZRotation ? names[2] :  
				this.transform.rotation instanceof QuaternionRotation ? names[2] : "Unknown rotation"
				, names, (ind, str) -> {
					this.onDeselect(editorPanes);
					IRotation old = transform.rotation;
					switch (ind)
					{
					case 0:
						Main.instance.project.onAction();
						transform.rotation = IRotation.NONE;
						break;
					case 1:
						Main.instance.project.onAction();
						(transform.rotation = new EulerZYXRotation()).setFromQuaternion(old.getQuaternion());
						break;
					//case 2:
					//	(defaultTransform.rotation = new EulerXYZRotation()).setFromQuaternion(old.getQuaternion());
					//	break;
					case 2:
						Main.instance.project.onAction();
						(transform.rotation = new QuaternionRotation()).setFromQuaternion(old.getQuaternion());
						break;
					}
					this.onSelect(editorPanes, origY);
				}));
		editorY += 20;
		return this.transform.rotation.onSelect(editorPanes, editorY, Main.instance.project::onAction, () -> {});
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelPos);
		editor.removeElement(posXT);
		editor.removeElement(posXP);
		editor.removeElement(posXS);
		editor.removeElement(posYT);
		editor.removeElement(posYP);
		editor.removeElement(posYS);
		editor.removeElement(posZT);
		editor.removeElement(posZP);
		editor.removeElement(posZS);
		editor.removeElement(rotMode);
		labelPos  = null;
		posXT     = null;
		posXP     = null;
		posXS     = null;
		posYT     = null;
		posYP     = null;
		posYS     = null;
		posZT     = null;
		posZP     = null;
		posZS     = null;
		rotMode   = null;
		this.transform.rotation.onDeselect(editorPanes);
	}
}