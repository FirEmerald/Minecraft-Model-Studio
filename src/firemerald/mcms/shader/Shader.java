package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.Level;
import org.lwjgl.system.MemoryStack;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.Matrix4;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.FileUtils;

public class Shader
{
	public static final MatrixStack4 MODEL = new MatrixStack4();
	public static final MatrixStack4 VIEW = new MatrixStack4();
	public static final MatrixStack4 PROJECTION = new MatrixStack4();
	public static final MatrixStack3 TEXTURE = new MatrixStack3();
	protected static float r, g, b, a;
	protected int model, viewProjection, normal, texture, texture_sampler, color, hueSet, invert;
	public int prog;
	
	public Shader(String vert, String frag)
	{
		prog = createShaderProgram(vert, frag);
		model = glGetUniformLocation(prog, "modelMatrix");
		viewProjection = glGetUniformLocation(prog, "viewProjectionMatrix");
		normal = glGetUniformLocation(prog, "normalMatrix");
		texture = glGetUniformLocation(prog, "textureMatrix");
		texture_sampler = glGetUniformLocation(prog, "texture_sampler");
		color = glGetUniformLocation(prog, "color");
		hueSet = glGetUniformLocation(prog, "hueSet");
		invert = glGetUniformLocation(prog, "invert");
	}
	
	public void bind()
	{
		glUseProgram(prog);
		updateModel();
		updateViewProjection();
		glUniform1i(texture_sampler, 0);
		glUniform4f(color, r, g, b, a);
		glUniform1i(hueSet, 0);
		glUniform1i(invert, 0);
	}
	
	public void setColor(Color c)
	{
		RGB rgb = c.c.getRGB();
		setColor(rgb.r, rgb.g, rgb.b, c.a);
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
		glUniform4f(color, Shader.r = r, Shader.g = g, Shader.b = b, Shader.a = a);
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
		TEXTURE.matrix().setTranslate(uOff, vOff);
		updateTexture();
	}
	
	public void unbind()
	{
		glUseProgram(0);
	}
	
	public void updateModel()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buf = stack.mallocFloat(16);
			glUniformMatrix4fv(model, false, MODEL.matrix().put(buf));
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
			FloatBuffer buf = stack.mallocFloat(16);
			glUniformMatrix4fv(model, false, MODEL.matrix().put(buf));
		}
		updateViewProjection();
		updateNormal();
	}
	
	private void updateViewProjection()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buf = stack.mallocFloat(16);
			glUniformMatrix4fv(viewProjection, false, new Matrix4(PROJECTION.matrix()).mul(VIEW.matrix()).put(buf));
		}
	}
	
	private void updateNormal()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buf = stack.mallocFloat(9);
			glUniformMatrix3fv(normal, false, new Matrix4(MODEL.matrix()).mul(VIEW.matrix()).transpose3().invert().put(buf));
		}
	}
	
	public void updateTexture()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buf = stack.mallocFloat(9);
			glUniformMatrix3fv(texture, false, TEXTURE.matrix().put(buf));
		}
	}
	
	public static int createShaderProgram(String vertFile, String fragFile)
	{
		int vert = glCreateShader(GL_VERTEX_SHADER);
		if (vert <= 0) throw new IllegalStateException("Unable to create a vertex shader!");
		String vertShader = FileUtils.readTextFile(vertFile + ".vsh", StandardCharsets.UTF_8);
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
		String fragShader = FileUtils.readTextFile(fragFile + ".fsh", StandardCharsets.UTF_8);
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