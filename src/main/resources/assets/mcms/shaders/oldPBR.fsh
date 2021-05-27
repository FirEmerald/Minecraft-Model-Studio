#version 330

in vec4 outLightPos;
in vec2 outTexCoord;
in vec3 outNormal;
in vec4 outColor;
in mat3 outTBN;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform sampler2D overlay_sampler;
uniform sampler2D shadow_sampler;
uniform sampler2D normal_sampler;
uniform sampler2D specular_sampler;
uniform sampler2D emissive_sampler;
uniform vec3 light;
uniform vec4 color;
uniform vec4 color2;
uniform bool hueSet;
uniform bool invert;
uniform bool use_overlay;
uniform bool clip_outside;
uniform bool ignore_lighting;
uniform bool enable_shadows;
uniform bool enable_normal;
uniform bool enable_specular;
uniform bool enable_emissive;

const vec3 shadowWeights[69]=vec3[69](
	vec3(-4.0, -2.0, 0.0040522087),
	vec3(-4.0, -1.0, 0.0067315814),
	vec3(-4.0, 0.0, 0.007676615),
	vec3(-4.0, 1.0, 0.0067315814),
	vec3(-4.0, 2.0, 0.0040522087),
	vec3(-3.0, -3.0, 0.005813957),
	vec3(-3.0, -2.0, 0.010704647),
	vec3(-3.0, -1.0, 0.014107487),
	vec3(-3.0, 0.0, 0.01535323),
	vec3(-3.0, 1.0, 0.014107487),
	vec3(-3.0, 2.0, 0.010704647),
	vec3(-3.0, 3.0, 0.005813957),
	vec3(-2.0, -4.0, 0.0040522087),
	vec3(-2.0, -3.0, 0.010704647),
	vec3(-2.0, -2.0, 0.01667033),
	vec3(-2.0, -1.0, 0.021217642),
	vec3(-2.0, 0.0, 0.023029845),
	vec3(-2.0, 1.0, 0.021217642),
	vec3(-2.0, 2.0, 0.01667033),
	vec3(-2.0, 3.0, 0.010704647),
	vec3(-2.0, 4.0, 0.0040522087),
	vec3(-1.0, -4.0, 0.0067315814),
	vec3(-1.0, -3.0, 0.014107487),
	vec3(-1.0, -2.0, 0.021217642),
	vec3(-1.0, -1.0, 0.0275267),
	vec3(-1.0, 0.0, 0.03070646),
	vec3(-1.0, 1.0, 0.0275267),
	vec3(-1.0, 2.0, 0.021217642),
	vec3(-1.0, 3.0, 0.014107487),
	vec3(-1.0, 4.0, 0.0067315814),
	vec3(0.0, -4.0, 0.007676615),
	vec3(0.0, -3.0, 0.01535323),
	vec3(0.0, -2.0, 0.023029845),
	vec3(0.0, -1.0, 0.03070646),
	vec3(0.0, 0.0, 0.038383074),
	vec3(0.0, 1.0, 0.03070646),
	vec3(0.0, 2.0, 0.023029845),
	vec3(0.0, 3.0, 0.01535323),
	vec3(0.0, 4.0, 0.007676615),
	vec3(1.0, -4.0, 0.0067315814),
	vec3(1.0, -3.0, 0.014107487),
	vec3(1.0, -2.0, 0.021217642),
	vec3(1.0, -1.0, 0.0275267),
	vec3(1.0, 0.0, 0.03070646),
	vec3(1.0, 1.0, 0.0275267),
	vec3(1.0, 2.0, 0.021217642),
	vec3(1.0, 3.0, 0.014107487),
	vec3(1.0, 4.0, 0.0067315814),
	vec3(2.0, -4.0, 0.0040522087),
	vec3(2.0, -3.0, 0.010704647),
	vec3(2.0, -2.0, 0.01667033),
	vec3(2.0, -1.0, 0.021217642),
	vec3(2.0, 0.0, 0.023029845),
	vec3(2.0, 1.0, 0.021217642),
	vec3(2.0, 2.0, 0.01667033),
	vec3(2.0, 3.0, 0.010704647),
	vec3(2.0, 4.0, 0.0040522087),
	vec3(3.0, -3.0, 0.005813957),
	vec3(3.0, -2.0, 0.010704647),
	vec3(3.0, -1.0, 0.014107487),
	vec3(3.0, 0.0, 0.01535323),
	vec3(3.0, 1.0, 0.014107487),
	vec3(3.0, 2.0, 0.010704647),
	vec3(3.0, 3.0, 0.005813957),
	vec3(4.0, -2.0, 0.0040522087),
	vec3(4.0, -1.0, 0.0067315814),
	vec3(4.0, 0.0, 0.007676615),
	vec3(4.0, 1.0, 0.0067315814),
	vec3(4.0, 2.0, 0.0040522087)
);

float shadowOp(vec4 fragPosLightSpace)
{
    vec3 projCoords = (fragPosLightSpace.xyz / fragPosLightSpace.w) * 0.5 + 0.5;
	float bias = max(0.00025 * (1.0 - dot(outNormal, light)), 0.000025);
	float shadow = 0.0;
	vec2 texelSize = 1.0 / textureSize(shadow_sampler, 0);
	for (int i = 0; i < 69; i++)
	{
		vec3 shadowWeight = shadowWeights[i];
		float pcfDepth = texture(shadow_sampler, projCoords.xy + shadowWeight.xy * texelSize).r;
		if (projCoords.z - bias > pcfDepth) shadow += shadowWeight.z;
	}
    return shadow;
} 

void main()
{
	if (clip_outside && (outTexCoord.x < 0.0 || outTexCoord.x > 1.0 || outTexCoord.y < 0.0 || outTexCoord.y > 1.0)) discard;
	fragColor = texture(texture_sampler, outTexCoord) * outColor;
	if (invert) fragColor.rgb = 1.0 - fragColor.rgb;
	if (hueSet)
	{
		float mx = max(max(fragColor.r, fragColor.g), fragColor.b);
		float mn = min(min(fragColor.r, fragColor.g), fragColor.b);
		float dev = mx - mn;
		fragColor.rgb = vec3(mn) + vec3(dev) * color.rgb;
		fragColor.a *= color.a;
	}
    else fragColor *= color;
	fragColor *= color2;
	vec4 normalTex = enable_normal ? texture(normal_sampler, outTexCoord) : vec4(0.5, 0.5, 1.0, 1.0);
	vec4 specularTex = enable_specular ? texture(specular_sampler, outTexCoord) : vec4(0.0, 0.0, 0.0, 1.0);
	vec4 emissiveTex = enable_emissive ? texture(emissive_sampler, outTexCoord) : vec4(0.0);
	if (!ignore_lighting)
	{
		vec3 normal;
		if (enable_normal) normal = normalize(outTBN * (normalTex.rgb * 2.0 - 1.0));
		else normal = normalize(outNormal);
		float dp = dot(normal, light);
		if (dp >= 0.0) fragColor.rgb *= vec3(0.5);
		else if (enable_shadows) fragColor.rgb *= vec3(0.5 + 0.5 * dp * (shadowOp(outLightPos) - 1.0));
		else fragColor.rgb *= vec3(0.5 - 0.5 * dp);
	}
	if (use_overlay)
	{
		vec4 overlay = texture(overlay_sampler, outTexCoord);
		if (overlay.a != 0.0)
		{
			if (overlay.a == 1.0 || fragColor.a == 0.0) fragColor = overlay;
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