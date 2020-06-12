package firemerald.mcms.api.model;

import firemerald.mcms.api.animation.Transformation;

public interface ITransformsProvider
{
	public static final ITransformsProvider NONE = new ITransformsProvider() {
		@Override
		public Transformation get(String name)
		{
			return new Transformation();
		}
	};
	
	public Transformation get(String name);
}