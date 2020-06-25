package firemerald.mcms.util.history;

public class HistoryAction implements IHistoryAction<HistoryAction>
{
	private Runnable undo, redo;
	
	public HistoryAction(Runnable undo, Runnable redo)
	{
		this.undo = undo;
		this.redo = redo;
	}

	@Override
	public HistoryAction perform()
	{
		Runnable temp = redo;
		(redo = undo).run();
		undo = temp;
		return this;
	}
}