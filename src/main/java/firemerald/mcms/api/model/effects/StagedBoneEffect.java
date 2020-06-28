package firemerald.mcms.api.model.effects;

import org.eclipse.jdt.annotation.Nullable;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.model.EditorPanes;

public abstract class StagedBoneEffect extends BoneEffect
{
	public EffectRenderStage stage;

	public StagedBoneEffect(String name, @Nullable RenderBone<?> parent, EffectRenderStage stage)
	{
		super(name, parent);
		this.stage = stage;
	}

	public StagedBoneEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage)
	{
		super(name, parent, transform);
		this.stage = stage;
	}
	
	public abstract EffectRenderStage getDefaultStage();
	
	@Override
	public void loadFromXML(AbstractElement el, float scale)
	{
		super.loadFromXML(el, scale);
		this.stage = el.getEnum("renderStage", EffectRenderStage.values(), getDefaultStage());
	}
	
	@Override
	public void saveToXML(AbstractElement el, float scale)
	{
		super.saveToXML(el, scale);
		el.setEnum("renderStage", stage);
	}
	
	@Override
	public void doPreRender(Runnable defaultTex)
	{
		if (stage == EffectRenderStage.PRE_BONE) render(defaultTex);
	}

	@Override
	public void doPostRenderBone(Runnable defaultTex)
	{
		if (stage == EffectRenderStage.POST_BONE) render(defaultTex);
	}

	@Override
	public void doPostRenderChildren(Runnable defaultTex)
	{
		if (stage == EffectRenderStage.POST_CHILDREN) render(defaultTex);
	}
	
	public abstract void render(Runnable defaultTex);

	private ComponentFloatingLabel labelStage;
	private SelectorButton renderStage;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editorY = super.onSelect(editorPanes, editorY);
		editor.addElement(labelStage = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Render stage"));
		editorY += 20;
		
		String[] names = new String[EffectRenderStage.values().length];
		for (int i = 0; i < names.length; i++) names[i] = EffectRenderStage.values()[i].name;
		
		editor.addElement(renderStage = new SelectorButton(editorX, editorY, editorX + 300, editorY + 20, 
				this.stage.name, names, (ind, str) -> {
					this.stage = EffectRenderStage.values()[ind];
				}));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelStage);
		editor.removeElement(renderStage);
		labelStage = null;
		renderStage = null;
	}
}