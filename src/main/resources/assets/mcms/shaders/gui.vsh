#version 330

layout (location=0) in vec2 vertex;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 modelMatrix;
uniform mat4 viewProjectionMatrix;
uniform mat4 textureMatrix;

void main()
{
    gl_Position = viewProjectionMatrix * modelMatrix * vec4(vertex, 0.0, 1.0);
    outTexCoord = (textureMatrix * vec4(texCoord, 0.0, 1.0)).xy;
}