package firemerald.mcms.api.model.effects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.api.util.TriFunction;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.util.ResourceLocation;

public abstract class BoneEffect implements IModelEditable
{
	private static final Map<String, TriFunction<RenderBone<?>, AbstractElement, Float, BoneEffect>> EFFECT_TYPES = new HashMap<>();

	/**
	 * Register a Bone Effect type
	 * 
	 * @param name the bone effect's name - the bone effect's XML name <i>must</i> be {domain}:{path with "/"'s replaced with "."'s} or it will not load properly!
	 * @param constructor the constructor lambda - constructs the bone effect. see the static constructor below for examples.
	 * 
	 * @return if the bone effect was registered
	 */
	public static boolean registerBoneType(ResourceLocation name, TriFunction<RenderBone<?>, AbstractElement, Float, BoneEffect> constructor)
	{
		return registerBoneType(name.toString().replace(':', '-').replace('/', '_'), constructor);
	}
	
	private static boolean registerBoneType(String name, TriFunction<RenderBone<?>, AbstractElement, Float, BoneEffect> constructor)
	{
		if (EFFECT_TYPES.containsKey(name)) return false;
		else
		{
			EFFECT_TYPES.put(name, constructor);
			return true;
		}
	}
	
	public static BoneEffect constructIfRegistered(String name, @Nullable RenderBone<?> parent, AbstractElement element, float scale)
	{
		TriFunction<RenderBone<?>, AbstractElement, Float, BoneEffect> constructor = EFFECT_TYPES.get(name);
		if (constructor == null) return null;
		else return constructor.apply(parent, element, scale);
	}
	
	static
	{
		registerBoneType("item", (parent, element, scale) -> {
			BoneEffect bone = new ItemRenderEffect(element.getString("name", "unnamed item"), parent, new Transformation(), 0);
			bone.loadFromXML(element, scale);
			return bone;
		});
		registerBoneType("fluid", (parent, element, scale) -> {
			BoneEffect bone = new FluidRenderEffect(element.getString("name", "unnamed fluid"), parent, new Transformation(), 0);
			bone.loadFromXML(element, scale);
			return bone;
		});
	}
	
	protected boolean visible = true;
	protected String name;
	protected RenderBone<?> parent;
	public final Transformation transform;

	public BoneEffect(String name, @Nullable RenderBone<?> parent)
	{
		this(name, parent, new Transformation());
	}

	public BoneEffect(String name, @Nullable RenderBone<?> parent, Transformation transform)
	{
		this.name = name;
		this.transform = transform;
		if (parent == null) this.parent = null;
		else (this.parent = parent).addEffect(this);
	}
	
	@Override
	public String getBoneName()
	{
		return parent != null ? parent.getBoneName() : null;
	}

	public Matrix4d getTransformation()
	{
		Matrix4d mat = transform.getTransformation();
		if (parent != null) parent.getTransformation().mul(mat, mat);
		return mat;
	}
	
	public void loadFromXML(AbstractElement el, float scale)
	{
		name = el.getString("name", "unnamed");
	}
	
	public void addToXML(AbstractElement el, float scale)
	{
		saveToXML(el.addChild(getXMLName()), scale);
	}
	
	public void saveToXML(AbstractElement el, float scale)
	{
		el.setString("name", name);
	}

	private ComponentFloatingLabel labelName;
	private ComponentText textName;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editorPanes.addBone.setBone(null);
		editorPanes.addItem.setBone(null);
		editorPanes.addFluid.setBone(null);
		editorPanes.copy.setEditable(parent, this);
		editorPanes.remove.setEditable(parent, this);
		editor.addElement(labelName = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Bone name"));
		editorY += 20;
		editor.addElement(textName  = new ComponentText(          editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, getName(), this::setName));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelName);
		editor.removeElement(textName);
		labelName = null;
		textName = null;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent) {}
	
	@Override
	public boolean isVisible()
	{
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	@Override
	public Transformation getDefaultTransformation()
	{
		return transform;
	}

	@Override
	public Collection<? extends IModelEditable> getChildren()
	{
		return Collections.emptySet();
	}

	@Override
	public boolean hasChildren()
	{
		return false;
	}

	@Override
	public boolean canBeChild(IModelEditable candidate)
	{
		return false;
	}

	@Override
	public void addChild(IModelEditable child) {}

	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position) {}

	@Override
	public void addChildAfter(IModelEditable child, IModelEditable position) {}

	@Override
	public void removeChild(IModelEditable child) {}
	
	@Override
	public int getChildIndex(IModelEditable child)
	{
		return -1;
	}

	@Override
	public void addChildAt(IModelEditable child, int index) {}
	
	public void preRender(Runnable defaultTex)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().mul(transform.getTransformation());
		Main.instance.shader.updateModel();
		doPreRender(defaultTex);
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
	
	public abstract void doPreRender(Runnable defaultTex);

	public void postRenderBone(Runnable defaultTex)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().mul(transform.getTransformation());
		Main.instance.shader.updateModel();
		doPostRenderBone(defaultTex);
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
	
	public abstract void doPostRenderBone(Runnable defaultTex);

	public void postRenderChildren(Runnable defaultTex)
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().mul(transform.getTransformation());
		Main.instance.shader.updateModel();
		doPostRenderChildren(defaultTex);
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}

	public abstract void doPostRenderChildren(Runnable defaultTex);
	
	public abstract String getXMLName();

	public abstract void doCleanUp();

	public abstract BoneEffect cloneObject(RenderBone<?> clonedParent);
}