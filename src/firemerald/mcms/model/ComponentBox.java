package firemerald.mcms.model;

import static org.lwjgl.opengl.GL15.*;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.util.Textures;

public class ComponentBox extends ComponentMesh
{
	private float lengthX, lengthY, lengthZ;
	
	private static Mesh makeMesh()
	{
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
	
	private void init()
	{
		lengthX = lengthY = lengthZ = .0625f;
		setVerts();
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
	
	public void setTexs()
	{
		int texSizeU = this.getTexSizeU();
		int texSizeV = this.getTexSizeV();
		float tU = texU / texSizeU, tV = texV / texSizeV;
		float tUX = lengthX * 16 / texSizeU, tUZ = lengthZ * 16 / texSizeU;
		float tVY = lengthY * 16 / texSizeV, tVZ = lengthZ * 16 / texSizeV;
		float u0 = tU, u1 = u0 + tUZ, u2 = u1 + tUX, u3a = u2 + tUZ, u3b = u2 + tUX, u4 = u3a + tUX;
		float v0 = tV, v1 = v0 + tVZ, v2 = v1 + tVY;
		mesh().setTexCoords(new float[] {
				u0, v1,
				u1, v1,
				u1, v2,
				u0, v2,
				u1, v0,
				u2, v0,
				u2, v1,
				u1, v1,
				u1, v1,
				u2, v1,
				u2, v2,
				u1, v2,
				u2, v1,
				u3a, v1,
				u3a, v2,
				u2, v2,
				u2, v0,
				u3b, v0,
				u3b, v1,
				u2, v1,
				u3a, v1,
				u4, v1,
				u4, v2,
				u3a, v2
		});
	}

	@Override
	public void doCleanUp()
	{
		mesh().cleanUp();
	}

	public double lengthX()
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

	@Override
	public String getDisplayIcon()
	{
		return Textures.EDITABLE_ICON_BOX;
	}

	@Override
	public void onTexSizeChange()
	{
		setTexs();
	}

	private ComponentLabel labelLength;
	private ComponentTextFloat lengthXT;
	private ComponentIncrementFloat lengthXP, lengthXS;
	private ComponentTextFloat lengthYT;
	private ComponentIncrementFloat lengthYP, lengthYS;
	private ComponentTextFloat lengthZT;
	private ComponentIncrementFloat lengthZP, lengthZS;
	
	@Override
	public void onSelect(EditorPanes editorPanes)
	{
		super.onSelect(editorPanes);
		GuiElementContainer editor = editorPanes.editor;
		float editorX = editorPanes.editorX;
		float editorY = editorPanes.editorY;
		editor.addElement(labelLength  = new ComponentLabel(         editorX      , editorY + 140, editorX + 300, editorY + 160, Main.instance.fontMsg, "Size"));
		editor.addElement(lengthXT     = new ComponentTextFloat(     editorX      , editorY + 160, editorX + 90 , editorY + 180, Main.instance.fontMsg, lengthX, 0, Float.POSITIVE_INFINITY, value -> this.lengthX(value)));
		editor.addElement(lengthXP     = new ComponentIncrementFloat(editorX + 90 , editorY + 160                  , lengthXT, .0625f));
		editor.addElement(lengthXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 170                  , lengthXT, -.0625f));
		editor.addElement(lengthYT     = new ComponentTextFloat(     editorX + 100, editorY + 160, editorX + 190, editorY + 180, Main.instance.fontMsg, lengthY, 0, Float.POSITIVE_INFINITY, value -> this.lengthY(value)));
		editor.addElement(lengthYP     = new ComponentIncrementFloat(editorX + 190, editorY + 160                  , lengthYT, .0625f));
		editor.addElement(lengthYS     = new ComponentIncrementFloat(editorX + 190, editorY + 170                  , lengthYT, -.0625f));
		editor.addElement(lengthZT     = new ComponentTextFloat(     editorX + 200, editorY + 160, editorX + 290, editorY + 180, Main.instance.fontMsg, lengthZ, 0, Float.POSITIVE_INFINITY, value -> this.lengthZ(value)));
		editor.addElement(lengthZP     = new ComponentIncrementFloat(editorX + 290, editorY + 160                  , lengthZT, .0625f));
		editor.addElement(lengthZS     = new ComponentIncrementFloat(editorX + 290, editorY + 170                  , lengthZT, -.0625f));
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor;
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
	}
	
	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		lengthX = el.getFloat("lengthX", lengthX);
		lengthY = el.getFloat("lengthY", lengthY);
		lengthZ = el.getFloat("lengthZ", lengthZ);
		setVerts();
		setTexs();
	}

	@Override
	public IEditable copy(IEditableParent newParent, IModel model)
	{
		if (newParent instanceof IComponentParent)
		{
			ComponentBox box = new ComponentBox((IComponentParent) newParent, this.name);
			box.lengthX(lengthX);
			box.lengthY(lengthY);
			box.lengthZ(lengthZ);
			box.posX(posX);
			box.posY(posY);
			box.posZ(posZ);
			box.offX(offX);
			box.offY(offY);
			box.offZ(offZ);
			box.rotX(rotX);
			box.rotY(rotY);
			box.rotZ(rotZ);
			box.setTexSizeU(texSizeU);
			box.setTexSizeV(texSizeV);
			this.copyChildren(box, model);
			return box;
		}
		else return null;
	}
}