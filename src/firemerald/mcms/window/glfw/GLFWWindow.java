package firemerald.mcms.window.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Window;
import firemerald.mcms.window.glfw.callbacks.*;
import net.sf.image4j.codec.ico.ICODecoder;

public class GLFWWindow extends Window
{
	public final Main main;
	public final long window;
	public final Cursors cursors;
	public final GLFWVidMode vidmode;
	public boolean maximized = false;
	public int x = 0, y = 0, w = 1280, h = 720;
	
	public GLFWWindow(Main main)
	{
		this.main = main;
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_DEPTH_BITS, 24);
		glfwWindowHint(GLFW_STENCIL_BITS, 8);
		glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
		vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		window = glfwCreateWindow(1280, 720, "Minecraft Model Studio", 0, 0);
		if (window == 0) throw new RuntimeException("Failed to create the GLFW window");
		glfwSetWindowSizeLimits(window, Main.MIN_W, Main.MIN_H, GLFW_DONT_CARE, GLFW_DONT_CARE);
		glfwSetCharCallback(window, new CharCallback(main));
		glfwSetKeyCallback(window, new KeyCallback(main, this));
		glfwSetMouseButtonCallback(window, new MouseButtonCallback(main));
		glfwSetCursorPosCallback(window, new CursorPosCallback(main));
		glfwSetScrollCallback(window, new ScrollCallback(main));
		glfwSetWindowSizeCallback(window, new WindowSizeCallback(main, this));
		glfwSetWindowFocusCallback(window, new WindowFocusCallback(main));
		glfwSetWindowPosCallback(window, new WindowPosCallback(main, this));
		glfwSetWindowMaximizeCallback(window, new WindowMaximizeCallback(main, this));
		main.focused = true;
		glfwMakeContextCurrent(window);
		try
		{
			InputStream in = Main.getResource("icon.ico");
			List<BufferedImage> icons = ICODecoder.read(in);
			FileUtil.closeSafe(in);
			GLFWImage.Buffer buffer = GLFWImage.malloc(icons.size());
			for (int i = 0; i < icons.size(); i++)
			{
				buffer.position(i);
				BufferedImage img = icons.get(i);
				int w = img.getWidth();
				int h = img.getHeight();
				int[] pixels = new int[w * h];
				img.getRGB(0, 0, w, h, pixels, 0, w);
				ByteBuffer buf = ByteBuffer.allocateDirect(w * h * 4);
				buf.order(ByteOrder.BIG_ENDIAN);
				for (int pixel : pixels) buf.putInt((pixel << 8) | (pixel >>> 24)); //ARGB -> RGBA
				buf.flip();
				buffer.width(w).height(h).pixels(buf);
			}
			glfwSetWindowIcon(window, buffer.position(0));
			buffer.free();
		}
		catch (Throwable t)
		{
			Main.LOGGER.warn("Failed to load window icon(s)", t);
		}
		glfwSwapInterval(1);
		cursors = new Cursors();
		main.initOpenGL();
		main.setSize(w, h);
	}

	@Override
	public void showWindow()
	{
		glfwShowWindow(window);
	}

	@Override
	public void hideWindow()
	{
		glfwHideWindow(window);
	}

	@Override
	public int getDisplayW()
	{
		return vidmode.width();
	}

	@Override
	public int getDisplayH()
	{
		return vidmode.height();
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public void setPosition(int x, int y)
	{
		glfwSetWindowPos(window, x, y);
	}

	@Override
	public int getW()
	{
		return w;
	}

	@Override
	public int getH()
	{
		return h;
	}

	@Override
	public void setSize(int w, int h)
	{
		glfwSetWindowSize(window, w, h);
	}

	@Override
	public boolean isMaximized()
	{
		return maximized;
	}

	@Override
	public void setMaximized(boolean maximized)
	{
		if (maximized != this.maximized)
		{
			if (maximized) glfwMaximizeWindow(window);
			else glfwRestoreWindow(window);
		}
	}

	@Override
	public boolean isClosed()
	{
		return glfwWindowShouldClose(window);
	}
	
	@Override
	public void close()
	{
		glfwSetWindowShouldClose(window, true);
	}
	
	@Override
	public void setTitle(String title)
	{
		glfwSetWindowTitle(window, title);
	}

	@Override
	public void setCursor(Cursor cursor)
	{
		glfwSetCursor(window, cursors.getCursor(cursor));
	}

	@Override
	public void tick(long thisTick)
	{
    	glfwPollEvents();
		main.tick(thisTick);
	}

	@Override
	public void render()
	{
		main.renderOpenGL();
		if (!main.tooSmall) glfwSwapBuffers(window);
	}
	
	@Override
	public boolean isKeyDown(Key key)
	{
		return glfwGetKey(window, KeyConverter.getGLFW(key)) == GLFW_PRESS;
	}

	@Override
	public boolean isMouseDown(int button)
	{
		return glfwGetMouseButton(window, button) == GLFW_PRESS;
	}
}
