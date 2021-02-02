package firemerald.mcms.util.mesh;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public enum DrawMode
{
	POINTS(GL_POINTS, 0, 1),
	LINES(GL_LINES, 0, 2),
	LINE_LOOP(GL_LINE_LOOP, 2, 1),
	LINE_STRIP(GL_LINE_STRIP, 2, 1),
	TRIANGLES(GL_TRIANGLES, 0, 3); //only valid draw modes!
	
	public final int mode;
	public final int min_verts;
	public final int stride;
	
	DrawMode(int mode, int min_verts, int stride)
	{
		this.mode = mode;
		this.min_verts = min_verts;
		this.stride = stride;
	}
	
	public boolean isValid(int numIndices)
	{
		if (numIndices == 0) return true;
		else if (numIndices < min_verts) return false;
		else if (stride <= 1) return true;
		else if ((numIndices % stride) != 0) return false;
		else return true;
	}
}