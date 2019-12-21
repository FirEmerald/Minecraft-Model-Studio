package firemerald.mcms.texture;

public class HSL extends ColorModel
{
	public float h, s, l;
	
	public HSL(float h, float s, float l)
	{
		this.h = h;
		this.s = s;
		this.l = l;
	}

	@Override
	public RGB getRGB()
	{
		float c = (1 - Math.abs(2 * l - 1)) * s;
		float h2 = (h * 6) % 6;
		float x = c * (1 - Math.abs((h2 % 2) - 1));
		float r, g, b;
		if (h2 <= 1)
		{
			r = c;
			g = x;
			b = 0;
		}
		else if (h2 <= 2)
		{
			r = x;
			g = c;
			b = 0;
		}
		else if (h2 <= 3)
		{
			r = 0;
			g = c;
			b = x;
		}
		else if (h2 <= 4)
		{
			r = 0;
			g = x;
			b = c;
		}
		else if (h2 <= 5)
		{
			r = x;
			g = 0;
			b = c;
		}
		else
		{
			r = c;
			g = 0;
			b = x;
		}
		float m = l - .5f * c;
		r += m;
		g += m;
		b += m;
		return new RGB(r, g, b);
	}

	@Override
	public HSV getHSV()
	{
		float v = (2 * l + s * (1 - Math.abs(2 * l - 1))) * .5f;
		float s = 2 * (v - l) / v;
		return new HSV(h, s, v);
	}

	@Override
	public HSL getHSL()
	{
		return this;
	}

	@Override
	public HSL copy()
	{
		return new HSL(h, s, l);
	}
}