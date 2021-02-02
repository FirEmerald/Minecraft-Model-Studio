package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import firemerald.mcms.Main;

public class ModelShaderLabPBR extends ModelShader
{
	protected int normal_sampler, specular_sampler, enable_normal, enable_specular;
	public static boolean enable_normalVal = false, enable_specularVal = false;
	
	public ModelShaderLabPBR(String vert, String frag)
	{
		super(vert, frag);
		normal_sampler = glGetUniformLocation(prog, "normal_sampler");
		specular_sampler = glGetUniformLocation(prog, "specular_sampler");
		enable_normal = glGetUniformLocation(prog, "enable_normal");
		enable_specular = glGetUniformLocation(prog, "enable_specular");
	}
	
	public void bind()
	{
		super.bind();
		glUniform1i(normal_sampler, 3);
		glUniform1i(specular_sampler, 4);
		glUniform1i(enable_normal, enable_normalVal ? 1 : 0);
		glUniform1i(enable_specular, enable_specularVal ? 1 : 0);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		enable_normalVal = enable_specularVal = false;
	}

    public void setNormalTexture(int tex)
    {
        glActiveTexture(GL_TEXTURE3);
        if (tex > 0)
        {
            glBindTexture(GL_TEXTURE_2D, tex);
            glUniform1i(this.enable_normal, 1);
            enable_normalVal = true;
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.enable_normal, 0);
            enable_normalVal = false;
        }
        glActiveTexture(GL_TEXTURE0);
    }

    public void setSpecularTexture(int tex)
    {
        glActiveTexture(GL_TEXTURE4);
        if (tex > 0)
        {
            glBindTexture(GL_TEXTURE_2D, tex);
            glUniform1i(this.enable_specular, 1);
            enable_specularVal = true;
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.enable_specular, 0);
            enable_specularVal = false;
        }
        glActiveTexture(GL_TEXTURE0);
    }
}