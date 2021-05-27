#version 330

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;
layout (location=3) in vec4 color;

out vec3 outVPos;
out vec4 outVLightPos;
out vec2 outVTexCoord;
out vec3 outVNormal;
out vec4 outVColor;

uniform mat4 modelMatrix;
uniform mat4 viewProjectionMatrix;
uniform mat3 normalMatrix;
uniform mat4 textureMatrix;
uniform mat4 lightSpaceMatrix;
uniform bool hasColor;

void main()
{
	vec4 fragPos4 = modelMatrix * vec4(vertex, 1.0);
    gl_Position = viewProjectionMatrix * fragPos4;
	outVPos = vertex;
	outVLightPos = lightSpaceMatrix * fragPos4;
	outVNormal = normalMatrix * normal;
    outVTexCoord = (textureMatrix * vec4(texCoord, 0.0, 1.0)).xy;
	outVColor = hasColor ? color : vec4(1.0);
}