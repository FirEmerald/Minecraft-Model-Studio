package firemerald.mcms.api.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import firemerald.mcms.api.math.Vec2;
import firemerald.mcms.api.math.Vec3;

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

	public List<Vec3> vertices = new ArrayList<>();
	public List<Vec2> textureCoordinates = new ArrayList<>();
	public List<Vec3> vertexNormals = new ArrayList<>();
	public Map<String, List<int[][]>> groupObjects;
	
	public static ObjData tryLoad(File file)
	{
		try
		{
			InputStream in;
			ObjData data = new ObjData(in = new FileInputStream(file));
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return data;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public ObjData(InputStream in) throws Exception
	{
		groupObjects = loadObjModel(in);
	}
	
	public ObjData(Collection<Vec3> vertices, Collection<Vec2> textureCoordinates, Collection<Vec3> vertexNormals, Map<String, List<int[][]>> groupObjects)
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
					Vec3 vertex = parseVertex(currentLine, lineCount);
					if (vertex != null) vertices.add(vertex);
				}
				else if (currentLine.startsWith("vt "))
				{
					Vec2 textureCoordinate = parseTextureCoordinate(currentLine, lineCount);
					if (textureCoordinate != null) textureCoordinates.add(textureCoordinate);
				}
				else if (currentLine.startsWith("vn "))
				{
					Vec3 normal = parseVertexNormal(currentLine, lineCount);
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
		} catch (Exception e)
		{
			throw new Exception("IO Exception reading model format", e);
		} finally
		{
			try
			{
				reader.close();
			} catch (IOException e) {}
			try
			{
				inputStream.close();
			} catch (IOException e) {}
		}
		return groupObjects;
	}

	private Vec3 parseVertex(String line, int lineCount) throws Exception
	{
		if (isValidVertexLine(line))
		{
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try
			{
				if (tokens.length == 3) return new Vec3(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
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

	private Vec3 parseVertexNormal(String line, int lineCount) throws Exception
	{
		if (isValidVertexNormalLine(line))
		{
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try
			{
				if (tokens.length == 3) return new Vec3(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
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

	private Vec2 parseTextureCoordinate(String line, int lineCount) throws Exception
	{
		if (isValidTextureCoordinateLine(line))
		{
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try
			{
				if (tokens.length == 2) return new Vec2(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]));
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
	
	public ObjData optimize()
	{ 
		//find unused values
		BitSet usedVerts = new BitSet(vertices.size());
		BitSet usedTexs = new BitSet(textureCoordinates.size());
		BitSet usedNorms = new BitSet(vertexNormals.size());
		for (List<int[][]> mesh : groupObjects.values()) for (int[][] face : mesh) for (int[] vertData : face)
		{
			usedVerts.set(vertData[0]);
			if (vertData[1] >= 0) usedTexs.set(vertData[1]);
			if (vertData[2] >= 0) usedNorms.set(vertData[2]);
		}
		//vertex optimization
		Map<Integer, Integer> newVert = new HashMap<>();
		List<Vec3> verts = new ArrayList<>();
		for (int i = 0; i < vertices.size(); i++) if (usedVerts.get(i))
		{
			Vec3 vert = vertices.get(i);
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
		List<Vec2> texs = new ArrayList<>();
		for (int i = 0; i < textureCoordinates.size(); i++) if (usedTexs.get(i))
		{
			Vec2 tex = textureCoordinates.get(i);
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
		List<Vec3> norms = new ArrayList<>();
		for (int i = 0; i < vertexNormals.size(); i++) if (usedNorms.get(i))
		{
			Vec3 norm = vertexNormals.get(i);
			int ind = norms.indexOf(norm);
			if (ind < 0)
			{
				ind = norms.size();
				norms.add(norm);
			}
			newNorm.put(i, ind);
		}
		//index optimization
		Map<String, List<int[][]>> objects = new LinkedHashMap<>(groupObjects.size());
		for (Entry<String, List<int[][]>> entry : groupObjects.entrySet())
		{
			List<int[][]> oldMesh;
			List<int[][]> newMesh;
			objects.put(entry.getKey(), newMesh = new ArrayList<>((oldMesh = entry.getValue()).size()));
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
		return new ObjData(verts, texs, norms, objects);
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder("#Model generated with MCAMC\n\n#Number of verticies: ");
		str.append(vertices.size());
		str.append("\n#Number of texture coordinates: ");
		str.append(textureCoordinates.size());
		str.append("\n#Number of normals: ");
		str.append(vertexNormals.size());
		str.append("\n#Number of groups: ");
		str.append(groupObjects.size());
		str.append("\n\n#Verticies\n");
		for (Vec3 vec : vertices)
		{
			str.append("v ");
			str.append(vec.x());
			str.append(' ');
			str.append(vec.y());
			str.append(' ');
			str.append(vec.z());
			str.append('\n');
		}
		str.append("\n#Texture Coordinates\n");
		for (Vec2 vec : textureCoordinates)
		{
			str.append("vt ");
			str.append(vec.x());
			str.append(' ');
			str.append(vec.y());
			str.append('\n');
		}
		str.append("\n#Normals\n");
		for (Vec3 vec : vertexNormals)
		{
			str.append("vn ");
			str.append(vec.x());
			str.append(' ');
			str.append(vec.y());
			str.append(' ');
			str.append(vec.z());
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