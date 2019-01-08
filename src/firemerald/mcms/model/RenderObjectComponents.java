package firemerald.mcms.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.Element;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.Model;
import firemerald.mcms.api.model.MultiModel;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.util.MiscUtil;

public class RenderObjectComponents extends Bone implements IComponentParent
{
	private final List<ModelComponent> components = new ArrayList<>();
	
	public RenderObjectComponents(String name, Transformation defaultTransform)
	{
		super(name, defaultTransform);
	}

	public RenderObjectComponents(String name, Transformation defaultTransform, Bone parent)
	{
		super(name, defaultTransform, parent);
	}

	@Override
	public void doRender()
	{
		if (childrenVisible) for (ModelComponent component : components) component.render();
	}
	
	public void addComponent(ModelComponent component)
	{
		components.add(component);
	}
	
	public Collection<ModelComponent> getComponents()
	{
		return components;
	}
	
	public void removeComponent(String name)
	{
		for (int i = 0; i < components.size(); i++)
		{
			ModelComponent component = components.get(i);
			if (component.name.equals(name))
			{
				components.remove(i);
				component.cleanUp();
				break;
			}
		}
	}

	@Override
	public void doCleanUp()
	{
		for (ModelComponent component : components) component.cleanUp();
	}
	
	public void addToObj(Matrix4 trans, ObjData obj)
	{
		List<int[][]> mesh;
		obj.groupObjects.put(name, mesh = new ArrayList<>());
		for (ModelComponent component : components) component.addToObjModel(trans, obj, mesh);
	}
	
	public static ObjData createObj(MultiModel model)
	{
		ObjData obj = new ObjData();
		for (Bone bone : model.getBase()) addToObj(bone, obj, new Matrix4());
		return obj;
	}
	
	public static ObjData createObj(Model model)
	{
		ObjData obj = new ObjData();
		addToObj(model.getBase(), obj, new Matrix4());
		return obj;
	}
	
	private static void addToObj(Bone bone, ObjData obj, Matrix4 transformation)
	{
		Matrix4 trans = new Matrix4(transformation);
		trans.translate(bone.defaultTransform.translation);
		trans.mul(bone.defaultTransform.rotation.getMatrix4());
		if (bone instanceof RenderObjectComponents) ((RenderObjectComponents) bone).addToObj(trans, obj);
		for (Bone bone2 : bone.children) addToObj(bone2, obj, trans);
	}
	
	@Override
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4> transformations, Matrix4 transformation)
	{
		RaytraceResult result = null;
		for (ModelComponent component : getComponents())
		{
			RaytraceResult res = component.raytrace(fx, fy, fz, dx, dy, dz, transformation);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		for (Bone child : children)
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
	public Collection<? extends IEditable> getChildren()
	{
		List<IEditable> list = new ArrayList<>();
		list.addAll(components);
		list.addAll(super.getChildren());
		return list;
	}

	@Override
	public boolean hasChildren()
	{
		return !components.isEmpty() || super.hasChildren();
	}

	@Override
	public boolean canBeChild(IEditable candidate)
	{
		return (candidate instanceof ModelComponent) || super.canBeChild(candidate);
	}

	@Override
	public void addChild(IEditable child)
	{
		if (child instanceof ModelComponent)
		{
			if (!components.contains(child)) components.add((ModelComponent) child);
		}
		else super.addChild(child);
	}

	@Override
	public void addChildBefore(IEditable child, IEditable position)
	{
		if (child instanceof ModelComponent)
		{
			if (!components.contains(child))
			{
				int pos = this.components.indexOf(position);
				if (pos < 0) pos = 0;
				this.components.add(pos, (ModelComponent) child);
			}
		}
		else super.addChildBefore(child, position);
	}

	@Override
	public void addChildAfter(IEditable child, IEditable position)
	{
		if (child instanceof ModelComponent)
		{
			if (!components.contains(child))
			{
				int pos = this.components.indexOf(position) + 1;
				if (pos <= 0) pos = this.components.size();
				this.components.add(pos, (ModelComponent) child);
			}
		}
		else super.addChildAfter(child, position);
	}

	@Override
	public void removeChild(IEditable child)
	{
		if (child instanceof ModelComponent) components.remove(child);
		else super.removeChild(child);
	}

	@Override
	public List<ModelComponent> getChildrenComponents()
	{
		return components;
	}
	
	@Override
	public void onSelect(EditorPanes editorPanes)
	{
		editorPanes.addBox.setParent(this);
		editorPanes.addMesh.setParent(this);
		super.onSelect(editorPanes);
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		editorPanes.addBox.setParent(null);
		editorPanes.addMesh.setParent(null);
		super.onDeselect(editorPanes);
	}
	
	@Override
	public String getXMLName()
	{
		return "component_holder";
	}
	
	@Override
	public void addChildrenToXML(Element addTo)
	{
		for (ModelComponent child : this.components) child.addToXML(addTo);
		super.addChildrenToXML(addTo);
	}
	
	@Override
	public void loadChildrenFromXML(Element el)
	{
		components.clear();
		super.loadChildrenFromXML(el);
	}

	@Override
	public void tryLoadChild(Element el)
	{
		if (!ModelComponent.loadComponent(this, el)) super.tryLoadChild(el);
	}

	@Override
	public IEditable copy(IEditableParent newParent, IModel model)
	{
		RenderObjectComponents bone = null;
		if (newParent instanceof Bone)
		{
			bone = new RenderObjectComponents(MiscUtil.getNewBoneName(name, model), new Transformation(defaultTransform), (Bone) newParent);
			model.updateBonesList();
		}
		else if (newParent instanceof MultiModel)
		{
			bone = new RenderObjectComponents(MiscUtil.getNewBoneName(name, model), new Transformation(defaultTransform));
			((MultiModel) newParent).addBaseBone(bone);
		}
		if (bone != null)
		{
			copyChildren(bone, model);
			for (ModelComponent child : this.components) child.copy(bone, model);
		}
		return bone;
	}
}