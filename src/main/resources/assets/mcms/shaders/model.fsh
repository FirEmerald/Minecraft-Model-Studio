#version 330

in vec4 outLightPos;
in vec2 outTexCoord;
in vec3 outNormal;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform sampler2D overlay_sampler;
uniform sampler2D shadow_sampler;
uniform vec3 light;
uniform vec4 color;
uniform vec4 color2;
uniform bool hueSet;
uniform bool invert;
uniform bool use_overlay;
uniform bool clip_outside;
uniform bool ignore_lighting;
uniform bool enable_shadows;

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
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    //float closestDepth = texture(shadow_sampler, projCoords.xy).r; 
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;
    // check whether current frag pos is in shadow
	
	float bias = max(0.00025 * (1.0 - dot(outNormal, light)), 0.000025);
	float shadow = 0.0;
	vec2 texelSize = 1.0 / textureSize(shadow_sampler, 0);
	for (int i = 0; i < 69; i++)
	{
		vec3 shadowWeight = shadowWeights[i];
		float pcfDepth = texture(shadow_sampler, projCoords.xy + shadowWeight.xy * texelSize).r;
		if (currentDepth - bias > pcfDepth) shadow += shadowWeight.z;
	}
	
	
	//for(int x = -5; x <= 5; ++x)
	//{
	//	for(int y = -5; y <= 5; ++y)
	//	{
	//		float pcfDepth = texture(shadow_sampler, projCoords.xy + vec2(x, y) * texelSize).r; 
	//		shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;        
	//	}    
	//}
	//shadow /= 121.0;
	//shadow = shadow == 9.0 ? 1.0 : 0.0;
    //float shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;

    return shadow;
} 

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
	if (!ignore_lighting)
	{
		float dp = dot(normalize(outNormal), light);
		if (dp >= 0.0) fragColor.rgb *= vec3(0.5);
		else if (enable_shadows) fragColor.rgb *= vec3(0.5 + 0.5 * dp * (shadowOp(outLightPos) - 1.0));
		else fragColor.rgb *= vec3(0.5 - 0.5 * dp);
	}
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