package firemerald.mcms.model;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.Mesh;

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

	@Override
	public void texU(float texU)
	{
		super.texU(texU);
		setTexs();
	}

	@Override
	public void texV(float texV)
	{
		super.texV(texV);
		setTexs();
	}
	
	@Override
	public void setTexs()
	{
		float u1 = this.texU;
		float v1 = this.texV;
		float uScale = (float) this.getTexSizeU() / Main.instance.project.getTextureWidth();
		float vScale = (float) this.getTexSizeV() / Main.instance.project.getTextureHeight();
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
		return Textures.MODEL_ICON_MESH;
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
	public IModelEditable copy(IEditableParent newParent, IModel model)
	{
		if (newParent instanceof IComponentParent)
		{
			ComponentMeshTrue mesh = new ComponentMeshTrue(this.mesh(), (IComponentParent) newParent, this.name);
			mesh.posX(posX());
			mesh.posY(posY());
			mesh.posZ(posZ());
			mesh.offX(offX());
			mesh.offY(offY());
			mesh.offZ(offZ());
			mesh.rotation(rotation().copy());
			mesh.setTexSizeU(texSizeU);
			mesh.setTexSizeV(texSizeV);
			this.copyChildren(mesh, model);
			return mesh;
		}
		else return null;
	}
}