package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import firemerald.mcms.Main;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.texture.space.EnumTextureSpace;
import firemerald.mcms.texture.space.Material;

public class ModelShaderOldPBR extends ModelShaderLabPBR
{
	public final EnumTextureSpace emmisiveSpace;
	protected int emissive_sampler, enable_emissive;
	public static boolean enable_emissiveVal = false;
	
	public ModelShaderOldPBR(String vert, String geom, String frag)
	{
		this(vert, geom, frag, EnumTextureSpace.NORMAL_OLD_PBR, EnumTextureSpace.SPECULAR_OLD_PBR, EnumTextureSpace.EMISSIVE_OLD_PBR);
	}
	
	public ModelShaderOldPBR(String vert, String geom, String frag, EnumTextureSpace normalSpace, EnumTextureSpace specularSpace, EnumTextureSpace emmisiveSpace)
	{
		super(vert, geom, frag, normalSpace, specularSpace);
		emissive_sampler = glGetUniformLocation(prog, "emissive_sampler");
		enable_emissive = glGetUniformLocation(prog, "enable_emissive");
		this.emmisiveSpace = emmisiveSpace;
	}
	
	public void bind()
	{
		super.bind();
		glUniform1i(emissive_sampler, 5);
		glUniform1i(enable_emissive, enable_emissiveVal ? 1 : 0);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		enable_emissiveVal = false;
	}

    public void setEmissiveTexture(Texture tex)
    {
        glActiveTexture(GL_TEXTURE5);
        if (tex != null)
        {
        	tex.bind();
        	glUniform1i(this.enable_emissive, 1);
            enable_emissiveVal = true;
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.enable_emissive, 0);
            enable_emissiveVal = false;
        }
        glActiveTexture(GL_TEXTURE0);
    }

    public void setEmissiveTexture(int tex)
    {
        glActiveTexture(GL_TEXTURE5);
        if (tex > 0)
        {
            glBindTexture(GL_TEXTURE_2D, tex);
            glUniform1i(this.enable_emissive, 1);
            enable_emissiveVal = true;
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.enable_emissive, 0);
            enable_emissiveVal = false;
        }
        glActiveTexture(GL_TEXTURE0);
    }
	
	@Override
	public void bindMaterial(Material mat)
	{
		super.bindMaterial(mat);
		this.setEmissiveTexture(mat.getTexture(emmisiveSpace));
	}
}