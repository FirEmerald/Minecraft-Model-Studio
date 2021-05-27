#version 330

layout (location=0) in vec3 vertex;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;
layout (location=3) in vec4 color;

out vec2 outTexCoord;
out vec4 outColor;

uniform mat4 modelMatrix;
uniform mat4 viewProjectionMatrix;
uniform mat4 textureMatrix;
uniform bool hasColor;

void main()
{
    gl_Position = viewProjectionMatrix * modelMatrix * vec4(vertex, 1.0);
    outTexCoord = (textureMatrix * vec4(texCoord, 0.0, 1.0)).xy;
	outColor = hasColor ? color : vec4(1.0);
}