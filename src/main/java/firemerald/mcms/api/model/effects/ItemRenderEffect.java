package firemerald.mcms.api.model.effects;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelHolder;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.shader.ModelShaderBase;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.TransformType;

public class ItemRenderEffect extends StagedPosedBoneEffect
{
	protected int slot = 0;
	protected float scale = 1;
	protected TransformType transformType;

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, 1f);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot)
	{
		this(name, parent, transform, stage, slot, 1f);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot, float scale)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, scale, TransformType.FIXED);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot, float scale)
	{
		this(name, parent, transform, stage, slot, scale, TransformType.FIXED);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot, TransformType transformType)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, transformType);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot, TransformType transformType)
	{
		this(name, parent, transform, stage, slot, 1, transformType);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, int slot, float scale, TransformType transformType)
	{
		this(name, parent, transform, EffectRenderStage.POST_BONE, slot, scale, transformType);
	}

	public ItemRenderEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, int slot, float scale, TransformType transformType)
	{
		super(name, parent, transform, stage);
		this.slot = slot;
		this.scale = scale;
		this.transformType = transformType;
	}
	
	public int getSlot()
	{
		return slot;
	}
	
	public void setSlot(int slot)
	{
		this.slot = slot;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
	}
	
	public TransformType getTransformType()
	{
		return transformType;
	}
	
	public void setTransformType(TransformType transformType)
	{
		this.transformType = transformType;
	}
	
	public static final ResourceLocation TEX = new ResourceLocation(Main.ID, "item.png");
	
	@Override
	public void render(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTexture) //TODO
	{
		ModelShaderBase.MODEL.push();
		ModelShaderBase.MODEL.matrix().scale(scale / Main.instance.project.getScale());
		ModelShaderBase.MODEL.matrix().mul(transformType.matrix());
		Main.instance.currentModelShader.updateModel();
		Main.instance.textureManager.bindTexture(TEX);
		RenderUtil.ITEM_MESH.render();
		ModelShaderBase.MODEL.pop();
		Main.instance.currentModelShader.updateModel();
		defaultTexture.run();
	}
	
	@Override
	public void loadFromXML(AbstractElement el, float scale)
	{
		super.loadFromXML(el, scale);
		slot = el.getInt("slot", 0);
		this.scale = el.getFloat("scale", 1f);
		transformType = el.getEnum("transformType", TransformType.values(), TransformType.FIXED);
	}
	
	@Override
	public void saveToXML(AbstractElement el, float scale)
	{
		super.saveToXML(el, scale);
		el.setInt("slot", slot);
		if (this.scale != 1f) el.setFloat("scale", this.scale);
		el.setEnum("transformType", transformType);
	}
	
	@Override
	public String getXMLName()
	{
		return "item";
	}

	@Override
	public ItemRenderEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new ItemRenderEffect(this.name, clonedParent, transform.copy(), slot, scale, transformType);
	}

	@Override
	public ResourceLocation getDisplayIcon()
	{
		return Textures.MODEL_ICON_ITEM;
	}
	private ComponentFloatingLabel labelIndex;
	private ComponentTextInt indexT;
	private ComponentIncrementInt indexP, indexS;
	private ComponentFloatingLabel labelType;
	private SelectorButton typeS;
	private ComponentFloatingLabel labelScale;
	private ComponentTextFloat scaleXT;
	private ComponentIncrementFloat scaleXP, scaleXS;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		editorY = super.onSelect(editorPanes, editorY);
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelIndex = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Item index"));
		editorY += 20;
		editor.addElement(indexT      = new ComponentTextInt(     editorX      , editorY, editorX + 290 , editorY + 20, Main.instance.fontMsg, getSlot(), Integer.MIN_VALUE, Integer.MAX_VALUE, this::setSlot));
		editor.addElement(indexP      = new ComponentIncrementInt(editorX + 290 , editorY                             , indexT, 1));
		editor.addElement(indexS      = new ComponentIncrementInt(editorX + 290 , editorY + 10                        , indexT, -1));
		editorY += 20;
		editor.addElement(labelType = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Item transform type"));
		editorY += 20;
		editor.addElement(typeS      = new SelectorButton(        editorX      , editorY, editorX + 300 , editorY + 20, getTransformType(), TransformType.values(), this::setTransformType));
		editorY += 20;
		editor.addElement(labelScale = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Scale"));
		editorY += 20;
		editor.addElement(scaleXT    = new ComponentTextFloat(     editorX      , editorY, editorX + 290 , editorY + 20 , Main.instance.fontMsg, getScale(), Float.MIN_VALUE, Float.POSITIVE_INFINITY, this::setScale));
		editor.addElement(scaleXP    = new ComponentIncrementFloat(editorX + 290 , editorY                              , scaleXT, 0.0625f));
		editor.addElement(scaleXS    = new ComponentIncrementFloat(editorX + 290 , editorY + 10                         , scaleXT, -0.0625f));
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
		editor.removeElement(labelType);
		editor.removeElement(typeS);
		editor.removeElement(labelScale);
		editor.removeElement(scaleXT);
		editor.removeElement(scaleXP);
		editor.removeElement(scaleXS);
		labelIndex  = null;
		indexT      = null;
		indexP      = null;
		indexS      = null;
		labelScale = null;
		scaleXT    = null;
		scaleXP    = null;
		scaleXS    = null;
	}

	@Override
	public ItemRenderEffect copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public void doCleanUp() {}

	@Override
	public EffectRenderStage getDefaultStage()
	{
		return EffectRenderStage.POST_BONE;
	}
}