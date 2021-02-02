package firemerald.mcms.window.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Toolkit;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import firemerald.mcms.Main;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Window;
import firemerald.mcms.window.awt.listeners.*;

public class AWTWindow extends Window //TODO is borked, don't know why
{
	public final Main main;
	public final JFrame frame;
	public final AWTGLCanvas canvas;
	public final KeyListener keyListener;
	public final MouseListener mouseListener;
	public final Queue<Runnable> actions = new ConcurrentLinkedQueue<>();
	public boolean closed = false;
	
	public AWTWindow(Main main)
	{
		this.main = main;
		JFrame frame = this.frame = new JFrame("Minecraft Model Studio");
		frame.setMinimumSize(new Dimension(Main.MIN_W, Main.MIN_H));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowListener(this));
		frame.setLayout(new BorderLayout());
		MenuBar menubar = new MenuBar();
		Map<String, Menu> menus = Main.makeTitlebar();
		menus.values().forEach(menubar::add);
		frame.setMenuBar(menubar);
		
		GLData data = new GLData();
		data.samples = 4;
		data.majorVersion = 3;
		data.minorVersion = 3;
		data.forwardCompatible = true;
		data.profile = GLData.Profile.CORE;
		data.depthSize = 24;
		data.stencilSize = 8;
		data.doubleBuffer = true;
		frame.add(canvas = new AWTGLCanvas(data)
		{
			private static final long serialVersionUID = -8641969527043065271L;
			
			@Override
			public void initGL()
			{
				main.initOpenGL();
				main.setSize(canvas.getWidth(), canvas.getHeight());
			}
			
			@Override
			public void paintGL()
			{
				while (!actions.isEmpty()) actions.poll().run();
				main.renderOpenGL();
			}
		});
		canvas.addKeyListener(keyListener = new KeyListener(main, this));
		canvas.addMouseListener(mouseListener = new MouseListener(main, this));
		canvas.addMouseMotionListener(mouseListener);
		canvas.addMouseWheelListener(mouseListener);
		canvas.addComponentListener(new ComponentListener(main, this));
	}

	@Override
	public void showWindow()
	{
		frame.setVisible(true);
	}

	@Override
	public void focusWindow()
	{
		frame.requestFocus();
	}

	@Override
	public void hideWindow()
	{
		frame.setVisible(false);
	}

	@Override
	public int getDisplayW()
	{
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	@Override
	public int getDisplayH()
	{
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	@Override
	public int getX()
	{
		return frame.getX();
	}

	@Override
	public int getY()
	{
		return frame.getY();
	}

	@Override
	public void setPosition(int x, int y)
	{
		frame.setLocation(x, y);
	}

	@Override
	public int getW()
	{
		return frame.getWidth();
	}

	@Override
	public int getH()
	{
		return frame.getHeight();
	}

	@Override
	public void setSize(int w, int h)
	{
		frame.setSize(w, h);
	}

	@Override
	public boolean isMaximized()
	{
		return (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
	}

	@Override
	public void setMaximized(boolean maximized)
	{
		frame.setExtendedState(maximized ? (frame.getExtendedState() | Frame.MAXIMIZED_BOTH) : (frame.getExtendedState() & ~Frame.MAXIMIZED_BOTH));
	}

	@Override
	public boolean isClosed()
	{
		return closed;
	}
	
	@Override
	public void close() 
	{
		frame.dispose();
	}

	@Override
	public void setTitle(String title)
	{
		frame.setTitle(title);
	}

	@Override
	public void setCursor(Cursor cursor)
	{
		frame.setCursor(CursorConverter.getCursor(cursor));
	}

	@Override
	public void tick(long thisTick)
	{
		if (Main.glActive)
		{
			while (!actions.isEmpty()) actions.remove().run();
			main.tick(thisTick);
		}
	}

	@Override
	public void render()
	{
		canvas.render();
		canvas.swapBuffers();
	}

	@Override
	public boolean isKeyDown(Key key)
	{
		return keyListener.keyDown(key);
	}

	@Override
	public boolean isMouseDown(int button)
	{
		return mouseListener.mouseDown(button);
	}
}