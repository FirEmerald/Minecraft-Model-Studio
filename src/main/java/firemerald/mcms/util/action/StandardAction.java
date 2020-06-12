package firemerald.mcms.util.action;

public class StandardAction implements IAction
{
	protected Runnable undo, redo;
	
	public StandardAction(Runnable undo, Runnable redo)
	{
		this.undo = undo;
		this.redo = redo;
	}
	
	@Override
	public StandardAction get()
	{
		undo.run();
		Runnable temp = undo;
		undo = redo;
		redo = temp;
		return this;
	}
}