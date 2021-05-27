package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.EnumTextureSpace;
import firemerald.mcms.texture.space.Material;

public class ModelShaderLabPBR extends ModelShader
{
	public final EnumTextureSpace normalSpace, specularSpace;
	protected int normal_sampler, specular_sampler, enable_normal, enable_specular;
	public static boolean enable_normalVal = false, enable_specularVal = false;
	
	public ModelShaderLabPBR(String vert, String geom, String frag)
	{
		this(vert, geom, frag, EnumTextureSpace.NORMAL_LAB_PBR, EnumTextureSpace.SPECULAR_LAB_PBR);
	}
	
	public ModelShaderLabPBR(String vert, String geom, String frag, EnumTextureSpace normalSpace, EnumTextureSpace specularSpace)
	{
		super(vert, geom, frag);
		normal_sampler = glGetUniformLocation(prog, "normal_sampler");
		specular_sampler = glGetUniformLocation(prog, "specular_sampler");
		enable_normal = glGetUniformLocation(prog, "enable_normal");
		enable_specular = glGetUniformLocation(prog, "enable_specular");
		this.normalSpace = normalSpace;
		this.specularSpace = specularSpace;
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
	
	

    public void setNormalTexture(Texture tex)
    {
        glActiveTexture(GL_TEXTURE3);
        if (tex != null)
        {
        	tex.bind();
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

    public void setSpecularTexture(Texture tex)
    {
        glActiveTexture(GL_TEXTURE4);
        if (tex != null)
        {
        	tex.bind();
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
	
	@Override
	public void bindMaterial(Material mat)
	{
		super.bindMaterial(mat);
		this.setNormalTexture(mat.getTexture(normalSpace));
		this.setSpecularTexture(mat.getTexture(specularSpace));
	}
}