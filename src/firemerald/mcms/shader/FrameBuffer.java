package firemerald.mcms.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import firemerald.mcms.Main;

public class FrameBuffer
{
	public final int frameBuffer;
	public final int frameBufferTex;
	public final int depth_stencil_texture;
	private int sizeW, sizeH;
	
	public FrameBuffer(int sizeW, int sizeH)
	{
		this.sizeW = sizeW;
		this.sizeH = sizeH;
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		
		glBindTexture(GL_TEXTURE_2D, depth_stencil_texture = glGenTextures());
		setTexArgs(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_NEAREST, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, sizeW, sizeH, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
		
		glBindTexture(GL_TEXTURE_2D, frameBufferTex = glGenTextures());
		setTexArgs(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_NEAREST, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, sizeW, sizeH, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, frameBufferTex, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth_stencil_texture, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depth_stencil_texture, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		Main.listGLErrors("Create framebuffer");
	}
	
	public void setSize(int sizeW, int sizeH)
	{
		if (sizeW == this.sizeW && sizeH == this.sizeH) return;
		glBindTexture(GL_TEXTURE_2D, depth_stencil_texture);
		setTexArgs(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_NEAREST, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, sizeW, sizeH, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
		glBindTexture(GL_TEXTURE_2D, frameBufferTex);
		setTexArgs(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_NEAREST, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, sizeW, sizeH, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
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
		Main.CLEANUP_ACTIONS.add(() -> glDeleteTextures(depth_stencil_texture));
		Main.CLEANUP_ACTIONS.add(() -> glDeleteTextures(frameBufferTex));
	}
}