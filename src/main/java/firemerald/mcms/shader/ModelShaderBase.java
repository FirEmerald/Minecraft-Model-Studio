package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.Material;

public abstract class ModelShaderBase
{
	public static final MatrixStack4 MODEL = new MatrixStack4();
	public static final MatrixStack4 VIEW = new MatrixStack4();
	public static final MatrixStack4 PROJECTION = new MatrixStack4();
	public static final MatrixStack4 TEXTURE = new MatrixStack4();
	protected int model, viewProjection, texture, texture_sampler, clip_outside, hasColor;
	public int prog;
	
	protected static float r1, g1, b1, a1, r2, g2, b2, a2, lX, lY, lZ;
	protected static boolean hueSetVal, invertVal, use_overlayVal, clip_outsideVal, ignore_lightingVal;
	
	public ModelShaderBase(String vert, String frag)
	{
		prog = GuiShader.createShaderProgram(vert, frag);
		model = glGetUniformLocation(prog, "modelMatrix");
		viewProjection = glGetUniformLocation(prog, "viewProjectionMatrix");
		texture = glGetUniformLocation(prog, "textureMatrix");
		texture_sampler = glGetUniformLocation(prog, "texture_sampler");
		clip_outside = glGetUniformLocation(prog, "clip_outside");
		hasColor = glGetUniformLocation(prog, "hasColor");
	}
	
	public ModelShaderBase(String vert, String geom, String frag)
	{
		prog = GuiShader.createShaderProgram(vert, geom, frag);
		model = glGetUniformLocation(prog, "modelMatrix");
		viewProjection = glGetUniformLocation(prog, "viewProjectionMatrix");
		texture = glGetUniformLocation(prog, "textureMatrix");
		texture_sampler = glGetUniformLocation(prog, "texture_sampler");
		clip_outside = glGetUniformLocation(prog, "clip_outside");
		hasColor = glGetUniformLocation(prog, "hasColor");
	}
	
	public void reset()
	{
		r1 = g1 = b1 = a1 = r2 = g2 = b2 = a2 = 1;
		lX = 0;
		lY = 0;
		lZ = -1;
		hueSetVal = invertVal = use_overlayVal = clip_outsideVal = ignore_lightingVal = false;
	}
	
	public void bind()
	{
		glUseProgram(prog);
		updateModel();
		updateViewProjection();
		glUniform1i(texture_sampler, 0);
		glUniform1i(clip_outside, clip_outsideVal ? 1 : 0);
	}
	
	public void setColor(Color c)
	{
		RGB rgb = c.c.getRGB();
		setColor(rgb.r, rgb.g, rgb.b, c.a);
	}
	
	public void setColor2(Color c)
	{
		RGB rgb = c.c.getRGB();
		setColor2(rgb.r, rgb.g, rgb.b, c.a);
	}
	
	public void setHue(float h, float a)
	{
		float h2 = (h * 6) % 6;
		float x = 1 - Math.abs((h2 % 2) - 1);
		float r, g, b;
		if (h2 <= 1)
		{
			r = 1;
			g = x;
			b = 0;
		}
		else if (h2 <= 2)
		{
			r = x;
			g = 1;
			b = 0;
		}
		else if (h2 <= 3)
		{
			r = 0;
			g = 1;
			b = x;
		}
		else if (h2 <= 4)
		{
			r = 0;
			g = x;
			b = 1;
		}
		else if (h2 <= 5)
		{
			r = x;
			g = 0;
			b = 1;
		}
		else
		{
			r = 1;
			g = 0;
			b = x;
		}
		setColor(r, g, b, a);
	}
	
	public abstract void setColor(float r, float g, float b, float a);

	public abstract void setColor2(float r, float g, float b, float a);
	
	public abstract void setLight(Vector3f light);

	public abstract void setHueSet(boolean hueSet);

	public abstract void setInvert(boolean invert);

    public abstract void setOverlayTexture(Texture tex);

	public void setClipOutside(boolean clip)
	{
		glUniform1i(clip_outside, (clip_outsideVal = clip) ? 1 : 0);
	}

	public abstract void setIgnoreLighting(boolean ignoreLighting);
	
	public void setHasColor()
	{
		glUniform1i(hasColor, 1);
	}
	
	public void unsetHasColor()
	{
		glUniform1i(hasColor, 0);
	}
	
	public void setTexOffset(float uOff, float vOff)
	{
		TEXTURE.matrix().identity().translate(uOff, vOff, 0);
		updateTexture();
	}
	
	public void setTexScale(float scaleU, float scaleV)
	{
		TEXTURE.matrix().identity().scale(scaleU, scaleV, 1);
		updateTexture();
	}
	
	public void setTexSection(float u1, float v1, float u2, float v2)
	{
		TEXTURE.matrix().identity().translate(u1, v1, 0).scale((u2 - u1), (v2 - v1), 1);
		updateTexture();
	}
	
	public void setTexIdentity()
	{
		TEXTURE.matrix().identity();
		updateTexture();
	}
	
	public void unbind()
	{
		glUseProgram(0);
	}
	
	public void bindMaterial(Material mat)
	{
		mat.getDiffuse().bind();
	}
	
	public void updateModel()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(16 * Float.BYTES);
			glUniformMatrix4fv(model, false, MODEL.matrix().getFloats(buf).asFloatBuffer());
		}
		updateNormal();
	}
	
	public void updateView()
	{
		updateViewProjection();
		updateNormal();
	}
	
	public void updateProjection()
	{
		updateViewProjection();
	}
	
	public void updateModelViewProjection()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(16 * Float.BYTES);
			glUniformMatrix4fv(model, false, MODEL.matrix().getFloats(buf).asFloatBuffer());
		}
		updateViewProjection();
		updateNormal();
	}
	
	private void updateViewProjection()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(16 * Float.BYTES);
			glUniformMatrix4fv(viewProjection, false, new Matrix4d(PROJECTION.matrix()).mul(VIEW.matrix()).getFloats(buf).asFloatBuffer());
		}
	}
	
	protected abstract void updateNormal();

	public void updateTexture()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(16 * Float.BYTES);
			glUniformMatrix4fv(texture, false, TEXTURE.matrix().getFloats(buf).asFloatBuffer());
		}
	}
	
	public static String getStateString()
	{
		return "Model:\n" + 
				MODEL.matrix().toString() + 
				"\nView:\n" + 
				VIEW.matrix().toString() + 
				"\nProjection:\n" + 
				PROJECTION.matrix().toString() + 
				"\nTexture:\n" + 
				TEXTURE.matrix().toString() + 
				"\nColor 1: " + r1 + ", " + g1 + ", " + b1 + 
				"\nAlpha 1: " + a1 + 
				"\nColor 2: " + r2 + ", " + g2 + ", " + b2 + 
				"\nAlpha 2: " + a2 + 
				"\nuse hue set: " + hueSetVal + 
				"\ninvert colors: " + invertVal + 
				"\nuse overlay: " + use_overlayVal + 
				"\nclip outside of texture: " + clip_outsideVal + 
				"\nignore lighting: " + ignore_lightingVal;
	}
}