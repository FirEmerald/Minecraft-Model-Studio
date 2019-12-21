package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.math.EulerZYXRotation;
import firemerald.mcms.api.math.IRotation;
import firemerald.mcms.api.math.QuaternionRotation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ElementButton;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IModelEditable;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.IEditable;

public class ComponentKeyFrame extends ElementButton implements IEditable
{
	public final String name;
	public final Bone bone;
	public final float time;
	public final Transformation transform;
	
	public ComponentKeyFrame(int x1, int y1, int x2, int y2, int outline, int radius, String name, Bone bone, float time, Transformation transform)
	{
		super(x1, y1, x2, y2, (w, h, theme) -> theme.genRoundedBox(w.intValue(), h.intValue(), outline, radius), null);
		this.name = name;
		this.bone = bone;
		this.time = time;
		this.transform = transform;
		this.onRelease = () -> {
			Main.instance.setEditing(this);
			Main.instance.animTime = this.time;
			Main.instance.animMode = EnumPlaybackMode.PAUSED;
		};
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.getEditing() != this;
	}

	private ComponentFloatingLabel labelName;
	private ComponentFloatingLabel labelTime;
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
		editor.addElement(labelName = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, name));
		editorY += 20;
		editor.addElement(labelTime = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, Float.toString(time)));
		editorY += 20;
		editor.addElement(labelPos  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Position"));
		editorY += 20;
		editor.addElement(posXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20 , Main.instance.fontMsg, transform.translation.x, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> transform.translation.x = value));
		editor.addElement(posXP     = new ComponentIncrementFloat(editorX + 90 , editorY                              , posXT, 1));
		editor.addElement(posXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                         , posXT, -1));
		editor.addElement(posYT     = new ComponentTextFloat(     editorX + 100, editorY , editorX + 190, editorY + 20, Main.instance.fontMsg, transform.translation.y, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> transform.translation.y = value));
		editor.addElement(posYP     = new ComponentIncrementFloat(editorX + 190, editorY                              , posYT, 1));
		editor.addElement(posYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                         , posYT, -1));
		editor.addElement(posZT     = new ComponentTextFloat(     editorX + 200, editorY , editorX + 290, editorY + 20, Main.instance.fontMsg, transform.translation.z, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> transform.translation.z = value));
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
				transform.rotation == IRotation.NONE ? names[0] :  
				transform.rotation instanceof EulerZYXRotation ? names[1] :  
				//this.defaultTransform.rotation instanceof EulerXYZRotation ? names[2] :  
				transform.rotation instanceof QuaternionRotation ? names[2] : "Unknown rotation"
				, names, (ind, str) -> {
					this.onDeselect(editorPanes);
					IRotation old = transform.rotation;
					switch (ind)
					{
					case 0:
						transform.rotation = IRotation.NONE;
						break;
					case 1:
						(transform.rotation = new EulerZYXRotation()).setFromQuaternion(old.getQuaternion());
						break;
					//case 2:
					//	(defaultTransform.rotation = new EulerXYZRotation()).setFromQuaternion(old.getQuaternion());
					//	break;
					case 2:
						(transform.rotation = new QuaternionRotation()).setFromQuaternion(old.getQuaternion());
						break;
					}
					this.onSelect(editorPanes, origY);
				}));
		editorY += 20;
		return this.transform.rotation.onSelect(editorPanes, editorY, () -> {});
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelName);
		editor.removeElement(labelTime);
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
		labelName = null;
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
		transform.rotation.onDeselect(editorPanes);
	}

	@Override
	public IModelEditable getRenderComponent()
	{
		return bone;
	}
}