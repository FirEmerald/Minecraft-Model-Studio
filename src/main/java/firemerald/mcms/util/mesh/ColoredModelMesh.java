package firemerald.mcms.util.mesh;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.Main;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.util.MiscUtil;

public class ColoredModelMesh extends ModelMesh
{
    protected final int vboCol;
    private float[] cols;
    
    public ColoredModelMesh()
    {
    	this(new float[0], new float[0], new float[0], new float[0], new int[0]);
    }
    
    public ColoredModelMesh(DrawMode mode)
    {
    	this(new float[0], new float[0], new float[0], new float[0], new int[0], mode);
    }

    public ColoredModelMesh(float[] positions, float[] textCoords, float[] normals, float[] colors, int[] indices)
    {
    	this(positions, textCoords, normals, colors, indices, DrawMode.TRIANGLES);
    }

    public ColoredModelMesh(float[] positions, float[] textCoords, float[] normals, float[] colors, int[] indices, DrawMode mode)
    {
    	this(positions, textCoords, normals, colors, indices, mode, GL_STATIC_DRAW);
    }

    public ColoredModelMesh(float[] positions, float[] textCoords, float[] normals, float[] colors, int[] indices, DrawMode mode, int usage)
    {
    	super(positions, textCoords, normals, indices, mode, usage);
        FloatBuffer colorBuffer = null;
        try
        {
            glBindVertexArray(vaoId);
            // Color VBO
            vboCol = glGenBuffers();
            colorBuffer = MemoryUtil.memAllocFloat(colors.length);
            colorBuffer.put(cols = colors).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboCol);
            glBufferData(GL_ARRAY_BUFFER, colorBuffer, usage);
            glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (colorBuffer != null) MemoryUtil.memFree(colorBuffer);
        }
    }
    
    @Override
    protected void initRender()
    {
    	Main.instance.currentModelShader.setHasColor();
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
    }

    @Override
    protected void endRender()
    {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);
    	Main.instance.currentModelShader.unsetHasColor();
    }

    @Override
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
        	final int vboCol = this.vboCol;
        	Main.CLEANUP_ACTIONS.add(() -> glDeleteBuffers(vboCol));
        	final int vboInd = this.vboInd;
        	Main.CLEANUP_ACTIONS.add(() -> glDeleteBuffers(vboInd));
    	}
    }

	public void setMesh(float[] positions, float[] textCoords, float[] normals, float[] colors, int[] indices)
	{
		super.setMesh(positions, textCoords, normals, indices);
        FloatBuffer colorBuffer = null;
        try
        {
            // Color VBO
            colorBuffer = MemoryUtil.memAllocFloat(colors.length);
            colorBuffer.put(cols = colors).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboCol);
            glBufferData(GL_ARRAY_BUFFER, colorBuffer, usage);
            glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);
        }
        finally
        {
            if (colorBuffer != null) MemoryUtil.memFree(colorBuffer);
        }
	}

	public void setColors(float[] colors)
	{
        FloatBuffer vecColorsBuffer = null;
        try
        {
            glBindVertexArray(vaoId);
            // Vertex normals VBO
            vecColorsBuffer = MemoryUtil.memAllocFloat(colors.length);
            vecColorsBuffer.put(cols = colors).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboCol);
            glBufferData(GL_ARRAY_BUFFER, vecColorsBuffer, usage);
            glVertexAttribPointer(3, 4, GL_FLOAT, true, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (vecColorsBuffer != null) MemoryUtil.memFree(vecColorsBuffer);
        }
	}
	
	public float[] getColors()
	{
		return cols;
	}
	
	@Override
	public void saveToXML(AbstractElement saveTo)
	{
		super.saveToXML(saveTo);
		AbstractElement colors = saveTo.addChild("colors");
		StringBuilder str = new StringBuilder();
		for (float color : cols)
		{
			str.append(Float.toString(color));
			str.append(' ');
		}
		colors.setValue(str.toString());
	}

	@Override
	public void loadFromXML(AbstractElement loadFrom) throws Exception
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
		if (colorsStr == null) throw new Exception("Failed to load mesh: missing colors");
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
		setMesh(verts, texs, norms, colors, inds);
		this.drawMode = drawMode;
	}
    
	@Override
    public ColoredModelMesh copy()
    {
    	return new ColoredModelMesh(MiscUtil.copy(this.verts), MiscUtil.copy(this.texs), MiscUtil.copy(this.norms), MiscUtil.copy(this.cols), MiscUtil.copy(this.inds), this.drawMode, this.usage);
    }
}