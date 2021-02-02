#version 330

in vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform float colorA;
uniform float color2A;
uniform bool clip_outside;

void main()
{
	if (colorA <= 0.0 || color2A <= 0.0) discard;
	if (clip_outside && (outTexCoord.x < 0.0 || outTexCoord.x > 1.0 || outTexCoord.y < 0.0 || outTexCoord.y > 1.0)) discard;
	if (texture(texture_sampler, outTexCoord).a <= 0.0) discard;
}