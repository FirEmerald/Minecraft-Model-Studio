package firemerald.mcms.util.history;

import java.util.Stack;

import firemerald.mcms.Main;

public class ActionHistory
{
	protected final Stack<IHistoryAction<?>> undoActions = new Stack<>();
	protected final Stack<IHistoryAction<?>> redoActions = new Stack<>();
	protected final Stack<Integer> stack = new Stack<>();
	
	public boolean canUndo()
	{
		return !undoActions.isEmpty();
	}
	
	public boolean canRedo()
	{
		return !redoActions.isEmpty();
	}
	
	public void undo()
	{
		if (!undoActions.isEmpty())
		{
			redoActions.push(undoActions.pop().perform());
			Main.instance.setEditing(Main.instance.getEditing());
		}
	}

	public void redo()
	{
		if (!redoActions.isEmpty())
		{
			undoActions.push(redoActions.pop().perform());
			Main.instance.setEditing(Main.instance.getEditing());
		}
	}
	
	public void onAction(IHistoryAction<?> undoThisAction)
	{
		redoActions.clear();
		undoActions.push(undoThisAction);
	}
	
	public void clearActions()
	{
		undoActions.clear();
		redoActions.clear();
	}
}