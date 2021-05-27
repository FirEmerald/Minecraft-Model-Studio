#version 330

//CREDITS FOR COMPLEX FRESNEL EQUATION
//Unless otherwise mentioned, all shader code in this space that is provided by Chaos Software is licensed under the MIT license. All other trademarks and copyrights belong to their respective owners as indicated on the individual shader pages.
//The MIT License (MIT)
//Copyright © 2020 Chaos Software
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
uniform vec3 light;
uniform vec4 color;
uniform vec4 color2;
uniform bool use_overlay;
uniform bool ignore_lighting;
uniform bool enable_shadows;
uniform bool enable_normal;
uniform bool enable_specular;

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

vec3 fresnel(vec3 n, vec3 k, float c)
{
    vec3 k2 = k * k;
	vec3 t = n - c;
    vec3 num = t * t + k2;
	t = n + c;
    vec3 rs = num / (t * t + k2);
	t = (n * n + k2) * c * c + 1.0; //t = n^2+k^2+1
    vec3 rp = (t - 2.0 * n * c) / (t + 2.0 * n * c); //(n^2+k^2+1-2n)/(n^2+k^2+1+2n)
    return clamp(0.5 * (rs + rp), 0.0, 1.0);
}

void main()
{
	vec4 albedo = texture(texture_sampler, outTexCoord) * outColor * color * color2;
	vec4 diffuse = albedo;
	vec4 specular = vec4(0.0);
	vec4 emmisssion = vec4(0.0);
	float ambient = 0.25;
	if (!ignore_lighting)
	{
		vec4 normalTex = enable_normal ? texture(normal_sampler, outTexCoord) : vec4(0.5, 0.5, 1.0, 1.0);
		vec4 specularTex = enable_specular ? texture(specular_sampler, outTexCoord) : vec4(0.0, 0.0, 0.0, 1.0);
		vec3 normal;
		if (enable_normal)
		{
			vec2 tn = normalTex.rg * 2.0 - 1.0;
			normal = normalize(outTBN * vec3(tn, sqrt(1.0 - dot(tn, tn))));
		}
		else normal = normalize(outNormal);
		float dp = dot(normal, light);
		if (dp >= 0.0) diffuse.rgb *= vec3(0.5);
		else if (enable_shadows) diffuse.rgb *= vec3(0.5 + 0.5 * dp * (shadowOp(outLightPos) - 1.0));
		else diffuse.rgb *= vec3(0.5 - 0.5 * dp);
		float roughness = pow(1.0 - specularTex.r, 2.0);
		vec3 fres;
		if (specularTex.g < 229.5 / 255.0 || specularTex.g >= 237.5 / 255.0)
		{
			vec3 f0 = specularTex.g < 229.5 / 255.0 ? vec3(specularTex.g) : albedo.rgb;
		}
		else if (specularTex.g < 230.5 / 255.0)
		{
			fres = fresnel(vec3(2.9114, 2.9497, 2.5845), vec3(3.0893, 2.9318, 2.7670), abs(dp));
		}
		else if (specularTex.g < 231.5 / 255.0)
		{
			fres = fresnel(vec3(0.18299, 0.42108, 1.3734), vec3(3.4242, 2.3459, 1.7704), abs(dp));
		}
		else if (specularTex.g < 232.5 / 255.0)
		{
			fres = fresnel(vec3(1.3456, 0.96521, 0.61722), vec3(7.4746, 6.3995, 5.3031), abs(dp));
		}
		else if (specularTex.g < 233.5 / 255.0)
		{
			fres = fresnel(vec3(3.1071, 3.1812, 2.3230), vec3(3.3314, 3.3291, 3.1350), abs(dp));
		}
		else if (specularTex.g < 234.5 / 255.0)
		{
			fres = fresnel(vec3(0.27105, 0.67693, 1.3164), vec3(3.6092, 2.6248, 2.2921), abs(dp));
		}
		else if (specularTex.g < 235.5 / 255.0)
		{
			fres = fresnel(vec3(1.9100, 1.8300, 1.4400), vec3(3.5100, 3.4000, 3.1800), abs(dp));
		}
		else if (specularTex.g < 236.5 / 255.0)
		{
			fres = fresnel(vec3(2.3757, 2.0847, 1.8453), vec3(4.2655, 3.7153, 3.1365), abs(dp));
		}
		else if (specularTex.g < 237.5 / 255.0)
		{
			fres = fresnel(vec3(0.15943, 0.14512, 0.13547), vec3(3.9291, 3.1900, 2.3808), abs(dp));
		}
	}
	fragColor = diffuse + vec4(specular.rgb * specular.a + ambient, 0.0);
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