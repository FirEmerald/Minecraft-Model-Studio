package firemerald.mcms.util;

import firemerald.mcms.model.Mesh;

public interface Meshes
{
	public static final Mesh X16 = new Mesh(0, 0, 16, 16, 0, 0, 0, 1, 1);
	public static final Mesh X32 = new Mesh(0, 0, 32, 32, 0, 0, 0, 1, 1);
	public static final Mesh MESH_80x20 = new Mesh(0, 0, 80, 20, 0, 0, 0, 1, 1);
	public static final Mesh MESH_320x160 = new Mesh(0, 0, 320, 160, 0, 0, 0, 1, 1);
}