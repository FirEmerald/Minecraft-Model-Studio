package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import firemerald.mcms.Main;

public class DepthBuffer
{
	public final int frameBuffer;
	public final int depth_texture;
	private int sizeW, sizeH;
	
	public DepthBuffer(int sizeW, int sizeH)
	{
		this.sizeW = sizeW;
		this.sizeH = sizeH;
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		
		glBindTexture(GL_TEXTURE_2D, depth_texture = glGenTextures());
		setTexArgs(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, sizeW, sizeH, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);

		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth_texture, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		Main.listGLErrors("Create framebuffer");
	}
	
	public void setSize(int sizeW, int sizeH)
	{
		if (sizeW < 1) sizeW = 1;
		if (sizeH < 1) sizeH = 1;
		if (sizeW == this.sizeW && sizeH == this.sizeH) return;
		glBindTexture(GL_TEXTURE_2D, depth_texture);
		setTexArgs(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, sizeW, sizeH, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);
		Main.listGLErrors("Resize framebuffer");
	}
	
	public static void setTexArgs(int wrapS, int wrapT, int minF, int magF)
	{
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minF);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magF);
	}
	
	@Override
	public void finalize()
	{
		if (Main.glActive) this.cleanUp();
	}

	public void cleanUp()
	{
		Main.CLEANUP_ACTIONS.add(() -> glDeleteFramebuffers(frameBuffer));
		Main.CLEANUP_ACTIONS.add(() -> glDeleteTextures(depth_texture));
	}
}