package firemerald.mcms.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4d;

import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IModel;
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
	
	public void addToObj(Matrix4d trans, ObjData obj)
	{
		List<int[][]> mesh;
		obj.groupObjects.put(name, mesh = new ArrayList<>());
		for (ModelComponent component : components) component.addToObjModel(trans, obj, mesh);
	}
	
	public static ObjData createObj(IModel model)
	{
		ObjData obj = new ObjData();
		for (Bone bone : model.getRootBones()) addToObj(bone, obj, new Matrix4d());
		return obj;
	}
	
	private static void addToObj(Bone bone, ObjData obj, Matrix4d transformation)
	{
		Matrix4d trans = new Matrix4d(transformation);
		//trans.mul(bone.defaultTransform.getTransformation());
		trans.translate(bone.defaultTransform.translation);
		trans.mul(bone.defaultTransform.rotation.getQuaternion().get(new Matrix4d()));
		if (bone instanceof RenderObjectComponents) ((RenderObjectComponents) bone).addToObj(trans, obj);
		for (Bone bone2 : bone.children) addToObj(bone2, obj, trans);
	}
	
	@Override
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Map<String, Matrix4d> transformations, Matrix4d transformation)
	{
		RaytraceResult result = null;
		for (ModelComponent component : getComponents())
		{
			RaytraceResult res = component.raytrace(fx, fy, fz, dx, dy, dz, transformation);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		for (Bone child : children)
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
	public Collection<? extends IModelEditable> getChildren()
	{
		List<IModelEditable> list = new ArrayList<>();
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
	public boolean canBeChild(IModelEditable candidate)
	{
		return (candidate instanceof ModelComponent) || super.canBeChild(candidate);
	}

	@Override
	public void addChild(IModelEditable child)
	{
		if (child instanceof ModelComponent)
		{
			if (!components.contains(child)) components.add((ModelComponent) child);
		}
		else super.addChild(child);
	}

	@Override
	public void addChildBefore(IModelEditable child, IModelEditable position)
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
	public void addChildAfter(IModelEditable child, IModelEditable position)
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
	public void removeChild(IModelEditable child)
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
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		editorPanes.addBox.setParent(this);
		editorPanes.addMesh.setParent(this);
		return super.onSelect(editorPanes, editorY);
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
	public void addChildrenToXML(AbstractElement addTo)
	{
		for (ModelComponent child : this.components) child.addToXML(addTo);
		super.addChildrenToXML(addTo);
	}
	
	@Override
	public void loadChildrenFromXML(AbstractElement el)
	{
		components.clear();
		super.loadChildrenFromXML(el);
	}

	@Override
	public void tryLoadChild(AbstractElement el)
	{
		if (!ModelComponent.loadComponent(this, el)) super.tryLoadChild(el);
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IModel model)
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
	
	@Override
	public void updateTex()
	{
		getChildrenComponents().forEach(component -> component.updateTex());
		super.updateTex();
	}
}