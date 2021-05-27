package firemerald.mcms.util.mesh;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.util.MiscUtil;

public class ModelMesh
{
    protected final int vaoId, vboPos, vboTex, vboNorm, vboInd;
    public DrawMode drawMode;
    public int usage;
    protected float[] verts, texs, norms;
    protected int[] inds;

    protected int vertexCount;
    
    public ModelMesh()
    {
    	this(new float[0], new float[0], new float[0], new int[0]);
    }
    
    public ModelMesh(DrawMode mode)
    {
    	this(new float[0], new float[0], new float[0], new int[0], mode);
    }
    
    public ModelMesh(float x1, float y1, float z1, float x2, float y2, float z2)
    {
		this(new float[] {
				x2, y1, z2,
				x2, y1, z1,
				x2, y2, z1,
				x2, y2, z2,
				
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1,
				x1, y2, z1,
				
				x1, y1, z2,
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2,
				
				x1, y1, z1,
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1,
				
				x2, y1, z2,
				x1, y1, z2,
				x1, y1, z1,
				x2, y1, z1,
				
				x2, y1, z1,
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1
		}, new float[24 * 2], new float[] {
				1, 0, 0,
				1, 0, 0,
				1, 0, 0,
				1, 0, 0,
				0, 1, 0,
				0, 1, 0,
				0, 1, 0,
				0, 1, 0,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				-1, 0, 0,
				-1, 0, 0,
				-1, 0, 0,
				-1, 0, 0,
				0, -1, 0,
				0, -1, 0,
				0, -1, 0,
				0, -1, 0,
				0, 0, -1,
				0, 0, -1,
				0, 0, -1,
				0, 0, -1
		}, new int[] {
				0, 1, 3,
				3, 1, 2,
				4, 5, 7,
				7, 5, 6,
				8, 9, 11,
				11, 9, 10,
				12, 13, 15,
				15, 13, 14,
				16, 17, 19,
				19, 17, 18,
				20, 21, 23,
				23, 21, 22
		}, DrawMode.TRIANGLES);
    }

    public ModelMesh(float[] positions, float[] textCoords, float[] normals, int[] indices)
    {
    	this(positions, textCoords, normals, indices, DrawMode.TRIANGLES);
    }

    public ModelMesh(float[] positions, float[] textCoords, float[] normals, int[] indices, DrawMode mode)
    {
    	this(positions, textCoords, normals, indices, mode, GL_STATIC_DRAW);
    }

    public ModelMesh(float[] positions, float[] textCoords, float[] normals, int[] indices, DrawMode mode, int usage)
    {
    	this.drawMode = mode;
    	this.usage = usage;
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
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
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            // Texture coordinates VBO
            vboTex = glGenBuffers();
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(texs = textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboTex);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, usage);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            // Vertex normals VBO
            vboNorm = glGenBuffers();
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(norms = normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboNorm);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, usage);
            glVertexAttribPointer(2, 3, GL_FLOAT, true, 0, 0);
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
            if (vecNormalsBuffer != null) MemoryUtil.memFree(vecNormalsBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
    }
    
    protected void initRender()
    {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    protected void endRender()
    {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

	public void render() 
    {
        initRender();
        glDrawElements(drawMode.mode, vertexCount, GL_UNSIGNED_INT, 0);
        endRender();
    }
    
    protected boolean cleaned = false;

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
        	final int vboNorm = this.vboNorm;
        	Main.CLEANUP_ACTIONS.add(() -> glDeleteBuffers(vboNorm));
        	final int vboInd = this.vboInd;
        	Main.CLEANUP_ACTIONS.add(() -> glDeleteBuffers(vboInd));
    	}
    }

	public void setMesh(float[] positions, float[] textCoords, float[] normals, int[] indices)
	{
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
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
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            // Texture coordinates VBO
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(texs = textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboTex);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, usage);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            // Vertex normals VBO
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(norms = normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboNorm);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, usage);
            glVertexAttribPointer(2, 3, GL_FLOAT, true, 0, 0);
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
            if (vecNormalsBuffer != null) MemoryUtil.memFree(vecNormalsBuffer);
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
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
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

	public void setNormals(float[] normals)
	{
        FloatBuffer vecNormalsBuffer = null;
        try
        {
            glBindVertexArray(vaoId);
            // Vertex normals VBO
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(norms = normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboNorm);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, usage);
            glVertexAttribPointer(2, 3, GL_FLOAT, true, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (vecNormalsBuffer != null) MemoryUtil.memFree(vecNormalsBuffer);
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
	
	public float[] getNormals()
	{
		return norms;
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
	
	public void saveAsChild(AbstractElement el, String name)
	{
		AbstractElement saveTo = el.addChild(name);
		saveToXML(saveTo);
	}
	
	public void saveToXML(AbstractElement saveTo)
	{
		saveTo.setEnum("mode", drawMode);
		AbstractElement verts = saveTo.addChild("vertices");
		StringBuilder str = new StringBuilder();
		for (float vert : this.verts)
		{
			str.append(Float.toString(vert));
			str.append(' ');
		}
		verts.setValue(str.toString());
		AbstractElement texs = saveTo.addChild("texture_coordinates");
		str = new StringBuilder();
		for (float tex : this.texs)
		{
			str.append(Float.toString(tex));
			str.append(' ');
		}
		texs.setValue(str.toString());
		AbstractElement norms = saveTo.addChild("normals");
		str = new StringBuilder();
		for (float norm : this.norms)
		{
			str.append(Float.toString(norm));
			str.append(' ');
		}
		norms.setValue(str.toString());
		AbstractElement inds = saveTo.addChild("indices");
		str = new StringBuilder();
		for (int ind : this.inds)
		{
			str.append(Integer.toString(ind));
			str.append(' ');
		}
		inds.setValue(str.toString());
	}
	
	public void loadFromXML(AbstractElement loadFrom) throws Exception
	{
		String vertsStr = null, texsStr = null, normsStr = null, indsStr = null;
		for (AbstractElement el : loadFrom.getChildren()) switch (el.getName())
		{
		case "vertices":
		{
			vertsStr = el.getValue();
			break;
		}
		case "texture_coordinates":
		{
			texsStr = el.getValue();
			break;
		}
		case "normals":
		{
			normsStr = el.getValue();
			break;
		}
		case "indices":
		{
			indsStr = el.getValue();
			break;
		}
		}
		if (vertsStr == null) throw new Exception("Failed to load mesh: missing vertices");
		if (texsStr == null) throw new Exception("Failed to load mesh: missing texture coordinates");
		if (normsStr == null) throw new Exception("Failed to load mesh: missing normals");
		if (indsStr == null) throw new Exception("Failed to load mesh: missing indices");
		DrawMode drawMode = loadFrom.getEnum("mode", DrawMode.values(), DrawMode.TRIANGLES);
		String[] indsStrs = indsStr.split(" ");
		if (!drawMode.isValid(indsStrs.length)) throw new Exception("Failed to load mesh: invalid number of indices for draw mode");
		int[] inds = new int[indsStrs.length];
		int maxInd = 0;
		for (int i = 0; i < inds.length; i++) try
		{
			int ind = inds[i] = Integer.parseInt(indsStrs[i]);
			if (ind < 0) throw new Exception("Failed to load mesh: vertex index below 0");
			else if (ind > maxInd) maxInd = ind;
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-integer vertex index", e);
		}
		String[] vertsStrs = vertsStr.split(" ");
		if (vertsStrs.length < (maxInd * 3)) throw new Exception("Failed to load mesh: too few vertex coordinates.");
		float[] verts = new float[vertsStrs.length];
		for (int i = 0; i < verts.length; i++) try
		{
			verts[i] = Float.parseFloat(vertsStrs[i]);
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-float vertex coordinate", e);
		}
		String[] texsStrs = texsStr.split(" ");
		if (texsStrs.length < (maxInd * 2)) throw new Exception("Failed to load mesh: too few texture coordinates.");
		float[] texs = new float[texsStrs.length];
		for (int i = 0; i < texs.length; i++) try
		{
			texs[i] = Float.parseFloat(texsStrs[i]);
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-float texture coordinate", e);
		}
		String[] normsStrs = normsStr.split(" ");
		if (normsStrs.length < (maxInd * 3)) throw new Exception("Failed to load mesh: too few normal coordinates.");
		float[] norms = new float[normsStrs.length];
		for (int i = 0; i < norms.length; i++) try
		{
			norms[i] = Float.parseFloat(normsStrs[i]);
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-float normal coordinate", e);
		}
		setMesh(verts, texs, norms, inds);
		this.drawMode = drawMode;
	}
    
    public ModelMesh copy()
    {
    	return new ModelMesh(MiscUtil.copy(this.verts), MiscUtil.copy(this.texs), MiscUtil.copy(this.norms), MiscUtil.copy(this.inds), this.drawMode, this.usage);
    }

	public static ModelMesh readFromXML(AbstractElement loadFrom) throws Exception
	{
		String vertsStr = null, texsStr = null, normsStr = null, colorsStr = null, indsStr = null;
		for (AbstractElement el : loadFrom.getChildren()) switch (el.getName())
		{
		case "vertices":
		{
			vertsStr = el.getValue();
			break;
		}
		case "texture_coordinates":
		{
			texsStr = el.getValue();
			break;
		}
		case "normals":
		{
			normsStr = el.getValue();
			break;
		}
		case "colors":
		{
			colorsStr = el.getValue();
			break;
		}
		case "indices":
		{
			indsStr = el.getValue();
			break;
		}
		}
		if (vertsStr == null) throw new Exception("Failed to load mesh: missing vertices");
		if (texsStr == null) throw new Exception("Failed to load mesh: missing texture coordinates");
		if (normsStr == null) throw new Exception("Failed to load mesh: missing normals");
		if (indsStr == null) throw new Exception("Failed to load mesh: missing indices");
		DrawMode drawMode = loadFrom.getEnum("mode", DrawMode.values(), DrawMode.TRIANGLES);
		String[] indsStrs = indsStr.split(" ");
		if (!drawMode.isValid(indsStrs.length)) throw new Exception("Failed to load mesh: invalid number of indices for draw mode");
		int[] inds = new int[indsStrs.length];
		int maxInd = 0;
		for (int i = 0; i < inds.length; i++) try
		{
			int ind = inds[i] = Integer.parseInt(indsStrs[i]);
			if (ind < 0) throw new Exception("Failed to load mesh: vertex index below 0");
			else if (ind > maxInd) maxInd = ind;
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-integer vertex index", e);
		}
		String[] vertsStrs = vertsStr.split(" ");
		if (vertsStrs.length < (maxInd * 3)) throw new Exception("Failed to load mesh: too few vertex coordinates.");
		float[] verts = new float[vertsStrs.length];
		for (int i = 0; i < verts.length; i++) try
		{
			verts[i] = Float.parseFloat(vertsStrs[i]);
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-float vertex coordinate", e);
		}
		String[] texsStrs = texsStr.split(" ");
		if (texsStrs.length < (maxInd * 2)) throw new Exception("Failed to load mesh: too few texture coordinates.");
		float[] texs = new float[texsStrs.length];
		for (int i = 0; i < texs.length; i++) try
		{
			texs[i] = Float.parseFloat(texsStrs[i]);
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-float texture coordinate", e);
		}
		String[] normsStrs = normsStr.split(" ");
		if (normsStrs.length < (maxInd * 3)) throw new Exception("Failed to load mesh: too few normal coordinates.");
		float[] norms = new float[normsStrs.length];
		for (int i = 0; i < norms.length; i++) try
		{
			norms[i] = Float.parseFloat(normsStrs[i]);
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Failed to load mesh: non-float normal coordinate", e);
		}
		if (colorsStr != null)
		{
			String[] colorsStrs = colorsStr.split(" ");
			if (colorsStrs.length < (maxInd * 4)) throw new Exception("Failed to load mesh: too few color values.");
			float[] colors = new float[colorsStrs.length];
			for (int i = 0; i < colors.length; i++) try
			{
				colors[i] = Float.parseFloat(colorsStrs[i]);
			}
			catch (NumberFormatException e)
			{
				throw new Exception("Failed to load mesh: non-float color value", e);
			}
			return new ColoredModelMesh(verts, texs, norms, colors, inds, drawMode);
		}
		else return new ModelMesh(verts, texs, norms, inds, drawMode);
	}
}