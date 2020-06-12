package firemerald.mcms.util;

import static org.lwjgl.opengl.GL15.*;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import firemerald.mcms.Main;
import firemerald.mcms.api.math.MathUtils;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.HSV;
import firemerald.mcms.util.mesh.Mesh;

public class RenderUtil
{
	private static int stencil = 0;
	
	public static final Mesh STENCIL_CLEAR = new Mesh(1, -1, -1, 1, 0, 0, 0, 1, 1);
	
	public static final float SIZE = .09375f / 2;
	public static final Mesh SKELETON_NODE_BOX = new Mesh(new float[] {
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
	}, Mesh.DrawMode.TRIANGLES, GL_STATIC_DRAW);
	
	public static final Mesh BONE_MESH;
	public static final Mesh ITEM_MESH;
	
	static
	{
		Mesh boneMesh = null;
		try
		{
			boneMesh = ObjUtil.makeMesh(new ObjData(Main.getResource(new ResourceLocation(Main.ID, "bone.obj"))));
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load bone mesh", e);
			boneMesh = new Mesh();
		}
		BONE_MESH = boneMesh;
		Mesh itemMesh = null;
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
			itemMesh = new Mesh();
		}
		ITEM_MESH = itemMesh;
	}
	
	public static void renderSkeleton(Bone bone, Map<String, Matrix4d> transforms, boolean showNodes, boolean showBones)
	{
		if (!bone.visible) return;
		Main.instance.textureManager.bindTexture(Textures.BOX);
		Main.MODMESH.drawMode = Mesh.DrawMode.LINES;
		Main.MODMESH.setMesh(new float[] {
				0, 0, 0,
				0, 0, 1
		}, new float[4], new float[] {
				0, 0, 1,
				0, 0, 1
		}, new int[] {
				0, 1
		});
		renderSkeletonMesh(bone, transforms, showNodes, showBones);
		Main.MODMESH.drawMode = Mesh.DrawMode.TRIANGLES;
	}
	
	public static void renderSkeletonMesh(Bone bone, Map<String, Matrix4d> transforms, boolean showNodes, boolean showBones)
	{
		Matrix4d m = transforms.get(bone.getName());
		Shader.MODEL.push();
		if (m != null)
		{
			Shader.MODEL.matrix().mul(m);
			Main.instance.shader.updateModel();
		}
		else m = new Matrix4d();
		if (showNodes)
		{
			Main.instance.textureManager.bindTexture(Textures.BOX);
			Shader.MODEL.push();
			Shader.MODEL.matrix().scale(1 / Main.instance.project.getScale());
			Main.instance.shader.updateModel();
			SKELETON_NODE_BOX.render();
			Shader.MODEL.pop();
			Main.instance.shader.updateModel();
		}
		for (Bone child : bone.children) if (child.visible)
		{
			if (showBones)
			{
				Matrix4d m2 = transforms.get(child.getName());
				Vector3d pos = new Vector3d();
				if (m2 != null) pos = m2.getTranslation(new Vector3d());
				double mag = pos.length();
				if (mag > 0)
				{
					Main.instance.shader.setColor(.5f, .5f, .5f, 1);
					Matrix4d o;
					if (pos.x() != 0 || pos.z() != 0) o = new Matrix4d().setLookAlong(pos, new Vector3d(0, 1, 0)).transpose();
					else if (pos.y() > 0) o = new Matrix4d().rotateX(Math.PI / 2);
					else o = new Matrix4d().rotateX(-Math.PI / 2);
					o = o.scale(mag);
					Main.instance.textureManager.unbindTexture();
					Shader.MODEL.push();
					Shader.MODEL.matrix().mul(o);
					Main.instance.shader.updateModel();
					BONE_MESH.render();
					Shader.MODEL.pop();
					Main.instance.shader.updateModel();
					Main.instance.shader.setColor(1, 1, 1, 1);
				}
			}
			renderSkeletonMesh(child, transforms, showNodes, showBones);
		}
		Shader.MODEL.pop();
	}
	
	public static void clearStencil()
	{
		Shader.MODEL.push();
		Shader.VIEW.push();
		Shader.PROJECTION.push();
		Shader.MODEL.matrix().identity();
		Shader.VIEW.matrix().identity();
		Shader.PROJECTION.matrix().identity();
		Main.instance.shader.updateModelViewProjection();
		glColorMask(false, false, false, false);
		glStencilFunc(GL_NEVER, 0, 0xFF);
		glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
		glStencilMask(0xFF);
		Main.instance.textureManager.unbindTexture();
		STENCIL_CLEAR.render();
		//endStencil();

		glColorMask(true, true, true, true);
		glStencilMask(0x00);
		glStencilFunc(GL_ALWAYS, 0, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
		
		Shader.MODEL.pop();
		Shader.VIEW.pop();
		Shader.PROJECTION.pop();
		Main.instance.shader.updateModelViewProjection();
	}

	@SuppressWarnings("deprecation")
	public static void pushStencil()
	{
		if (stencil >= 255)
		{
			IllegalStateException e = new IllegalStateException("Stencil buffer overflow");
			Main.LOGGER.log(Level.FATAL, e);
			Thread.currentThread().stop(e);
		}
		else
		{
			stencil++;
			if (stencil == 1) glEnable(GL_STENCIL_TEST);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void popStencil()
	{
		if (stencil <= 0)
		{
			IllegalStateException e = new IllegalStateException("Stencil buffer underflow");
			Main.LOGGER.log(Level.FATAL, e);
			Thread.currentThread().stop(e);
		}
		else
		{
			stencil--;
			if (stencil == 0) clearStencil();
			else
			{
				Shader.MODEL.push();
				Shader.VIEW.push();
				Shader.PROJECTION.push();
				Shader.MODEL.matrix().identity();
				Shader.VIEW.matrix().identity();
				Shader.PROJECTION.matrix().identity();
				Main.instance.shader.updateModelViewProjection();
				glColorMask(false, false, false, false);
				glStencilFunc(GL_NOTEQUAL, stencil + 1, 0xFF);
				glStencilOp(GL_DECR, GL_KEEP, GL_KEEP);
				glStencilMask(0xFF);
				Main.instance.textureManager.unbindTexture();
				STENCIL_CLEAR.render();
				//endStencil();

				glColorMask(true, true, true, true);
				glStencilMask(0x00);
				if (stencil == 0) glStencilFunc(GL_ALWAYS, 0, 0xFF);
				else glStencilFunc(GL_EQUAL, stencil, 0xFF);
				glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
				
				Shader.MODEL.pop();
				Shader.VIEW.pop();
				Shader.PROJECTION.pop();
				Main.instance.shader.updateModelViewProjection();
				if (stencil == 0) glDisable(GL_STENCIL_TEST);
			}
		}
		saveStencilbuffer("pop_to_" + stencil);
	}
	
	public static void startStencil(boolean subtract)
	{
		glColorMask(false, false, false, false);
		if (subtract)
		{
			glStencilFunc(GL_NOTEQUAL, stencil, 0xFF);
			glStencilOp(GL_DECR, GL_KEEP, GL_KEEP);
		}
		else
		{
			glStencilFunc(GL_NOTEQUAL, stencil - 1, 0xFF);
			glStencilOp(GL_INCR, GL_KEEP, GL_KEEP);
		}
		glStencilMask(0xFF);
	}
	
	public static void endStencil()
	{
		glColorMask(true, true, true, true);
		glStencilMask(0x00);
		glStencilFunc(GL_EQUAL, stencil, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
		saveStencilbuffer("end_at_" + stencil);
	}
	
	public static int stencilC = 0;
	public static boolean save = false;

	public static void saveStencilbuffer(String phase)
	{
		if (!save) return;
		int w = Main.instance.sizeW;
		int h = Main.instance.sizeH;
		int h2 = h - 1;
		String fileName = "stencil_" + stencilC + "_" + phase;
		stencilC++;
		int k = w * h;
		ByteBuffer pixels = BufferUtils.createByteBuffer(k);
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glReadPixels(0, 0, w, h, GL_STENCIL_INDEX, GL_UNSIGNED_BYTE, pixels);
		BufferedImage bufferedimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		WritableRaster alpha = bufferedimage.getAlphaRaster();
		for (int i1 = 0; i1 < h; ++i1) for (int j1 = 0; j1 < w; ++j1)
		{
			int pixel = (pixels.get((h2 - i1) * w + j1)) & 0xFF;
			float hue = pixel / 32f;
			float sat = (8 - (pixel - ((int) Math.floor(hue) * 32)) / 32f) / 8f;
			HSV hsv = new HSV(hue, sat, 1);
			bufferedimage.setRGB(j1, i1, hsv.getRGB().hashCode());
			alpha.setSample(j1, i1, 0, 0xFF);
		}
		File file3 = new File(fileName + ".png");
		try
		{
			ImageIO.write(bufferedimage, "png", file3);
		}
		catch (IOException e) {}
	}
	
	public static Mesh makeMesh(List<int[][]> mesh, ObjData obj, Matrix4d m)
	{
		Matrix3d n = null;
		if (m != null) n = m.transpose3x3(new Matrix3d()).invert().normal();
		List<Float> verts = new ArrayList<>();
		List<Float> texs = new ArrayList<>();
		List<Float> norms = new ArrayList<>();
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
				Vector3f vertex = obj.vertices.get(vert);
				Vector4f vertexVec = new Vector4f(vertex, 1);
				if (m != null) vertexVec = MathUtils.toVector4f(m.transform(MathUtils.toVector4d(vertexVec)));
				verts.add(vertexVec.x());
				verts.add(vertexVec.y());
				verts.add(vertexVec.z());
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
		return new Mesh(toFloats(verts), toFloats(texs), toFloats(norms), toInts(indicies));
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
}