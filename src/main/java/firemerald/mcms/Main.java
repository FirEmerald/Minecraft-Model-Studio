package firemerald.mcms;

import static org.lwjgl.opengl.GL33.*;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.util.RaytraceResult;
import firemerald.mcms.events.ApplicationEvent;
import firemerald.mcms.events.EventBus;
import firemerald.mcms.events.RenderEvent;
import firemerald.mcms.events.TickEvent;
import firemerald.mcms.events.gui.GuiEvent;
import firemerald.mcms.events.gui.TitlebarInitEvent;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.gui.popups.GuiPopupUnsavedChanges;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.plugin.PluginLoader;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.shader.ModelShader;
import firemerald.mcms.shader.ModelShaderBase;
import firemerald.mcms.shader.ShadowShader;
import firemerald.mcms.texture.BlendMode;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.HSL;
import firemerald.mcms.texture.HSV;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.tools.ITool;
import firemerald.mcms.texture.tools.IToolHolder;
import firemerald.mcms.texture.tools.ToolView;
import firemerald.mcms.theme.GuiTheme;
import firemerald.mcms.util.ApplicationState;
import firemerald.mcms.util.FileWatcher;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.IEditable;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.TitlebarItems;
import firemerald.mcms.util.font.FontRenderer;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.util.mesh.DrawMode;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.util.mesh.ModelMesh;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Window;
import firemerald.mcms.window.glfw.GLFWWindow;

public class Main
{
	public static final String ID = "mcms";
	public static final String VERSION = "0.1.3";
	public static final String BUILD_DATE = "10/12/2020 14:33";
	public static final Logger LOGGER = LogManager.getLogger("MCMS"); //the main logger;
	public static Main instance;
	public static final int MIN_W = 640, MIN_H = 480;
	public int sizeW, sizeH;
	public boolean focused = false;
	public double mX = -1, mY = -1;
	public GuiShader guiShader;
	public ModelShaderBase currentModelShader;
	public ModelShader modelShader;
	public ShadowShader shadowShader;
	public TextureManager textureManager;
	protected GuiScreen gui = new GuiScreen() {}; //blank gui to prevent null pointer exceptions
	public FontRenderer font0, fontMsg;
	public RaytraceResult trace = null;
	private IEditable editing = null;
	private IModelEditable editingModel = null;
	public static final Queue<Runnable> CLEANUP_ACTIONS = new ConcurrentLinkedQueue<>();
	public GuiMesh screen;
	public FileWatcher watcher;
	public boolean tooSmall = false;
	public final ApplicationState state = new ApplicationState();
	public final Project project = new Project("untitled", 16, 16);
	private Texture overlay;
	public static double time = 0;
	public EditorPanes editorPanes;
	public IToolHolder toolHolder = new IToolHolder()
	{
		public Color color1 = new Color(0, 0, 0, 1), color2 = new Color(1, 1, 1, 1);
		public BlendMode blendMode = BlendMode.NORMAL;
		
		@Override
		public Color getColor1()
		{
			return color1;
		}

		@Override
		public Color getColor2()
		{
			return color2;
		}

		@Override
		public void setColor1(ColorModel color)
		{
			color1.c = color;
		}

		@Override
		public void setColor2(ColorModel color)
		{
			color2.c = color;
		}

		@Override
		public void setAlpha(float alpha)
		{
			color1.a = alpha;
			color2.a = alpha;
		}

		@Override
		public BlendMode getBlendMode()
		{
			return blendMode;
		}

		@Override
		public void setBlendMode(BlendMode mode)
		{
			blendMode = mode;
		}
	};
	public ITool tool = ToolView.INSTANCE;
	public final EventBus eventBus = new EventBus("mcms_event_bus");
	
	public boolean doAction(Action action)
	{
		if (action.canRun.getAsBoolean() && !getGui().onHotkey(action))
		{
			action.action.run();
			return true;
		}
		else return false;
	}
	
	public void openGui(@NonNull GuiScreen gui)
	{
		GuiEvent.Open event = new GuiEvent.Open(gui);
		eventBus.post(event);
		gui = event.getGui();
		if (gui != null && gui != this.gui)
		{
			if (gui instanceof GuiPopup) ((GuiPopup) gui).under = this.gui;
			else eventBus.post(new GuiEvent.Close(this.gui));
			this.gui = gui;
			gui.setSize(sizeW, sizeH);
			eventBus.post(new GuiEvent.Init(gui, sizeW, sizeH));
		}
	}
	
	public void closePopup()
	{
		if (gui instanceof GuiPopup)
		{
			eventBus.post(new GuiEvent.Close(this.gui));
			gui = ((GuiPopup) gui).under;
		}
	}
	
	public void onGuiUpdate(GuiUpdate reason)
	{
		project.models.values().forEach(model -> model.onGuiUpdate(reason));
		gui.onGuiUpdate(reason);
	}
	
	public GuiScreen getGui()
	{
		return gui;
	}
	
	public void setEditing(IEditable editable)
	{
		if (this.editing != null) this.editing.onDeselect(editorPanes);
		if ((this.editing = editable) != null)
		{
			this.editingModel = editable.getRenderComponent();
			editable.onSelect(editorPanes, editorPanes.editor.minY);
		}
	}
	
	public IEditable getEditing()
	{
		return this.editing;
	}
	
	public IModelEditable getEditingModel()
	{
		return this.editingModel;
	}

    public static GuiMesh guiTempMesh;
    public static ModelMesh modelTempMesh;
	
	public static void launch(String[] args)
	{
		(instance = new Main()).run(args);
	}
	
	public void setSize(int w, int h)
	{
		if (w < MIN_W || h < MIN_H)
		{
			tooSmall = true;
		}
		else
		{
			tooSmall = false;
			sizeW = w;
			sizeH = h;
			if (glActive) screen.setMesh(0, 0, w, h, 0, 0, 1, 1);
			if (gui != null) gui.setSize(w, h);
		}
	}
	
	public static boolean glActive = false;
	
	//public SystemTray tray;
	//public TrayIcon trayIcon = null;
	
	public Main() {}
	
	public GuiTheme getTheme()
	{
		return state.getTheme();
	}
	
	public void setTheme(GuiTheme theme)
	{
		state.setTheme(theme);
	}
	
	public void setThemeNoStateUpdate(GuiTheme theme)
	{
		state.setThemeNoStateUpdate(theme);
	}
	
	public Window window;
	
	@SuppressWarnings("deprecation")
	public void run(String[] args)
	{
		/*
		// temporary shader helper
		List<Vector3f> vecs = new ArrayList<>();
		float weight = 0;
		for (float x = -5; x <= 5; x++) for (float y = -5; y <= 5; y++)
		{
			float r = x * x + y * y;
			float w = (5 - (float) Math.sqrt(r));
			if (w > 0)
			{
				weight += w;
				vecs.add(new Vector3f(x, y, w));
			}
		}
		for (Vector3f v : vecs) v.z /= weight;
		StringBuilder str = new StringBuilder("const vec3 shadowWeights[");
		str.append(vecs.size());
		str.append("]=vec3[");
		str.append(vecs.size());
		str.append("](\n");
		for (Vector3f v : vecs) str.append("\tvec3(" + v.x + ", " + v.y + ", " + v.z + "),\n");
		str.append(");");
		System.out.println(str);
		*/
		
		
		PluginLoader.INSTANCE.constructPlugins();
		eventBus.post(new ApplicationEvent.PreInitialization());
		try
		{
			watcher = new FileWatcher();
		}
		catch (IOException e)
		{
			LOGGER.log(Level.FATAL, "Could not instantiate the filesystem watcher service", e);
			Thread.currentThread().stop(e);
		}
		window = new GLFWWindow(this);
		//window = new AWTWindow(this);
		if (ApplicationState.FILE.exists()) state.loadState();
		state.saveState();
		window.showWindow();
		window.focusWindow();

		/*
        PopupMenu menu = new PopupMenu();
        {
        	MenuItem exit = new MenuItem("Exit");
        	ActionListener actionExit = action -> window.close();
        	exit.addActionListener(actionExit);
        	menu.add(exit);
        	menu.add("-");
        	exit = new MenuItem("Exit 2");
        	exit.addActionListener(actionExit);
        	menu.add(exit);
        	exit = new MenuItem("Exit 3");
        	exit.addActionListener(actionExit);
        	menu.add(exit);
        	Menu menu2 = new Menu("menu");
        	exit = new MenuItem("Exit 4");
        	exit.addActionListener(actionExit);
        	menu2.add(exit);
        	exit = new MenuItem("Exit 5");
        	exit.addActionListener(actionExit);
        	menu2.add(exit);
        	Menu menu3 = new Menu("menu");
        	exit = new MenuItem("Exit 6");
        	exit.addActionListener(actionExit);
        	menu3.add(exit);
        	exit = new MenuItem("Exit 7");
        	exit.addActionListener(actionExit);
        	menu3.add(exit);
        	menu2.add(menu3);
        	menu.add(menu2);
        	menu.add("nothing");
        	exit = new MenuItem("Close");
        	exit.addActionListener(action -> {
        		GuiScreen gui;
        		if ((gui = Main.instance.gui) instanceof GuiPopup) ((GuiPopup) gui).deactivate();
        	});
        	menu.add(exit);
        }
		if (!SystemTray.isSupported())
		{
			//tray not supported
			LOGGER.log(Level.INFO, "System tray is not supported. tray icon and notification features won't be available.");
		}
		else
		{
			LOGGER.log(Level.INFO, "Enabling system tray features.");
			tray = SystemTray.getSystemTray();
			Image img = Toolkit.getDefaultToolkit().createImage(getResourceURL("tray_icon.png"));
			trayIcon = new TrayIcon(img, "MC Model Studio");
			trayIcon.setImageAutoSize(true);
	        trayIcon.setToolTip("MC Model Studio");
	        trayIcon.setPopupMenu(menu);
	        try
	        {
				tray.add(trayIcon);
			}
	        catch (AWTException e) {} //already prevented
	        trayIcon.displayMessage("MC Model Studio", "System tray features enabled", MessageType.INFO);
		}
		*/
		
		// testing lwjgl3-awt
		
		//TODO
		//window = new AWTWindow(this);
		
		/*
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
		
		//TODO
		//saveColorWheel(155, 15);
		//saveColorSquare(155, 135);
		//saveHueSquare(155, 1, false);
		//saveHueSquare(15, 155, true);
		//saveColorTriangle(125);
		//saveGradientSquare(155, 15, new RGB(0, 0, 0), new RGB(1, 0, 0), new RGB(0, 1, 1), new RGB(1, 1, 1), "red");
		//saveGradientSquare(155, 15, new RGB(0, 0, 0), new RGB(0, 1, 0), new RGB(1, 0, 1), new RGB(1, 1, 1), "green");
		//saveGradientSquare(155, 15, new RGB(0, 0, 0), new RGB(0, 0, 1), new RGB(1, 1, 0), new RGB(1, 1, 1), "blue");
		eventBus.post(new ApplicationEvent.Initialization());
		eventBus.post(new ApplicationEvent.PostInitialization());
		if (args.length > 0)
		{
			File file = new File(args[0]);
			if (file.exists() && !file.isDirectory())
			{
				try
				{
					 project.load(file);
				}
				catch (Exception e)
				{
					GuiPopupException.onException("Couldn't open invalid or corrupt project file " + file.getAbsolutePath(), e);
				}
			}
			else GuiPopupException.onException("Couldn't open nonexistent project file " + file.getAbsolutePath());
		}
		boolean first = true;
		while (!window.isClosed())
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
			//System.out.println(thisTick);
			eventBus.post(new TickEvent.Pre(thisTick));
			window.tick(thisTick);
			eventBus.post(new TickEvent.Post(thisTick));
			eventBus.post(new RenderEvent.Pre());
			window.render();
			eventBus.post(new RenderEvent.Post());
			String title = "Minecraft Model Studio - " + project.getName();
			if (project.needsSave()) title += " *";
			if (project.getSource() != null) title += " (" + project.getSource().toString() + ")";
			window.setTitle(title);
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				LOGGER.log(Level.WARN, e);
			}
		}
		eventBus.post(new ApplicationEvent.Shutdown());
		//tray.remove(trayIcon);
		//tray = null;
		//trayIcon = null;
	}
	
	public long lastNanos = 0;
	
	public void initOpenGL()
	{
		GL.createCapabilities();
		screen = new GuiMesh(0, 0, sizeW, sizeH, 0, 0, 1, 1);
		font0 = new FontRenderer(new ResourceLocation(ID, "0"), 8, 32, 255, true);
		fontMsg = new FontRenderer(new ResourceLocation(ID, "msg"), 12, true);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
		guiShader = new GuiShader("gui", "gui");
		currentModelShader = modelShader = new ModelShader("model", "model");
		shadowShader = new ShadowShader("shadows", "shadows");
		glDisable(GL_CULL_FACE);

		guiTempMesh = new GuiMesh(new float[0], new float[0], new int[0], DrawMode.TRIANGLES, GL_DYNAMIC_DRAW);
		modelTempMesh = new ModelMesh(new float[0], new float[0], new float[0], new int[0], DrawMode.TRIANGLES, GL_DYNAMIC_DRAW);
		textureManager = new TextureManager();
		overlay = new Texture(1, 1);

		this.openGui(new GuiMain());
		glActive = true;
	}
	
	public void tick(long thisTick)
	{
		time += thisTick * .000000001;
    	watcher.poll();
        getOverlay().clearTexture();
        if (gui != null)
        {
            float mx = (float) mX;
            float my = (float) mY;
        	gui.tick(mx, my, (float) (thisTick * .000000001));
        	window.setCursor(gui.getCursor(mx, my));
        }
        else window.setCursor(Cursor.STANDARD);
		while (!CLEANUP_ACTIONS.isEmpty()) CLEANUP_ACTIONS.remove().run();
	}
	
	public void renderOpenGL()
	{
    	if (!tooSmall)
    	{
    		guiShader.reset();
			guiShader.bind();
			glViewport(0, 0, sizeW, sizeH);
			GuiShader.MODEL.matrix().identity();
			GuiShader.VIEW.matrix().identity();
			GuiShader.PROJECTION.matrix().identity();
    		GuiShader.VIEW.matrix().translate(0, 0, -1000);
    		GuiShader.PROJECTION.matrix().setOrtho(0, sizeW, sizeH, 0, 0, 2000);
    		guiShader.updateModel();
    		guiShader.updateView();
    		guiShader.updateProjection();
    		RenderUtil.clearScissor();
			getTheme().drawBackground();
            float mx = (float) mX;
            float my = (float) mY;
            if (gui != null) gui.render(mx, my, true);
			guiShader.unbind();
			listGLErrors("End render");
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
	

	public static void saveFloatTexture()
	{
		int tex = glGetInteger(GL_TEXTURE_BINDING_2D);
		saveFloatTexture(tex, "test" + tex);
	}
	

	public static void saveFloatTexture(int tex)
	{
		saveFloatTexture(tex, "test" + tex);
	}
	

	public static void saveFloatTexture(int tex, String fileName)
	{
		int prevTex = glGetInteger(GL_TEXTURE_BINDING_2D);
		glBindTexture(GL_TEXTURE_2D, tex);
		int w = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
		int h = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
		int k = w * h;
		FloatBuffer pixelBuffer = BufferUtils.createFloatBuffer(k);
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		pixelBuffer.clear();
		glGetTexImage(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, GL_FLOAT, pixelBuffer);
		BufferedImage bufferedimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int i1 = 0; i1 < h; ++i1) for (int j1 = 0; j1 < w; ++j1)
		{
			int pixel = (int) (255 * (pixelBuffer.get(i1 * w + j1)));
			if (pixel < 0) pixel = 0;
			else if (pixel > 255) pixel = 255;
			bufferedimage.setRGB(j1, i1, pixel | (pixel << 8) | (pixel << 16));
		}
		File file3 = new File(fileName + ".png");
		try
		{
			ImageIO.write(bufferedimage, "png", file3);
		}
		catch (IOException e) {}
		glBindTexture(GL_TEXTURE_2D, prevTex);
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
        int ind = 0;
        int skipped = 0;
        while ((error = glGetError()) != GL_NO_ERROR)
        {
        	if (ind == 0)
        	{
        		Main.LOGGER.warn("OpenGL ERROR");
        		Main.LOGGER.warn("@ " + phase);
        	}
        	if (ind < 10) Main.LOGGER.warn(error + ": " + getErrorCode(error));
        	else skipped++;
        	ind++;
        }
        if (skipped > 0) Main.LOGGER.warn("and " + skipped + " more");
    }
    
    public static InputStream getResource(ResourceLocation resource)
    {
    	return Main.class.getClassLoader().getResourceAsStream(resource.getLocation());
    }
    
    public static URL getResourceURL(ResourceLocation resource)
    {
    	return Main.class.getClassLoader().getResource(resource.getLocation());
    }

	public Texture getOverlay() {
		return overlay;
	}
	
	public void tryClose() //TODO dialogue
	{
		if (project.needsSave()) new GuiPopupUnsavedChanges(window::close).activate();
		else window.close();
	}
	
	public static Map<String, Menu> makeTitlebar()
	{
		TitlebarInitEvent event = new TitlebarInitEvent();
		Function<MenuItem, MenuItem> file = event.getOrMakeCategory("File");
		file.apply(TitlebarItems.NEW_PROJECT);
		file.apply(TitlebarItems.LOAD_PROJECT);
		file.apply(TitlebarItems.EDIT_PROJECT);
		file.apply(TitlebarItems.SAVE_PROJECT);
		file.apply(TitlebarItems.SAVE_PROJECT_AS);
		file.apply(TitlebarItems.EXPORT_PROJECT);
		file.apply(TitlebarItems.EXIT);
		Function<MenuItem, MenuItem> edit = event.getOrMakeCategory("Edit");
		edit.apply(TitlebarItems.UNDO);
		edit.apply(TitlebarItems.REDO);
		Function<MenuItem, MenuItem> model = event.getOrMakeCategory("Model");
		model.apply(TitlebarItems.NEW_MODEL);
		model.apply(TitlebarItems.ADD_MODEL);
		model.apply(TitlebarItems.LOAD_MODEL);
		model.apply(TitlebarItems.CLONE_MODEL);
		model.apply(TitlebarItems.EXPORT_MODEL);
		model.apply(TitlebarItems.EXPORT_UNPOSED_MODEL);
		model.apply(TitlebarItems.EDIT_MODEL);
		model.apply(TitlebarItems.REMOVE_MODEL);
		model.apply(TitlebarItems.IMPORT_SKELETON);
		model.apply(TitlebarItems.EXPORT_SKELETON);
		Function<MenuItem, MenuItem> texture = event.getOrMakeCategory("Texture");
		texture.apply(TitlebarItems.NEW_TEXTURE);
		texture.apply(TitlebarItems.ADD_TEXTURE);
		texture.apply(TitlebarItems.LOAD_TEXTURE);
		texture.apply(TitlebarItems.CLONE_TEXTURE);
		texture.apply(TitlebarItems.SAVE_TEXTURE);
		texture.apply(TitlebarItems.EDIT_TEXTURE);
		texture.apply(TitlebarItems.REMOVE_TEXTURE);
		Function<MenuItem, MenuItem> animation = event.getOrMakeCategory("Animation");
		animation.apply(TitlebarItems.NEW_ANIMATION);
		animation.apply(TitlebarItems.ADD_ANIMATION);
		animation.apply(TitlebarItems.LOAD_ANIMATION);
		animation.apply(TitlebarItems.CLONE_ANIMATION);
		animation.apply(TitlebarItems.SAVE_ANIMATION);
		animation.apply(TitlebarItems.EDIT_ANIMATION);
		animation.apply(TitlebarItems.REVERSE_ANIMATION);
		animation.apply(TitlebarItems.REMOVE_ANIMATION);
		Function<MenuItem, MenuItem> options = event.getOrMakeCategory("Options");
		options.apply(TitlebarItems.TOGGLE_NODES);
		options.apply(TitlebarItems.TOGGLE_BONES);
		options.apply(TitlebarItems.TOGGLE_SHADOWS);
		options.apply(TitlebarItems.CHANGE_THEME);
		options.apply(TitlebarItems.HOTKEYS);
		options.apply(TitlebarItems.LAYOUT_MENU);
		Function<MenuItem, MenuItem> help = event.getOrMakeCategory("Help");
		help.apply(TitlebarItems.ABOUT_MCMS);
		help.apply(TitlebarItems.PLUGINS);
		Main.instance.eventBus.post(event);
		return event.getTitlebar();
	}
}