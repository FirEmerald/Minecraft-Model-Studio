package firemerald.mcms.api.math;

import org.joml.Vector3d;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.text.ComponentIncrementDouble;
import firemerald.mcms.gui.components.text.ComponentTextDouble;
import firemerald.mcms.model.EditorPanes;

public abstract class EulerRotation implements IRotation
{
	public final Vector3d vec = new Vector3d();
	
	public EulerRotation() {}
	
	public EulerRotation(Vector3d vec)
	{
		this.vec.set(vec);
	}
	
	@Override
	public void save(AbstractElement el)
	{
		if (vec.x() != 0) el.setDouble("rX", vec.x());
		if (vec.y() != 0) el.setDouble("rY", vec.y());
		if (vec.z() != 0) el.setDouble("rZ", vec.z());
	}
	
	@Override
	public void load(AbstractElement el)
	{
		vec.set(el.getDouble("rX", 0), el.getDouble("rY", 0), el.getDouble("rZ", 0));
	}
	
	private ComponentTextDouble rotXT;
	private ComponentIncrementDouble rotXP, rotXS;
	private ComponentTextDouble rotYT;
	private ComponentIncrementDouble rotYP, rotYS;
	private ComponentTextDouble rotZT;
	private ComponentIncrementDouble rotZP, rotZS;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY, Runnable onUpdate)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(rotXT     = new ComponentTextDouble(     editorX      , editorY, editorX + 90 , editorY + 20, Main.instance.fontMsg, vec.x(), -360, 360, value -> {
			vec.x = value;
			onUpdate.run();
		}));
		editor.addElement(rotXP     = new ComponentIncrementDouble(editorX + 90 , editorY                             , rotXT, 1));
		editor.addElement(rotXS     = new ComponentIncrementDouble(editorX + 90 , editorY + 10                        , rotXT, -1));
		editor.addElement(rotYT     = new ComponentTextDouble(     editorX + 100, editorY, editorX + 190, editorY + 20, Main.instance.fontMsg, vec.y(), -360, 360, value -> {
			vec.y = value;
			onUpdate.run();
		}));
		editor.addElement(rotYP     = new ComponentIncrementDouble(editorX + 190, editorY                             , rotYT, 1));
		editor.addElement(rotYS     = new ComponentIncrementDouble(editorX + 190, editorY + 10                        , rotYT, -1));
		editor.addElement(rotZT     = new ComponentTextDouble(     editorX + 200, editorY, editorX + 290, editorY + 20, Main.instance.fontMsg, vec.z(), -360, 360, value -> {
			vec.z = value;
			onUpdate.run();
		}));
		editor.addElement(rotZP     = new ComponentIncrementDouble(editorX + 290, editorY                             , rotZT, 1));
		editor.addElement(rotZS     = new ComponentIncrementDouble(editorX + 290, editorY + 10                        , rotZT, -1));
		return editorY + 20;
	}
	
	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(rotXT);
		editor.removeElement(rotXP);
		editor.removeElement(rotXS);
		editor.removeElement(rotYT);
		editor.removeElement(rotYP);
		editor.removeElement(rotYS);
		editor.removeElement(rotZT);
		editor.removeElement(rotZP);
		editor.removeElement(rotZS);
		rotXT     = null;
		rotXP     = null;
		rotXS     = null;
		rotYT     = null;
		rotYP     = null;
		rotYS     = null;
		rotZT     = null;
		rotZP     = null;
		rotZS     = null;
	}
}