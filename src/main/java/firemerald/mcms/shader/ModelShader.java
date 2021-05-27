package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Texture;

public class ModelShader extends ModelShaderBase
{
	public static final MatrixStack4 LIGHT = new MatrixStack4();
	protected int normal, lightSpace, overlay_sampler, shadow_sampler, color, color2, light, hueSet, invert, use_overlay, ignore_lighting, enable_shadows;
	public static boolean enable_shadowsVal = false;
	
	public ModelShader(String vert, String frag)
	{
		super(vert, frag);
		normal = glGetUniformLocation(prog, "normalMatrix");
		lightSpace = glGetUniformLocation(prog, "lightSpaceMatrix");
		overlay_sampler = glGetUniformLocation(prog, "overlay_sampler");
		shadow_sampler = glGetUniformLocation(prog, "shadow_sampler");
		color = glGetUniformLocation(prog, "color");
		color2 = glGetUniformLocation(prog, "color2");
		light = glGetUniformLocation(prog, "light");
		hueSet = glGetUniformLocation(prog, "hueSet");
		invert = glGetUniformLocation(prog, "invert");
		use_overlay = glGetUniformLocation(prog, "use_overlay");
		ignore_lighting = glGetUniformLocation(prog, "ignore_lighting");
		enable_shadows = glGetUniformLocation(prog, "enable_shadows");
	}
	
	public ModelShader(String vert, String geom, String frag)
	{
		super(vert, geom, frag);
		normal = glGetUniformLocation(prog, "normalMatrix");
		lightSpace = glGetUniformLocation(prog, "lightSpaceMatrix");
		overlay_sampler = glGetUniformLocation(prog, "overlay_sampler");
		shadow_sampler = glGetUniformLocation(prog, "shadow_sampler");
		color = glGetUniformLocation(prog, "color");
		color2 = glGetUniformLocation(prog, "color2");
		light = glGetUniformLocation(prog, "light");
		hueSet = glGetUniformLocation(prog, "hueSet");
		invert = glGetUniformLocation(prog, "invert");
		use_overlay = glGetUniformLocation(prog, "use_overlay");
		ignore_lighting = glGetUniformLocation(prog, "ignore_lighting");
		enable_shadows = glGetUniformLocation(prog, "enable_shadows");
	}
	
	public void bind()
	{
		super.bind();
		glUniform1i(overlay_sampler, 1);
		glUniform1i(shadow_sampler, 2);
		glUniform4f(color, r1, g1, b1, a1);
		glUniform4f(color2, r2, g2, b2, a2);
		glUniform3f(light, lX, lY, lZ);
		glUniform1i(hueSet, hueSetVal ? 1 : 0);
		glUniform1i(invert, invertVal ? 1 : 0);
		glUniform1i(use_overlay, use_overlayVal ? 1 : 0);
		glUniform1i(ignore_lighting, ignore_lightingVal ? 1 : 0);
		glUniform1i(enable_shadows, enable_shadowsVal ? 1 : 0);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		enable_shadowsVal = false;
	}
	
	@Override
	public void setColor(float r, float g, float b, float a)
	{
		glUniform4f(color, r1 = r, g1 = g, b1 = b, a1 = a);
	}

	@Override
	public void setColor2(float r, float g, float b, float a)
	{
		glUniform4f(color2, r2 = r, g2 = g, b2 = b, a2 = a);
	}

	@Override
	public void setLight(Vector3f light)
	{
		light.normalize();
		glUniform3f(this.light, lX = light.x, lY = light.y, lZ = light.z);
	}

	@Override
	public void setHueSet(boolean hueSet)
	{
		glUniform1i(this.hueSet, (hueSetVal = hueSet) ? 1 : 0);
	}

	@Override
	public void setInvert(boolean invert)
	{
		glUniform1i(this.invert, (invertVal = invert) ? 1 : 0);
	}

	@Override
    public void setOverlayTexture(Texture tex)
    {
        glActiveTexture(GL_TEXTURE1);
        if (tex != null)
        {
            tex.bind();
            glUniform1i(this.use_overlay, 1);
            use_overlayVal = true;
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.use_overlay, 0);
            use_overlayVal = false;
        }
        glActiveTexture(GL_TEXTURE0);
    }

	@Override
	public void setIgnoreLighting(boolean ignoreLighting)
	{
		glUniform1i(ignore_lighting, (ignore_lightingVal = ignoreLighting) ? 1 : 0);
	}

	@Override
	protected void updateNormal()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(9 * Float.BYTES);
			glUniformMatrix3fv(normal, false, new Matrix4d(MODEL.matrix()).mul(VIEW.matrix()).transpose3x3(new Matrix3d()).invert().getFloats(buf).asFloatBuffer());
		}
	}

    public void setShadowTexture(int tex)
    {
        glActiveTexture(GL_TEXTURE2);
        if (tex > 0)
        {
            glBindTexture(GL_TEXTURE_2D, tex);
            glUniform1i(this.enable_shadows, 1);
            enable_shadowsVal = true;
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.enable_shadows, 0);
            enable_shadowsVal = false;
        }
        glActiveTexture(GL_TEXTURE0);
    }

	public void updateLightSpace()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buf = stack.malloc(16 * Float.BYTES);
			glUniformMatrix4fv(lightSpace, false, LIGHT.matrix().getFloats(buf).asFloatBuffer());
		}
	}
}