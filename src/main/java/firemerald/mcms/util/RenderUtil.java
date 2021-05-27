package firemerald.mcms.util;

import static org.lwjgl.opengl.GL15.*;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.MathUtils;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.shader.ModelShaderBase;
import firemerald.mcms.util.mesh.ColoredModelMesh;
import firemerald.mcms.util.mesh.DrawMode;
import firemerald.mcms.util.mesh.ModelMesh;

public class RenderUtil
{
	public static final float SIZE = .09375f / 2;
	public static final ModelMesh SKELETON_NODE_BOX = new ModelMesh(new float[] {
			 SIZE, -SIZE,  SIZE,
			 SIZE, -SIZE, -SIZE,
			 SIZE,  SIZE, -SIZE,
			 SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			 SIZE,  SIZE,  SIZE,
			 SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			 SIZE, -SIZE,  SIZE,
			 SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE, -SIZE,
			 SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE, -SIZE,
			 SIZE, -SIZE, -SIZE,
			 SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			 SIZE,  SIZE, -SIZE
	}, new float[] {
			0, .5f,
			.25f, .5f,
			.25f, 1,
			0, 1,
			.25f, 0,
			.5f, 0,
			.5f, .5f,
			.25f, .5f,
			.25f, .5f,
			.5f, .5f,
			.5f, 1,
			.25f, 1,
			.5f, .5f,
			.75f, .5f,
			.75f, 1,
			.5f, 1,
			.5f, 0,
			.75f, 0,
			.75f, .5f,
			.5f, .5f,
			.75f, .5f,
			1, .5f,
			1, 1,
			.75f, 1
	}, new float[] {
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
	}, DrawMode.TRIANGLES, GL_STATIC_DRAW);
	
	public static final ModelMesh BONE_MESH;
	public static final ModelMesh ITEM_MESH;
	public static final ModelMesh SPHERE_MESH;
	
	static
	{
		ModelMesh boneMesh = null;
		try
		{
			boneMesh = ObjUtil.makeMesh(new ObjData(Main.getResource(new ResourceLocation(Main.ID, "bone.obj"))));
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load bone mesh", e);
			boneMesh = new ModelMesh();
		}
		BONE_MESH = boneMesh;
		ModelMesh itemMesh = null;
		try
		{
			itemMesh = ObjUtil.makeMesh(new ObjData(Main.getResource(new ResourceLocation(Main.ID, "item.obj"))), vert -> {
				vert.x = -vert.x;
				vert.z = -vert.z;
				return vert;
			}, norm -> {
				norm.x = -norm.x;
				norm.z = -norm.z;
				return norm;
			}); //optimized 180 degree rotation around the Y axis
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load item mesh", e);
			itemMesh = new ModelMesh();
		}
		ITEM_MESH = itemMesh;
		ModelMesh sphereMesh = null;
		try
		{
			sphereMesh = ObjUtil.makeMesh(new ObjData(Main.getResource(new ResourceLocation(Main.ID, "sphere.obj"))));
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load sphere mesh", e);
			sphereMesh = new ModelMesh();
		}
		SPHERE_MESH = sphereMesh;
	}
	
	public static void renderSkeleton(Bone<?> bone, Map<String, Matrix4d> transforms, boolean showNodes, boolean showBones)
	{
		if (!bone.visible) return;
		Main.instance.textureManager.bindTexture(Textures.BOX);
		Main.modelTempMesh.drawMode = DrawMode.LINES;
		Main.modelTempMesh.setMesh(new float[] {
				0, 0, 0,
				0, 0, 1
		}, new float[4], new float[] {
				0, 0, 1,
				0, 0, 1
		}, new int[] {
				0, 1
		});
		renderSkeletonMesh(bone, transforms, showNodes, showBones);
		Main.modelTempMesh.drawMode = DrawMode.TRIANGLES;
	}
	
	public static void renderSkeletonMesh(Bone<?> bone, Map<String, Matrix4d> transforms, boolean showNodes, boolean showBones)
	{
		Matrix4d m = transforms.get(bone.getName());
		ModelShaderBase.MODEL.push();
		if (m != null)
		{
			ModelShaderBase.MODEL.matrix().mul(m);
			Main.instance.currentModelShader.updateModel();
		}
		else m = new Matrix4d();
		if (showNodes)
		{
			Main.instance.textureManager.bindTexture(Textures.BOX);
			ModelShaderBase.MODEL.push();
			ModelShaderBase.MODEL.matrix().scale(1 / Main.instance.project.getScale());
			Main.instance.currentModelShader.updateModel();
			SKELETON_NODE_BOX.render();
			ModelShaderBase.MODEL.pop();
			Main.instance.currentModelShader.updateModel();
		}
		for (Bone<?> child : bone.children) if (child.visible)
		{
			if (showBones)
			{
				Matrix4d m2 = transforms.get(child.getName());
				Vector3d pos = new Vector3d();
				if (m2 != null) pos = m2.getTranslation(new Vector3d());
				double mag = pos.length();
				if (mag > 0)
				{
					Main.instance.currentModelShader.setColor(.5f, .5f, .5f, 1);
					Matrix4d o;
					if (pos.x() != 0 || pos.z() != 0) o = new Matrix4d().setLookAlong(pos, new Vector3d(0, 1, 0)).transpose();
					else if (pos.y() > 0) o = new Matrix4d().rotateX(Math.PI / 2);
					else o = new Matrix4d().rotateX(-Math.PI / 2);
					o = o.scale(mag);
					Main.instance.textureManager.unbindTexture();
					ModelShaderBase.MODEL.push();
					ModelShaderBase.MODEL.matrix().mul(o);
					Main.instance.currentModelShader.updateModel();
					BONE_MESH.render();
					ModelShaderBase.MODEL.pop();
					Main.instance.currentModelShader.updateModel();
					Main.instance.currentModelShader.setColor(1, 1, 1, 1);
				}
			}
			renderSkeletonMesh(child, transforms, showNodes, showBones);
		}
		ModelShaderBase.MODEL.pop();
	}
	
	public static ModelMesh makeMesh(List<int[][]> mesh, ObjData obj, Matrix4d m)
	{
		Matrix3d n = null;
		if (m != null) n = m.transpose3x3(new Matrix3d()).invert().normal();
		List<Float> verts = new ArrayList<>();
		List<Float> texs = new ArrayList<>();
		List<Float> norms = new ArrayList<>();
		List<Float> colors = obj.hasColorData() ? new ArrayList<>() : null;
		List<Integer> indicies = new ArrayList<>();
		for (int[][] face : mesh)
		{
			int index = verts.size() / 3;
			for (int i = 0; i < face.length; i++)
			{
				int[] data = face[i];
				int vert = data[0];
				int tex = data[1];
				int norm = data[2];
				Pair<Vector3f, Vector4f> vertex = obj.vertices.get(vert);
				Vector4f vertexVec = new Vector4f(vertex.left, 1);
				if (m != null) vertexVec = MathUtils.toVector4f(m.transform(MathUtils.toVector4d(vertexVec)));
				verts.add(vertexVec.x());
				verts.add(vertexVec.y());
				verts.add(vertexVec.z());
				if (vertex.right != null)
				{
					colors.add(vertex.right.x());
					colors.add(vertex.right.y());
					colors.add(vertex.right.z());
					colors.add(vertex.right.w());
				}
				else if (obj.hasColorData())
				{
					colors.add(1f);
					colors.add(1f);
					colors.add(1f);
					colors.add(1f);
				}
				if (tex >= 0)
				{
					Vector2f texture = obj.textureCoordinates.get(tex);
					texs.add(texture.x());
					texs.add(1 - texture.y());
				}
				else
				{
					texs.add(0f);
					texs.add(0f);
				}
				if (norm >= 0)
				{
					Vector3f normal = obj.vertexNormals.get(norm);
					if (n != null) normal = MathUtils.toVector3f(n.transform(MathUtils.toVector3d(normal)));
					norms.add(normal.x());
					norms.add(normal.y());
					norms.add(normal.z());
				}
				else
				{
					norms.add(0f);
					norms.add(0f);
					norms.add(0f);
				}
				if (i >= 2)
				{
					indicies.add(index);
					indicies.add(index + i - 1);
					indicies.add(index + i);
				}
			}
		}
		return obj.hasColorData() ? new ColoredModelMesh(toFloats(verts), toFloats(texs), toFloats(norms), toFloats(colors), toInts(indicies)) : new ModelMesh(toFloats(verts), toFloats(texs), toFloats(norms), toInts(indicies));
	}
	
	public static float[] toFloats(List<Float> list)
	{
		float[] array = new float[list.size()];
		Iterator<Float> it = list.iterator();
		for (int i = 0; i < array.length; i++) array[i] = it.next().floatValue();
		return array;
	}
	
	public static int[] toInts(List<Integer> list)
	{
		int[] array = new int[list.size()];
		Iterator<Integer> it = list.iterator();
		for (int i = 0; i < array.length; i++) array[i] = it.next();
		return array;
	}

	private static final Stack<Rectangle> scissorStack = new Stack<>();
	private static Rectangle scissor = new Rectangle(0, 0, 1, 1);
	
	public static void updateScissor()
	{
		GL11.glScissor(scissor.x, scissor.y, scissor.width, scissor.height);
	}
	
	public static void clearScissor()
	{
		scissorStack.clear();
		scissor = new Rectangle(0, 0, Main.instance.sizeW, Main.instance.sizeH);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		updateScissor();
	}
	
	public static void pushScissor(int x1, int y1, int x2, int y2)
	{
		int t = y1;
		y1 = Main.instance.sizeH - y2;
		y2 = Main.instance.sizeH - t;
		x1 = Math.max(x1, scissor.x);
		y1 = Math.max(y1, scissor.y);
		x2 = Math.min(x2, scissor.x + scissor.width);
		y2 = Math.min(y2, scissor.y + scissor.height);
		if (scissorStack.isEmpty()) GL11.glEnable(GL11.GL_SCISSOR_TEST);
		scissorStack.push(scissor);
		scissor = new Rectangle(x1, y1, x2 <= x1 ? 0 : x2 - x1, y2 <= y1 ? 0 : y2 - y1);
		updateScissor();
	}
	
	public static void popScissor()
	{
		scissor = scissorStack.pop();
		if (scissorStack.isEmpty()) GL11.glDisable(GL11.GL_SCISSOR_TEST);
		updateScissor();
	}
	
	public static void disableScissor()
	{
		if (!scissorStack.isEmpty()) GL11.glDisable(GL11.GL_SCISSOR_TEST);;
	}
	
	public static void enableScissor()
	{
		if (!scissorStack.isEmpty()) GL11.glEnable(GL11.GL_SCISSOR_TEST);;
	}
}