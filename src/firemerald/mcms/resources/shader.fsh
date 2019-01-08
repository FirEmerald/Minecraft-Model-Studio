#version 330

in vec2 outTexCoord;
in vec3 outNormal;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec4 color;
uniform bool hueSet;
uniform bool invert;

void main()
{
	fragColor = texture(texture_sampler, outTexCoord);
	if (invert) fragColor.rgb = 1.0 - fragColor.rgb;
	if (hueSet)
	{
		float mx = max(max(fragColor.r, fragColor.g), fragColor.b);
		float mn = min(min(fragColor.r, fragColor.g), fragColor.b);
		float dev = mx - mn;
		fragColor.rgb = vec3(mn) + vec3(dev) * color.rgb;
		fragColor.a *= color.a;
	}
    else
	{
		fragColor *= color;
	}
	fragColor *= vec4(vec3((normalize(outNormal).z + 1.0) * 0.5), 1.0);
}