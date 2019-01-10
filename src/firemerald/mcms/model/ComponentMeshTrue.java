package firemerald.mcms.model;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.util.Textures;

public class ComponentMeshTrue extends ComponentMesh
{
	private final float[] origUV;
	private final float[] newUV;
	
	public ComponentMeshTrue(Mesh mesh, String name)
	{
		super(mesh, name);
		origUV = mesh.getTexs();
		newUV = new float[origUV.length];
	}
	
	public ComponentMeshTrue(Mesh mesh, IComponentParent parent, String name)
	{
		super(mesh, parent, name);
		origUV = mesh.getTexs();
		newUV = new float[origUV.length];
	}
	
	public void setTexs()
	{
		float u1 = this.texU;
		float v1 = this.texV;
		float uScale = (float) this.getTexSizeU() / Main.instance.texSizeU;
		float vScale = (float) this.getTexSizeV() / Main.instance.texSizeV;
		for (int i = 0; i < origUV.length; i += 2)
		{
			newUV[i] = u1 + origUV[i] * uScale;
			newUV[i + 1] = v1 + origUV[i + 1] * vScale;
		}
		this.mesh().setTexCoords(newUV);
	}

	@Override
	public String getDisplayIcon()
	{
		return Textures.EDITABLE_ICON_MESH;
	}

	@Override
	public void onTexSizeChange()
	{
		setTexs();
	}

	@Override
	public String getXMLName()
	{
		return "mesh";
	}
	@Override
	public void addData(AbstractElement el)
	{
		super.addData(el);
		this.mesh().saveToXML(el);
	}
	
	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		try
		{
			this.mesh().loadFromXML(el);
		}
		catch (Exception e)
		{
			Main.LOGGER.log(Level.WARN, e);
		}
	}

	@Override
	public IEditable copy(IEditableParent newParent, IModel model)
	{
		if (newParent instanceof IComponentParent)
		{
			ComponentMeshTrue mesh = new ComponentMeshTrue(this.mesh(), (IComponentParent) newParent, this.name);
			mesh.posX(posX);
			mesh.posY(posY);
			mesh.posZ(posZ);
			mesh.offX(offX);
			mesh.offY(offY);
			mesh.offZ(offZ);
			mesh.rotX(rotX);
			mesh.rotY(rotY);
			mesh.rotZ(rotZ);
			mesh.setTexSizeU(texSizeU);
			mesh.setTexSizeV(texSizeV);
			this.copyChildren(mesh, model);
			return mesh;
		}
		else return null;
	}
}