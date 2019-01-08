package firemerald.mcms.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.Element;
import firemerald.mcms.api.math.Matrix3;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.api.math.Quaternion;
import firemerald.mcms.api.math.Vec2;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.IRaytraceTarget;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.api.util.MatrixHandler;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.util.ObjUtil;

/**
 * @author test
 *
 */
public abstract class ModelComponent implements IRaytraceTarget, IComponentParent
{
	public boolean visible = true, childrenVisible = true;
	public String name;
	public IComponentParent parent;
	public final List<ModelComponent> children = new ArrayList<>();
	protected float posX = 0, posY = 0, posZ = 0, rotX = 0, rotY = 0, rotZ = 0, offX = 0, offY = 0, offZ = 0, texU = 0, texV = 0;
	/**
	 * Texture size when computing UV's, as opposed to actual size.
	 * for boxes, it allows HD textures (but bitmixing is discouraged!)
	 * for meshes, it allows you to scale the mesh UV's down to a specific area on the texture.
	 * 0 means it uses the actual texture size.
	 */
	protected int texSizeU = 0, texSizeV = 0;
	protected Matrix4 transformation = new Matrix4();
	
	public ModelComponent(String name)
	{
		parent = null;
		this.name = name;
	}
	
	public ModelComponent(IComponentParent parent, String name)
	{
		(this.parent = parent).getChildrenComponents().add(this);
		this.name = name;
	}
	
	public Matrix4 transformation()
	{
		return transformation;
	}
	
	public void updateTransform()
	{
		transformation.identity();
		transformation.translate(posX, posY, posZ);
		transformation.mul(Quaternion.forEulerXZY(rotX, rotY, rotZ).getMatrix4());
		transformation.translate(offX, offY, offZ);
	}
	
	public void updateTransformVals()
	{
		Quaternion q = new Quaternion();
		q.setFromMatrix(transformation);
		Vec3 rots = q.toEulerXZY();
		rotX = rots.x();
		rotY = rots.y();
		rotZ = rots.z();
		offX = transformation.m30();
		offY = transformation.m31();
		offZ = transformation.m32();
	}
	
	public int getTexSizeU()
	{
		return texSizeU > 0 ? texSizeU : Main.instance.texSizeU;
	}
	
	public void setTexSizeU(int size)
	{
		texSizeU = size;
		onTexSizeChange();
	}
	
	public int getTexSizeV()
	{
		return texSizeV > 0 ? texSizeV : Main.instance.texSizeV;
	}
	
	public void setTexSizeV(int size)
	{
		texSizeV = size;
		onTexSizeChange();
	}
	
	public abstract void onTexSizeChange();

	public float posX()
	{
		return posX;
	}

	public void posX(float posX)
	{
		this.posX = posX;
		updateTransform();
	}

	public float posY()
	{
		return posY;
	}

	public void posY(float posY)
	{
		this.posY = posY;
		updateTransform();
	}

	public float posZ()
	{
		return posZ;
	}

	public void posZ(float posZ)
	{
		this.posZ = posZ;
		updateTransform();
	}

	public float rotX()
	{
		return rotX;
	}

	public void rotX(float rotX)
	{
		this.rotX = rotX;
		updateTransform();
	}

	public float rotY()
	{
		return rotY;
	}

	public void rotY(float rotY)
	{
		this.rotY = rotY;
		updateTransform();
	}

	public float rotZ()
	{
		return rotZ;
	}

	public void rotZ(float rotZ)
	{
		this.rotZ = rotZ;
		updateTransform();
	}

	public float offX()
	{
		return offX;
	}

	public void offX(float offX)
	{
		this.offX = offX;
		updateTransform();
	}

	public float offY()
	{
		return offY;
	}

	public void offY(float offY)
	{
		this.offY = offY;
		updateTransform();
	}

	public float offZ()
	{
		return offZ;
	}

	public void offZ(float offZ)
	{
		this.offZ = offZ;
		updateTransform();
	}
	
	public void render()
	{
		MatrixHandler.instance.push();
		MatrixHandler.instance.multMatrix(transformation);
		if (visible) this.doRender();
		if (childrenVisible) for (ModelComponent c : children) c.render();
		MatrixHandler.instance.pop();
	}
	
	public abstract void doRender();
	
	public void cleanUp()
	{
		this.doCleanUp();
		for (ModelComponent c : children) c.cleanUp();
	}
	
	public abstract void doCleanUp();
	
	public void addToObjModel(Matrix4 transformation, ObjData obj, List<int[][]> mesh)
	{
		Matrix4 trans = new Matrix4();
		transformation.mul(this.transformation, trans);
		Matrix3 norm = trans.transpose3().invert();
		float[][][] m = generateMesh();
		for (float[][] fData : m)
		{
			int[][] vInd = new int[fData.length][3];
			for (int i = 0; i < fData.length; i++)
			{
				float[] vData = fData[i];
				vInd[i][0] = ObjUtil.getSetIndex(obj.vertices, trans.mul(vData[0], vData[1], vData[2], 1).xyz());
				vInd[i][1] = ObjUtil.getSetIndex(obj.textureCoordinates, new Vec2(vData[3], vData[4]));
				vInd[i][2] = ObjUtil.getSetIndex(obj.vertexNormals, norm.mul(vData[5], vData[6], vData[7]));
			}
			mesh.add(vInd);
		}
	}
	
	/** float[face][vertex][{x,y,z,u,v,nx,ny,nz}] **/
	public abstract float[][][] generateMesh();
	
	public RaytraceResult raytrace(float fx, float fy, float fz, float dx, float dy, float dz, Matrix4 transformation)
	{
		RaytraceResult result = visible ? doRaytrace(fx, fy, fz, dx, dy, dz, transformation = transformation.mul(this.transformation, new Matrix4())) : null;
		if (childrenVisible) for (ModelComponent child : this.children)
		{
			RaytraceResult res = child.raytrace(fx, fy, fz, dx, dy, dz, transformation);
			if (res != null && (result == null || res.m < result.m)) result = res;
		}
		return result;
	}
	
	protected abstract RaytraceResult doRaytrace(float fx, float fy, float fz, float dx, float dy, float dz, Matrix4 transformation);

	private ComponentText labelName;
	private ComponentLabel labelPos;
	private ComponentTextFloat posXT;
	private ComponentIncrementFloat posXP, posXS;
	private ComponentTextFloat posYT;
	private ComponentIncrementFloat posYP, posYS;
	private ComponentTextFloat posZT;
	private ComponentIncrementFloat posZP, posZS;
	private ComponentLabel labelRot;
	private ComponentTextFloat rotXT;
	private ComponentIncrementFloat rotXP, rotXS;
	private ComponentTextFloat rotYT;
	private ComponentIncrementFloat rotYP, rotYS;
	private ComponentTextFloat rotZT;
	private ComponentIncrementFloat rotZP, rotZS;
	private ComponentLabel labelOff;
	private ComponentTextFloat offXT;
	private ComponentIncrementFloat offXP, offXS;
	private ComponentTextFloat offYT;
	private ComponentIncrementFloat offYP, offYS;
	private ComponentTextFloat offZT;
	private ComponentIncrementFloat offZP, offZS;
	
	@Override
	public void onSelect(EditorPanes editorPanes)
	{
		editorPanes.addBone.enabled = false;
		editorPanes.addBox.setParent(this);
		editorPanes.addMesh.setParent(this);
		editorPanes.copy.setEditable(parent, this);
		editorPanes.remove.setEditable(parent, this);
		
		GuiElementContainer editor = editorPanes.editor;
		float editorX = editorPanes.editorX;
		float editorY = editorPanes.editorY;
		editor.addElement(labelName = new ComponentText(          editorX      , editorY      , editorX + 300, editorY + 20 , Main.instance.fontMsg, name, string -> this.name = string));
		editor.addElement(labelPos  = new ComponentLabel(         editorX      , editorY + 20 , editorX + 300, editorY + 40 , Main.instance.fontMsg, "Position"));
		editor.addElement(posXT     = new ComponentTextFloat(     editorX      , editorY + 40 , editorX + 90 , editorY + 60 , Main.instance.fontMsg, posX, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.posX(value)));
		editor.addElement(posXP     = new ComponentIncrementFloat(editorX + 90 , editorY + 40                   , posXT, 1));
		editor.addElement(posXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 50                   , posXT, -1));
		editor.addElement(posYT     = new ComponentTextFloat(     editorX + 100, editorY + 40 , editorX + 190, editorY + 60 , Main.instance.fontMsg, posY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.posY(value)));
		editor.addElement(posYP     = new ComponentIncrementFloat(editorX + 190, editorY + 40                   , posYT, 1));
		editor.addElement(posYS     = new ComponentIncrementFloat(editorX + 190, editorY + 50                   , posYT, -1));
		editor.addElement(posZT     = new ComponentTextFloat(     editorX + 200, editorY + 40 , editorX + 290, editorY + 60 , Main.instance.fontMsg, posZ, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.posZ(value)));
		editor.addElement(posZP     = new ComponentIncrementFloat(editorX + 290, editorY + 40                   , posZT, 1));
		editor.addElement(posZS     = new ComponentIncrementFloat(editorX + 290, editorY + 50                   , posZT, -1));
		editor.addElement(labelRot  = new ComponentLabel(         editorX      , editorY + 60 , editorX + 300, editorY + 80 , Main.instance.fontMsg, "Rotation"));
		editor.addElement(rotXT     = new ComponentTextFloat(     editorX      , editorY + 80 , editorX + 90 , editorY + 100, Main.instance.fontMsg, rotX, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.rotX(value)));
		editor.addElement(rotXP     = new ComponentIncrementFloat(editorX + 90 , editorY + 80                   , rotXT, 1));
		editor.addElement(rotXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 90                   , rotXT, -1));
		editor.addElement(rotYT     = new ComponentTextFloat(     editorX + 100, editorY + 80 , editorX + 190, editorY + 100, Main.instance.fontMsg, rotY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.rotY(value)));
		editor.addElement(rotYP     = new ComponentIncrementFloat(editorX + 190, editorY + 80                   , rotYT, 1));
		editor.addElement(rotYS     = new ComponentIncrementFloat(editorX + 190, editorY + 90                   , rotYT, -1));
		editor.addElement(rotZT     = new ComponentTextFloat(     editorX + 200, editorY + 80 , editorX + 290, editorY + 100, Main.instance.fontMsg, rotZ, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.rotZ(value)));
		editor.addElement(rotZP     = new ComponentIncrementFloat(editorX + 290, editorY + 80                   , rotZT, 1));
		editor.addElement(rotZS     = new ComponentIncrementFloat(editorX + 290, editorY + 90                   , rotZT, -1));
		editor.addElement(labelOff  = new ComponentLabel(         editorX      , editorY + 100, editorX + 300, editorY + 120, Main.instance.fontMsg, "Offset"));
		editor.addElement(offXT     = new ComponentTextFloat(     editorX      , editorY + 120, editorX + 90 , editorY + 140, Main.instance.fontMsg, offX, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.offX(value)));
		editor.addElement(offXP     = new ComponentIncrementFloat(editorX + 90 , editorY + 120                  , offXT, 1));
		editor.addElement(offXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 130                  , offXT, -1));
		editor.addElement(offYT     = new ComponentTextFloat(     editorX + 100, editorY + 120, editorX + 190, editorY + 140, Main.instance.fontMsg, offY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.offY(value)));
		editor.addElement(offYP     = new ComponentIncrementFloat(editorX + 190, editorY + 120                  , offYT, 1));
		editor.addElement(offYS     = new ComponentIncrementFloat(editorX + 190, editorY + 130                  , offYT, -1));
		editor.addElement(offZT     = new ComponentTextFloat(     editorX + 200, editorY + 120, editorX + 290, editorY + 140, Main.instance.fontMsg, offZ, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, value -> this.offZ(value)));
		editor.addElement(offZP     = new ComponentIncrementFloat(editorX + 290, editorY + 120                  , offZT, 1));
		editor.addElement(offZS     = new ComponentIncrementFloat(editorX + 290, editorY + 130                  , offZT, -1));
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		editorPanes.addBone.enabled = true;
		editorPanes.addBox.setParent(null);
		editorPanes.addMesh.setParent(null);
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
		editor.removeElement(labelOff);
		editor.removeElement(offXT);
		editor.removeElement(offXP);
		editor.removeElement(offXS);
		editor.removeElement(offYT);
		editor.removeElement(offYP);
		editor.removeElement(offYS);
		editor.removeElement(offZT);
		editor.removeElement(offZP);
		editor.removeElement(offZS);
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
		labelOff  = null;
		offXT     = null;
		offXP     = null;
		offXS     = null;
		offYT     = null;
		offYP     = null;
		offYS     = null;
		offZT     = null;
		offZP     = null;
		offZS     = null;
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
		return candidate instanceof ModelComponent;
	}

	@Override
	public void addChild(IEditable child)
	{
		if (child instanceof ModelComponent) this.children.add((ModelComponent) child);
	}

	@Override
	public void addChildBefore(IEditable child, IEditable position)
	{
		if (child instanceof ModelComponent)
		{
			if (!children.contains(child))
			{
				int pos = this.children.indexOf(position);
				if (pos < 0) pos = 0;
				this.children.add(pos, (ModelComponent) child);
			}
		}
	}

	@Override
	public void addChildAfter(IEditable child, IEditable position)
	{
		if (child instanceof ModelComponent)
		{
			if (!children.contains(child))
			{
				int pos = this.children.indexOf(position) + 1;
				if (pos <= 0) pos = this.children.size();
				this.children.add(pos, (ModelComponent) child);
			}
		}
	}

	@Override
	public void removeChild(IEditable child)
	{
		if (child instanceof ModelComponent) this.children.remove(child);
	}

	@Override
	public void movedTo(IEditableParent oldParent, IEditableParent newParent)
	{
		this.parent = oldParent instanceof IComponentParent ? (IComponentParent) oldParent : null;
		Matrix4 targetTransform = getTransformation();
		this.parent = newParent instanceof IComponentParent ? (IComponentParent) newParent : null;
		Matrix4 parentTransform = parent == null ? new Matrix4() : parent.getTransformation();
		this.transformation = parentTransform.invert().mul(targetTransform);
		this.updateTransformVals();
	}

	@Override
	public Matrix4 getTransformation()
	{
		Matrix4 mat = new Matrix4(this.transformation);
		if (parent != null) parent.getTransformation().mul(mat, mat);
		return mat;
	}

	@Override
	public List<ModelComponent> getChildrenComponents()
	{
		return children;
	}
	
	@Override
	public boolean isVisible()
	{
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		childrenVisible = this.visible = visible;
	}
	
	public void addToXML(Element addTo)
	{
		Element el = addTo.addChild(getXMLName());
		addData(el);
		addChildrenToXML(el);
	}
	
	public abstract String getXMLName();
	
	public void addData(Element el)
	{
		el.setString("name", name);
		el.setFloat("posX", posX);
		el.setFloat("posY", posY);
		el.setFloat("posZ", posZ);
		el.setFloat("rotX", rotX);
		el.setFloat("rotY", rotY);
		el.setFloat("rotZ", rotZ);
		el.setFloat("offX", offX);
		el.setFloat("offY", offY);
		el.setFloat("offZ", offZ);
		el.setFloat("texU", texU);
		el.setFloat("texV", texV);
		el.setInt("texSizeU", texSizeU);
		el.setInt("texSizeV", texSizeV);
	}
	
	public void addChildrenToXML(Element addTo)
	{
		for (ModelComponent child : this.children) child.addToXML(addTo);
	}
	
	public void loadFromXML(Element el)
	{
		name = el.getString("name", "null");
		posX = el.getFloat("posX", posX);
		posY = el.getFloat("posY", posY);
		posZ = el.getFloat("posZ", posZ);
		rotX = el.getFloat("rotX", rotX);
		rotY = el.getFloat("rotY", rotY);
		rotZ = el.getFloat("rotZ", rotZ);
		offX = el.getFloat("offX", offX);
		offY = el.getFloat("offY", offY);
		offZ = el.getFloat("offZ", offZ);
		updateTransform();
		texU = el.getFloat("texU", texU);
		texV = el.getFloat("texV", texV);
		texSizeU = el.getInt("texSizeU", texSizeU);
		texSizeV = el.getInt("texSizeV", texSizeV);
		onTexSizeChange();
		loadChildrenFromXML(el);
	}
	
	public void loadChildrenFromXML(Element el)
	{
		children.clear();
		for (Element child : el.getChildren()) tryLoadChild(child);
	}

	public void tryLoadChild(Element el)
	{
		ModelComponent.loadComponent(this, el);
	}
	
	public void copyChildren(ModelComponent newParent, IModel model)
	{
		for (ModelComponent child : children) child.copy(newParent, model);
	}
	
	public static boolean loadComponent(IComponentParent parent, Element el)
	{
		switch (el.getName())
		{
		case "box":
		{
			ComponentBox box = new ComponentBox(parent, el.getString("name", "unnamed box"));
			box.loadFromXML(el);
			return true;
		}
		case "mesh":
		{
			ComponentMeshTrue mesh = new ComponentMeshTrue(new Mesh(), parent, el.getString("name", "unnamed mesh"));
			mesh.loadFromXML(el);
			return true;
		}
		default: return false;
		}
	}
}