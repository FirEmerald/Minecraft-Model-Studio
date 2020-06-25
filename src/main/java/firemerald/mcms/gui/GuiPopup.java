package firemerald.mcms.gui;

import firemerald.mcms.Main;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.history.ActionHistory;
import firemerald.mcms.util.history.IHistoryAction;
import firemerald.mcms.util.hotkey.Action;

public abstract class GuiPopup extends GuiScreen
{
	public GuiScreen under;
	public final ActionHistory undoStack;
	public boolean active = false;
	
	public GuiPopup()
	{
		this(true);
	}
	
	public GuiPopup(boolean useUndoStack)
	{
		undoStack = useUndoStack ? new ActionHistory() : null;
	}
	
	public void activate()
	{
		active = true;
		Main.instance.openGui(this);
	}
	
	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		under.setSize(w, h);
	}
	
	public void deactivate()
	{
		active = false;
		Main.instance.closePopup();
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		under.render(mx, my, false);
		doRender(mx, my, canHover);
		super.render(mx, my, canHover);
	}
	
	public void doRender(float mx, float my, boolean canHover) {}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (under != null) under.onGuiUpdate(reason);
	}
	
	@Override
	public boolean onHotkey(Action action)
	{
		if (active && undoStack != null && (action == Action.UNDO || action == Action.REDO))
		{
			Main.instance.project.pushUndoStack(undoStack);
			if (!super.onHotkey(action)) action.action.run();
			Main.instance.project.popUndoStack();
			return true;
		}
		else return super.onHotkey(action);
	}

	@Override
	public boolean onAction(IHistoryAction<?> action)
	{
		if (active && undoStack != null)
		{
			if (!super.onAction(action)) undoStack.onAction(action);
			return true;
		}
		else return super.onAction(action);
	}
}