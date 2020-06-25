package firemerald.mcms.api.model.effects;

import static org.lwjgl.opengl.GL15.*;

import org.eclipse.jdt.annotation.Nullable;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditableParent;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.Mesh;

public class FluidRenderEffect extends StagedPosedBoneEffect
{
	protected int index = 0;
	protected float sizeX = 1, sizeY = 1, sizeZ = 1, margin = 0.00390625f;
	
	private final Mesh mesh = new Mesh(new float[24 * 3], new float[24 * 2], new float[] {
				1, 0, 0,
				1, 0, 0,
				1, 0, 0,
				1, 0, 0,
				0, 1, 0,
				0, 1, 0,
				0, 1, 0,
				0, 1, 0,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				-1, 0, 0,
				-1, 0, 0,
				-1, 0, 0,
				-1, 0, 0,
				0, -1, 0,
				0, -1, 0,
				0, -1, 0,
				0, -1, 0,
				0, 0, -1,
				0, 0, -1,
				0, 0, -1,
				0, 0, -1
		}, new int[] {
				0, 1, 3,
				3, 1, 2,
				4, 5, 7,
				7, 5, 6,
				8, 9, 11,
				11, 9, 10,
				12, 13, 15,
				15, 13, 14,
				16, 17, 19,
				19, 17, 18,
				20, 21, 23,
				23, 21, 22,
		}, Mesh.DrawMode.TRIANGLES, GL_DYNAMIC_DRAW);

	public FluidRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int index)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, index);
	}

	public FluidRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int index)
	{
		this(name, parent, transform, index, 1f, 1f, 1f, 0.00390625f);
	}

	public FluidRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int index, float sizeX, float sizeY, float sizeZ, float margin)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, index, sizeX, sizeY, sizeZ, margin);
	}

	public FluidRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int index, float sizeX, float sizeY, float sizeZ, float margin)
	{
		super(name, parent, transform, stage);
		this.index = index;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.margin = margin;
		setMesh();
	}
	
	public void setMesh()
	{
		float minX = margin;
		float minY = margin;
		float minZ = margin;
		float maxX = sizeX - margin;
		float maxY = sizeY - margin;
		float maxZ = sizeZ - margin;
		mesh.setPositions(new float[] {
				maxX, minY, maxZ,
				maxX, minY, minZ,
				maxX, maxY, minZ,
				maxX, maxY, maxZ,
				
				minX, maxY, maxZ,
				maxX, maxY, maxZ,
				maxX, maxY, minZ,
				minX, maxY, minZ,
				
				minX, minY, maxZ,
				maxX, minY, maxZ,
				maxX, maxY, maxZ,
				minX, maxY, maxZ,
				
				minX, minY, minZ,
				minX, minY, maxZ,
				minX, maxY, maxZ,
				minX, maxY, minZ,
				
				maxX, minY, maxZ,
				minX, minY, maxZ,
				minX, minY, minZ,
				maxX, minY, minZ,
				
				maxX, minY, minZ,
				minX, minY, minZ,
				minX, maxY, minZ,
				maxX, maxY, minZ
		});
		maxX = (maxX - minX) / 16;
		maxY = (maxY - minY) / 16;
		maxZ = (maxZ - minZ) / 16;
		mesh.setTexCoords(new float[] {
				maxZ, maxY,
				0   , maxY,
				0   , 0   ,
				maxZ, 0   , //2
				
				maxX, maxZ,
				0   , maxZ,
				0   , 0   ,
				maxX, 0   , //0
				
				maxX, maxY,
				0   , maxY,
				0   , 0   ,
				maxX, 0   , //3

				maxZ, maxY,
				0   , maxY,
				0   , 0   ,
				maxZ, 0   , //4
				
				0   , 0   ,
				maxX, 0   , 
				maxX, maxZ,
				0   , maxZ, //1
				
				maxX, maxY,
				0   , maxY,
				0   , 0   ,
				maxX, 0    //5
		});
	}
	
	public void setSizeX(float sizeX)
	{
		this.sizeX = sizeX;
		setMesh();
	}
	
	public float getSizeX()
	{
		return sizeX;
	}
	
	public void setSizeY(float sizeY)
	{
		this.sizeY = sizeY;
		setMesh();
	}
	
	public float getSizeY()
	{
		return sizeY;
	}
	
	public void setSizeZ(float sizeZ)
	{
		this.sizeZ = sizeZ;
		setMesh();
	}
	
	public float getSizeZ()
	{
		return sizeZ;
	}
	
	public void setMargin(float margin)
	{
		this.margin = margin;
		setMesh();
	}
	
	public float getMargin()
	{
		return margin;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
		setMesh();
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public static final ResourceLocation TEX = new ResourceLocation(Main.ID, "water.png");
	
	@Override
	public void render(Runnable defaultTexture) //TODO
	{
		Shader.MODEL.push();
		Shader.MODEL.matrix().mul(transform.getTransformation());
		Main.instance.shader.updateModel();
		Main.instance.textureManager.bindTexture(TEX);
		mesh.render();
		defaultTexture.run();
		Shader.MODEL.pop();
		Main.instance.shader.updateModel();
	}
	
	@Override
	public void loadFromXML(AbstractElement el, float scale)
	{
		super.loadFromXML(el, scale);
		index = el.getInt("index", 0);
		this.sizeX = el.getFloat("sizeX", 1f) * scale;
		this.sizeY = el.getFloat("sizeY", 1f) * scale;
		this.sizeZ = el.getFloat("sizeZ", 1f) * scale;
		this.margin = el.getFloat("margin", 0f) * scale;
		setMesh();
	}
	
	@Override
	public void addDataToXML(AbstractElement el, float scale)
	{
		super.addDataToXML(el, scale);
		el.setInt("index", index);
		el.setFloat("sizeX", this.sizeX * scale);
		el.setFloat("sizeY", this.sizeY * scale);
		el.setFloat("sizeZ", this.sizeZ * scale);
		el.setFloat("margin", this.margin * scale);
	}

	private ComponentFloatingLabel labelIndex;
	private ComponentTextInt indexT;
	private ComponentIncrementInt indexP, indexS;
	private ComponentFloatingLabel labelSize;
	private ComponentTextFloat sizeXT;
	private ComponentIncrementFloat sizeXP, sizeXS;
	private ComponentTextFloat sizeYT;
	private ComponentIncrementFloat sizeYP, sizeYS;
	private ComponentTextFloat sizeZT;
	private ComponentIncrementFloat sizeZP, sizeZS;
	private ComponentFloatingLabel labelMargin;
	private ComponentTextFloat marginXT;
	private ComponentIncrementFloat marginXP, marginXS;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editorY = super.onSelect(editorPanes, editorY);
		
		editor.addElement(labelIndex = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Fluid index"));
		editorY += 20;
		editor.addElement(indexT      = new ComponentTextInt(     editorX      , editorY, editorX + 290 , editorY + 20, Main.instance.fontMsg, getIndex(), Integer.MIN_VALUE, Integer.MAX_VALUE, this::setIndex));
		editor.addElement(indexP      = new ComponentIncrementInt(editorX + 290 , editorY                             , indexT, 1));
		editor.addElement(indexS      = new ComponentIncrementInt(editorX + 290 , editorY + 10                        , indexT, -1));
		editorY += 20;
		editor.addElement(labelSize   = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Size"));
		editorY += 20;
		editor.addElement(sizeXT      = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20 , Main.instance.fontMsg, getSizeX(), 0, Float.POSITIVE_INFINITY, this::setSizeX));
		editor.addElement(sizeXP      = new ComponentIncrementFloat(editorX + 90 , editorY                              , sizeXT, 1));
		editor.addElement(sizeXS      = new ComponentIncrementFloat(editorX + 90 , editorY + 10                         , sizeXT, -1));
		editor.addElement(sizeYT      = new ComponentTextFloat(     editorX + 100, editorY , editorX + 190, editorY + 20, Main.instance.fontMsg, getSizeY(), 0, Float.POSITIVE_INFINITY, this::setSizeY));
		editor.addElement(sizeYP      = new ComponentIncrementFloat(editorX + 190, editorY                              , sizeYT, 1));
		editor.addElement(sizeYS      = new ComponentIncrementFloat(editorX + 190, editorY + 10                         , sizeYT, -1));
		editor.addElement(sizeZT      = new ComponentTextFloat(     editorX + 200, editorY , editorX + 290, editorY + 20, Main.instance.fontMsg, getSizeZ(), 0, Float.POSITIVE_INFINITY, this::setSizeZ));
		editor.addElement(sizeZP      = new ComponentIncrementFloat(editorX + 290, editorY                              , sizeZT, 1));
		editor.addElement(sizeZS      = new ComponentIncrementFloat(editorX + 290, editorY + 10                         , sizeZT, -1));
		editorY += 20;
		editor.addElement(labelMargin = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Margin"));
		editorY += 20;
		editor.addElement(marginXT    = new ComponentTextFloat(     editorX      , editorY, editorX + 290 , editorY + 20 , Main.instance.fontMsg, getMargin(), 0, Float.POSITIVE_INFINITY, this::setMargin));
		editor.addElement(marginXP    = new ComponentIncrementFloat(editorX + 290 , editorY                              , marginXT, 0.00390625f));
		editor.addElement(marginXS    = new ComponentIncrementFloat(editorX + 290 , editorY + 10                         , marginXT, -0.00390625f));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelIndex);
		editor.removeElement(indexT);
		editor.removeElement(indexP);
		editor.removeElement(indexS);
		editor.removeElement(labelSize);
		editor.removeElement(sizeXT);
		editor.removeElement(sizeXP);
		editor.removeElement(sizeXS);
		editor.removeElement(sizeYT);
		editor.removeElement(sizeYP);
		editor.removeElement(sizeYS);
		editor.removeElement(sizeZT);
		editor.removeElement(sizeZP);
		editor.removeElement(sizeZS);
		editor.removeElement(labelMargin);
		editor.removeElement(marginXT);
		editor.removeElement(marginXP);
		editor.removeElement(marginXS);
		labelIndex  = null;
		indexT      = null;
		indexP      = null;
		indexS      = null;
		labelSize   = null;
		sizeXT      = null;
		sizeXP      = null;
		sizeXS      = null;
		sizeYT      = null;
		sizeYP      = null;
		sizeYS      = null;
		sizeZT      = null;
		sizeZP      = null;
		sizeZS      = null;
		labelMargin = null;
		marginXT    = null;
		marginXP    = null;
		marginXS    = null;
	}
	
	@Override
	public String getXMLName()
	{
		return "fluid";
	}

	@Override
	public FluidRenderEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new FluidRenderEffect(this.name, clonedParent, transform.copy(), index, sizeX, sizeY, sizeZ, margin);
	}
	
	@Override
	public void doCleanUp()
	{
		mesh.cleanUp();
	}

	@Override
	public ResourceLocation getDisplayIcon()
	{
		return Textures.MODEL_ICON_FLUID;
	}

	@Override
	public FluidRenderEffect copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public EffectRenderStage getDefaultStage()
	{
		return EffectRenderStage.POST_BONE;
	}
}