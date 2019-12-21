package firemerald.mcms.window.awt.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.BitSet;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.window.awt.AWTWindow;
import firemerald.mcms.window.awt.ModifierConverter;

public class MouseListener implements java.awt.event.MouseListener, MouseMotionListener, MouseWheelListener
{
	final Main main;
	public final AWTWindow window;
	final BitSet state = new BitSet(3);
	
	public MouseListener(Main main, AWTWindow window)
	{
		this.main = main;
		this.window = window;
	}

	@Override
	public void mouseClicked(MouseEvent event) {}

	@Override
	public void mouseEntered(MouseEvent event) {}

	@Override
	public void mouseExited(MouseEvent event) {}

	@Override
	public void mousePressed(MouseEvent event)
	{
		if (event.getButton() > 0)
		{
			int button = event.getButton() - 1;
			int mods = ModifierConverter.getModifiers(event.getModifiersEx());
			boolean prevState = state.get(button);
			state.set(button);
			GuiScreen gui;
			if ((gui = main.gui) != null)
			{
				final float mX = event.getX();
				final float mY = event.getY();
				if (prevState) window.actions.add(() -> gui.onMouseRepeat(mX, mY, button, mods));
				else window.actions.add(() -> gui.onMousePressed(mX, mY, button, mods));
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (event.getButton() > 0)
		{
			int button = event.getButton() - 1;
			int mods = ModifierConverter.getModifiers(event.getModifiersEx());
			boolean prevState = state.get(button);
			state.set(button);
			GuiScreen gui;
			if ((gui = main.gui) != null)
			{
				final float mX = event.getX();
				final float mY = event.getY();
				if (prevState) window.actions.add(() -> gui.onMouseReleased(mX, mY, button, mods)); //prevent repeating
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		GuiScreen gui;
		if ((gui = main.gui) != null)
		{
			final float mX = event.getX();
			final float mY = event.getY();
			window.actions.add(() -> {
				main.mX = mX;
				main.mY = mY;
				gui.onMouseMoved(mX, mY);
			});
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {} //mouse dragging handled internally

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		GuiScreen gui;
		if ((gui = main.gui) != null && (gui.canScrollH((float) main.mX, (float) main.mY) || gui.canScrollV((float) main.mX, (float) main.mY)))
		{
			final boolean isHoriz = event.isShiftDown();
			final float amount = (float) event.getPreciseWheelRotation();
			final float mX = event.getX();
			final float mY = event.getY();
			window.actions.add(() -> gui.onMouseScroll(mX, mY, isHoriz ? amount : 0, isHoriz ? 0 : amount));
		}
	}
	
	public boolean mouseDown(int button)
	{
		return state.get(button);
	}
}