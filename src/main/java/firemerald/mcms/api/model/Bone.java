package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.EulerZYXRotation;
import firemerald.mcms.api.math.IRotation;
import firemerald.mcms.api.math.QuaternionRotation;
import firemerald.mcms.api.util.ISelfTyped;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.shader.ModelShaderBase;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public abstract class Bone<T extends Bone<T>> implements ISelfTyped<T>, IModelEditable, ITransformed
{
	public static class Actual extends Bone<Actual>
	{
		public Actual(String name, Transformation defaultTransform, @Nullable Actual parent)
		{
			super(name, defaultTransform, parent);
		}
		
		@Override
		public String getXMLName()
		{
			return "bone";
		}

		@Override
		public Actual makeBone(String name, Transformation transform, Actual parent)
		{
			return new Actual(name, transform, parent);
		}
	}
	
	protected String name;
	public final Transformation defaultTransform;
	public T parent;
	public final List<T> children = new ArrayList<T>();
	public boolean visible = true;
	public boolean childrenVisible = true;
	
	public Bone(String name, Transformation defaultTransform, @Nullable T parent)
	{
		this.name = name;
		this.defaultTransform = defaultTransform;
		if (parent == null) this.parent = null;
		else
		{
			this.parent = parent;
			parent.children.add(self());
		}
	}

	@Override
	public IModelEditable getParent()
	{
		return parent;
	}
	
	@Override
	public String getBoneName()
	{
		return name;
	}

	public void onGuiUpdate(GuiUpdate reason)
	{
		this.children.forEach(child -> child.onGuiUpdate(reason));
	}
	
	public abstract String getXMLName();

	private ComponentFloatingLabel labelName;
	private ComponentText textName;
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
		editorPanes.addBone.setBone(this);
		editorPanes.addItem.setBone(this);
		editorPanes.addFluid.setBone(this);
		editorPanes.addEffect.setBone(this);
		editorPanes.copy.setEditable(parent, this);
		editorPanes.remove.setEditable(parent, this);

		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelName = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Bone name"));
		editorY += 20;
		editor.addElement(textName  = new ComponentText(          editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, getName(), this::setName));
		editorY += 20;
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
		editor.addElement(labelScale  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Scaling"));
		editorY += 20;
		editor.addElement(scaleXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20 , Main.instance.fontMsg, sX(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, this::sX));
		editor.addElement(scaleXP     = new ComponentIncrementFloat(editorX + 90 , editorY                              , scaleXT, 1));
		editor.addElement(scaleXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                         , scaleXT, -1));
		editor.addElement(scaleYT     = new ComponentTextFloat(     editorX + 100, editorY , editorX + 190, editorY + 20, Main.instance.fontMsg, sY(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, this::sY));
		editor.addElement(scaleYP     = new ComponentIncrementFloat(editorX + 190, editorY                              , scaleYT, 1));
		editor.addElement(scaleYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                         , scaleYT, -1));
		editor.addElement(scaleZT     = new ComponentTextFloat(     editorX + 200, editorY , editorX + 290, editorY + 20, Main.instance.fontMsg, sZ(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, this::sZ));
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
				this.defaultTransform.rotation == IRotation.NONE ? names[0] :  
				this.defaultTransform.rotation instanceof EulerZYXRotation ? names[1] :  
				//this.defaultTransform.rotation instanceof EulerXYZRotation ? names[2] :  
				this.defaultTransform.rotation instanceof QuaternionRotation ? names[2] : "Unknown rotation"
				, names, (ind, str) -> {
					this.onDeselect(editorPanes);
					switch (ind)
					{
					case 0:
						defaultTransform.setRotationTo(this, IRotation.NONE);
						break;
					case 1:
						defaultTransform.setRotationTo(this, new EulerZYXRotation());
						break;
					//case 2:
					//	(defaultTransform.rotation = new EulerXYZRotation()).setFromQuaternion(old.getQuaternion());
					//	break;
					case 2:
						defaultTransform.setRotationTo(this, new QuaternionRotation());;
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
		this.defaultTransform.rotation.onDeselect(editorPanes);
		editorPanes.addBone.setBone(null);
		editorPanes.addItem.setBone(null);
		editorPanes.addFluid.setBone(null);
		editorPanes.addEffect.setBone(null);
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
	public ResourceLocation getDisplayIcon()
	{
		return Textures.MODEL_ICON_BONE;
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

	@SuppressWarnings("unchecked")
	@Override
	public void addChild(IModelEditable child)
	{
		if (child instanceof Bone && !this.children.contains(child)) this.children.add((T) child);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position);
			if (pos < 0) pos = 0;
			this.children.add(pos, (T) child);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position)
	{
		if (child instanceof Bone && !this.children.contains(child))
		{
			int pos = this.children.indexOf(position) + 1;
			if (pos <= 0) pos = this.children.size();
			this.children.add(pos, (T) child);
		}
	}

	@Override
	public void removeChild(IModelEditable child)
	{
		if (child instanceof Bone) this.children.remove(child);
	}
	
	@Override
	public int getChildIndex(IModelEditable child)
	{
		if (child instanceof Bone)
		{
			if (children.contains(child)) return children.indexOf(child);
			else return -1;
		}
		else return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addChildAt(IModelEditable child, int index)
	{
		if (child instanceof Bone)
		{
			if (!children.contains(child))
			{
				if (index <= 0) index = 0;
				this.children.add(index, (T) child);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent)
	{
		this.parent = oldParent instanceof Bone ? (T) oldParent : null;
		Matrix4d targetTransform = getTransformation();
		this.parent = newParent instanceof Bone ? (T) newParent : null;
		Matrix4d parentTransform = parent == null ? new Matrix4d() : parent.getTransformation();
		Matrix4d newTransform = parentTransform.invert().mul(targetTransform);
		this.defaultTransform.setFromMatrix(newTransform);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> model) //TODO action
	{
		T bone = null;
		if (newParent instanceof Bone)
		{
			bone = this.cloneSingle((T) newParent, model);
			model.updateBonesList();
		}
		else if (newParent instanceof IModel)
		{
			bone = this.cloneSingle(null, model);
			((IModel<?, ?>) newParent).addChild(bone);
		}
		if (bone != null) copyChildren(bone, model);
		return bone;
	}
	
	public void copyChildren(T newParent, IRigged<?, ?> model)
	{
		for (T child : children) child.copy(newParent, model);
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
	
	public void setDefTransform(Map<String, Matrix4d> map)
	{
		map.put(this.name, defaultTransform.getTransformation());
		for (T bone : this.children) bone.setDefTransform(map);
	}
	
	public void cleanUp()
	{
		this.doCleanUp();
		for (T child : children) child.cleanUp();
	}
	
	public void doCleanUp() {}
	
	@Override
	public String toString()
	{
		return name + ":" + this.getClass().toString();
	}
	
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
	
	public float sX()
	{
		return defaultTransform.scaling.x();
	}
	
	public void sX(float x)
	{
		defaultTransform.scaling.x = x;
	}
	
	public float sY()
	{
		return defaultTransform.scaling.y();
	}
	
	public void sY(float y)
	{
		defaultTransform.scaling.y = y;
	}
	
	public float sZ()
	{
		return defaultTransform.scaling.z();
	}
	
	public void sZ(float z)
	{
		defaultTransform.scaling.z = z;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public void addBone(T bone)
	{
		this.children.add(bone);
	}

	public void removeBone(T child)
	{
		this.children.remove(child);
	}

	@Override
	public Matrix4d getTransformation()
	{
		Matrix4d mat = defaultTransform.getTransformation();
		if (parent != null) parent.getTransformation().mul(mat, mat);
		return mat;
	}

	public Matrix4d getTransformation(Map<String, Matrix4d> pose)
	{
		Matrix4d mat = pose.get(name);
		if (mat == null) mat = this.defaultTransform.getTransformation();
		if (parent != null) parent.getTransformation(pose).mul(mat, mat);
		return mat;
	}
	
	public void addToXML(AbstractElement addTo, float scale)
	{
		AbstractElement el = addTo.addChild(getXMLName());
		addDataToXML(el, scale);
		addChildrenToXML(el, scale);
	}
	
	public void addDataToXML(AbstractElement el, float scale)
	{
		saveData(el, scale);
	}
	
	protected void saveData(AbstractElement el, float scale)
	{
		el.setString("name", name);
		defaultTransform.save(el, scale);
	}
	
	public void addChildrenToXML(AbstractElement addTo, float scale)
	{
		this.children.forEach(child -> child.addToXML(addTo, scale));
	}
	
	public void loadFromXML(AbstractElement el, float scale)
	{
		loadData(el, scale);
		loadChildrenFromXML(el, scale);
	}
	
	protected void loadData(AbstractElement el, float scale)
	{
		defaultTransform.load(el, scale);
	}
	
	public void loadChildrenFromXML(AbstractElement el, float scale)
	{
		children.clear();
		for (AbstractElement child : el.getChildren()) tryLoadChild(child, scale);
	}
	
	public void tryLoadChild(AbstractElement el, float scale)
	{
		if (el.getName().equals(getXMLName()))
		{
			String name = el.getString("name", "unnamed bone");
			Transformation transform = new Transformation(el, scale);
			makeBone(name, transform, self()).loadFromXML(el, scale);
		}
		else
		{
			GuiPopupException.onException("Encountered unknown model element " + el.getName(), new Exception());
		}
	}
	
	public abstract T makeBone(String name, Transformation transform, T parent);
	
	public void setTransforms(Bone<?> ref)
	{
		this.defaultTransform.rotation = ref.defaultTransform.rotation;
		this.defaultTransform.translation.set(ref.defaultTransform.translation);
		Bone<?>[] roots = this.children.toArray(new Bone[this.children.size()]);
		ref.children.forEach(bone -> {
			boolean flag = true;
			for (Bone<?> root : roots) if (root.name.equals(bone.name))
			{
				root.setTransforms(bone);
				flag = false;
				break;
			}
			if (flag)
			{
				T root = makeBone(bone.name, new Transformation(), self());
				root.setTransforms(bone);
			}
		});
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public T cloneSingle(T clonedParent)
	{
		return makeBone(this.name, this.defaultTransform.copy(), clonedParent);
	}
	
	public T cloneSingle(T clonedParent, IRigged<?, ?> model)
	{
		return makeBone(MiscUtil.getNewBoneName(this.name, model), this.defaultTransform.copy(), clonedParent);
	}
	
	public T cloneToModel(T clonedParent)
	{
		return makeBone(this.name, this.defaultTransform.copy(), clonedParent);
	}
	
	public T cloneObject(T clonedParent)
	{
		T newBone = cloneSingle(clonedParent);
		this.children.forEach(child -> child.cloneObject(newBone));
		return newBone;
	}
	
	public void cloneProperties(Bone<?> from)
	{
		this.defaultTransform.set(from.defaultTransform);
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

	public Actual cloneToSkeleton(Actual parent)
	{
		return new Bone.Actual(name, defaultTransform.copy(), parent);
	}
	
	public void render(Object holder, Map<String, Matrix4d> transformations, Matrix4d parentTransform, Runnable defaultTexture)
	{
		if (visible || childrenVisible)
		{
			ModelShaderBase.MODEL.push();
			Matrix4d transform = transformations.get(this.name);
			if (transform != null)
			{
				ModelShaderBase.MODEL.matrix().mul(transform);
				Main.instance.currentModelShader.updateModel();
				transform = parentTransform.mul(transform, transform);
			}
			else transform = parentTransform;
			final Matrix4d currentTransform = transform;
			if (childrenVisible) children.forEach(child -> child.render(holder, transformations, currentTransform, defaultTexture));
			ModelShaderBase.MODEL.pop();
			Main.instance.currentModelShader.updateModel();
		}
	}
}