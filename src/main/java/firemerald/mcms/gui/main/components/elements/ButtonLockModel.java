package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.gui.main.components.animation.PlaybackButton;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;

public class ButtonLockModel extends PlaybackButton
{
	public ButtonLockModel(int x, int y)
	{
		super(x, y);
	}

	@Override
	public boolean isEnabled()
	{
		return Main.instance.project.getModel() != null;
	}

	@Override
	public void onRelease()
	{
		Project proj = Main.instance.project;
		IModel<?, ? extends RenderObjectComponents<?>> model = proj.getModel();
		if (proj.lockedModels.containsKey(model)) proj.lockedModels.remove(model);
		else proj.lockedModels.put(model, proj.getTexture());
		Main.instance.project.setNeedsSave();
	}

	@Override
	public ResourceLocation getIcon()
	{
		Project proj = Main.instance.project;
		IModel<?, ? extends RenderObjectComponents<?>> model = proj.getModel();
		return model == null || proj.lockedModels.containsKey(model) ? Textures.EDITABLE_UNLOCKED : Textures.EDITABLE_LOCKED;
	}
}