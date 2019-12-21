package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.EulerZYXRotation;
import firemerald.mcms.api.math.IRotation;
import firemerald.mcms.api.math.QuaternionRotation;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IModelEditable;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.model.ITransformed;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Textures;

public class Bone implements IRaytraceTarget, IModelEditable, ITransformed
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
	
	public void setDefTransform(Map<String, Matrix4d> map)
	{
		map.put(this.name, defaultTransform.getTransformation());
		for (Bone bone : this.children) bone.setDefTransform(map);
	}
	
	public void render(Map<String, Matrix4d> transformations)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().mul(transformations.get(this.name));
		Main.instance.shader.updateModel();
		if (visible) doRender();
		if (childrenVisible) for (Bone child : children) child.render(transformations);
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
	
	public void cleanUp()
	{
		this.doCleanUp();
		for (Bone child : children) child.cleanUp();
	}
	
	public void doRender() {}
	
	public void doCleanUp() {}
	
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation)
	{
		RaytraceResult result = null;
		if (childrenVisible) for (Bone child : children)
		{
			Matrix4d transform = transformations.get(child.name);
			if (transform == null) transform = new Matrix4d(transformation);
			else transform = transformation.mul(transform, new Matrix4d());
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

	private ComponentFloatingLabel labelName;
	private ComponentText textName;
	private ComponentFloatingLabel labelPos;
	private ComponentTextFloat posXT;
	private ComponentIncrementFloat posXP, posXS;
	private ComponentTextFloat posYT;
	private ComponentIncrementFloat posYP, posYS;
	private ComponentTextFloat posZT;
	private ComponentIncrementFloat posZP, posZS;
	private SelectorButton rotMode;
	
	public float tX()
	{
		return defaultTransform.translation.x();
	}
	
	public void tX(float x)
	{
		defaultTransform.translation.x = x;
	}
	
	public float tY()
	{
		return defaultTransform.translation.y();
	}
	
	public void tY(float y)
	{
		defaultTransform.translation.y = y;
	}
	
	public float tZ()
	{
		return defaultTransform.translation.z();
	}
	
	public void tZ(float z)
	{
		defaultTransform.translation.z = z;
	}
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		final int origY = editorY;
		editorPanes.addBone.setBone(this);
		editorPanes.copy.setEditable(parent, this);
		editorPanes.remove.setEditable(parent, this);

		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelName = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Bone name"));
		editorY += 20;
		editor.addElement(textName  = new ComponentText(          editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, name, string -> this.name = string));
		editorY += 20;
		editor.addElement(labelPos  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Position"));
		editorY += 20;
		editor.addElement(posXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20 , Main.instance.fontMsg, tX(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> tX(value)));
		editor.addElement(posXP     = new ComponentIncrementFloat(editorX + 90 , editorY                              , posXT, 1));
		editor.addElement(posXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                         , posXT, -1));
		editor.addElement(posYT     = new ComponentTextFloat(     editorX + 100, editorY , editorX + 190, editorY + 20, Main.instance.fontMsg, tY(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> tY(value)));
		editor.addElement(posYP     = new ComponentIncrementFloat(editorX + 190, editorY                              , posYT, 1));
		editor.addElement(posYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                         , posYT, -1));
		editor.addElement(posZT     = new ComponentTextFloat(     editorX + 200, editorY , editorX + 290, editorY + 20, Main.instance.fontMsg, tZ(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> tZ(value)));
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
				this.defaultTransform.rotation == IRotation.NONE ? names[0] :  
				this.defaultTransform.rotation instanceof EulerZYXRotation ? names[1] :  
				//this.defaultTransform.rotation instanceof EulerXYZRotation ? names[2] :  
				this.defaultTransform.rotation instanceof QuaternionRotation ? names[2] : "Unknown rotation"
				, names, (ind, str) -> {
					this.onDeselect(editorPanes);
					IRotation old = defaultTransform.rotation;
					switch (ind)
					{
					case 0:
						defaultTransform.rotation = IRotation.NONE;
						break;
					case 1:
						(defaultTransform.rotation = new EulerZYXRotation()).setFromQuaternion(old.getQuaternion());
						break;
					//case 2:
					//	(defaultTransform.rotation = new EulerXYZRotation()).setFromQuaternion(old.getQuaternion());
					//	break;
					case 2:
						(defaultTransform.rotation = new QuaternionRotation()).setFromQuaternion(old.getQuaternion());
						break;
					}
					this.onSelect(editorPanes, origY);
				}));
		editorY += 20;
		return this.defaultTransform.rotation.onSelect(editorPanes, editorY, () -> {});
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		editorPanes.addBone.setBone(null);
		editorPanes.copy.setEditable(null, null);
		editorPanes.remove.setEditable(null, null);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelName);
		editor.removeElement(textName);
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
		this.defaultTransform.rotation.onDeselect(editorPanes);
	}

	@Override
	public String getDisplayIcon()
	{
		return Textures.MODEL_ICON_BONE;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Collection<? extends IModelEditable> getChildren()
	{
		return children;
	}

	@Override
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return candidate instanceof Bone;
	}

	@Override
	public void addChild(IModelEditable child)
	{
		if (child instanceof Bone && !this.children.contains(child)) this.children.add((Bone) child);
	}

	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position);
			if (pos < 0) pos = 0;
			this.children.add(pos, (Bone) child);
		}
	}

	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position) + 1;
			if (pos <= 0) pos = this.children.size();
			this.children.add(pos, (Bone) child);
		}
	}

	@Override
	public void removeChild(IModelEditable child)
	{
		if (child instanceof Bone) this.children.remove(child);
	}

	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent)
	{
		this.parent = oldParent instanceof Bone ? (Bone) oldParent : null;
		Matrix4d targetTransform = getTransformation();
		this.parent = newParent instanceof Bone ? (Bone) newParent : null;
		Matrix4d parentTransform = parent == null ? new Matrix4d() : parent.getTransformation();
		Matrix4d newTransform = parentTransform.invert().mul(targetTransform);
		this.defaultTransform.setFromMatrix(newTransform);
	}

	@Override
	public Matrix4d getTransformation()
	{
		Matrix4d mat = defaultTransform.getTransformation();
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
		addDataToXML(el);
		addChildrenToXML(el);
	}
	
	public void addToSkeleton(AbstractElement addTo, float scale)
	{
		AbstractElement el = addTo.addChild(getSkeletonName());
		addDataToSkeleton(el, scale);
		addChildrenToSkeleton(el, scale);
	}
	
	public String getXMLName()
	{
		return "bone";
	}
	
	public String getSkeletonName()
	{
		return "bone";
	}
	
	public void addDataToXML(AbstractElement el)
	{
		el.setString("name", name);
		defaultTransform.save(el);
	}
	
	public void addDataToSkeleton(AbstractElement el, float scale)
	{
		el.setString("name", name);
		defaultTransform.save(el);
	}
	
	public void addChildrenToXML(AbstractElement addTo)
	{
		for (Bone child : this.children) child.addToXML(addTo);
	}
	
	public void addChildrenToSkeleton(AbstractElement addTo, float scale)
	{
		for (Bone child : this.children) child.addToSkeleton(addTo, scale);
	}
	
	public void loadFromXML(AbstractElement el)
	{
		defaultTransform.load(el);
		loadChildrenFromXML(el);
	}
	
	public void loadFromSkeleton(AbstractElement el)
	{
		defaultTransform.load(el);
		loadChildrenFromSkeleton(el);
	}
	
	public void loadChildrenFromXML(AbstractElement el)
	{
		children.clear();
		for (AbstractElement child : el.getChildren()) tryLoadChild(child);
	}
	
	public void loadChildrenFromSkeleton(AbstractElement el)
	{
		for (AbstractElement child : el.getChildren()) tryLoadChildSkeleton(child);
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
	
	public void tryLoadChildSkeleton(AbstractElement el)
	{
		switch (el.getName())
		{
		case "bone":
		{
			String name = el.getString("name", "unnamed bone");
			boolean flag = true;
			for (Bone child : children) if (child.name.equals(name))
			{
				child.loadFromSkeleton(el);
				flag = false;
			}
			if (flag)
			{
				RenderObjectComponents bone = new RenderObjectComponents(name, new Transformation(), this);
				bone.loadFromXML(el);
				break;
			}
		}
		}
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IModel model)
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
	
	public void setTransforms(Bone ref)
	{
		this.defaultTransform.rotation = ref.defaultTransform.rotation;
		this.defaultTransform.translation.set(ref.defaultTransform.translation);
		Bone[] roots = this.children.toArray(new Bone[this.children.size()]);
		ref.children.forEach(bone -> {
			boolean flag = true;
			for (Bone root : roots) if (root.name.equals(bone.name))
			{
				root.setTransforms(bone);
				flag = false;
				break;
			}
			if (flag)
			{
				Bone root = new Bone(bone.name, new Transformation(), this);
				this.addChild(root);
				root.setTransforms(bone);
			}
		});
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public void updateTex()
	{
		children.forEach(child -> child.updateTex());
	}

	@Override
	public Transformation getDefaultTransformation()
	{
		return this.defaultTransform;
	}
}