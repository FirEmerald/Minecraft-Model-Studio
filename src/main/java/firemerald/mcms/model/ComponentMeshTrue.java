package firemerald.mcms.model;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.ModelMesh;

public class ComponentMeshTrue extends ComponentMesh
{
	private float[] origUV;
	private float[] newUV;
	/**
	 * Texture size when computing UV's, as opposed to actual size.
	 * for meshes, it allows you to scale the mesh UV's down to a specific area on the texture.
	 * 0 means it uses the actual texture size.
	 */
	protected float texSizeU = 0, texSizeV = 0;
	
	public ComponentMeshTrue(ModelMesh mesh, String name)
	{
		super(mesh, name);
		origUV = mesh.getTexs();
		newUV = new float[origUV.length];
		setTexs();
	}
	
	public ComponentMeshTrue(ModelMesh mesh, IComponentParent parent, String name)
	{
		super(mesh, parent, name);
		origUV = mesh.getTexs();
		newUV = new float[origUV.length];
		setTexs();
	}
	
	public ComponentMeshTrue(IComponentParent parent, ComponentMeshTrue from)
	{
		super(parent, from);
		this.origUV = MiscUtil.copy(from.origUV);
		this.newUV = MiscUtil.copy(from.newUV);
		this.texSizeU = from.texSizeU;
		this.texSizeV = from.texSizeV;
		setTexs();
	}

	@Override
	public void texU(float texU)
	{
		this.texU = texU;
		setTexs();
	}

	@Override
	public void texV(float texV)
	{
		this.texV = texV;
		setTexs();
	}
	
	public float getTexSizeU()
	{
		return texSizeU > 0 ? texSizeU : Main.instance.project.getTextureWidth();
	}
	
	public void setTexSizeU(float size)
	{
		texSizeU = size;
		setTexs();
	}
	
	public float getTexSizeV()
	{
		return texSizeV > 0 ? texSizeV : Main.instance.project.getTextureHeight();
	}
	
	public void setTexSizeV(float size)
	{
		texSizeV = size;
		setTexs();
	}
	
	@Override
	public void setTexs()
	{
		float uScale = this.getTexSizeU() / Main.instance.project.getTextureWidth();
		float vScale = this.getTexSizeV() / Main.instance.project.getTextureHeight();
		float u1 = this.texU / Main.instance.project.getTextureWidth();
		float v1 = this.texV / Main.instance.project.getTextureHeight();
		for (int i = 0; i < origUV.length; i += 2)
		{
			newUV[i] = origUV[i] * uScale + u1;
			newUV[i + 1] = origUV[i + 1] * vScale + v1;
		}
		this.mesh().setTexCoords(newUV);
		setTexMesh();
	}
	
	public void unsetTexs()
	{
		float uScale = Main.instance.project.getTextureWidth() / this.getTexSizeU();
		float vScale = Main.instance.project.getTextureHeight() / this.getTexSizeV();
		float u1 = this.texU / this.getTexSizeU();
		float v1 = this.texV / this.getTexSizeV();
		for (int i = 0; i < origUV.length; i += 2)
		{
			origUV[i] = newUV[i] * uScale - u1;
			origUV[i + 1] = newUV[i + 1] * vScale - v1;
		}
		this.mesh().setTexCoords(newUV);
		setTexMesh();
	}

	@Override
	public ResourceLocation getDisplayIcon()
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
		if (texSizeU != 0) el.setFloat("texSizeU", texSizeU);
		if (texSizeV != 0) el.setFloat("texSizeV", texSizeV);
		this.mesh().setTexCoords(origUV);
		this.mesh().saveToXML(el);
		this.mesh().setTexCoords(newUV);
	}
	
	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		try
		{
			this.mesh = ModelMesh.readFromXML(el);
			origUV = mesh().getTexs();
			newUV = new float[origUV.length];
			setTexs();
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load mesh from data", e);
		}
		texSizeU = el.getFloat("texSizeU", 0);
		texSizeV = el.getFloat("texSizeV", 0);
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> model)
	{
		if (newParent instanceof IComponentParent)
		{
			ComponentMeshTrue mesh = cloneSelf((IComponentParent) newParent);
			this.copyChildren(mesh, model);
			return mesh;
		}
		else return null;
	}

	@Override
	public ComponentMeshTrue cloneSelf(IComponentParent clonedParent)
	{
		return new ComponentMeshTrue(clonedParent, this);
	}
	
	private ComponentFloatingLabel labelTexSize;
	private ComponentTextFloat texW;
	private ComponentIncrementFloat texWP, texWS;
	private ComponentTextFloat texH;
	private ComponentIncrementFloat texHP, texHS;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		editorY = super.onSelect(editorPanes, editorY);
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelTexSize = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, "Texture Size"));
		editorY += 20;
		editor.addElement(texW         = new ComponentTextFloat(     editorX      , editorY, editorX + 140, editorY + 20, Main.instance.fontMsg, this.texSizeU, 0, Float.POSITIVE_INFINITY, value -> this.setTexSizeU(value), "default"));
		if (texSizeU == 0) texW.setTextNoUpdate("");
		editor.addElement(texWP        = new ComponentIncrementFloat(editorX + 140, editorY                             , texW, 1));
		editor.addElement(texWS        = new ComponentIncrementFloat(editorX + 140, editorY + 10                        , texW, -1));
		editor.addElement(texH         = new ComponentTextFloat(     editorX + 150, editorY, editorX + 290, editorY + 20, Main.instance.fontMsg, this.texSizeV, 0, Float.POSITIVE_INFINITY, value -> this.setTexSizeV(value), "default"));
		if (texSizeV == 0) texH.setTextNoUpdate("");
		editor.addElement(texHP        = new ComponentIncrementFloat(editorX + 290, editorY                             , texH, 1));
		editor.addElement(texHS        = new ComponentIncrementFloat(editorX + 290, editorY + 10                        , texH, -1));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelTexSize);
		editor.removeElement(texW);
		editor.removeElement(texWP);
		editor.removeElement(texWS);
		editor.removeElement(texH);
		editor.removeElement(texHP);
		editor.removeElement(texHS);
		labelTexSize  = null;
		texW     = null;
		texWP    = null;
		texWS    = null;
		texH     = null;
		texHP    = null;
		texHS    = null;
	}
}