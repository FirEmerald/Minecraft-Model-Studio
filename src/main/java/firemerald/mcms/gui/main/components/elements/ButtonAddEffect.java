package firemerald.mcms.gui.main.components.elements;

import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.api.model.effects.BoneEffect;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardIconButton;
import firemerald.mcms.gui.popups.GuiPopupSelector;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.Triple;

public class ButtonAddEffect<T extends RenderBone<T>> extends EditableButton
{
	private T bone;
	
	public ButtonAddEffect(int x, int y)
	{
		super(x, y);
	}

	@Override
	public ResourceLocation getTexture()
	{
		return Textures.EDITABLE_ADD_EFFECT;
	}
	
	@Override
	public void onRelease()
	{
		@SuppressWarnings("unchecked")
		Triple<String, ResourceLocation, Consumer<RenderBone<?>>>[] values = BoneEffect.EFFECTS_VIEW.toArray(new Triple[BoneEffect.EFFECTS_VIEW.size()]);
		new GuiPopupSelector(this, values, (ind, val) -> val.right.accept(bone), (val, bounds, action) -> new StandardIconButton(bounds.x, bounds.y, bounds.z, bounds.w, 16, val.middle, val.left, action)).activate();
	}
	
	@Override
	public int getSelectorX1(GuiPopup selector)
	{
		return getTrueX2() - 160;
	}

	@Override
	public int getSelectorY1(GuiPopup selector)
	{
		return getTrueY1();
	}

	@Override
	public int getSelectorX2(GuiPopup selector)
	{
		return getTrueX2();
	}

	@Override
	public int getSelectorY2(GuiPopup selector)
	{
		return getTrueY1() + 20;
	}
	
	public void setBone(T bone)
	{
		this.bone = bone;
	}

	@Override
	public boolean isEnabled()
	{
		return bone != null && Main.instance.project.getModel() != null && !BoneEffect.EFFECTS_VIEW.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public void setBone(Bone<?> bone)
	{
		if (bone instanceof RenderBone) setBone((T) bone);
	}
}