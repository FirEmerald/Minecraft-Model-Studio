package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL20.*;

import firemerald.mcms.Main;

public class ModelShaderOldPBR extends ModelShaderLabPBR
{
	protected int emmisive_sampler, enable_emmisive;
	public static boolean enable_emmisiveVal = false;
	
	public ModelShaderOldPBR(String vert, String frag)
	{
		super(vert, frag);
		emmisive_sampler = glGetUniformLocation(prog, "emmisive_sampler");
		enable_emmisive = glGetUniformLocation(prog, "enable_emmisive");
	}
	
	public void bind()
	{
		super.bind();
		glUniform1i(emmisive_sampler, 5);
		glUniform1i(enable_emmisive, enable_emmisiveVal ? 1 : 0);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		enable_emmisiveVal = false;
	}

    public void setEmmisiveTexture(int tex)
    {
        glActiveTexture(GL_TEXTURE5);
        if (tex > 0)
        {
            glBindTexture(GL_TEXTURE_2D, tex);
            glUniform1i(this.enable_emmisive, 1);
            enable_emmisiveVal = true;
        }
        else
        {
            Main.instance.textureManager.unbindTexture();
            glUniform1i(this.enable_emmisive, 0);
            enable_emmisiveVal = false;
        }
        glActiveTexture(GL_TEXTURE0);
    }
}