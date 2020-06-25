package firemerald.mcms.util.history;

@FunctionalInterface
public interface IHistoryAction<H extends IHistoryAction<?>>
{
	public H perform();
}