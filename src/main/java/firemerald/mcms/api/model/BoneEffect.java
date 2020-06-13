package firemerald.mcms.api.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.model.IModelEditable;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.function.TriFunction;

public abstract class BoneEffect implements IModelEditable
{
	private static final Map<String, TriFunction<Bone, AbstractElement, Float, BoneEffect>> EFFECT_TYPES = new HashMap<>();

	/**
	 * Register a Bone Effect type
	 * 
	 * @param name the bone effect's name - the bone effect's XML name <i>must</i> be {domain}:{path with "/"'s replaced with "."'s} or it will not load properly!
	 * @param constructor the constructor lambda - constructs the bone effect. see the static constructor below for examples.
	 * 
	 * @return if the bone effect was registered
	 */
	public static boolean registerBoneType(ResourceLocation name, TriFunction<Bone, AbstractElement, Float, BoneEffect> constructor)
	{
		return registerBoneType(name.toString().replace(':', '-').replace('/', '_'), constructor);
	}
	
	private static boolean registerBoneType(String name, TriFunction<Bone, AbstractElement, Float, BoneEffect> constructor)
	{
		if (EFFECT_TYPES.containsKey(name)) return false;
		else
		{
			EFFECT_TYPES.put(name, constructor);
			return true;
		}
	}
	
	public static BoneEffect constructIfRegistered(String name, @Nullable Bone parent, AbstractElement element, float scale)
	{
		TriFunction<Bone, AbstractElement, Float, BoneEffect> constructor = EFFECT_TYPES.get(name);
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
	protected Bone parent;

	public BoneEffect(String name, Bone parent)
	{
		this.name = name;
		if (parent == null) this.parent = null;
		else (this.parent = parent).addEffect(this);
	}
	
	public void loadFromXML(AbstractElement el, float scale)
	{
		name = el.getString("name", "unnamed");
	}
	
	public void addToXML(AbstractElement addTo, float scale)
	{
		AbstractElement el = addTo.addChild(getXMLName());
		addDataToXML(el, scale);
	}
	
	public void addDataToXML(AbstractElement el, float scale)
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
		Main.instance.project.onAction();
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
		return new Transformation();
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
	
	public abstract void preRender(Runnable defaultTex);

	public abstract void postRenderBone(Runnable defaultTex);

	public abstract void postRenderChildren(Runnable defaultTex);
	
	public abstract String getXMLName();

	public abstract void doCleanUp();

	public abstract BoneEffect cloneObject(Bone clonedParent);
}