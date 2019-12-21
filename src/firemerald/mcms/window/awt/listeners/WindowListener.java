package firemerald.mcms.window.awt.listeners;

import java.awt.event.WindowEvent;

import firemerald.mcms.window.awt.AWTWindow;

public class WindowListener implements java.awt.event.WindowListener
{
	public final AWTWindow window;
	
	public WindowListener(AWTWindow window)
	{
		this.window = window;
	}
	
	@Override
	public void windowActivated(WindowEvent event) {}

	@Override
	public void windowClosed(WindowEvent event) {}

	@Override
	public void windowClosing(WindowEvent event)
	{
		window.closed = true;
	}

	@Override
	public void windowDeactivated(WindowEvent event) {}

	@Override
	public void windowDeiconified(WindowEvent event) {}

	@Override
	public void windowIconified(WindowEvent event) {}

	@Override
	public void windowOpened(WindowEvent event) {}
}