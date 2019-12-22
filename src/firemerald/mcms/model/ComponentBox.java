package firemerald.mcms.model;

import static org.lwjgl.opengl.GL15.*;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.ComponentToggle;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.mesh.Mesh;

public class ComponentBox extends ComponentMesh
{
	private float lengthX, lengthY, lengthZ;
	private boolean mirror = false;
	private boolean enableUp = true, enableDown = true, enableNorth = true, enableEast = true, enableSouth = true, enableWest = true; //x+ = east z+ = south
	
	private static Mesh makeMesh()
	{
		/**/
		return new Mesh(new float[24 * 3], new float[24 * 2], new float[] {
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
		}, new int[] {
				0, 1, 3,
				3, 1, 2,
				4, 5, 7,
				7, 5, 6,
				8, 9, 11,
				11, 9, 10,
				12, 13, 15,
				15, 13, 14,
				16, 17, 19,
				19, 17, 18,
				20, 21, 23,
				23, 21, 22
		}, Mesh.DrawMode.TRIANGLES, GL_DYNAMIC_DRAW);
		/*/
		return new Mesh(new float[24 * 3], new float[24 * 2], new float[] {
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
		}, new int[] {
				0, 1, 2, 3,
				4, 5, 6, 7,
				8, 9, 10, 11,
				12, 13, 14, 15,
				16, 17, 18, 19,
				20, 21, 22, 23
		}, Mesh.DrawMode.QUADS, GL_DYNAMIC_DRAW);
		/**/
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
		this.lengthX = from.lengthX;
		this.lengthY = from.lengthY;
		this.lengthZ = from.lengthZ;
		this.mirror = from.mirror;
		this.enableUp = from.enableUp;
		this.enableDown = from.enableDown;
		this.enableNorth = from.enableNorth;
		this.enableEast = from.enableEast;
		this.enableSouth = from.enableSouth;
		this.enableWest = from.enableWest;
	}
	
	private void init()
	{
		lengthX = lengthY = lengthZ = .0625f;
		setVerts();
		setTexs();
		setInds();
	}

	@Override
	public void texU(float texU)
	{
		super.texU(texU);
		setTexs();
	}

	@Override
	public void texV(float texV)
	{
		super.texV(texV);
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
		int texSizeU = this.getTexSizeU();
		int texSizeV = this.getTexSizeV();
		float tU = texU / texSizeU, tV = texV / texSizeV;
		float tUX = lengthX / texSizeU, tUZ = lengthZ / texSizeU;
		float tVY = lengthY / texSizeV, tVZ = lengthZ / texSizeV;
		float u0 = tU, u1 = u0 + tUZ, u2 = u1 + tUX, u3a = u2 + tUZ, u3b = u2 + tUX, u4 = u3a + tUX;
		float v0 = tV, v1 = v0 + tVZ, v2 = v1 + tVY;
		if (mirror)
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
	
	public void setInds()
	{
		int numInds = 0;
		if (enableEast) numInds += 6;
		if (enableWest) numInds += 6;
		if (enableUp) numInds += 6;
		if (enableDown) numInds += 6;
		if (enableSouth) numInds += 6;
		if (enableNorth) numInds += 6;
		int[] inds = new int[numInds];
		int i = 0;
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
		mesh().setIndices(inds);
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
	
	public boolean hasUp()
	{
		return enableUp;
	}
	
	public void setUp(boolean enabled)
	{
		enableUp = enabled;
	}
	
	public boolean hasDown()
	{
		return enableDown;
	}
	
	public void setDown(boolean enabled)
	{
		enableDown = enabled;
	}
	
	public boolean hasNorth()
	{
		return enableNorth;
	}
	
	public void setNorth(boolean enabled)
	{
		enableNorth = enabled;
	}
	
	public boolean hasEast()
	{
		return enableEast;
	}
	
	public void setEast(boolean enabled)
	{
		enableEast = enabled;
	}
	
	public boolean hasSouth()
	{
		return enableSouth;
	}
	
	public void setSouth(boolean enabled)
	{
		enableSouth = enabled;
	}
	
	public boolean hasWest()
	{
		return enableWest;
	}
	
	public void setWest(boolean enabled)
	{
		enableWest = enabled;
	}

	@Override
	public String getDisplayIcon()
	{
		return Textures.MODEL_ICON_BOX;
	}

	@Override
	public void onTexSizeChange()
	{
		setTexs();
	}

	private ComponentFloatingLabel labelLength;
	private ComponentTextFloat lengthXT;
	private ComponentIncrementFloat lengthXP, lengthXS;
	private ComponentTextFloat lengthYT;
	private ComponentIncrementFloat lengthYP, lengthYS;
	private ComponentTextFloat lengthZT;
	private ComponentIncrementFloat lengthZP, lengthZS;
	private ComponentFloatingLabel mirrorLabel;
	private ComponentToggle mirrorButton;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		editorY = super.onSelect(editorPanes, editorY);
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
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
		editor.addElement(mirrorLabel  = new ComponentFloatingLabel(editorX     , editorY    , editorX + 45, editorY + 20, Main.instance.fontMsg, "mirror"));
		editor.addElement(mirrorButton = new ComponentToggle(       editorX + 45, editorY + 5, editorX + 55, editorY + 15, this.isMirrored(), (value) -> this.setMirrored(value.booleanValue())));
		editorY += 20;
		//TODO inverted
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
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
		editor.removeElement(mirrorLabel);
		editor.removeElement(mirrorButton);
		labelLength  = null;
		lengthXT     = null;
		lengthXP     = null;
		lengthXS     = null;
		lengthYT     = null;
		lengthYP     = null;
		lengthYS     = null;
		lengthZT     = null;
		lengthZP     = null;
		lengthZS     = null;
		mirrorButton = null;
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
		el.setFloat("lengthX", lengthX);
		el.setFloat("lengthY", lengthY);
		el.setFloat("lengthZ", lengthZ);
		if (mirror) el.setBoolean("mirror", true);
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
		lengthX = el.getFloat("lengthX", 0);
		lengthY = el.getFloat("lengthY", 0);
		lengthZ = el.getFloat("lengthZ", 0);
		mirror = el.getBoolean("mirror", false);
		enableUp = el.getBoolean("enableUp", true);
		enableDown = el.getBoolean("enableDown", true);
		enableNorth = el.getBoolean("enableNorth", true);
		enableEast = el.getBoolean("enableEast", true);
		enableSouth = el.getBoolean("enableSouth", true);
		enableWest = el.getBoolean("enableWest", true);
		setVerts();
		setTexs();
		setInds();
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IModel model)
	{
		if (newParent instanceof IComponentParent)
		{
			ComponentBox box = new ComponentBox((IComponentParent) newParent, this.name);
			box.lengthX(lengthX());
			box.lengthY(lengthY());
			box.lengthZ(lengthZ());
			box.setMirrored(mirror);
			box.posX(posX());
			box.posY(posY());
			box.posZ(posZ());
			box.offX(offX());
			box.offY(offY());
			box.offZ(offZ());
			box.rotation(rotation().copy());
			box.setTexSizeU(texSizeU);
			box.setTexSizeV(texSizeV);
			box.texU(texU);
			box.texV(texV);
			this.copyChildren(box, model);
			return box;
		}
		else return null;
	}
	
	@Override
	public void drawOnTexture(float x, float y, float sizeX, float sizeY)
	{
		int texSizeU = this.getTexSizeU();
		int texSizeV = this.getTexSizeV();
		float tU = sizeX * texU / texSizeU, tV = sizeY * texV / texSizeV;
		float tUX = sizeX * lengthX / texSizeU, tUZ = sizeX * lengthZ / texSizeU;
		float tVY = sizeY * lengthY / texSizeV, tVZ = sizeY * lengthZ / texSizeV;
		float u0 = x + tU, u1 = u0 + tUZ, u2 = u1 + tUX * 2, u3 = u2 + tUZ;
		float v0 = y + tV, v1 = v0 + tVZ, v2 = v1 + tVY;
		Mesh m = new Mesh(new float[] {
				u0, v1, 0,
				u1, v1, 0,
				u1, v0, 0,
				u2, v0, 0,
				u2, v1, 0,
				u3, v1, 0,
				u3, v2, 0,
				u0, v2, 0
		}, new float[8 * 2], new float[] {
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1
		}, new int[] {
				0, 1, 2, 3, 4, 5, 6, 7
		}, Mesh.DrawMode.LINE_LOOP);
		Main.instance.shader.setColor(0, 0, 0, 1);
		Main.instance.textureManager.unbindTexture();
		m.render();
		m.cleanUp();
		Main.instance.shader.setColor(1, 1, 1, 1);
	}

	@Override
	public ComponentBox cloneSelf(IComponentParent clonedParent)
	{
		return new ComponentBox(clonedParent, this);
	}
}