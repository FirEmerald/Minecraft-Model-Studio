package firemerald.mcms;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.util.MatrixHandler;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.callbacks.*;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.gui.model.GuiModel;
import firemerald.mcms.gui.themes.GuiThemes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.MatrixHandlerCore;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.HSL;
import firemerald.mcms.texture.HSV;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.texture.ReloadingTexture;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.theme.BasicTheme;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.Action;
import firemerald.mcms.util.ApplicationState;
import firemerald.mcms.util.Cursors;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.FileWatcher;
import firemerald.mcms.util.FontRenderer;
import firemerald.mcms.util.TextureManager;
import net.sf.image4j.codec.ico.ICODecoder;

public class Main
{
	public static final Logger LOGGER;
	public static Main instance;
	public long window;
	public int sizeW, sizeH;
	public boolean focused = false;
	public double mX = -1, mY = -1;
	public Shader shader;
	public TextureManager textureManager;
	public GuiScreen gui = null;
	public FontRenderer font0, fontMsg;
	public int texSizeU = 1, texSizeV = 1;
	public RaytraceResult trace = null;
	public IEditable editing = null;
	public static final Queue<Action> CLEANUP_ACTIONS = new ConcurrentLinkedQueue<>();
	public Mesh screen;
	public GuiTheme theme;
	public FileWatcher watcher;
	public boolean tooSmall = false;
	public final ApplicationState state = new ApplicationState();
	
    static
    {
    	System.setProperty("log4j2.configurationFile", "firemerald/mcms/resources/log4j2.xml");
    	LOGGER = LogManager.getLogger("MCAMC"); //the main logger
    	Logger stdOut = LogManager.getLogger("STDOUT"); //the logger for System.out
    	Logger stdErr = LogManager.getLogger("STDERR"); //the logger for System.err
    	System.setOut(new PrintStreamLogger(System.out, stdOut, Level.INFO)); //replace the default output stream with one that goes to the logger
    	System.setErr(new PrintStreamLogger(System.err, stdErr, Level.ERROR)); //replace the default error stream with one that goes to the logger
    }
    
    public static Mesh MODMESH;
	
	public static void main(String[] args)
	{
		(instance = new Main()).run();
	}
	
	public void setSize(int w, int h)
	{
		if (w <= 0 || h <= 0)
		{
			tooSmall = true;
		}
		else
		{
			tooSmall = false;
			sizeW = w;
			sizeH = h;
			if (glActive) screen.setMesh(0, 0, w, h, 0, 0, 0, 1, 1);
			if (gui != null) gui.setSize(w, h);
			state.setSize(w, h);
		}
	}
	
	public void setPos(int x, int y)
	{
		state.setPos(x, y);
	}
	
	public void setMaximized(boolean maximized)
	{
		state.setMaximized(maximized);
	}
	
	public static boolean glActive = false;
	
	public Main() {}
	
	public void setTheme(GuiTheme theme)
	{
		this.theme = theme;
		this.state.setTheme(theme.origin);
	}
	
	public void run()
	{
		try
		{
			watcher = new FileWatcher();
		}
		catch (IOException e)
		{
			LOGGER.log(Level.FATAL, "Could not instantiate the filesystem watcher service", e);
			System.exit(-1);
		}
		this.theme = (new BasicTheme());
		state.loadState();
		state.saveState();
		/*Testing NFD
		LOGGER.info("Open file");
		{
			File file = FileUtils.getOpenFile(null, null);
			LOGGER.info(file);
		}
		LOGGER.info("Open files");
		{
			File[] files = FileUtils.getOpenFiles(null, null);
			for (File file : files) LOGGER.info(file);
		}
		LOGGER.info("Save file");
		{
			File file = FileUtils.getSaveFile(null, null);
			LOGGER.info(file);
		}
		LOGGER.info("get folder");
		{
			File file = FileUtils.getFolder(null);
			LOGGER.info(file);
		}
		System.exit(0);
		*/
		/*raytrace unit tests
		final int numTriangles = 100000;
		final int numRays = 1000;
		final int numTraces = numTriangles * numRays;
		Vec3[][] triangles = new Vec3[numTriangles][3];
		for (int i = 0; i < numTriangles; i++)
		{
			Vec3[] tri = triangles[i];
			tri[0] = Vec3.random(-10, 10);
			tri[1] = Vec3.random(-10, 10);
			tri[2] = Vec3.random(-10, 10);
		}
		Vec3[][] rays = new Vec3[numRays][2];
		for (int i = 0; i < numRays; i++)
		{
			Vec3[] ray = rays[i];
			ray[0] = Vec3.random(-2, 2);
			ray[1] = Vec3.random(-1, 1).normalize();
		}
		int numSucceeded = 0, numFailed = 0;
		float averageError = 0, maxError = 0, minError = Float.MAX_VALUE;
		long start = System.nanoTime();
		for (int i = 0; i < numTriangles; i++)
		{
			Vec3[] tri = triangles[i];
			Vec3 p1 = tri[0];
			Vec3 p2 = tri[1];
			Vec3 p3 = tri[2];
			for (int j = 0; j < numRays; j++)
			{
				Vec3[] ray = rays[j];
				Vec3 from = ray[0];
				Vec3 dir = ray[1];
				//System.out.println("From: " + from + " Dir: " + dir);
				//System.out.println("Triangle: " + p1 + ", " + p2 + ", " + p3);
				Vec4 trace = MathUtil.rayTrace(p1, p2, p3, from, dir);
				if (trace == null || trace.x() < 0 || trace.y() < 0 || trace.z() < 0)
				{
					numFailed++;
					//System.out.println("Trace failed");
				}
				else
				{
					numSucceeded++;
					//System.out.println("Trace result: " + trace);
					Vec3 amnts = new Vec3(p1.x() * trace.x() + p2.x() * trace.y() + p3.x() * trace.z(), p1.y() * trace.x() + p2.y() * trace.y() + p3.y() * trace.z(), p1.z() * trace.x() + p2.z() * trace.y() + p3.z() * trace.z());
					Vec3 mag = new Vec3(from.x() + dir.x() * trace.w(), from.y() + dir.y() * trace.w(), from.z() + dir.z() * trace.w());
					//System.out.println("Yeilds from amounts: " + amnts);
					//System.out.println("Yeilds from magnitude: " + mag);
					float error = amnts.subtract(mag).magnitude();
					//System.out.println("Error: " + error);
					averageError += error;
					if (error > maxError) maxError = error;
					if (error < minError) minError = error;
				}
			}
		}
		long end = System.nanoTime();
		System.out.println("Processed " + numTraces + " raytraces in " + (end - start) + " nanoseconds.");
		System.out.println("Number of successful traces: " + numSucceeded + "/" + numTraces);
		System.out.println("Number of failed traces: " + numFailed + "/" + numTraces);
		System.out.println("Minimum error: " + minError);
		System.out.println("Maximum error: " + maxError);
		System.out.println("Average error: " + (averageError / (numSucceeded)));
		System.exit(0);
		*/
		/*
		//QUAT->EULER testing
		for (int i = 0; i < 5; i++)
		{
			Vec3 axis = Vec3.random(-1, 1).normalize();
			float ang = MathUtil.random(0, MathUtil.TAU);
			Quaternion q = Quaternion.forAngle(ang, axis.x(), axis.y(), axis.z());
			System.out.println(q.getMatrix3());
			Vec3 euler = q.toEulerXZY();
			q = Quaternion.forEulerXZY(euler.x(), euler.y(), euler.z());
			System.out.println(q.getMatrix3());
		}
		System.exit(0);
		*/
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		initWindow();
		Cursors.init();
		GL.createCapabilities();
		glActive = true;
		screen = new Mesh(0, 0, sizeW, sizeH, 0, 0, 0, 1, 1);
		font0 = new FontRenderer("0", 8, 32, 255, true);
		fontMsg = new FontRenderer("msg", 12, true);
        glEnable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
		shader = new Shader("shader", "shader");
		glEnable(GL_LINE_SMOOTH);
		MatrixHandler.instance = new MatrixHandlerCore();
		
		MODMESH = new Mesh(new float[0], new float[0], new float[0], new int[0], Mesh.DrawMode.TRIANGLES, GL_DYNAMIC_DRAW);
		textureManager = new TextureManager();
		
		Animation anim = Animation.loadAnim(new File("example.anim"));
		/** /
		VBOObjModel model = VBOObjModel.tryLoadModel(new File("example.obj"), new File("example.skel"));
		/*/
		
		//System.out.println(RenderObjectComponents.createObj(model).optimize());
		/**/
		AnimationState animState = new AnimationState(anim);
		Texture tex;
		try
		{
			tex = new ReloadingTexture(new File("test6.png"));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			tex = new Texture(1, 1);
		}
		
		gui = new GuiModel(tex);
		gui.setSize(sizeW, sizeH);
		new GuiThemes().activate();
		
		//TODO
		//saveColorWheel(155, 15);
		//saveColorSquare(155, 135);
		//saveHueSquare(155, 1, false);
		//saveHueSquare(15, 155, true);
		//saveColorTriangle(125);
		//saveGradientSquare(155, 15, new RGB(0, 0, 0), new RGB(1, 0, 0), new RGB(0, 1, 1), new RGB(1, 1, 1), "red");
		//saveGradientSquare(155, 15, new RGB(0, 0, 0), new RGB(0, 1, 0), new RGB(1, 0, 1), new RGB(1, 1, 1), "green");
		//saveGradientSquare(155, 15, new RGB(0, 0, 0), new RGB(0, 0, 1), new RGB(1, 1, 0), new RGB(1, 1, 1), "blue");
		long lastNanos = 0;
		boolean first = true;
		while (!glfwWindowShouldClose(window))
		{
			long nanos = System.nanoTime();
			long thisTick;
			if (first)
			{
				thisTick = 0;
				first = false;
			}
			else thisTick = nanos - lastNanos;
			lastNanos = nanos;
	    	glfwPollEvents();
	    	
	    	watcher.poll();
	    	
	    	if (!tooSmall)
	    	{
		    	/**/
				shader.bind();
	    		shader.setColor(1, 1, 1, 1);
				glViewport(0, 0, sizeW, sizeH);
				Shader.MODEL.matrix().identity();
				Shader.VIEW.matrix().identity();
				Shader.PROJECTION.matrix().identity();
	    		Shader.VIEW.matrix().translate(0, 0, -1000);
	    		Shader.PROJECTION.matrix().setOrtho(0, sizeW, sizeH, 0, 0, 2000);
	    		shader.updateModel();
	    		shader.updateView();
	    		shader.updateProjection();
				theme.drawBackground();
	            float mx = (float) mX;
	            float my = (float) mY;
	            if (gui != null) gui.tick(mx, my, (float) (thisTick * .000000001));
	            if (gui != null)
	            {
	            	glfwSetCursor(window, gui.getCursor(mx, my));
	            	gui.render(mx, my, true);
	            }
	            else glfwSetCursor(window, Cursors.standard);
				shader.unbind();
				glfwSwapBuffers(window);
				listGLErrors("End render");
	    	}
			
			while (!CLEANUP_ACTIONS.isEmpty()) CLEANUP_ACTIONS.remove().apply();
			
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void saveColorWheel(final int s, final int w)
	{
		try
		{
			final float rMax = (s - 1) * .5f;
			final float rMin = rMax + 1 - w;
			BufferedImage wheel = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
			WritableRaster wheelAlpha = wheel.getAlphaRaster();
			float ay = -rMax;
			HSV hsv = new HSV(0, 1, 1);
			Color color = new Color(hsv, 1);
			for (int y = 0; y < s; y++)
			{
				float ax = rMax;
				for (int x = 0; x < s; x++)
				{
					hsv.h = (float) ((Math.atan2(ay, ax) / (2 * Math.PI)) + .5);
					hsv.s = 1;
					hsv.v = 1;
					float r = ax * ax + ay * ay;
					if (r > rMax * rMax)
					{
						float d = rMax - (float) Math.sqrt(r);
						if (d <= -1) color.a = 0;
						else color.a = d + 1;
					}
					else if (r < rMin * rMin)
					{
						float d = (float) Math.sqrt(r) - rMin;
						if (d <= -1) color.a = 0;
						else color.a = d + 1;
					}
					else color.a = 1;
					int p = color.toARGB();
					wheel.setRGB(x, y, p & 0xFFFFFF);
					wheelAlpha.setSample(x, y, 0, (p & 0xFF000000) >>> 24);
					ax--;
				}
				ay++;
			}
			File file = new File("ColorWheel_" + s + ".png");
			ImageIO.write(wheel, "png", file);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	
	public static void saveColorSquare(final int w, final int h)
	{
		try
		{
			BufferedImage square = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			WritableRaster squareAlpha = square.getAlphaRaster();
			HSV hsv = new HSV(0, 1, 1);
			Color color = new Color(hsv, 1);
			for (int y = 0; y < h; y++)
			{
				hsv.v = 1 - (y / (h - 1f));
				for (int x = 0; x < w; x++)
				{
					hsv.s = x / (w - 1f);
					int p = color.toARGB();
					square.setRGB(x, y, p & 0xFFFFFF);
					squareAlpha.setSample(x, y, 0, 0xFF);
				}
			}
			File file = new File("ColorSquare_" + w + "_" + h + ".png");
			ImageIO.write(square, "png", file);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	
	public static void saveHueSquare(final int w, final int h, final boolean vertical)
	{
		try
		{
			BufferedImage square = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			WritableRaster squareAlpha = square.getAlphaRaster();
			HSV hsv = new HSV(0, 1, 1);
			Color color = new Color(hsv, 1);
			for (int y = 0; y < h; y++)
			{
				if (vertical) hsv.h = y / (h - 1f);
				for (int x = 0; x < w; x++)
				{
					if (!vertical) hsv.h = x / (w - 1f);
					int p = color.toARGB();
					square.setRGB(x, y, p & 0xFFFFFF);
					squareAlpha.setSample(x, y, 0, 0xFF);
				}
			}
			File file = new File("HueSquare_" + w + "_" + h + ".png");
			ImageIO.write(square, "png", file);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	
	public static void saveGradientSquare(final int w, final int h, final RGB colTL, final RGB colTR, final RGB colBL, final RGB colBR, final String name)
	{
		try
		{
			BufferedImage square = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			WritableRaster squareAlpha = square.getAlphaRaster();
			RGB rgb = new RGB(0, 0, 0);
			float dRR = colBR.r - colTR.r;
			float dGR = colBR.g - colTR.g;
			float dBR = colBR.b - colTR.b;
			float dRL = colBL.r - colTL.r;
			float dGL = colBL.g - colTL.g;
			float dBL = colBL.b - colTL.b;
			Color color = new Color(rgb, 1);
			for (int y = 0; y < h; y++)
			{
				float a = y / (h - 1f);
				float rL = colTL.r + dRL * a;
				float gL = colTL.g + dGL * a;
				float bL = colTL.b + dBL * a;
				float rR = colTR.r + dRR * a;
				float gR = colTR.g + dGR * a;
				float bR = colTR.b + dBR * a;
				float dR = rR - rL;
				float dG = gR - gL;
				float dB = bR - bL;
				for (int x = 0; x < w; x++)
				{
					a = x / (w - 1f);
					rgb.r = rL + dR * a;
					rgb.g = gL + dG * a;
					rgb.b = bL + dB * a;
					int p = color.toARGB();
					square.setRGB(x, y, p & 0xFFFFFF);
					squareAlpha.setSample(x, y, 0, 0xFF);
				}
			}
			File file = new File(name + "Square_" + w + "_" + h + ".png");
			ImageIO.write(square, "png", file);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	
	public static void saveColorTriangle(final int s)
	{
		try
		{
			final float rMax = (s - 1) * .5f;
			final float trsx = rMax * .5f;
			final float trsy = trsx * (float) Math.sqrt(3);
			BufferedImage triangle = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
			WritableRaster triangleAlpha = triangle.getAlphaRaster();
			float ay = -rMax;
			HSL hsl = new HSL(0, 1, .5f);
			Color color = new Color(hsl, 1);
			for (int y = 0; y < s; y++)
			{
				float ax = rMax;
				for (int x = 0; x < s; x++)
				{
					float ty = 1 - Math.abs(ay / trsy);
					hsl.s = 1 - (ax * 2 / (rMax * 3) + (2f / 3f));
					if (ty != 0) hsl.s /= ty;
					hsl.l = .5f - ay / (trsy * 2);
					/* we don't need it
					double angle = Math.atan2(ay, ax);
					double a2;
					if (angle >= Math.PI / 3) a2 = 2 * Math.PI / 3;
					else if (angle > -Math.PI / 3) a2 = 0;
					else a2 = -2 * Math.PI / 3;
					float cs = (float) Math.cos(a2);
					float sn = (float) Math.sin(a2);
					float adx = ax * cs + ay * sn;
					if (adx > trsx)
					{
						float d = trsx - adx;
						if (d < -1) color.a = 0;
						else color.a = d + 1;
					}
					else color.a = 1;
					*/
					int p = color.toARGB();
					triangle.setRGB(x, y, p & 0xFFFFFF);
					triangleAlpha.setSample(x, y, 0, 0xFF);
					ax--;
				}
				ay++;
			}
			File file = new File("ColorTriangle_" + s + ".png");
			ImageIO.write(triangle, "png", file);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	

	public static void saveTexture()
	{
		int tex = glGetInteger(GL_TEXTURE_BINDING_2D);
		saveTexture(tex, "test" + tex);
	}
	

	public static void saveTexture(int tex)
	{
		saveTexture(tex, "test" + tex);
	}
	

	public static void saveTexture(int tex, String fileName)
	{
		int prevTex = glGetInteger(GL_TEXTURE_BINDING_2D);
		glBindTexture(GL_TEXTURE_2D, tex);
		int w = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
		int h = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
		int k = w * h;
		IntBuffer pixelBuffer = BufferUtils.createIntBuffer(k);
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		pixelBuffer.clear();
		glGetTexImage(GL_TEXTURE_2D, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
		BufferedImage bufferedimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		WritableRaster alpha = bufferedimage.getAlphaRaster();
		for (int i1 = 0; i1 < h; ++i1) for (int j1 = 0; j1 < w; ++j1)
		{
			int pixel = pixelBuffer.get(i1 * w + j1);
			bufferedimage.setRGB(j1, i1, pixel & 0xFFFFFF);
			alpha.setSample(j1, i1, 0, (pixel & 0xFF000000) >> 24);
		}
		File file3 = new File(fileName + ".png");
		try
		{
			ImageIO.write(bufferedimage, "png", file3);
		}
		catch (IOException e) {}
		glBindTexture(GL_TEXTURE_2D, prevTex);
	}
	
	public void initWindow()
	{
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_DEPTH_BITS, 24);
		glfwWindowHint(GLFW_STENCIL_BITS, 8);
		glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
		int windowW = state.getWindowW();
		int windowH = state.getWindowH();
		window = glfwCreateWindow(windowW, windowH, "Minecraft Model Studio", 0, 0);
		if (window == 0) throw new RuntimeException("Failed to create the GLFW window");
		glfwSetWindowSizeLimits(window, 640, 480, GLFW_DONT_CARE, GLFW_DONT_CARE);
		glfwSetCharCallback(window, new CharCallback());
		glfwSetKeyCallback(window, new KeyCallback());
		glfwSetMouseButtonCallback(window, new MouseButtonCallback());
		glfwSetCursorPosCallback(window, new CursorPosCallback());
		glfwSetScrollCallback(window, new ScrollCallback());
		glfwSetWindowSizeCallback(window, new WindowSizeCallback());
		glfwSetWindowFocusCallback(window, new WindowFocusCallback());
		glfwSetWindowPosCallback(window, new WindowPosCallback());
		glfwSetWindowMaximizeCallback(window, new WindowMaximizeCallback());
		focused = true;
		if (state.getMaximized())
		{
			glfwMaximizeWindow(window);
		}
		else try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			int windowX = state.getWindowX(vidmode.width(), pWidth.get(0));
			int windowY = state.getWindowY(vidmode.height(), pHeight.get(0));
			glfwSetWindowPos(window, windowX, windowY);
		}
		glfwMakeContextCurrent(window);
		try
		{
			InputStream in = Main.getResource("icon.ico");
			List<BufferedImage> icons = ICODecoder.read(in);
			FileUtils.closeSafe(in);
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
		glfwShowWindow(window);
	}
	
	
    public static String getErrorCode(int error)
    {
    	switch (error)
    	{
    	case GL_INVALID_ENUM:
    		return "Invalid ENUM";
    	case GL_INVALID_VALUE:
    		return "Invalid value";
    	case GL_INVALID_OPERATION:
    		return "Invalid operation";
    	case GL_STACK_OVERFLOW:
    		return "Stack overflow";
    	case GL_STACK_UNDERFLOW:
    		return "Stack underflow";
    	case GL_OUT_OF_MEMORY:
    		return "Out of memory";
    	case GL_INVALID_FRAMEBUFFER_OPERATION:
    		return "Invalid framebuffer operation";
    	default:
    		return "Error Unkown";
    	}
    }
    
    
    public static void listGLErrors(String phase)
    {
        int error;
        boolean first = true;
        while ((error = glGetError()) != GL_NO_ERROR)
        {
        	if (first)
        	{
        		first = false;
        		Main.LOGGER.warn("OpenGL ERROR");
        		Main.LOGGER.warn("@ " + phase);
        	}
        	Main.LOGGER.warn(error + ": " + getErrorCode(error));
        }
    }
    
    
    public static InputStream getResource(String name)
    {
    	return Main.class.getResourceAsStream("resources/" + name);
    }
}