package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.util.MatrixHandler;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementDouble;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextDouble;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.model.ITransformed;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Textures;

public class Bone implements IRaytraceTarget, IEditable, ITransformed
{
	public String name;
	public final Transformation defaultTransform;
	public Bone parent;
	public final List<Bone> children = new ArrayList<Bone>();
	public boolean visible = true;
	public boolean childrenVisible = true;
	
	public Bone(String name, Transformation defaultTransform)
	{
		this.name = name;
		this.defaultTransform = defaultTransform;
		this.parent = null;
	}
	
	public Bone(String name, Transformation defaultTransform, Bone parent)
	{
		this.name = name;
		this.defaultTransform = defaultTransform;
		if (parent == null) this.parent = null;
		else (this.parent = parent).children.add(this);
	}
	
	public void setDefTransform(Map<String, Matrix4> map)
	{
		map.put(this.name, defaultTransform.getTransformation());
		for (Bone bone : this.children) bone.setDefTransform(map);
	}
	
	public void render(Map<String, Matrix4> transformations)
	{
		MatrixHandler.instance.push();
		MatrixHandler.instance.multMatrix(transformations.get(this.name));
		if (visible) doRender();
		if (childrenVisible) for (Bone child : children) child.render(transformations);
		MatrixHandler.instance.pop();
	}
	
	public void cleanUp()
	{
		this.doCleanUp();
		for (Bone child : children) child.cleanUp();
	}
	
	public void doRender() {}
	
	public void doCleanUp() {}
	
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4> transformations, Matrix4 transformation)
	{
		RaytraceResult result = null;
		if (childrenVisible) for (Bone child : children)
		{
			Matrix4 transform = transformations.get(child.name);
			if (transform == null) transform = new Matrix4(transformation);
			else transform = transformation.mul(transform, new Matrix4());
			RaytraceResult res = child.raytrace(fx, fy, fz, dx, dy, dz, transformations, transform);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		return result;
	}
	
	@Override
	public String toString()
	{
		return name + ":" + this.getClass().toString();
	}

	private ComponentText labelName;
	private ComponentLabel labelPos;
	private ComponentTextFloat posXT;
	private ComponentIncrementFloat posXP, posXS;
	private ComponentTextFloat posYT;
	private ComponentIncrementFloat posYP, posYS;
	private ComponentTextFloat posZT;
	private ComponentIncrementFloat posZP, posZS;
	private ComponentLabel labelRot;
	private ComponentTextDouble rotXT;
	private ComponentIncrementDouble rotXP, rotXS;
	private ComponentTextDouble rotYT;
	private ComponentIncrementDouble rotYP, rotYS;
	private ComponentTextDouble rotZT;
	private ComponentIncrementDouble rotZP, rotZS;
	
	@Override
	public void onSelect(EditorPanes editorPanes)
	{
		editorPanes.addBone.setBone(this);
		editorPanes.copy.setEditable(parent, this);
		editorPanes.remove.setEditable(parent, this);
		
		GuiElementContainer editor = editorPanes.editor;
		float editorX = editorPanes.editorX;
		float editorY = editorPanes.editorY;
		editor.addElement(labelName = new ComponentText(           editorX      , editorY      , editorX + 300, editorY + 20 , Main.instance.fontMsg, name, string -> this.name = string));
		editor.addElement(labelPos  = new ComponentLabel(          editorX      , editorY + 20 , editorX + 300, editorY + 40 , Main.instance.fontMsg, "Position"));
		editor.addElement(posXT     = new ComponentTextFloat(      editorX      , editorY + 40 , editorX + 90 , editorY + 60 , Main.instance.fontMsg, defaultTransform.translation.x(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> defaultTransform.translation.x(value)));
		editor.addElement(posXP     = new ComponentIncrementFloat( editorX + 90 , editorY + 40                   , posXT, 1));
		editor.addElement(posXS     = new ComponentIncrementFloat( editorX + 90 , editorY + 50                   , posXT, -1));
		editor.addElement(posYT     = new ComponentTextFloat(      editorX + 100, editorY + 40 , editorX + 190, editorY + 60 , Main.instance.fontMsg, defaultTransform.translation.y(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> defaultTransform.translation.y(value)));
		editor.addElement(posYP     = new ComponentIncrementFloat( editorX + 190, editorY + 40                   , posYT, 1));
		editor.addElement(posYS     = new ComponentIncrementFloat( editorX + 190, editorY + 50                   , posYT, -1));
		editor.addElement(posZT     = new ComponentTextFloat(      editorX + 200, editorY + 40 , editorX + 290, editorY + 60 , Main.instance.fontMsg, defaultTransform.translation.z(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> defaultTransform.translation.z(value)));
		editor.addElement(posZP     = new ComponentIncrementFloat( editorX + 290, editorY + 40                   , posZT, 1));
		editor.addElement(posZS     = new ComponentIncrementFloat( editorX + 290, editorY + 50                   , posZT, -1));
		editor.addElement(labelRot  = new ComponentLabel(          editorX      , editorY + 60 , editorX + 300, editorY + 80 , Main.instance.fontMsg, "Rotation"));
		editor.addElement(rotXT     = new ComponentTextDouble(     editorX      , editorY + 80 , editorX + 90 , editorY + 100, Main.instance.fontMsg, defaultTransform.rX, -180, 180, value -> this.defaultTransform.setRX(value)));
		editor.addElement(rotXP     = new ComponentIncrementDouble(editorX + 90 , editorY + 80                   , rotXT, 1));
		editor.addElement(rotXS     = new ComponentIncrementDouble(editorX + 90 , editorY + 90                   , rotXT, -1));
		editor.addElement(rotYT     = new ComponentTextDouble(     editorX + 100, editorY + 80 , editorX + 190, editorY + 100, Main.instance.fontMsg, defaultTransform.rY, -180, 180, value -> this.defaultTransform.setRY(value)));
		editor.addElement(rotYP     = new ComponentIncrementDouble(editorX + 190, editorY + 80                   , rotYT, 1));
		editor.addElement(rotYS     = new ComponentIncrementDouble(editorX + 190, editorY + 90                   , rotYT, -1));
		editor.addElement(rotZT     = new ComponentTextDouble(     editorX + 200, editorY + 80 , editorX + 290, editorY + 100, Main.instance.fontMsg, defaultTransform.rZ, -180, 180, value -> this.defaultTransform.setRZ(value)));
		editor.addElement(rotZP     = new ComponentIncrementDouble(editorX + 290, editorY + 80                   , rotZT, 1));
		editor.addElement(rotZS     = new ComponentIncrementDouble(editorX + 290, editorY + 90                   , rotZT, -1));
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		editorPanes.addBone.setBone(null);
		editorPanes.copy.setEditable(null, null);
		editorPanes.remove.setEditable(null, null);
		GuiElementContainer editor = editorPanes.editor;
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
		editor.removeElement(labelRot);
		editor.removeElement(rotXT);
		editor.removeElement(rotXP);
		editor.removeElement(rotXS);
		editor.removeElement(rotYT);
		editor.removeElement(rotYP);
		editor.removeElement(rotYS);
		editor.removeElement(rotZT);
		editor.removeElement(rotZP);
		editor.removeElement(rotZS);
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
		labelRot  = null;
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

	@Override
	public String getDisplayIcon()
	{
		return Textures.EDITABLE_ICON_BONE;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Collection<? extends IEditable> getChildren()
	{
		return children;
	}

	@Override
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	@Override
	public boolean canBeChild(IEditable candidate)
	{
		return candidate instanceof Bone;
	}

	@Override
	public void addChild(IEditable child)
	{
		if (child instanceof Bone && !this.children.contains(child)) this.children.add((Bone) child);
	}

	@Override
	public void addChildBefore(IEditable child, IEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position);
			if (pos < 0) pos = 0;
			this.children.add(pos, (Bone) child);
		}
	}

	@Override
	public void addChildAfter(IEditable child, IEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position) + 1;
			if (pos <= 0) pos = this.children.size();
			this.children.add(pos, (Bone) child);
		}
	}

	@Override
	public void removeChild(IEditable child)
	{
		if (child instanceof Bone) this.children.remove(child);
	}

	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent)
	{
		this.parent = oldParent instanceof Bone ? (Bone) oldParent : null;
		Matrix4 targetTransform = getTransformation();
		this.parent = newParent instanceof Bone ? (Bone) newParent : null;
		Matrix4 parentTransform = parent == null ? new Matrix4() : parent.getTransformation();
		Matrix4 newTransform = parentTransform.invert().mul(targetTransform);
		this.defaultTransform.setFromMatrix(newTransform);
	}

	@Override
	public Matrix4 getTransformation()
	{
		Matrix4 mat = defaultTransform.getTransformation();
		if (parent != null) parent.getTransformation().mul(mat, mat);
		return mat;
	}
	
	@Override
	public boolean isVisible()
	{
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		this.childrenVisible = this.visible = visible;
	}
	
	public void addToXML(AbstractElement addTo)
	{
		AbstractElement el = addTo.addChild(getXMLName());
		addData(el);
		addChildrenToXML(el);
	}
	
	public String getXMLName()
	{
		return "bone";
	}
	
	public void addData(AbstractElement el)
	{
		el.setString("name", name);
		defaultTransform.saveAsChild(el, "default_position", true, true, false);
	}
	
	public void addChildrenToXML(AbstractElement addTo)
	{
		for (Bone child : this.children) child.addToXML(addTo);
	}
	
	public void loadFromXML(AbstractElement el)
	{
		defaultTransform.loadFromChild(el, "default_position");
		loadChildrenFromXML(el);
	}
	
	public void loadChildrenFromXML(AbstractElement el)
	{
		children.clear();
		for (AbstractElement child : el.getChildren()) tryLoadChild(child);
	}
	
	public void tryLoadChild(AbstractElement el)
	{
		switch (el.getName())
		{
		case "bone":
		{
			Bone bone = new Bone(el.getString("name", "unnamed bone"), new Transformation(), this);
			bone.loadFromXML(el);
			break;
		}
		case "component_holder":
		{
			RenderObjectComponents bone = new RenderObjectComponents(el.getString("name", "unnamed bone"), new Transformation(), this);
			bone.loadFromXML(el);
			break;
		}
		}
	}

	@Override
	public IEditable copy(IEditableParent newParent, IModel model)
	{
		Bone bone = null;
		if (newParent instanceof Bone)
		{
			bone = new Bone(MiscUtil.getNewBoneName(name, model), new Transformation(defaultTransform), (Bone) newParent);
			model.updateBonesList();
		}
		else if (newParent instanceof MultiModel)
		{
			bone = new Bone(MiscUtil.getNewBoneName(name, model), new Transformation(defaultTransform));
			((MultiModel) newParent).addBaseBone(bone);
		}
		if (bone != null) copyChildren(bone, model);
		return bone;
	}
	
	public void copyChildren(Bone newParent, IModel model)
	{
		for (Bone child : children) child.copy(newParent, model);
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}