package firemerald.mcms.api.util;

import firemerald.mcms.api.data.AbstractElement;

public interface ISaveable
{
	public String getElementName();
	
	public void save(AbstractElement el);
	
	public void load(AbstractElement el);
}