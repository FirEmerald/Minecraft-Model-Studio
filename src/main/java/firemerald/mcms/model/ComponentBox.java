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
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.history.IHistoryAction;
import firemerald.mcms.util.mesh.DrawMode;
import firemerald.mcms.util.mesh.ModelMesh;

public class ComponentBox extends ComponentMesh
{
	public static enum Type
	{
		CUBEMAP,
		SIDES_ALL,
		SIDES_FLAT,
		OLD
	}
	
	private float texScale = 1;
	private float lengthX, lengthY, lengthZ, marginX, marginY, marginZ;
	private boolean mirror = false;
	private boolean enableUp = true, enableDown = true, enableNorth = true, enableEast = true, enableSouth = true, enableWest = true, flipped = false; //x+ = east z+ = south
	private Type type = Type.CUBEMAP;
	
	private static ModelMesh makeMesh()
	{
		/**/
		return new ModelMesh(new float[24 * 3], new float[24 * 2], new float[24 * 3], new int[24], DrawMode.TRIANGLES, GL_DYNAMIC_DRAW);
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
		this.marginX = from.marginX;
		this.marginY = from.marginY;
		this.marginZ = from.marginZ;
		this.mirror = from.mirror;
		this.flipped = from.flipped;
		this.enableUp = from.enableUp;
		this.enableDown = from.enableDown;
		this.enableNorth = from.enableNorth;
		this.enableEast = from.enableEast;
		this.enableSouth = from.enableSouth;
		this.enableWest = from.enableWest;
		this.type = from.type;
		setVerts();
		setTexs();
		setNorms();
		setInds();
	}
	
	private void init()
	{
		lengthX = lengthY = lengthZ = 1;
		marginX = marginY = marginZ = 0;
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
		float minX = -marginX;
		float minY = -marginY;
		float minZ = -marginZ;
		float maxX = lengthX + marginX;
		float maxY = lengthY + marginY;
		float maxZ = lengthZ + marginZ;
		mesh().setPositions(new float[] {
				maxX, minY, maxZ,
				maxX, minY, minZ,
				maxX, maxY, minZ,
				maxX, maxY, maxZ,
				
				minX, maxY, maxZ,
				maxX, maxY, maxZ,
				maxX, maxY, minZ,
				minX, maxY, minZ,
				
				minX, minY, maxZ,
				maxX, minY, maxZ,
				maxX, maxY, maxZ,
				minX, maxY, maxZ,
				
				minX, minY, minZ,
				minX, minY, maxZ,
				minX, maxY, maxZ,
				minX, maxY, minZ,
				
				maxX, minY, maxZ,
				minX, minY, maxZ,
				minX, minY, minZ,
				maxX, minY, minZ,
				
				maxX, minY, minZ,
				minX, minY, minZ,
				minX, maxY, minZ,
				maxX, maxY, minZ
		});
	}
	
	private static void copyVertex(float[] source, int sourceIndex, float[] des, int desIndex)
	{
		System.arraycopy(source, sourceIndex * 2, des, desIndex * 2, 2);
	}
	
	@Override
	public void setTexs()
	{
		float texSizeU = Main.instance.project.getTextureWidth();
		float texSizeV = Main.instance.project.getTextureHeight();
		float tU = texU / texSizeU, tV = texV / texSizeV;
		float tUX = lengthX * texScale / texSizeU, tUZ = lengthZ * texScale / texSizeU;
		float tVY = lengthY * texScale / texSizeV, tVZ = lengthZ * texScale / texSizeV;
		float[] texCoordsTop = null, texCoordsBottom = null, texCoordsLeft = null, texCoordsRight = null, texCoordsFront = null, texCoordsBack = null;
		switch (type)
		{
		case CUBEMAP:
		default:
		{
			float u0 = tU, u1 = u0 + tUZ, u2 = u1 + tUX, u3a = u2 + tUZ, u3b = u2 + tUX, u4 = u3a + tUX;
			float v0 = tV, v1 = v0 + tVZ, v2 = v1 + tVY;
			texCoordsLeft = new float[] {
					u2, v2,
					u3a, v2,
					u3a, v1,
					u2, v1
			};
			texCoordsTop = new float[] {
					u1, v1,
					u2, v1,
					u2, v0,
					u1, v0
			};
			texCoordsFront = new float[] {
					u1, v2,
					u2, v2,
					u2, v1,
					u1, v1
			};
			texCoordsRight = new float[] {
					u0, v2,
					u1, v2,
					u1, v1,
					u0, v1
			};
			texCoordsBottom = new float[] {
					u2, v1,
					u3b, v1,
					u3b, v0,
					u2, v0
			};
			texCoordsBack = new float[] {
					u3a, v2,
					u4, v2,
					u4, v1,
					u3a, v1
			};
			break;
		}
		case SIDES_ALL:
		{
			float u0 = tU;
			float u1X = tU + tUX;
			float u1Z = tU + tUZ;
			float v0 = tV;
			float v1Y = v0 + tVY;
			float v1Z = v0 + tVZ;
			texCoordsLeft = new float[] {
					u0, v1Y,
					u1Z, v1Y,
					u1Z, v0,
					u0, v0
			};
			texCoordsTop = new float[] {
					u0, v1Z,
					u1X, v1Z,
					u1X, v0,
					u0, v0
			};
			texCoordsFront = new float[] {
					u0, v1Y,
					u1X, v1Y,
					u1X, v0,
					u0, v0
			};
			texCoordsRight = new float[] {
					u0, v1Y,
					u1Z, v1Y,
					u1Z, v0,
					u0, v0
			};
			texCoordsBottom = new float[] {
					u0, v1Z,
					u1X, v1Z,
					u1X, v0,
					u0, v0
			};
			texCoordsBack = new float[] {
					u0, v1Y,
					u1X, v1Y,
					u1X, v0,
					u0, v0
			};
			break;
		}
		case SIDES_FLAT:
		{
			float u0 = tU;
			float u1X = tU + tUX;
			float u1Z = tU + tUZ;
			float v0 = tV;
			float v1Y = v0 + tVY;
			float v1Z = v0 + tVZ;
			texCoordsLeft = new float[] {
					u0, v1Y,
					u1Z, v1Y,
					u1Z, v0,
					u0, v0
			};
			texCoordsTop = new float[] {
					u0, v1Z,
					u1X, v1Z,
					u1X, v0,
					u0, v0
			};
			texCoordsFront = new float[] {
					u0, v1Y,
					u1X, v1Y,
					u1X, v0,
					u0, v0
			};
			texCoordsRight = new float[] {
					u1Z, v1Y,
					u0, v1Y,
					u0, v0,
					u1Z, v0
			};
			texCoordsBottom = new float[] {
					u1X, v1Z,
					u0, v1Z,
					u0, v0,
					u1X, v0
			};
			texCoordsBack = new float[] {
					u1X, v1Y,
					u0, v1Y,
					u0, v0,
					u1X, v0
			};
			break;
		}
		case OLD:
		{
			float u0 = tU, u1 = u0 + tUZ, u2 = u1 + tUX, u3a = u2 + tUZ, u3b = u2 + tUX, u4 = u3a + tUX;
			float v0 = tV, v1 = v0 + tVZ, v2 = v1 + tVY;
			texCoordsLeft = new float[] {
					u2, v2,
					u3a, v2,
					u3a, v1,
					u2, v1
			};
			texCoordsTop = new float[] {
					u1, v1,
					u2, v1,
					u2, v0,
					u1, v0
			};
			texCoordsFront = new float[] {
					u1, v2,
					u2, v2,
					u2, v1,
					u1, v1
			};
			texCoordsRight = new float[] {
					u0, v2,
					u1, v2,
					u1, v1,
					u0, v1
			};
			texCoordsBottom = new float[] {
					u3b, v0,
					u2, v0,
					u2, v1,
					u3b, v1
			};
			texCoordsBack = new float[] {
					u3a, v2,
					u4, v2,
					u4, v1,
					u3a, v1
			};
			break;
		}
		}
		float[] texCoords = new float[48];
		if (mirror)
		{
			if (flipped)
			{
				copyVertex(texCoordsLeft, 0, texCoords, 0);
				copyVertex(texCoordsLeft, 1, texCoords, 1);
				copyVertex(texCoordsLeft, 2, texCoords, 2);
				copyVertex(texCoordsLeft, 3, texCoords, 3);

				copyVertex(texCoordsBottom, 0, texCoords, 4);
				copyVertex(texCoordsBottom, 1, texCoords, 5);
				copyVertex(texCoordsBottom, 2, texCoords, 6);
				copyVertex(texCoordsBottom, 3, texCoords, 7);

				copyVertex(texCoordsBack, 0, texCoords, 8);
				copyVertex(texCoordsBack, 1, texCoords, 9);
				copyVertex(texCoordsBack, 2, texCoords, 10);
				copyVertex(texCoordsBack, 3, texCoords, 11);

				copyVertex(texCoordsRight, 0, texCoords, 12);
				copyVertex(texCoordsRight, 1, texCoords, 13);
				copyVertex(texCoordsRight, 2, texCoords, 14);
				copyVertex(texCoordsRight, 3, texCoords, 15);

				copyVertex(texCoordsTop, 0, texCoords, 16);
				copyVertex(texCoordsTop, 1, texCoords, 17);
				copyVertex(texCoordsTop, 2, texCoords, 18);
				copyVertex(texCoordsTop, 3, texCoords, 19);

				copyVertex(texCoordsFront, 0, texCoords, 20);
				copyVertex(texCoordsFront, 1, texCoords, 21);
				copyVertex(texCoordsFront, 2, texCoords, 22);
				copyVertex(texCoordsFront, 3, texCoords, 23);
			}
			else
			{
				copyVertex(texCoordsRight, 1, texCoords, 0);
				copyVertex(texCoordsRight, 0, texCoords, 1);
				copyVertex(texCoordsRight, 3, texCoords, 2);
				copyVertex(texCoordsRight, 2, texCoords, 3);

				copyVertex(texCoordsTop, 1, texCoords, 4);
				copyVertex(texCoordsTop, 0, texCoords, 5);
				copyVertex(texCoordsTop, 3, texCoords, 6);
				copyVertex(texCoordsTop, 2, texCoords, 7);

				copyVertex(texCoordsFront, 1, texCoords, 8);
				copyVertex(texCoordsFront, 0, texCoords, 9);
				copyVertex(texCoordsFront, 3, texCoords, 10);
				copyVertex(texCoordsFront, 2, texCoords, 11);

				copyVertex(texCoordsLeft, 1, texCoords, 12);
				copyVertex(texCoordsLeft, 0, texCoords, 13);
				copyVertex(texCoordsLeft, 3, texCoords, 14);
				copyVertex(texCoordsLeft, 2, texCoords, 15);

				copyVertex(texCoordsBottom, 1, texCoords, 16);
				copyVertex(texCoordsBottom, 0, texCoords, 17);
				copyVertex(texCoordsBottom, 3, texCoords, 18);
				copyVertex(texCoordsBottom, 2, texCoords, 19);

				copyVertex(texCoordsBack, 1, texCoords, 20);
				copyVertex(texCoordsBack, 0, texCoords, 21);
				copyVertex(texCoordsBack, 3, texCoords, 22);
				copyVertex(texCoordsBack, 2, texCoords, 23);
			}
		}
		else
		{
			if (flipped)
			{
				copyVertex(texCoordsRight, 1, texCoords, 0);
				copyVertex(texCoordsRight, 0, texCoords, 1);
				copyVertex(texCoordsRight, 3, texCoords, 2);
				copyVertex(texCoordsRight, 2, texCoords, 3);

				copyVertex(texCoordsBottom, 1, texCoords, 4);
				copyVertex(texCoordsBottom, 0, texCoords, 5);
				copyVertex(texCoordsBottom, 3, texCoords, 6);
				copyVertex(texCoordsBottom, 2, texCoords, 7);

				copyVertex(texCoordsBack, 1, texCoords, 8);
				copyVertex(texCoordsBack, 0, texCoords, 9);
				copyVertex(texCoordsBack, 3, texCoords, 10);
				copyVertex(texCoordsBack, 2, texCoords, 11);

				copyVertex(texCoordsLeft, 1, texCoords, 12);
				copyVertex(texCoordsLeft, 0, texCoords, 13);
				copyVertex(texCoordsLeft, 3, texCoords, 14);
				copyVertex(texCoordsLeft, 2, texCoords, 15);

				copyVertex(texCoordsTop, 1, texCoords, 16);
				copyVertex(texCoordsTop, 0, texCoords, 17);
				copyVertex(texCoordsTop, 3, texCoords, 18);
				copyVertex(texCoordsTop, 2, texCoords, 19);

				copyVertex(texCoordsFront, 1, texCoords, 20);
				copyVertex(texCoordsFront, 0, texCoords, 21);
				copyVertex(texCoordsFront, 3, texCoords, 22);
				copyVertex(texCoordsFront, 2, texCoords, 23);
			}
			else
			{
				System.arraycopy(texCoordsLeft, 0, texCoords, 0, 8);
				System.arraycopy(texCoordsTop, 0, texCoords, 8, 8);
				System.arraycopy(texCoordsFront, 0, texCoords, 16, 8);
				System.arraycopy(texCoordsRight, 0, texCoords, 24, 8);
				System.arraycopy(texCoordsBottom, 0, texCoords, 32, 8);
				System.arraycopy(texCoordsBack, 0, texCoords, 40, 8);
			}
		}
		mesh().setTexCoords(texCoords);
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

	public float marginX()
	{
		return marginX;
	}

	public void marginX(float marginX)
	{
		this.marginX = marginX;
		setVerts();
		setTexs();
	}

	public float marginY()
	{
		return marginY;
	}

	public void marginY(float marginY)
	{
		this.marginY = marginY;
		setVerts();
		setTexs();
	}

	public float marginZ()
	{
		return marginZ;
	}

	public void marginZ(float marginZ)
	{
		this.marginZ = marginZ;
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
	
	public Type getType()
	{
		return type;
	}
	
	public void setType(Type type)
	{
		if (type != this.type)
		{
			Type old = this.type;
			this.type = type;
			setTexs();
			Main.instance.project.onAction(new ChangeBoxType(this, old));
		}
	}
	
	public static class ChangeBoxType implements IHistoryAction<ChangeBoxType>
	{
		public final ComponentBox box;
		public final Type type;
		public final ChangeBoxType opposite;
		
		public ChangeBoxType(ComponentBox box, Type type)
		{
			this.box = box;
			this.type = type;
			this.opposite = new ChangeBoxType(this);
		}
		
		private ChangeBoxType(ChangeBoxType opposite)
		{
			this.box = opposite.box;
			this.type = box.getType();
			this.opposite = opposite;
		}
		
		@Override
		public ChangeBoxType perform()
		{
			if (Main.instance.getEditing() == box) Main.instance.setEditing(null);
			this.box.setType(type);
			Main.instance.setEditing(box);
			return opposite;
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
	private ComponentFloatingLabel labelMargin;
	private ComponentTextFloat marginXT;
	private ComponentIncrementFloat marginXP, marginXS;
	private ComponentTextFloat marginYT;
	private ComponentIncrementFloat marginYP, marginYS;
	private ComponentTextFloat marginZT;
	private ComponentIncrementFloat marginZP, marginZS;
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
	private ComponentFloatingLabel typeLabel;
	private SelectorButton typeSelector;
	
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
		editor.addElement(labelMargin  = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, "Margin"));
		editorY += 20;
		editor.addElement(marginXT     = new ComponentTextFloat(     editorX      , editorY, editorX + 90 , editorY + 20, Main.instance.fontMsg, marginX(), 0, Float.POSITIVE_INFINITY, value -> this.marginX(value)));
		editor.addElement(marginXP     = new ComponentIncrementFloat(editorX + 90 , editorY                             , marginXT,  1));
		editor.addElement(marginXS     = new ComponentIncrementFloat(editorX + 90 , editorY + 10                        , marginXT, -1));
		editor.addElement(marginYT     = new ComponentTextFloat(     editorX + 100, editorY, editorX + 190, editorY + 20, Main.instance.fontMsg, marginY(), 0, Float.POSITIVE_INFINITY, value -> this.marginY(value)));
		editor.addElement(marginYP     = new ComponentIncrementFloat(editorX + 190, editorY                             , marginYT, 1));
		editor.addElement(marginYS     = new ComponentIncrementFloat(editorX + 190, editorY + 10                        , marginYT, -1));
		editor.addElement(marginZT     = new ComponentTextFloat(     editorX + 200, editorY, editorX + 290, editorY + 20, Main.instance.fontMsg, marginZ(), 0, Float.POSITIVE_INFINITY, value -> this.marginZ(value)));
		editor.addElement(marginZP     = new ComponentIncrementFloat(editorX + 290, editorY                             , marginZT, 1));
		editor.addElement(marginZS     = new ComponentIncrementFloat(editorX + 290, editorY + 10                        , marginZT, -1));
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
		editor.addElement(typeLabel    = new ComponentFloatingLabel(editorX, editorY, editorX + 150, editorY + 20, Main.instance.fontMsg, "Box type"));
		editor.addElement(typeSelector = new SelectorButton(editorX + 150, editorY, editorX + 300, editorY + 20, this.getType(), Type.values(), this::setType));
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
		editor.removeElement(labelMargin);
		editor.removeElement(marginXT);
		editor.removeElement(marginXP);
		editor.removeElement(marginXS);
		editor.removeElement(marginYT);
		editor.removeElement(marginYP);
		editor.removeElement(marginYS);
		editor.removeElement(marginZT);
		editor.removeElement(marginZP);
		editor.removeElement(marginZS);
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
		editor.removeElement(typeLabel);
		editor.removeElement(typeSelector);
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
		labelMargin   = null;
		marginXT      = null;
		marginXP      = null;
		marginXS      = null;
		marginYT      = null;
		marginYP      = null;
		marginYS      = null;
		marginZT      = null;
		marginZP      = null;
		marginZS      = null;
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
		typeLabel     = null;
		typeSelector  = null;
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
		el.setFloat("marginX", marginX);
		el.setFloat("marginY", marginY);
		el.setFloat("marginZ", marginZ);
		if (mirror) el.setBoolean("mirror", true);
		if (flipped) el.setBoolean("flipped", true);
		if (!enableUp) el.setBoolean("enableUp", false);
		if (!enableDown) el.setBoolean("enableDown", false);
		if (!enableNorth) el.setBoolean("enableNorth", false);
		if (!enableEast) el.setBoolean("enableEast", false);
		if (!enableSouth) el.setBoolean("enableSouth", false);
		if (!enableWest) el.setBoolean("enableWest", false);
		el.setEnum("type", type);
	}
	
	@Override
	public void loadFromXML(AbstractElement el)
	{
		super.loadFromXML(el);
		texScale = el.getFloat("texScale", 1);
		lengthX = el.getFloat("lengthX", 0);
		lengthY = el.getFloat("lengthY", 0);
		lengthZ = el.getFloat("lengthZ", 0);
		marginX = el.getFloat("marginX", 0);
		marginY = el.getFloat("marginY", 0);
		marginZ = el.getFloat("marginZ", 0);
		mirror = el.getBoolean("mirror", false);
		flipped = el.getBoolean("flipped", false);
		enableUp = el.getBoolean("enableUp", true);
		enableDown = el.getBoolean("enableDown", true);
		enableNorth = el.getBoolean("enableNorth", true);
		enableEast = el.getBoolean("enableEast", true);
		enableSouth = el.getBoolean("enableSouth", true);
		enableWest = el.getBoolean("enableWest", true);
		type = el.getEnum("type", Type.values(), Type.OLD);
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

	@Override
	public void move(float dU, float dV)
	{
		super.move(dU, dV);
		setTexs();
	}
}