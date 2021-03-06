#version 330

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;
layout (location=3) in vec4 color;

out vec4 outLightPos;
out vec2 outTexCoord;
out vec3 outNormal;
out vec4 outColor;

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
	outLightPos = lightSpaceMatrix * fragPos4;
	outNormal = normalMatrix * normal;
    outTexCoord = (textureMatrix * vec4(texCoord, 0.0, 1.0)).xy;
	outColor = hasColor ? color : vec4(1.0);
}