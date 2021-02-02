package firemerald.mcms.util.mesh;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;
import firemerald.mcms.util.MiscUtil;

public class GuiMesh
{
    protected final int vaoId, vboPos, vboTex, vboInd;
    public DrawMode drawMode;
    public int usage;
    private float[] verts, texs;
    private int[] inds;

    protected int vertexCount;
    
    public GuiMesh()
    {
    	this(new float[0], new float[0], new int[0]);
    }
    
    public GuiMesh(DrawMode mode)
    {
    	this(new float[0], new float[0], new int[0], mode);
    }
    
    public GuiMesh(float x1, float y1, float x2, float y2)
    {
    	this(x1, y1, x2, y2, 0, 0, 1, 1);
    }
    
    public GuiMesh(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2)
    {
    	this(new float[] {
    			x1, y1,
    			x2, y1,
    			x2, y2,
    			x1, y2
    	}, new float[] {
    			u1, v1,
    			u2, v1,
    			u2, v2,
    			u1, v2
    	}, new int[] {
    			0, 3, 1,
    			1, 3, 2
    	}, DrawMode.TRIANGLES);
    }
    
    public GuiMesh(float x1, float y1, float u1, float v1, float x2, float y2, float u2, float v2, float x3, float y3, float u3, float v3, float x4, float y4, float u4, float v4)
    {
    	this(new float[] {
    			x1, y1,
    			x2, y2,
    			x3, y3,
    			x4, y4
    	}, new float[] {
    			u1, v1,
    			u2, v2,
    			u3, v3,
    			u4, v4
    	}, new int[] {
    			0, 3, 1,
    			1, 3, 2
    	}, DrawMode.TRIANGLES);
    }

    public GuiMesh(float[] positions, float[] textCoords, int[] indices)
    {
    	this(positions, textCoords, indices, DrawMode.TRIANGLES);
    }

    public GuiMesh(float[] positions, float[] textCoords, int[] indices, DrawMode mode)
    {
    	this(positions, textCoords, indices, mode, GL_STATIC_DRAW);
    }

    public GuiMesh(float[] positions, float[] textCoords, int[] indices, DrawMode mode, int usage)
    {
    	this.drawMode = mode;
    	this.usage = usage;
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        IntBuffer indicesBuffer = null;
        try
        {
            vertexCount = indices.length;
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);
            // Position VBO
            vboPos = glGenBuffers();
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(verts = positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboPos);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, usage);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
            // Texture coordinates VBO
            vboTex = glGenBuffers();
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(texs = textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboTex);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, usage);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            // Index VBO
            vboInd = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(inds = indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboInd);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, usage);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (posBuffer != null) MemoryUtil.memFree(posBuffer);
            if (textCoordsBuffer != null) MemoryUtil.memFree(textCoordsBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
    }
    
    protected void initRender()
    {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    protected void endRender()
    {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

	public void render() 
    {
        initRender();
        glDrawElements(drawMode.mode, vertexCount, GL_UNSIGNED_INT, 0);
        endRender();
    }
    
    private boolean cleaned = false;

	public synchronized void cleanUp()
    {
    	if (!cleaned)
    	{
        	cleaned = true;
        	final int vaoId = this.vaoId;
            Main.CLEANUP_ACTIONS.add(() -> glDeleteVertexArrays(vaoId));
        	final int vboPos = this.vboPos;
        	Main.CLEANUP_ACTIONS.add(() -> glDeleteBuffers(vboPos));
        	final int vboTex = this.vboTex;
        	Main.CLEANUP_ACTIONS.add(() -> glDeleteBuffers(vboTex));
        	final int vboInd = this.vboInd;
        	Main.CLEANUP_ACTIONS.add(() -> glDeleteBuffers(vboInd));
    	}
    }

	public void setMesh(float[] positions, float[] textCoords, int[] indices)
	{
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        IntBuffer indicesBuffer = null;
        try
        {
            vertexCount = indices.length;
            glBindVertexArray(vaoId);
            // Position VBO
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(verts = positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboPos);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, usage);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
            // Texture coordinates VBO
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(texs = textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboTex);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, usage);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            // Index VBO
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(inds = indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboInd);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, usage);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (posBuffer != null) MemoryUtil.memFree(posBuffer);
            if (textCoordsBuffer != null) MemoryUtil.memFree(textCoordsBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
	}

	public void setPositions(float[] positions)
	{
        FloatBuffer posBuffer = null;
        try
        {
            glBindVertexArray(vaoId);
            // Position VBO
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(verts = positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboPos);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, usage);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (posBuffer != null) MemoryUtil.memFree(posBuffer);
        }
	}

	public void setTexCoords(float[] textCoords)
	{
        FloatBuffer textCoordsBuffer = null;
        try
        {
            glBindVertexArray(vaoId);
            // Texture coordinates VBO
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(texs = textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboTex);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, usage);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (textCoordsBuffer != null) MemoryUtil.memFree(textCoordsBuffer);
        }
	}

	public void setIndices(int[] indices)
	{
        IntBuffer indicesBuffer = null;
        try
        {
            vertexCount = indices.length;
            glBindVertexArray(vaoId);
            // Index VBO
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(inds = indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboInd);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, usage);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
	}
	
	public float[] getVerticies()
	{
		return verts;
	}
	
	public float[] getTexs()
	{
		return texs;
	}
	
	public int[] getIndicies()
	{
		return inds;
	}
	
	@Override
	public void finalize()
	{
		this.cleanUp();
	}
	
	public void setMesh(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2)
	{
		setMesh(new float[] {
    			x1, y1,
    			x2, y1,
    			x2, y2,
    			x1, y2
    	}, new float[] {
    			u1, v1,
    			u2, v1,
    			u2, v2,
    			u1, v2
    	}, new int[] {
    			0, 3, 1,
    			1, 3, 2
    	});
	}
    
    public GuiMesh copy()
    {
    	return new GuiMesh(MiscUtil.copy(this.verts), MiscUtil.copy(this.texs), MiscUtil.copy(this.inds), this.drawMode, this.usage);
    }
}