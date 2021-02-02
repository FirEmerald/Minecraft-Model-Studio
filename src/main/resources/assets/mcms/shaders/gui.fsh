#version 330

in vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform sampler2D overlay_sampler;
uniform vec4 color;
uniform vec4 color2;
uniform bool hueSet;
uniform bool invert;
uniform bool use_overlay;
uniform bool clip_outside;

void main()
{
	if (clip_outside && (outTexCoord.x < 0.0 || outTexCoord.x > 1.0 || outTexCoord.y < 0.0 || outTexCoord.y > 1.0)) discard;
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
	fragColor *= color2;
	if (use_overlay)
	{
		vec4 overlay = texture(overlay_sampler, outTexCoord);
		if (overlay.a != 0.0)
		{
			if (overlay.a == 1.0 || fragColor.a == 0.0)
			{
				fragColor = overlay;
			}
			else
			{
				float inva = 1.0 - overlay.a;
				float a = overlay.a + fragColor.a * inva;
				if (a != 0.0) fragColor.rgb = (overlay.rgb * overlay.a + fragColor.rgb * fragColor.a * inva) / a;
				fragColor.a = a;
			}
		}
	}
	if (fragColor.a <= 0.0) discard;
}