package firemerald.mcms.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;
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
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.function.TriFunction;

public class Bone implements IRaytraceTarget, IModelEditable, ITransformed
{
	private static final Map<String, TriFunction<Bone, AbstractElement, Float, Bone>> BONE_TYPES = new HashMap<>();

	/**
	 * Register a Bone type
	 * 
	 * @param name the bone's name - the bone's XML name <i>must</i> be {domain}:{path with "/"'s replaced with "."'s} or it will not load properly!
	 * @param constructor the constructor lambda - constructs the bone. see the static constructor below for examples.
	 * 
	 * @return if the bone was registered
	 */
	public static boolean registerBoneType(ResourceLocation name, TriFunction<Bone, AbstractElement, Float, Bone> constructor)
	{
		return registerBoneType(name.toString().replace(':', '-').replace('/', '_'), constructor);
	}
	
	private static boolean registerBoneType(String name, TriFunction<Bone, AbstractElement, Float, Bone> constructor)
	{
		if (BONE_TYPES.containsKey(name)) return false;
		else
		{
			BONE_TYPES.put(name, constructor);
			return true;
		}
	}
	
	public static Bone construct(String name, @Nullable Bone parent, AbstractElement element, float scale)
	{
		TriFunction<Bone, AbstractElement, Float, Bone> constructor = BONE_TYPES.get(name);
		if (constructor == null)
		{
			Bone bone = new Bone(element.getString("name", "unnamed bone"), new Transformation(), parent);
			bone.loadFromXML(element, scale);
			return bone;
		}
		else return constructor.apply(parent, element, scale);
	}
	
	public static Bone constructIfRegistered(String name, @Nullable Bone parent, AbstractElement element, float scale)
	{
		TriFunction<Bone, AbstractElement, Float, Bone> constructor = BONE_TYPES.get(name);
		if (constructor == null) return null;
		else return constructor.apply(parent, element, scale);
	}
	
	static
	{
		registerBoneType("bone", (parent, element, scale) -> {
			Bone bone = new Bone(element.getString("name", "unnamed bone"), new Transformation(), parent);
			bone.loadFromXML(element, scale);
			return bone;
		});
		registerBoneType("component_holder", (parent, element, scale) -> {
			Bone bone = new RenderObjectComponents(element.getString("name", "unnamed bone"), new Transformation(), parent);
			bone.loadFromXML(element, scale);
			return bone;
		});
	}
	/*
	@Override
	public String toString()
	{
		return name + ":" + this.getClass().toString();
	}
	*/
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
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		final int origY = editorY;
		editorPanes.addBone.setBone(this);
		editorPanes.addItem.setBone(this);
		editorPanes.addFluid.setBone(this);
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
						Main.instance.project.onAction();
						defaultTransform.rotation = IRotation.NONE;
						break;
					case 1:
						Main.instance.project.onAction();
						(defaultTransform.rotation = new EulerZYXRotation()).setFromQuaternion(old.getQuaternion());
						break;
					//case 2:
					//	(defaultTransform.rotation = new EulerXYZRotation()).setFromQuaternion(old.getQuaternion());
					//	break;
					case 2:
						Main.instance.project.onAction();
						(defaultTransform.rotation = new QuaternionRotation()).setFromQuaternion(old.getQuaternion());
						break;
					}
					this.onSelect(editorPanes, origY);
				}));
		editorY += 20;
		return this.defaultTransform.rotation.onSelect(editorPanes, editorY, Main.instance.project::onAction, () -> {});
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		editorPanes.addBone.setBone(null);
		editorPanes.addItem.setBone(null);
		editorPanes.addFluid.setBone(null);
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
	public ResourceLocation getDisplayIcon()
	{
		return Textures.MODEL_ICON_BONE;
	}

	@Override
	public Collection<? extends IModelEditable> getChildren()
	{
		return Stream.concat(effects.stream(), children.stream()).collect(Collectors.toList());
	}

	@Override
	public boolean hasChildren()
	{
		return !children.isEmpty() || !effects.isEmpty();
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return candidate instanceof Bone || candidate instanceof BoneEffect;
	}

	@Override
	public void addChild(IModelEditable child)
	{
		if (child instanceof Bone && !this.children.contains(child)) this.children.add((Bone) child);
		else if (child instanceof BoneEffect && !this.effects.contains(child)) this.effects.add((BoneEffect) child);
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
		else if (child instanceof BoneEffect && !this.effects.contains(child))
		{
			int pos = this.effects.indexOf(position);
			if (pos < 0) pos = 0;
			this.effects.add(pos, (BoneEffect) child);
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
		else if (child instanceof BoneEffect && !this.effects.contains(child))
		{
			int pos = this.effects.indexOf(position) + 1;
			if (pos <= 0) pos = this.effects.size();
			this.effects.add(pos, (BoneEffect) child);
		}
	}

	@Override
	public void removeChild(IModelEditable child)
	{
		if (child instanceof Bone) this.children.remove(child);
		else if (child instanceof BoneEffect) this.effects.remove(child);
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
	
	public void loadFromSkeleton(AbstractElement el, float scale)
	{
		defaultTransform.load(el, scale);
		loadChildrenFromSkeleton(el, scale);
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?> model) //TODO action
	{
		Bone bone = null;
		if (newParent instanceof Bone)
		{
			bone = this.cloneSingle((Bone) newParent, model);
			model.updateBonesList();
		}
		else if (newParent instanceof IModel)
		{
			bone = this.cloneSingle(null, model);
			((IModel) newParent).addChild(bone);
		}
		if (bone != null) copyChildren(bone, model);
		return bone;
	}
	
	public void copyChildren(Bone newParent, IRigged<?> model)
	{
		for (Bone child : children) child.copy(newParent, model);
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
	
	protected String name;
	public final Transformation defaultTransform;
	public Bone parent;
	protected final List<BoneEffect> effects = new ArrayList<>();
	public final List<Bone> children = new ArrayList<Bone>();
	public boolean visible = true;
	public boolean childrenVisible = true;
	
	public Bone(String name, Transformation defaultTransform, @Nullable Bone parent)
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
	
	public void render(Map<String, Matrix4d> transformations, Runnable defaultTexture)
	{
		if (visible || childrenVisible)
		{
			Shader.MODEL.push();
			Shader.MODEL.matrix().mul(transformations.get(this.name));
			Main.instance.shader.updateModel();
			effects.forEach(effect -> effect.preRender(defaultTexture));
			if (visible) doRender(defaultTexture);
			effects.forEach(effect -> effect.postRenderBone(defaultTexture));
			if (childrenVisible) for (Bone child : children) child.render(transformations, defaultTexture);
			effects.forEach(effect -> effect.postRenderChildren(defaultTexture));
			Shader.MODEL.pop();
			Main.instance.shader.updateModel();
		}
	}
	
	public void cleanUp()
	{
		this.doCleanUp();
		for (Bone child : children) child.cleanUp();
	}
	
	public void doRender(Runnable defaultTexture) {}
	
	public void doCleanUp() {}
	
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation)
	{
		RaytraceResult result = raytraceLocal(fx, fy, fz, dx, dy, dz, transformations, transformation);
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

	public RaytraceResult raytraceLocal(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation)
	{
		return null;
	}
	
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
		Main.instance.project.onAction();
		defaultTransform.translation.x = x;
	}
	
	public float tY()
	{
		return defaultTransform.translation.y();
	}
	
	public void tY(float y)
	{
		Main.instance.project.onAction();
		defaultTransform.translation.y = y;
	}
	
	public float tZ()
	{
		return defaultTransform.translation.z();
	}
	
	public void tZ(float z)
	{
		Main.instance.project.onAction();
		defaultTransform.translation.z = z;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		Main.instance.project.onAction();
		this.name = name;
	}

	public void addBone(Bone bone)
	{
		this.children.add(bone);
	}

	public void removeBone(Bone child)
	{
		this.children.remove(child);
	}
	
	public void addEffect(BoneEffect effect)
	{
		this.effects.add(effect);
	}
	
	public void removeEffect(BoneEffect effect)
	{
		this.effects.remove(effect);
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
	
	public void addToXML(AbstractElement addTo, float scale)
	{
		AbstractElement el = addTo.addChild(getXMLName());
		addDataToXML(el, scale);
		addChildrenToXML(el, scale);
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
	
	public void addDataToSkeleton(AbstractElement el, float scale)
	{
		saveData(el, scale);
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
		this.effects.forEach(effect -> effect.addToXML(addTo, scale));
		this.children.forEach(child -> child.addToXML(addTo, scale));
	}
	
	public void addChildrenToSkeleton(AbstractElement addTo, float scale)
	{
		for (Bone child : this.children) child.addToSkeleton(addTo, scale);
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
		effects.clear();
		children.clear();
		for (AbstractElement child : el.getChildren()) tryLoadChild(child, scale);
	}
	
	public void loadChildrenFromSkeleton(AbstractElement el, float scale)
	{
		for (AbstractElement child : el.getChildren()) tryLoadChildSkeleton(child, scale);
	}
	
	public void tryLoadChild(AbstractElement el, float scale)
	{
		if (BoneEffect.constructIfRegistered(el.getName(), this, el, scale) == null) Bone.construct(el.getName(), this, el, scale);
	}
	
	public void tryLoadChildSkeleton(AbstractElement el, float scale)
	{
		String name = el.getString("name", null);
		if (name != null)
		{
			boolean flag = true;
			for (Bone child : children) if (child.name.equals(name))
			{
				child.loadFromSkeleton(el, scale);
				flag = false;
			}
			if (flag) Bone.construct(el.getName(), this, el, scale);
		}
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
				this.addBone(root);
				root.setTransforms(bone);
			}
		});
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public Bone cloneSingle(Bone clonedParent)
	{
		return new Bone(this.name, this.defaultTransform.copy(), clonedParent);
	}
	
	public Bone cloneSingle(Bone clonedParent, IRigged<?> model)
	{
		return new Bone(MiscUtil.getNewBoneName(this.name, model), this.defaultTransform.copy(), clonedParent);
	}
	
	public Bone cloneToSkeleton(Bone clonedParent)
	{
		return cloneSingle(clonedParent);
	}
	
	public Bone cloneToModel(Bone clonedParent)
	{
		return new RenderObjectComponents(this.name, this.defaultTransform.copy(), clonedParent);
	}
	
	public Bone cloneObject(Bone clonedParent)
	{
		Bone newBone = cloneSingle(clonedParent);
		this.effects.forEach(effect -> effect.cloneObject(newBone));
		this.children.forEach(child -> child.cloneObject(newBone));
		return newBone;
	}
	
	public void cloneProperties(Bone from)
	{
		this.defaultTransform.set(from.defaultTransform);
	}
	
	public boolean hasCustomModelProperties()
	{
		return false;
	}
}