package firemerald.mcms.model;

import static org.lwjgl.opengl.GL15.*;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ComponentToggle;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.Mesh;

public class ComponentBox extends ComponentMesh
{
	private float texScale = 1;
	private float lengthX, lengthY, lengthZ;
	private boolean mirror = false;
	private boolean enableUp = true, enableDown = true, enableNorth = true, enableEast = true, enableSouth = true, enableWest = true, flipped = false; //x+ = east z+ = south
	
	private static Mesh makeMesh()
	{
		/**/
		return new Mesh(new float[24 * 3], new float[24 * 2], new float[24 * 3], new int[24], Mesh.DrawMode.TRIANGLES, GL_DYNAMIC_DRAW);
	}
	
	public ComponentBox(String name)
	{
		super(makeMesh(), name);
		init();
	}
	
	public ComponentBox(IComponentParent parent, String name)
	{
		super(makeMesh(), parent, name);
		init();
	}
	
	public ComponentBox(IComponentParent parent, ComponentBox from)
	{
		super(parent, from);
		this.texScale = from.texScale;
		this.lengthX = from.lengthX;
		this.lengthY = from.lengthY;
		this.lengthZ = from.lengthZ;
		this.mirror = from.mirror;
		this.flipped = from.flipped;
		this.enableUp = from.enableUp;
		this.enableDown = from.enableDown;
		this.enableNorth = from.enableNorth;
		this.enableEast = from.enableEast;
		this.enableSouth = from.enableSouth;
		this.enableWest = from.enableWest;
		setVerts();
		setTexs();
		setNorms();
		setInds();
	}
	
	private void init()
	{
		lengthX = lengthY = lengthZ = 1;
		setVerts();
		setTexs();
		setNorms();
		setInds();
	}
	
	public float texScale()
	{
		return texScale;
	}

	public void setTexScale(float texScale)
	{
		this.texScale = texScale;
		setTexs();
	}

	@Override
	public void texU(float texU)
	{
		this.texU = texU;
		setTexs();
	}

	@Override
	public void texV(float texV)
	{
		this.texV = texV;
		setTexs();
	}
	
	public void setVerts()
	{
		float maxX = lengthX;
		float maxY = lengthY;
		float maxZ = lengthZ;
		mesh().setPositions(new float[] {
				maxX, 0   , maxZ,
				maxX, 0   , 0   ,
				maxX, maxY, 0   ,
				maxX, maxY, maxZ,
				
				0   , maxY, maxZ,
				maxX, maxY, maxZ,
				maxX, maxY, 0   ,
				0   , maxY, 0   ,
				
				0   , 0   , maxZ,
				maxX, 0   , maxZ,
				maxX, maxY, maxZ,
				0   , maxY, maxZ,
				
				0   , 0   , 0   ,
				0   , 0   , maxZ,
				0   , maxY, maxZ,
				0   , maxY, 0   ,
				
				maxX, 0   , maxZ,
				0   , 0   , maxZ,
				0   , 0   , 0   ,
				maxX, 0   , 0   ,
				
				maxX, 0   , 0   ,
				0   , 0   , 0   ,
				0   , maxY, 0   ,
				maxX, maxY, 0   
		});
	}
	
	@Override
	public void setTexs()
	{
		float texSizeU = Main.instance.project.getTextureWidth();
		float texSizeV = Main.instance.project.getTextureHeight();
		float tU = texU * texScale / texSizeU, tV = texV * texScale / texSizeV;
		float tUX = lengthX * texScale / texSizeU, tUZ = lengthZ * texScale / texSizeU;
		float tVY = lengthY * texScale / texSizeV, tVZ = lengthZ * texScale / texSizeV;
		float u0 = tU, u1 = u0 + tUZ, u2 = u1 + tUX, u3a = u2 + tUZ, u3b = u2 + tUX, u4 = u3a + tUX;
		float v0 = tV, v1 = v0 + tVZ, v2 = v1 + tVY;
		if (mirror)
		{
			if (flipped)
			{
				mesh().setTexCoords(new float[] {
						u2, v2,
						u3a, v2,
						u3a, v1, //4
						u2, v1,
						
						u3b, v0, 
						u2, v0,
						u2, v1, //1
						u3b, v1,
						
						u3a, v2,
						u4, v2,
						u4, v1, //5
						u3a, v1,
						
						u0, v2,
						u1, v2,
						u1, v1, //2
						u0, v1,
						
						u1, v1,
						u2, v1,
						u2, v0, //0
						u1, v0,
						
						u1, v2,
						u2, v2,
						u2, v1, //3
						u1, v1,
				});
			}
			else
			{
				mesh().setTexCoords(new float[] {
						u1, v2,
						u0, v2,
						u0, v1,
						u1, v1, //2
						
						u2, v1,
						u1, v1,
						u1, v0,
						u2, v0, //0
						
						u2, v2,
						u1, v2,
						u1, v1,
						u2, v1, //3

						u3a, v2,
						u2, v2,
						u2, v1,
						u3a, v1, //4
						
						u2, v0,
						u3b, v0, 
						u3b, v1,
						u2, v1, //1
						
						u4, v2,
						u3a, v2,
						u3a, v1,
						u4, v1 //5
				});
			}
		}
		else
		{
			if (flipped)
			{
				mesh().setTexCoords(new float[] {
						u1, v2,
						u0, v2,
						u0, v1, //2
						u1, v1,
						
						u2, v0, 
						u3b, v0,
						u3b, v1, //1
						u2, v1,
						
						u4, v2,
						u3a, v2,
						u3a, v1, //5
						u4, v1,
						
						u3a, v2,
						u2, v2,
						u2, v1, //4
						u3a, v1,
						
						u2, v1,
						u1, v1,
						u1, v0, //0
						u2, v0,
						
						u2, v2,
						u1, v2,
						u1, v1, //3
						u2, v1,
				});
			}
			else
			{
				mesh().setTexCoords(new float[] {
						u2, v2,
						u3a, v2,
						u3a, v1,
						u2, v1, //4
						
						u1, v1,
						u2, v1,
						u2, v0,
						u1, v0, //0
						
						u1, v2,
						u2, v2,
						u2, v1,
						u1, v1, //3
						
						u0, v2,
						u1, v2,
						u1, v1,
						u0, v1, //2
						
						u3b, v0,
						u2, v0, 
						u2, v1,
						u3b, v1, //1
						
						u3a, v2,
						u4, v2,
						u4, v1,
						u3a, v1 //5
				});
			}
		}
		setTexMesh();
	}
	
	public void setNorms()
	{
		if (flipped)
		{
			mesh().setNormals(new float[] {
					-1, 0, 0,
					-1, 0, 0,
					-1, 0, 0,
					-1, 0, 0,
					0, -1, 0,
					0, -1, 0,
					0, -1, 0,
					0, -1, 0,
					0, 0, -1,
					0, 0, -1,
					0, 0, -1,
					0, 0, -1,
					1, 0, 0,
					1, 0, 0,
					1, 0, 0,
					1, 0, 0,
					0, 1, 0,
					0, 1, 0,
					0, 1, 0,
					0, 1, 0,
					0, 0, 1,
					0, 0, 1,
					0, 0, 1,
					0, 0, 1
			});
		}
		else
		{
			mesh().setNormals(new float[] {
					1, 0, 0,
					1, 0, 0,
					1, 0, 0,
					1, 0, 0,
					0, 1, 0,
					0, 1, 0,
					0, 1, 0,
					0, 1, 0,
					0, 0, 1,
					0, 0, 1,
					0, 0, 1,
					0, 0, 1,
					-1, 0, 0,
					-1, 0, 0,
					-1, 0, 0,
					-1, 0, 0,
					0, -1, 0,
					0, -1, 0,
					0, -1, 0,
					0, -1, 0,
					0, 0, -1,
					0, 0, -1,
					0, 0, -1,
					0, 0, -1
			});
		}
	}
	
	public void setInds()
	{
		/**/
		int numInds = 0;
		if (enableEast) numInds += 6;
		if (enableWest) numInds += 6;
		if (enableUp) numInds += 6;
		if (enableDown) numInds += 6;
		if (enableSouth) numInds += 6;
		if (enableNorth) numInds += 6;
		int[] inds = new int[numInds];
		int i = 0;
		if (flipped)
		{
			if (enableEast)
			{
				inds[i++] = 14;
				inds[i++] = 13;
				inds[i++] = 15;
				inds[i++] = 15;
				inds[i++] = 13;
				inds[i++] = 12;
			}
			if (enableUp)
			{
				inds[i++] = 18;
				inds[i++] = 17;
				inds[i++] = 19;
				inds[i++] = 19;
				inds[i++] = 17;
				inds[i++] = 16;
			}
			if (enableSouth)
			{
				inds[i++] = 22;
				inds[i++] = 21;
				inds[i++] = 23;
				inds[i++] = 23;
				inds[i++] = 21;
				inds[i++] = 20;
			}
			if (enableWest)
			{
				inds[i++] = 2;
				inds[i++] = 1;
				inds[i++] = 3;
				inds[i++] = 3;
				inds[i++] = 1;
				inds[i++] = 0;
			}
			if (enableDown)
			{
				inds[i++] = 6;
				inds[i++] = 5;
				inds[i++] = 7;
				inds[i++] = 7;
				inds[i++] = 5;
				inds[i++] = 4;
			}
			if (enableNorth)
			{
				inds[i++] = 10;
				inds[i++] = 9;
				inds[i++] = 11;
				inds[i++] = 11;
				inds[i++] = 9;
				inds[i++] = 8;
			}
		}
		else
		{
			if (enableEast)
			{
				inds[i++] = 0;
				inds[i++] = 1;
				inds[i++] = 3;
				inds[i++] = 3;
				inds[i++] = 1;
				inds[i++] = 2;
			}
			if (enableUp)
			{
				inds[i++] = 4;
				inds[i++] = 5;
				inds[i++] = 7;
				inds[i++] = 7;
				inds[i++] = 5;
				inds[i++] = 6;
			}
			if (enableSouth)
			{
				inds[i++] = 8;
				inds[i++] = 9;
				inds[i++] = 11;
				inds[i++] = 11;
				inds[i++] = 9;
				inds[i++] = 10;
			}
			if (enableWest)
			{
				inds[i++] = 12;
				inds[i++] = 13;
				inds[i++] = 15;
				inds[i++] = 15;
				inds[i++] = 13;
				inds[i++] = 14;
			}
			if (enableDown)
			{
				inds[i++] = 16;
				inds[i++] = 17;
				inds[i++] = 19;
				inds[i++] = 19;
				inds[i++] = 17;
				inds[i++] = 18;
			}
			if (enableNorth)
			{
				inds[i++] = 20;
				inds[i++] = 21;
				inds[i++] = 23;
				inds[i++] = 23;
				inds[i++] = 21;
				inds[i++] = 22;
			}
		}
		mesh().setIndices(inds);
		setTexMesh();
	}

	@Override
	public void doCleanUp()
	{
		mesh().cleanUp();
	}

	public float lengthX()
	{
		return lengthX;
	}

	public void lengthX(float lengthX)
	{
		this.lengthX = lengthX;
		setVerts();
		setTexs();
	}

	public float lengthY()
	{
		return lengthY;
	}

	public void lengthY(float lengthY)
	{
		this.lengthY = lengthY;
		setVerts();
		setTexs();
	}

	public float lengthZ()
	{
		return lengthZ;
	}

	public void lengthZ(float lengthZ)
	{
		this.lengthZ = lengthZ;
		setVerts();
		setTexs();
	}
	
	public boolean isMirrored()
	{
		return mirror;
	}
	
	public void setMirrored(boolean mirror)
	{
		if (mirror != this.mirror)
		{
			this.mirror = mirror;
			setTexs();
		}
	}
	
	public boolean isFlipped()
	{
		return flipped;
	}
	
	public void setFlipped(boolean flipped)
	{
		if (flipped != this.flipped)
		{
			this.flipped = flipped;
			setTexs();
			setNorms();
			setInds();
		}
	}
	
	public boolean hasUp()
	{
		return enableUp;
	}
	
	public void setUp(boolean enabled)
	{
		enableUp = enabled;
		setInds();
	}
	
	public boolean hasDown()
	{
		return enableDown;
	}
	
	public void setDown(boolean enabled)
	{
		enableDown = enabled;
		setInds();
	}
	
	public boolean hasNorth()
	{
		return enableNorth;
	}
	
	public void setNorth(boolean enabled)
	{
		enableNorth = enabled;
		setInds();
	}
	
	public boolean hasEast()
	{
		return enableEast;
	}
	
	public void setEast(boolean enabled)
	{
		enableEast = enabled;
		setInds();
	}
	
	public boolean hasSouth()
	{
		return enableSouth;
	}
	
	public void setSouth(boolean enabled)
	{
		enableSouth = enabled;
		setInds();
	}
	
	public boolean hasWest()
	{
		return enableWest;
	}
	
	public void setWest(boolean enabled)
	{
		enableWest = enabled;
		setInds();
	}

	@Override
	public ResourceLocation getDisplayIcon()
	{
		return Textures.MODEL_ICON_BOX;
	}

	@Override
	public void onTexSizeChange()
	{
		setTexs();
	}

	private ComponentFloatingLabel labelTexScale;
	private ComponentTextFloat scaleTex;
	private ComponentIncrementFloat scaleTexP, scaleTexS;
	private ComponentFloatingLabel labelLength;
	private ComponentTextFloat lengthXT;
	private ComponentIncrementFloat lengthXP, lengthXS;
	private ComponentTextFloat lengthYT;
	private ComponentIncrementFloat lengthYP, lengthYS;
	private ComponentTextFloat lengthZT;
	private ComponentIncrementFloat lengthZP, lengthZS;
	private ComponentFloatingLabel mirrorLabel;
	private ComponentToggle mirrorButton;
	private ComponentFloatingLabel flippedLabel;
	private ComponentToggle flippedButton;
	private ComponentFloatingLabel upLabel;
	private ComponentToggle upButton;
	private ComponentFloatingLabel downLabel;
	private ComponentToggle downButton;
	private ComponentFloatingLabel northLabel;
	private ComponentToggle northButton;
	private ComponentFloatingLabel southLabel;
	private ComponentToggle southButton;
	private ComponentFloatingLabel eastLabel;
	private ComponentToggle eastButton;
	private ComponentFloatingLabel westLabel;
	private ComponentToggle westButton;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		editorY = super.onSelect(editorPanes, editorY);
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelTexScale = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, "Texture Scale"));
		editorY += 20;
		editor.addElement(scaleTex         = new ComponentTextFloat(     editorX      , editorY, editorX + 290, editorY + 20, Main.instance.fontMsg, this.texScale, Float.MIN_VALUE, Float.POSITIVE_INFINITY, value -> this.setTexScale(value)));
		editor.addElement(scaleTexP        = new ComponentIncrementFloat(editorX + 290, editorY                             , scaleTex, 1));
		editor.addElement(scaleTexS        = new ComponentIncrementFloat(editorX + 290, editorY + 10                        , scaleTex, -1));
		editorY += 20;
		editor.addElement(labelLength  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, "Size"));
		editorY += 20;
		editor.addElement(lengthXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20, Main.instance.fontMsg, lengthX(), 0, Float.POSITIVE_INFINITY, value -> this.lengthX(value)));
		editor.addElement(lengthXP     = new ComponentIncrementFloat(editorX + 90 , editorY                             , lengthXT,  1));
		editor.addElement(lengthXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                        , lengthXT, -1));
		editor.addElement(lengthYT     = new ComponentTextFloat(     editorX + 100, editorY, editorX + 190, editorY + 20, Main.instance.fontMsg, lengthY(), 0, Float.POSITIVE_INFINITY, value -> this.lengthY(value)));
		editor.addElement(lengthYP     = new ComponentIncrementFloat(editorX + 190, editorY                             , lengthYT, 1));
		editor.addElement(lengthYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                        , lengthYT, -1));
		editor.addElement(lengthZT     = new ComponentTextFloat(     editorX + 200, editorY, editorX + 290, editorY + 20, Main.instance.fontMsg, lengthZ(), 0, Float.POSITIVE_INFINITY, value -> this.lengthZ(value)));
		editor.addElement(lengthZP     = new ComponentIncrementFloat(editorX + 290, editorY                             , lengthZT, 1));
		editor.addElement(lengthZS     = new ComponentIncrementFloat(editorX + 290, editorY + 10                        , lengthZT, -1));
		editorY += 20;
		editor.addElement(mirrorButton = new ComponentToggle(        editorX + 5  , editorY + 5, editorX + 15 , editorY + 15, this.isMirrored(), (value) -> this.setMirrored(value)));
		editor.addElement(mirrorLabel  = new ComponentFloatingLabel( editorX + 20 , editorY    , editorX + 150, editorY + 20, Main.instance.fontMsg, "mirror"));
		editor.addElement(flippedButton = new ComponentToggle(       editorX + 155, editorY + 5, editorX + 165 , editorY + 15, this.isFlipped(), (value) -> this.setFlipped(value)));
		editor.addElement(flippedLabel  = new ComponentFloatingLabel(editorX + 170, editorY    , editorX + 300, editorY + 20, Main.instance.fontMsg, "flip faces"));
		editorY += 20;
		editor.addElement(upButton     = new ComponentToggle(       editorX + 5  , editorY + 5, editorX + 15 , editorY + 15, this.hasUp(), (value) -> this.setUp(value)));
		editor.addElement(upLabel      = new ComponentFloatingLabel(editorX + 20 , editorY    , editorX + 150, editorY + 20, Main.instance.fontMsg, "up face"));
		editor.addElement(downButton   = new ComponentToggle(       editorX + 155, editorY + 5, editorX + 165, editorY + 15, this.hasDown(), (value) -> this.setDown(value)));
		editor.addElement(downLabel    = new ComponentFloatingLabel(editorX + 170, editorY    , editorX + 300, editorY + 20, Main.instance.fontMsg, "down face"));
		editorY += 20;
		editor.addElement(northButton  = new ComponentToggle(       editorX + 5  , editorY + 5, editorX + 15 , editorY + 15, this.hasNorth(), (value) -> this.setNorth(value)));
		editor.addElement(northLabel   = new ComponentFloatingLabel(editorX + 20 , editorY    , editorX + 150, editorY + 20, Main.instance.fontMsg, "north face"));
		editor.addElement(southButton  = new ComponentToggle(       editorX + 155, editorY + 5, editorX + 165, editorY + 15, this.hasSouth(), (value) -> this.setSouth(value)));
		editor.addElement(southLabel   = new ComponentFloatingLabel(editorX + 170, editorY    , editorX + 300, editorY + 20, Main.instance.fontMsg, "south face"));
		editorY += 20;
		editor.addElement(eastButton   = new ComponentToggle(       editorX + 5  , editorY + 5, editorX + 15 , editorY + 15, this.hasEast(), (value) -> this.setEast(value)));
		editor.addElement(eastLabel    = new ComponentFloatingLabel(editorX + 20 , editorY    , editorX + 150, editorY + 20, Main.instance.fontMsg, "east face"));
		editor.addElement(westButton   = new ComponentToggle(       editorX + 155, editorY + 5, editorX + 165, editorY + 15, this.hasWest(), (value) -> this.setWest(value)));
		editor.addElement(westLabel    = new ComponentFloatingLabel(editorX + 170, editorY    , editorX + 300, editorY + 20, Main.instance.fontMsg, "west face"));
		editorY += 20;
		//TODO inverted
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelTexScale);
		editor.removeElement(scaleTex);
		editor.removeElement(scaleTexP);
		editor.removeElement(scaleTexS);
		editor.removeElement(labelLength);
		editor.removeElement(lengthXT);
		editor.removeElement(lengthXP);
		editor.removeElement(lengthXS);
		editor.removeElement(lengthYT);
		editor.removeElement(lengthYP);
		editor.removeElement(lengthYS);
		editor.removeElement(lengthZT);
		editor.removeElement(lengthZP);
		editor.removeElement(lengthZS);
		editor.removeElement(mirrorButton);
		editor.removeElement(mirrorLabel);
		editor.removeElement(flippedButton);
		editor.removeElement(flippedLabel);
		editor.removeElement(upButton);
		editor.removeElement(upLabel);
		editor.removeElement(downButton);
		editor.removeElement(downLabel);
		editor.removeElement(northButton);
		editor.removeElement(northLabel);
		editor.removeElement(southButton);
		editor.removeElement(southLabel);
		editor.removeElement(eastButton);
		editor.removeElement(eastLabel);
		editor.removeElement(westButton);
		editor.removeElement(westLabel);
		labelTexScale = null;
		scaleTex      = null;
		scaleTexP     = null;
		scaleTexS     = null;
		labelLength   = null;
		lengthXT      = null;
		lengthXP      = null;
		lengthXS      = null;
		lengthYT      = null;
		lengthYP      = null;
		lengthYS      = null;
		lengthZT      = null;
		lengthZP      = null;
		lengthZS      = null;
		mirrorButton  = null;
		mirrorLabel   = null;
		flippedButton = null;
		flippedLabel  = null;
		upButton      = null;
		upLabel       = null;
		downButton    = null;
		downLabel     = null;
		northButton   = null;
		northLabel    = null;
		southButton   = null;
		southLabel    = null;
		eastButton    = null;
		eastLabel     = null;
		westButton    = null;
		westLabel     = null;
	}
	
	@Override
	public String getXMLName()
	{
		return "box";
	}
	
	@Override
	public void addData(AbstractElement el)
	{
		super.addData(el);
		if (texScale != 1) el.setFloat("texScale", texScale);
		el.setFloat("lengthX", lengthX);
		el.setFloat("lengthY", lengthY);
		el.setFloat("lengthZ", lengthZ);
		if (mirror) el.setBoolean("mirror", true);
		if (flipped) el.setBoolean("flipped", true);
		if (!enableUp) el.setBoolean("enableUp", false);
		if (!enableDown) el.setBoolean("enableDown", false);
		if (!enableNorth) el.setBoolean("enableNorth", false);
		if (!enableEast) el.setBoolean("enableEast", false);
		if (!enableSouth) el.setBoolean("enableSouth", false);
		if (!enableWest) el.setBoolean("enableWest", false);
	}
	
	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		texScale = el.getFloat("texScale", 1);
		lengthX = el.getFloat("lengthX", 0);
		lengthY = el.getFloat("lengthY", 0);
		lengthZ = el.getFloat("lengthZ", 0);
		mirror = el.getBoolean("mirror", false);
		flipped = el.getBoolean("flipped", false);
		enableUp = el.getBoolean("enableUp", true);
		enableDown = el.getBoolean("enableDown", true);
		enableNorth = el.getBoolean("enableNorth", true);
		enableEast = el.getBoolean("enableEast", true);
		enableSouth = el.getBoolean("enableSouth", true);
		enableWest = el.getBoolean("enableWest", true);
		setVerts();
		setTexs();
		setNorms();
		setInds();
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> model)
	{
		if (newParent instanceof IComponentParent)
		{
			ComponentBox box = new ComponentBox((IComponentParent) newParent, this);
			this.copyChildren(box, model);
			return box;
		}
		else return null;
	}

	@Override
	public ComponentBox cloneSelf(IComponentParent clonedParent)
	{
		return new ComponentBox(clonedParent, this);
	}
}