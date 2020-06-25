package firemerald.mcms.api.model.effects;

public enum EffectRenderStage
{
	PRE_BONE("pre-render"),
	POST_BONE("after bone"),
	POST_CHILDREN("post-render");
	
	public final String name;
	
	EffectRenderStage(String name)
	{
		this.name = name;
	}
}