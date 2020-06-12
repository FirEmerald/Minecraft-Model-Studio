package firemerald.mcms.api.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joml.Vector2f;
import org.joml.Vector3f;

import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.popups.GuiPopupException;

public class ObjData
{
	public static final Pattern vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
	public static final Pattern vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
	public static final Pattern textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+(\\.\\d+)?){2,3} *$)");
	public static final Pattern face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
	public static final Pattern face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
	public static final Pattern face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
	public static final Pattern face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
	public static final Pattern groupObjectPattern = Pattern.compile("([go]( [\\w\\d\\.]+) *\\n)|([go]( [\\w\\d\\.]+) *$)");
	
	public static Matcher vertexMatcher, vertexNormalMatcher, textureCoordinateMatcher;
	public static Matcher face_V_VT_VN_Matcher, face_V_VT_Matcher, face_V_VN_Matcher, face_V_Matcher;
	public static Matcher groupObjectMatcher;

	public List<Vector3f> vertices = new ArrayList<>();
	public List<Vector2f> textureCoordinates = new ArrayList<>();
	public List<Vector3f> vertexNormals = new ArrayList<>();
	public Map<String, List<int[][]>> groupObjects;
	
	public static ObjData tryLoad(File file)
	{
		try
		{
			InputStream in;
			ObjData data = new ObjData(in = new FileInputStream(file));
			FileUtil.closeSafe(in);
			return data;
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't load OBJ from " + file, e);
			return null;
		}
	}
	
	public ObjData(File file) throws Exception
	{
		InputStream in = null;
		Exception ex = null;
		try
		{
			groupObjects = loadObjModel(in = new FileInputStream(file));
		}
		catch (Exception e)
		{
			ex = e;
		}
		FileUtil.closeSafe(in);
		if (ex != null) throw ex;
	}
	
	public ObjData(InputStream in) throws Exception
	{
		groupObjects = loadObjModel(in);
	}
	
	public ObjData(Collection<Vector3f> vertices, Collection<Vector2f> textureCoordinates, Collection<Vector3f> vertexNormals, Map<String, List<int[][]>> groupObjects)
	{
		this.vertices.addAll(vertices);
		this.textureCoordinates.addAll(textureCoordinates);
		this.vertexNormals.addAll(vertexNormals);
		this.groupObjects = groupObjects;
	}

	public ObjData()
	{
		groupObjects = new LinkedHashMap<String, List<int[][]>>();
	}

	private Map<String, List<int[][]>> loadObjModel(InputStream inputStream) throws Exception
	{
		Map<String, List<int[][]>> groupObjects = new LinkedHashMap<>();
		BufferedReader reader = null;
		String currentLine = null;
		int lineCount = 0;
		List<int[][]> model = null;
		List<List<int[][]>> unnamed = new ArrayList<>();
		try
		{
			reader = new BufferedReader(new InputStreamReader(inputStream));
			while ((currentLine = reader.readLine()) != null)
			{
				lineCount++;
				currentLine = currentLine.replaceAll("\\s+", " ").trim();
				if (currentLine.startsWith("#") || currentLine.length() == 0) continue;
				else if (currentLine.startsWith("v "))
				{
					Vector3f vertex = parseVertex(currentLine, lineCount);
					if (vertex != null) vertices.add(vertex);
				}
				else if (currentLine.startsWith("vt "))
				{
					Vector2f textureCoordinate = parseTextureCoordinate(currentLine, lineCount);
					if (textureCoordinate != null) textureCoordinates.add(textureCoordinate);
				}
				else if (currentLine.startsWith("vn "))
				{
					Vector3f normal = parseVertexNormal(currentLine, lineCount);
					if (normal != null) vertexNormals.add(normal);
				}
				else if (currentLine.startsWith("f "))
				{
					if (model == null)
					{
						model = new ArrayList<>();
						unnamed.add(model);
					}
					parseFace(currentLine, lineCount, model);
				}
				else if (currentLine.startsWith("g ") | currentLine.startsWith("o "))
				{
					String group = parseGroupObject(currentLine, lineCount);
					if (group != null)
					{
						if (groupObjects.containsKey(group)) throw new Exception("Duplicate group name " + group);
						model = new ArrayList<>();
						groupObjects.put(group, model);
					}
				}
			}
			model = null;
			int defNum = 0;
			for (List<int[][]> mesh : unnamed)
			{
				String name;
				while (groupObjects.containsKey(name = "default_" + defNum++));
				groupObjects.put(name, mesh);
			}
		}
		catch (Exception e)
		{
			throw new Exception("IO Exception reading model format", e);
		}
		finally
		{
			FileUtil.closeSafe(reader);
			FileUtil.closeSafe(inputStream);
		}
		return groupObjects;
	}

	private Vector3f parseVertex(String line, int lineCount) throws Exception
	{
		if (isValidVertexLine(line))
		{
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try
			{
				if (tokens.length == 3) return new Vector3f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
			} catch (NumberFormatException e)
			{
				throw new Exception(String.format("Number formatting error at line %d", lineCount), e);
			}
		}
		else
		{
			throw new Exception("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") - Incorrect format");
		}
		return null;
	}

	private Vector3f parseVertexNormal(String line, int lineCount) throws Exception
	{
		if (isValidVertexNormalLine(line))
		{
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try
			{
				if (tokens.length == 3) return new Vector3f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
			} catch (NumberFormatException e)
			{
				throw new Exception(String.format("Number formatting error at line %d", lineCount), e);
			}
		}
		else
		{
			throw new Exception("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") - Incorrect format");
		}
		return null;
	}

	private Vector2f parseTextureCoordinate(String line, int lineCount) throws Exception
	{
		if (isValidTextureCoordinateLine(line))
		{
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try
			{
				if (tokens.length == 2) return new Vector2f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
			} catch (NumberFormatException e)
			{
				throw new Exception(String.format("Number formatting error at line %d", lineCount), e);
			}
		}
		else
		{
			throw new Exception("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") - Incorrect format");
		}
		return null;
	}

	private void parseFace(String line, int lineCount, List<int[][]> indicies) throws Exception
	{
		if (isValidFaceLine(line))
		{
			String trimmedLine = line.substring(line.indexOf(" ") + 1);
			String[] tokens = trimmedLine.split(" ");
			String[] subTokens = null;
			int[][] index;
			indicies.add(index = new int[tokens.length][]);
			if (isValidFace_V_VT_VN_Line(line))
			{
				for (int i = 0; i < tokens.length; ++i)
				{
					subTokens = tokens[i].split("/");
					index[i] = new int[3];
					index[i][0] = Integer.parseInt(subTokens[0]) - 1;
					index[i][1] = Integer.parseInt(subTokens[1]) - 1;
					index[i][2] = Integer.parseInt(subTokens[2]) - 1;
				}
			}
			else if (isValidFace_V_VT_Line(line))
			{
				for (int i = 0; i < tokens.length; ++i)
				{
					subTokens = tokens[i].split("/");
					index[i] = new int[3];
					index[i][0] = Integer.parseInt(subTokens[0]) - 1;
					index[i][1] = Integer.parseInt(subTokens[1]) - 1;
					index[i][2] = -1;
				}
			}
			else if (isValidFace_V_VN_Line(line))
			{
				for (int i = 0; i < tokens.length; ++i)
				{
					subTokens = tokens[i].split("/");
					index[i] = new int[3];
					index[i][0] = Integer.parseInt(subTokens[0]) - 1;
					index[i][1] = -1;
					index[i][2] = Integer.parseInt(subTokens[2]) - 1;
				}
			}
			else if (isValidFace_V_Line(line))
			{
				for (int i = 0; i < tokens.length; ++i)
				{
					subTokens = tokens[i].split("/");
					index[i] = new int[3];
					index[i][0] = Integer.parseInt(subTokens[0]) - 1;
					index[i][1] = -1;
					index[i][2] = -1;
				}
			}
			else
			{
				throw new Exception("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") - Incorrect format");
			}
		}
		else
		{
			throw new Exception("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") - Incorrect format");
		}
	}

	private String parseGroupObject(String line, int lineCount) throws Exception
	{
		String group = null;
		if (isValidGroupObjectLine(line))
		{
			String trimmedLine = line.substring(line.indexOf(" ") + 1);
			if (trimmedLine.length() > 0) group = trimmedLine;
		}
		else
		{
			throw new Exception("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") - Incorrect format");
		}
		return group;
	}

	/***
	 * Verifies that the given line from the model file is a valid vertex
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid vertex, false otherwise
	 */
	private static boolean isValidVertexLine(String line)
	{
		if (vertexMatcher != null) vertexMatcher.reset();
		vertexMatcher = vertexPattern.matcher(line);
		return vertexMatcher.matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid vertex normal
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid vertex normal, false otherwise
	 */
	private static boolean isValidVertexNormalLine(String line)
	{
		if (vertexNormalMatcher != null) vertexNormalMatcher.reset();
		vertexNormalMatcher = vertexNormalPattern.matcher(line);
		return vertexNormalMatcher.matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid texture
	 * coordinate
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid texture coordinate, false otherwise
	 */
	private static boolean isValidTextureCoordinateLine(String line)
	{
		if (textureCoordinateMatcher != null) textureCoordinateMatcher.reset();
		textureCoordinateMatcher = textureCoordinatePattern.matcher(line);
		return textureCoordinateMatcher.matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is
	 * described by vertices, texture coordinates, and vertex normals
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid face that matches the format
	 *         "f v1/vt1/vn1 ..." (with a minimum of 3 points in the face, and a
	 *         maximum of 4), false otherwise
	 */
	private static boolean isValidFace_V_VT_VN_Line(String line)
	{
		if (face_V_VT_VN_Matcher != null) face_V_VT_VN_Matcher.reset();
		face_V_VT_VN_Matcher = face_V_VT_VN_Pattern.matcher(line);
		return face_V_VT_VN_Matcher.matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is
	 * described by vertices and texture coordinates
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid face that matches the format
	 *         "f v1/vt1 ..." (with a minimum of 3 points in the face, and a
	 *         maximum of 4), false otherwise
	 */
	private static boolean isValidFace_V_VT_Line(String line)
	{
		if (face_V_VT_Matcher != null) face_V_VT_Matcher.reset();
		face_V_VT_Matcher = face_V_VT_Pattern.matcher(line);
		return face_V_VT_Matcher.matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is
	 * described by vertices and vertex normals
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid face that matches the format
	 *         "f v1//vn1 ..." (with a minimum of 3 points in the face, and a
	 *         maximum of 4), false otherwise
	 */
	private static boolean isValidFace_V_VN_Line(String line)
	{
		if (face_V_VN_Matcher != null) face_V_VN_Matcher.reset();
		face_V_VN_Matcher = face_V_VN_Pattern.matcher(line);
		return face_V_VN_Matcher.matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is
	 * described by only vertices
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid face that matches the format
	 *         "f v1 ..." (with a minimum of 3 points in the face, and a maximum
	 *         of 4), false otherwise
	 */
	private static boolean isValidFace_V_Line(String line)
	{
		if (face_V_Matcher != null) face_V_Matcher.reset();
		face_V_Matcher = face_V_Pattern.matcher(line);
		return face_V_Matcher.matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face of any
	 * of the possible face formats
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid face that matches any of the valid
	 *         face formats, false otherwise
	 */
	private static boolean isValidFaceLine(String line)
	{
		return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
	}

	/***
	 * Verifies that the given line from the model file is a valid group (or
	 * object)
	 * 
	 * @param line
	 *            the line being validated
	 * @return true if the line is a valid group (or object), false otherwise
	 */
	private static boolean isValidGroupObjectLine(String line)
	{
		if (groupObjectMatcher != null) groupObjectMatcher.reset();
		groupObjectMatcher = groupObjectPattern.matcher(line);
		return groupObjectMatcher.matches();
	}
	
	@FunctionalInterface
	public static interface ITriangulator
	{
		public static final ITriangulator FAN = face -> {
			if (face.length > 2)
			{
				int[][][] triangles = new int[face.length - 2][3][];
				for (int i = 0; i < triangles.length; i++)
				{
					int[][] destination = triangles[i];
					destination[0] = face[0];
					destination[1] = face[i + 1];
					destination[2] = face[i + 2];
				}
				return triangles;
			}
			else return new int[0][][];
		};
		
		public static final ITriangulator STRIP = face -> {
			if (face.length > 2)
			{
				int[][][] triangles = new int[face.length - 2][3][];
				for (int i = 0; i < triangles.length; i++)
				{
					int[][] destination = triangles[i];
					destination[0] = face[(i + 1) & ~1];
					destination[1] = face[(i & ~1) + 1];
					destination[2] = face[i + 2];
				}
				return triangles;
			}
			else return new int[0][][];
		};
		
		public int[][][] triangulate(int[][] face);
	}
	
	public ObjData optimize()
	{
		return optimize(ITriangulator.FAN, true, true);
	}
	
	public ObjData optimize(ITriangulator triangulator)
	{
		return optimize(triangulator, true, true);
	}
	
	public ObjData optimize(ITriangulator triangulator, boolean removeNonTriangles)
	{
		return optimize(triangulator, removeNonTriangles, true);
	}
	
	public ObjData optimize(boolean removeEmptyGroups)
	{
		return optimize(ITriangulator.FAN, true, removeEmptyGroups);
	}
	
	public ObjData optimize(ITriangulator triangulator, boolean removeNonTriangles, boolean removeEmptyGroups)
	{
		Map<String, List<int[][]>> processed = new LinkedHashMap<>();
		//triangulate and remove points and lines - only triangles are supported, although points and lines can be simulated by using identical vertices (with some lighting issues) - they will be removed if removeNonTriangles is true.
		groupObjects.forEach((name, mesh) -> {
			List<int[][]> newMesh = new ArrayList<>();
			for (int[][] face : mesh)
			{
				int[][][] triangles = triangulator.triangulate(face);
				for (int[][] triangle : triangles)
				{
					if (removeNonTriangles)
					{
						Vector3f p0 = vertices.get(triangle[0][0]);
						Vector3f p1 = vertices.get(triangle[1][0]);
						Vector3f p2 = vertices.get(triangle[2][0]);
						if ((p0.x() == p1.x() && p0.y() == p1.y() && p0.z() == p1.z()) || (p0.x() == p2.x() && p0.y() == p2.y() && p0.z() == p2.z()) || (p1.x() == p2.x() && p1.y() == p2.y() && p1.z() == p2.z())) //duplicate vertices are always points or lines
						{
							double x1 = p1.x() - p0.x();
							double y1 = p1.y() - p0.y();
							double z1 = p1.z() - p0.z();
							double x2 = p2.x() - p0.x();
							double y2 = p2.y() - p0.y();
							double z2 = p2.z() - p0.z();
							double x3 = y1 * z2 - y2 * z1;
							double y3 = z1 * x2 - z2 * x1;
							double z3 = x1 * y2 - x2 * y1;
							double a = x3 * x3 + y3 * y3 + z3 * z3;
							//a = x1 * x1 * (y2 * y2 + z2 * z2) + y1 * y1 * (x2 * x2 + z2 * z2) + z1 * z1 * (x2 * x2 + y2 * y2) - 2 * (x1 * y1 * x2 * y2 + x1 * z1 * x2 * z2 + y1 * z1 * y2 * z2);
							//a = x_0^2y_1^2+x_0^2y_2^2+x_0^2z_1^2+x_0^2z_2^2-2x_0x_1y_0y_1+2x_0x_1y_0y_2+2x_0x_1y_1y_2-2x_0x_1y_2^2-2x_0x_1z_0z_1+2x_0x_1z_0z_2+2x_0x_1z_1z_2-2x_0x_1z_2^2+2x_0x_2y_0y_1-2x_0x_2y_0y_2-2x_0x_2y_1^2+2x_0x_2y_1y_2+2x_0x_2z_0z_1-2x_0x_2z_0z_2-2x_0x_2z_1^2+2x_0x_2z_1z_2+x_1^2y_0^2+x_1^2y_2^2+x_1^2z_0^2+x_1^2z_2^2-2x_1x_2y_0^2+2x_1x_2y_0y_1+2x_1x_2y_0y_2-2x_1x_2y_1y_2-2x_1x_2z_0^2+2x_1x_2z_0z_1+2x_1x_2z_0z_2-2x_1x_2z_1z_2+x_2^2y_0^2+x_2^2y_1^2+x_2^2z_0^2+x_2^2z_1^2+y_0^2z_1^2+y_0^2z_2^2-2y_0y_1x_2^2-2y_0y_1z_0z_1+2y_0y_1z_0z_2+2y_0y_1z_1z_2-2y_0y_1z_2^2-2y_0y_2x_1^2+2y_0y_2z_0z_1-2y_0y_2z_0z_2-2y_0y_2z_1^2+2y_0y_2z_1z_2+y_1^2z_0^2+y_1^2z_2^2-2y_1y_2x_0^2-2y_1y_2z_0^2+2y_1y_2z_0z_1+2y_1y_2z_0z_2-2y_1y_2z_1z_2+y_2^2z_0^2+y_2^2z_1^2-2z_0z_1x_2^2-2z_0z_1y_2^2-2z_0z_2x_1^2-2z_0z_2y_1^2-2z_1z_2x_0^2-2z_1z_2y_0^2
							if (a != 0) newMesh.add(triangle); //don't add points or lines
						}
						else newMesh.add(triangle);
					}
					else newMesh.add(triangle);
				}
			}
			processed.put(name, newMesh);
		});
		//find unused values
		BitSet usedVerts = new BitSet(vertices.size());
		BitSet usedTexs = new BitSet(textureCoordinates.size());
		BitSet usedNorms = new BitSet(vertexNormals.size());
		for (List<int[][]> mesh : processed.values()) for (int[][] face : mesh) for (int[] vertData : face)
		{
			usedVerts.set(vertData[0]);
			if (vertData[1] >= 0) usedTexs.set(vertData[1]);
			if (vertData[2] >= 0) usedNorms.set(vertData[2]);
		}
		//vertex optimization
		Map<Integer, Integer> newVert = new HashMap<>();
		List<Vector3f> verts = new ArrayList<>();
		for (int i = 0; i < vertices.size(); i++) if (usedVerts.get(i))
		{
			Vector3f vert = vertices.get(i);
			int ind = verts.indexOf(vert);
			if (ind < 0)
			{
				ind = verts.size();
				verts.add(vert);
			}
			newVert.put(i, ind);
		}
		//tex optimization
		Map<Integer, Integer> newTex = new HashMap<>();
		List<Vector2f> texs = new ArrayList<>();
		for (int i = 0; i < textureCoordinates.size(); i++) if (usedTexs.get(i))
		{
			Vector2f tex = textureCoordinates.get(i);
			int ind = texs.indexOf(tex);
			if (ind < 0)
			{
				ind = texs.size();
				texs.add(tex);
			}
			newTex.put(i, ind);
		}
		//normal optimization
		Map<Integer, Integer> newNorm = new HashMap<>();
		List<Vector3f> norms = new ArrayList<>();
		for (int i = 0; i < vertexNormals.size(); i++) if (usedNorms.get(i))
		{
			Vector3f norm = vertexNormals.get(i);
			int ind = norms.indexOf(norm);
			if (ind < 0)
			{
				ind = norms.size();
				norms.add(norm);
			}
			newNorm.put(i, ind);
		}
		//group/index optimization
		Map<String, List<int[][]>> objects = new LinkedHashMap<>();
		processed.forEach((name, oldMesh) -> {
			if (!removeEmptyGroups || oldMesh.size() > 0)
			{
				List<int[][]> newMesh;
				objects.put(name, newMesh = new ArrayList<>(oldMesh.size()));
				for (int i = 0; i < oldMesh.size(); i++)
				{
					int[][] oldFace;
					int[][] newFace;
					newMesh.add(newFace = new int[(oldFace = oldMesh.get(i)).length][]);
					for (int j = 0; j < oldFace.length; j++)
					{
						int[] oldVertData = oldFace[j];
						int[] newVertData = newFace[j] = new int[3];
						newVertData[0] = newVert.get(oldVertData[0]);
						newVertData[1] = oldVertData[1] == -1 ? -1 : newTex.get(oldVertData[1]);
						newVertData[2] = oldVertData[2] == -1 ? -1 : newNorm.get(oldVertData[2]);
					}
				}
			}
		});
		return new ObjData(verts, texs, norms, objects);
	}
	
	public static final NumberFormat NF = new DecimalFormat("################################################.###########################################");
	
	public String toString(double val)
	{
		return NF.format(val);
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder("#Model generated with Minecraft Model Studio\n\n#Number of verticies: ");
		str.append(vertices.size());
		str.append("\n#Number of texture coordinates: ");
		str.append(textureCoordinates.size());
		str.append("\n#Number of normals: ");
		str.append(vertexNormals.size());
		str.append("\n#Number of groups: ");
		str.append(groupObjects.size());
		str.append("\n\n#Verticies\n");
		for (Vector3f vec : vertices)
		{
			str.append("v ");
			str.append(toString(vec.x()));
			str.append(' ');
			str.append(toString(vec.y()));
			str.append(' ');
			str.append(toString(vec.z()));
			str.append('\n');
		}
		str.append("\n#Texture Coordinates\n");
		for (Vector2f vec : textureCoordinates)
		{
			str.append("vt ");
			str.append(toString(vec.x()));
			str.append(' ');
			str.append(toString(vec.y()));
			str.append('\n');
		}
		str.append("\n#Normals\n");
		for (Vector3f vec : vertexNormals)
		{
			str.append("vn ");
			str.append(toString(vec.x()));
			str.append(' ');
			str.append(toString(vec.y()));
			str.append(' ');
			str.append(toString(vec.z()));
			str.append('\n');
		}
		str.append('\n');
		for (Entry<String, List<int[][]>> entry : groupObjects.entrySet())
		{
			str.append("g ");
			str.append(entry.getKey());
			str.append("\n#Number of faces: ");
			str.append(entry.getValue().size());
			str.append("\n\n");
			for (int[][] face : entry.getValue())
			{
				str.append('f');
				for (int[] vertex : face)
				{
					str.append(' ');
					str.append(vertex[0] + 1);
					str.append('/');
					if (vertex[1] >= 0) str.append(vertex[1] + 1);
					str.append('/');
					if (vertex[2] >= 0) str.append(vertex[2] + 1);
				}
				str.append('\n');
			}
			str.append('\n');
		}
		return str.toString();
	}
}