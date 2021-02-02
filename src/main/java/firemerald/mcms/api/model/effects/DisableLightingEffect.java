package firemerald.mcms.api.model.effects;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelHolder;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class DisableLightingEffect extends BoneEffect
{
	public DisableLightingEffect(String name, @Nullable RenderBone<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getXMLName()
	{
		return "no_lighting";
	}

	@Override
	public DisableLightingEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new DisableLightingEffect(this.name, clonedParent);
	}

	@Override
	public ResourceLocation getDisplayIcon()
	{
		return Textures.MODEL_ICON_NOLIGHTING;
	}

	@Override
	public DisableLightingEffect copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public void doPreRender(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTex)
	{
		Main.instance.currentModelShader.setIgnoreLighting(true);
	}

	@Override
	public void doPostRenderBone(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTex)
	{
		Main.instance.currentModelShader.setIgnoreLighting(false);
	}

	@Override
	public void doPostRenderChildren(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTex) {}

	@Override
	public void doCleanUp() {}
}