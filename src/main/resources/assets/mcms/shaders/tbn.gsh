#version 330
#extension GL_EXT_geometry_shader4 : enable

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

uniform mat3 normalMatrix;

in vec3 outVPos[3];
in vec4 outVLightPos[3];
in vec2 outVTexCoord[3];
in vec3 outVNormal[3];
in vec4 outVColor[3];
out vec4 outLightPos;
out vec2 outTexCoord;
out vec3 outNormal;
out vec4 outColor;
out mat3 outTBN;

void main()
{
	mat3 tbn;
	if (gl_VerticesIn >= 3)
	{
		vec3 edge1 = outVPos[1] - outVPos[0];
		vec3 edge2 = outVPos[2] - outVPos[0];
		vec2 deltaUV1 = outVTexCoord[1] - outVTexCoord[0];
		vec2 deltaUV2 = outVTexCoord[2] - outVTexCoord[0];
		
		vec3 normal = normalize(normalMatrix * cross(edge1, edge2));
		vec3 tangent = normalize(normalMatrix * (edge1 * deltaUV2.y - edge2 * deltaUV1.y));
		vec3 bitangent = normalize(normalMatrix * (edge2 * deltaUV1.x - edge1 * deltaUV2.x));
		tbn = mat3(tangent, bitangent, normal);
	}
	else
	{
		tbn = mat3(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0);
	}

	int i;
	
	
    // Pass through the original vertex
    for(i=0; i<gl_VerticesIn; i++)
    {
		gl_Position = gl_PositionIn[i];
		outLightPos = outVLightPos[i];
		outTexCoord = outVTexCoord[i];
		outNormal = outVNormal[i];
		outColor = outVColor[i];
		outTBN = tbn;
		EmitVertex();
    }

    EndPrimitive();
}