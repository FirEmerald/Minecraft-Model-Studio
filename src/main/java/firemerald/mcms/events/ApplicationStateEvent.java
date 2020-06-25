package firemerald.mcms.events;

import firemerald.mcms.api.data.AbstractElement;

public abstract class ApplicationStateEvent extends Event
{
	public final AbstractElement element;
	
	ApplicationStateEvent(AbstractElement element)
	{
		this.element = element;
	}
	
	public static class Load extends ApplicationStateEvent
	{
		public Load(AbstractElement element)
		{
			super(element);
		}
	}
	
	public static class Save extends ApplicationStateEvent
	{
		public Save(AbstractElement element)
		{
			super(element);
		}
	}
}