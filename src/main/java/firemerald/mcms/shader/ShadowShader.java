package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import org.joml.Vector3f;

import firemerald.mcms.texture.Texture;

public class ShadowShader extends ModelShaderBase
{
	protected int colorA, color2A;
	
	public ShadowShader(String vert, String frag)
	{
		super(vert, frag);
		colorA = glGetUniformLocation(prog, "colorA");
		color2A = glGetUniformLocation(prog, "color2A");
	}
	
	public void bind()
	{
		super.bind();
		glUniform1f(colorA, a1);
		glUniform1f(color2A, a2);
	}
	
	@Override
	public void setColor(float r, float g, float b, float a)
	{
		r1 = r;
		g1 = g;
		b1 = b;
		glUniform1f(colorA, a1 = a);
	}

	@Override
	public void setColor2(float r, float g, float b, float a)
	{
		r2 = r;
		g2 = g;
		b2 = b;
		glUniform1f(color2A, a2 = a);
	}

	@Override
	public void setLight(Vector3f light)
	{
		light.normalize();
		lX = light.x;
		lY = light.y;
		lZ = light.z;
	}

	@Override
	public void setHueSet(boolean hueSet)
	{
		hueSetVal = hueSet;
	}

	@Override
	public void setInvert(boolean invert)
	{
		invertVal = invert;
	}

	@Override
    public void setOverlayTexture(Texture tex)
    {
		use_overlayVal = tex != null;
    }

	@Override
	public void setIgnoreLighting(boolean ignoreLighting)
	{
		ignore_lightingVal = ignoreLighting;
	}

	@Override
	protected void updateNormal() {}
}