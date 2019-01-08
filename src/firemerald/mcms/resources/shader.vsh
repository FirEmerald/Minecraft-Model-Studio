#version 330

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;

out vec2 outTexCoord;
out vec3 outNormal;

uniform mat4 modelMatrix;
uniform mat4 viewProjectionMatrix;
uniform mat3 normalMatrix;
uniform mat3 textureMatrix;

void main()
{
    gl_Position = viewProjectionMatrix * modelMatrix * vec4(vertex, 1.0);
	outNormal = normalMatrix * normal;
    outTexCoord = (textureMatrix * vec3(texCoord, 1.0)).xy;
}