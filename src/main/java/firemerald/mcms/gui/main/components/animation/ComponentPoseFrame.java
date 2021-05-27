package firemerald.mcms.gui.main.components.animation;

import firemerald.mcms.Main;
import firemerald.mcms.Project.ExtendedAnimationState;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.math.EulerZYXRotation;
import firemerald.mcms.api.math.IRotation;
import firemerald.mcms.api.math.QuaternionRotation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ElementButton;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.IEditable;
import firemerald.mcms.util.history.HistoryActionChangeFloatValue;

public class ComponentPoseFrame extends ElementButton implements IEditable
{
	public final String name;
	public final Bone<?> bone;
	public final Transformation transform;
	
	public ComponentPoseFrame(int x1, int y1, int x2, int y2, int outline, int radius, String name, Bone<?> bone, Transformation transform)
	{
		super(x1, y1, x2, y2, (w, h, theme) -> theme.genRoundedBox(w.intValue(), h.intValue(), outline, radius), null);
		this.name = name;
		this.bone = bone;
		this.transform = transform;
		this.onRelease = () -> {
			Main.instance.setEditing(this);
			ExtendedAnimationState state = Main.instance.project.getAnimationState();
			state.time = 0;
			state.animMode = EnumPlaybackMode.PAUSED;
		};
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.getEditing() != this;
	}

	private ComponentFloatingLabel labelName;
	private ComponentFloatingLabel labelPos;
	private ComponentTextFloat posXT;
	private ComponentIncrementFloat posXP, posXS;
	private ComponentTextFloat posYT;
	private ComponentIncrementFloat posYP, posYS;
	private ComponentTextFloat posZT;
	private ComponentIncrementFloat posZP, posZS;
	private ComponentFloatingLabel labelScale;
	private ComponentTextFloat scaleXT;
	private ComponentIncrementFloat scaleXP, scaleXS;
	private ComponentTextFloat scaleYT;
	private ComponentIncrementFloat scaleYP, scaleYS;
	private ComponentTextFloat scaleZT;
	private ComponentIncrementFloat scaleZP, scaleZS;
	private SelectorButton rotMode;

	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		final int origY = editorY;
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelName = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, name));
		editorY += 20;
		editor.addElement(labelPos  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Position"));
		editorY += 20;
		editor.addElement(posXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20 , Main.instance.fontMsg, transform.translation.x, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> {
			Main.instance.project.onAction(new HistoryActionChangeFloatValue(transform.translation.x, () -> transform.translation.x, val -> transform.translation.x = val));
			transform.translation.x = value;
		}));
		editor.addElement(posXP     = new ComponentIncrementFloat(editorX + 90 , editorY                              , posXT, 1));
		editor.addElement(posXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                         , posXT, -1));
		editor.addElement(posYT     = new ComponentTextFloat(     editorX + 100, editorY , editorX + 190, editorY + 20, Main.instance.fontMsg, transform.translation.y, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> {
			Main.instance.project.onAction(new HistoryActionChangeFloatValue(transform.translation.y, () -> transform.translation.y, val -> transform.translation.y = val));
			transform.translation.y = value;
		}));
		editor.addElement(posYP     = new ComponentIncrementFloat(editorX + 190, editorY                              , posYT, 1));
		editor.addElement(posYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                         , posYT, -1));
		editor.addElement(posZT     = new ComponentTextFloat(     editorX + 200, editorY , editorX + 290, editorY + 20, Main.instance.fontMsg, transform.translation.z, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> {
			Main.instance.project.onAction(new HistoryActionChangeFloatValue(transform.translation.z, () -> transform.translation.z, val -> transform.translation.z = val));
			transform.translation.z = value;
		}));
		editor.addElement(posZP     = new ComponentIncrementFloat(editorX + 290, editorY                              , posZT, 1));
		editor.addElement(posZS     = new ComponentIncrementFloat(editorX + 290, editorY + 10                         , posZT, -1));
		editorY += 20;
		editor.addElement(labelScale  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Scale"));
		editorY += 20;
		editor.addElement(scaleXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20 , Main.instance.fontMsg, transform.scaling.x, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> {
			Main.instance.project.onAction(new HistoryActionChangeFloatValue(transform.scaling.x, () -> transform.scaling.x, val -> transform.scaling.x = val));
			transform.scaling.x = value;
		}));
		editor.addElement(scaleXP     = new ComponentIncrementFloat(editorX + 90 , editorY                              , scaleXT, 1));
		editor.addElement(scaleXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                         , scaleXT, -1));
		editor.addElement(scaleYT     = new ComponentTextFloat(     editorX + 100, editorY , editorX + 190, editorY + 20, Main.instance.fontMsg, transform.scaling.y, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> {
			Main.instance.project.onAction(new HistoryActionChangeFloatValue(transform.scaling.y, () -> transform.scaling.y, val -> transform.scaling.y = val));
			transform.scaling.y = value;
		}));
		editor.addElement(scaleYP     = new ComponentIncrementFloat(editorX + 190, editorY                              , scaleYT, 1));
		editor.addElement(scaleYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                         , scaleYT, -1));
		editor.addElement(scaleZT     = new ComponentTextFloat(     editorX + 200, editorY , editorX + 290, editorY + 20, Main.instance.fontMsg, transform.scaling.z, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> {
			Main.instance.project.onAction(new HistoryActionChangeFloatValue(transform.scaling.z, () -> transform.scaling.z, val -> transform.scaling.z = val));
			transform.scaling.z = value;
		}));
		editor.addElement(scaleZP     = new ComponentIncrementFloat(editorX + 290, editorY                              , scaleZT, 1));
		editor.addElement(scaleZS     = new ComponentIncrementFloat(editorX + 290, editorY + 10                         , scaleZT, -1));
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
		transform.rotation.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelName);
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
		editor.removeElement(labelScale);
		editor.removeElement(scaleXT);
		editor.removeElement(scaleXP);
		editor.removeElement(scaleXS);
		editor.removeElement(scaleYT);
		editor.removeElement(scaleYP);
		editor.removeElement(scaleYS);
		editor.removeElement(scaleZT);
		editor.removeElement(scaleZP);
		editor.removeElement(scaleZS);
		editor.removeElement(rotMode);
		labelName  = null;
		labelPos   = null;
		posXT      = null;
		posXP      = null;
		posXS      = null;
		posYT      = null;
		posYP      = null;
		posYS      = null;
		posZT      = null;
		posZP      = null;
		posZS      = null;
		labelScale = null;
		scaleXT    = null;
		scaleXP    = null;
		scaleXS    = null;
		scaleYT    = null;
		scaleYP    = null;
		scaleYS    = null;
		scaleZT    = null;
		scaleZP    = null;
		scaleZS    = null;
		rotMode    = null;
	}

	@Override
	public IModelEditable getRenderComponent()
	{
		return bone;
	}
}