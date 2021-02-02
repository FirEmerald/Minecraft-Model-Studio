package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.Level;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.lwjgl.system.MemoryStack;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.ResourceLocation;

public class DepthShader
{
	public static final MatrixStack4 MODEL = new MatrixStack4();
	public static final MatrixStack4 VIEW = new MatrixStack4();
	public static final MatrixStack4 PROJECTION = new MatrixStack4();
	public static final MatrixStack4 TEXTURE = new MatrixStack4();
	protected static float r, g, b, a;
	protected int model, viewProjection, normal, texture, texture_sampler, overlay_sampler, color, color2, hueSet, invert, use_overlay, clip_outside, ignore_lighting;
	public int prog;
	
	public DepthShader(String vert, String frag)
	{
		prog = createShaderProgram(vert, frag);
		model = glGetUniformLocation(prog, "modelMatrix");
		viewProjection = glGetUniformLocation(prog, "viewProjectionMatrix");
		normal = glGetUniformLocation(prog, "normalMatrix");
		texture = glGetUniformLocation(prog, "textureMatrix");
		texture_sampler = glGetUniformLocation(prog, "texture_sampler");
		overlay_sampler = glGetUniformLocation(prog, "overlay_sampler");
		color = glGetUniformLocation(prog, "color");
		color2 = glGetUniformLocation(prog, "color2");
		hueSet = glGetUniformLocation(prog, "hueSet");
		invert = glGetUniformLocation(prog, "invert");
		use_overlay = glGetUniformLocation(prog, "use_overlay");
		clip_outside = glGetUniformLocation(prog, "clip_outside");
		ignore_lighting = glGetUniformLocation(prog, "ignore_lighting");
	}
	
	public void bind()
	{
		glUseProgram(prog);
		updateModel();
		updateViewProjection();
		glUniform1i(texture_sampler, 0);
		glUniform1i(overlay_sampler, 1);
		glUniform4f(color, r, g, b, a);
		glUniform4f(color2, 1, 1, 1, 1);
		glUniform1i(hueSet, 0);
		glUniform1i(invert, 0);
		glUniform1i(use_overlay, 0);
		glUniform1i(clip_outside, 0);
		glUniform1i(ignore_lighting, 0);
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
	
	public void setColor(float r, float g, float b, float a)
	{
		glUniform4f(color, DepthShader.r = r, DepthShader.g = g, DepthShader.b = b, DepthShader.a = a);
	}
	
	public void setColor2(float r, float g, float b, float a)
	{
		glUniform4f(color2, r, g, b, a);
	}
	
	public void setHueSet(boolean hueSet)
	{
		glUniform1i(this.hueSet, hueSet ? 1 : 0);
	}
	
	public void setInvert(boolean invert)
	{
		glUniform1i(this.invert, invert ? 1 : 0);
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

    public void setOverlayTexture(Texture tex)
    {
        glActiveTexture(GL_TEXTURE1);
        if (tex != null)
        {
            tex.bind();
            glUniform1i(this.use_overlay, 1);
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.use_overlay, 0);
        }
        glActiveTexture(GL_TEXTURE0);
    }
	
	public void setClipOutside(boolean clip)
	{
		glUniform1i(clip_outside, clip ? 1 : 0);
	}
	
	public void setIgnoreLighting(boolean ignoreLighting)
	{
		glUniform1i(ignore_lighting, ignoreLighting ? 1 : 0);
	}
	
	public void unbind()
	{
		glUseProgram(0);
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
	
	private void updateNormal()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(9 * Float.BYTES);
			glUniformMatrix3fv(normal, false, new Matrix4d(MODEL.matrix()).mul(VIEW.matrix()).transpose3x3(new Matrix3d()).invert().getFloats(buf).asFloatBuffer());
		}
	}
	
	public void updateTexture()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(16 * Float.BYTES);
			glUniformMatrix4fv(texture, false, TEXTURE.matrix().getFloats(buf).asFloatBuffer());
		}
	}
	
	public static int createShaderProgram(String vertFile, String fragFile)
	{
		int vert = glCreateShader(GL_VERTEX_SHADER);
		if (vert <= 0) throw new IllegalStateException("Unable to create a vertex shader!");
		String vertShader = FileUtils.readTextFile(new ResourceLocation(Main.ID, vertFile + ".vsh"), StandardCharsets.UTF_8);
		Main.LOGGER.log(Level.DEBUG, "Loaded vertex shader:\n" + vertShader);
		glShaderSource(vert, vertShader);
		glCompileShader(vert);
		printErr(glGetShaderInfoLog(vert));
		if (glGetShaderi(vert, GL_COMPILE_STATUS) == GL_FALSE) 
		{
			glDeleteShader(vert);
			throw new IllegalStateException("Unable to compile vertex shader!");
		}
		int frag = glCreateShader(GL_FRAGMENT_SHADER);
		if (frag <= 0)
		{
			glDeleteShader(vert);
			throw new IllegalStateException("Unable to create a fragment shader!");
		}
		String fragShader = FileUtils.readTextFile(new ResourceLocation(Main.ID, fragFile + ".fsh"), StandardCharsets.UTF_8);
		Main.LOGGER.log(Level.DEBUG, "Loaded fragment shader:\n" + fragShader);
		glShaderSource(frag, fragShader);
		glCompileShader(frag);
		printErr(glGetShaderInfoLog(frag));
		if (glGetShaderi(frag, GL_COMPILE_STATUS) == GL_FALSE) 
		{
			glDeleteShader(vert);
			glDeleteShader(frag);
			throw new IllegalStateException("Unable to compile fragment shader!");
		}
		int prog = glCreateProgram();
		if (prog <= 0)
		{
			glDeleteShader(vert);
			glDeleteShader(frag);
			throw new IllegalStateException("Unable to create a shader program!");
		}
		glAttachShader(prog, vert);
		glAttachShader(prog, frag);
		glLinkProgram(prog);
		printErr(glGetProgramInfoLog(prog));
		if (glGetProgrami(prog, GL_LINK_STATUS) == GL_FALSE)
		{
			glDeleteShader(vert);
			glDeleteShader(frag);
			glDeleteProgram(prog);
			throw new IllegalStateException("Unable to link shader program!");
		}
		printErr(glGetProgramInfoLog(prog));
		return prog;
	}
	
	private static void printErr(String s)
	{
		if (s != null && s.length() > 0) Main.LOGGER.warn(s);
	}
}