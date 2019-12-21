package firemerald.mcms.window.awt.listeners;

import java.awt.event.ComponentEvent;

import firemerald.mcms.Main;
import firemerald.mcms.window.awt.AWTWindow;

public class ComponentListener implements java.awt.event.ComponentListener
{
	public final Main main;
	public final AWTWindow window;
	
	public ComponentListener(Main main, AWTWindow window)
	{
		this.main = main;
		this.window = window;
	}
	
	@Override
	public void componentHidden(ComponentEvent event) {}

	@Override
	public void componentMoved(ComponentEvent event) {}

	@Override
	public void componentResized(ComponentEvent event)
	{
		window.actions.add(() -> main.setSize(event.getComponent().getWidth(), event.getComponent().getHeight()));
	}

	@Override
	public void componentShown(ComponentEvent event) {}
}