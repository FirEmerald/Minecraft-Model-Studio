package firemerald.mcms.api.math;

import org.joml.Quaterniond;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.text.ComponentTextDouble;
import firemerald.mcms.model.EditorPanes;

public class QuaternionRotation implements IRotation
{
	public final Quaterniond q = new Quaterniond();
	
	public QuaternionRotation() {}
	
	public QuaternionRotation(Quaterniond q)
	{
		this.q.set(q);
	}
	
	@Override
	public void setFromQuaternion(Quaterniond q)
	{
		this.q.set(q);
	}
	
	@Override
	public void save(AbstractElement el)
	{
		/*if (q.x() != 0) */el.setDouble("qX", q.x());
		/*if (q.y() != 0) */el.setDouble("qY", q.y());
		/*if (q.z() != 0) */el.setDouble("qZ", q.z());
		/*if (q.w() != 1) */el.setDouble("qW", q.w());
	}

	@Override
	public Quaterniond getQuaternion()
	{
		return new Quaterniond(q);
	}
	
	@Override
	public void load(AbstractElement el)
	{
		q.set(el.getDouble("qX", 0), el.getDouble("qY", 0), el.getDouble("qZ", 0), el.getDouble("qW", 1));
	}

	@Override
	public IRotation copy()
	{
		return new QuaternionRotation(q);
	}
	
	private ComponentTextDouble rotXT;
	private ComponentTextDouble rotYT;
	private ComponentTextDouble rotZT;
	private ComponentTextDouble rotWT;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY, Runnable preUpdate, Runnable onUpdate)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(rotXT = new ComponentTextDouble(     editorX      , editorY, editorX + 75 , editorY + 20, Main.instance.fontMsg, q.x(), -180, 180, value -> {
			preUpdate.run();
			q.x = value;
			onUpdate.run();
		}));
		editor.addElement(rotYT = new ComponentTextDouble(     editorX + 75 , editorY, editorX + 150, editorY + 20, Main.instance.fontMsg, q.y(), -180, 180, value -> {
			preUpdate.run();
			q.y = value;
			onUpdate.run();
		}));
		editor.addElement(rotZT = new ComponentTextDouble(     editorX + 150, editorY, editorX + 225, editorY + 20, Main.instance.fontMsg, q.z(), -180, 180, value -> {
			preUpdate.run();
			q.z = value;
			onUpdate.run();
		}));
		editor.addElement(rotWT = new ComponentTextDouble(     editorX + 225, editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, q.w(), -180, 180, value -> {
			preUpdate.run();
			q.w = value;
			onUpdate.run();
		}));
		return editorY + 20;
	}
	
	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(rotXT);
		editor.removeElement(rotYT);
		editor.removeElement(rotZT);
		editor.removeElement(rotWT);
		rotXT     = null;
		rotYT     = null;
		rotZT     = null;
		rotWT     = null;
	}
	
	@Override
	public String toString()
	{
		return "Quaternion " + q.toString();
	}
}