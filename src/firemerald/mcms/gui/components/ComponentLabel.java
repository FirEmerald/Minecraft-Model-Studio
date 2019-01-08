package firemerald.mcms.gui.components;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.theme.RoundedBoxFormat;
import firemerald.mcms.util.ClipboardUtil;
import firemerald.mcms.util.Cursors;
import firemerald.mcms.util.FontRenderer;

public class ComponentLabel extends Component
{
	protected String text = "";
	public int clickPos = 0;
	public int selStart = 0, selEnd = 0;
	public FontRenderer font;
	protected float selX1, selX2;
	public float clickTime = 0;
	public int clickNum = 0;
	protected RoundedBoxFormat rectangle;
	protected final Mesh mesh;
	
	public ComponentLabel(float x1, float y1, float x2, float y2, FontRenderer font, String text)
	{
		this(x1, y1, x2, y2, font);
		setText(text);
	}
	
	public ComponentLabel(float x1, float y1, float x2, float y2, FontRenderer font)
	{
		super(x1, y1, x2, y2);
		this.font = font;
		rectangle = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1));
		mesh = new Mesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		updatePos();
		rectangle = new RoundedBoxFormat((int) (x2 - x1), (int) (y2 - y1));
		mesh.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
	}
	
	protected void updatePos()
	{
		selX1 = x1 + 2 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + 2 + font.getStringWidth(text.substring(0, selEnd));
		clickNum = 0;
		clickTime = 0;
	}
	
	protected void updatePos2()
	{
		selX1 = x1 + 2 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + 2 + font.getStringWidth(text.substring(0, selEnd));
	}
	
	public void setText(String text)
	{
		this.text = text;
		selStart = selEnd = 0;
	}
	
	public String getText()
	{
		return text;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			int clicked = font.getSelectedPos(text, (int) (mx - (x1 + 2)));
			if (clicked == clickPos && clickTime > 0)
			{
				if (clickNum == 0)
				{
					selStart = getBeforePreviousSymbol(clicked);
					selEnd = getNextSymbol(clicked);
					updatePos2();
					clickNum = 1;
				}
				else if (clickNum == 1)
				{
					selStart = 0;
					selEnd = text.length();
					updatePos2();
					clickNum = 2;
				}
				else
				{
					clickPos = selStart = selEnd = clicked;
					updatePos();
				}
			}
			else
			{
				clickPos = selStart = selEnd = clicked;
				updatePos();
			}
			clickTime = .5f;
		}
	}
	
	public int getBeforePreviousSymbol(int pos)
	{
		if (pos <= 0) return 0;
		else if (pos >= text.length()) return text.length();
		else
		{
			for (int i = pos; i >= 0; i--) if (!Character.isLetterOrDigit(text.charAt(i))) return i + 1;
			return 0;
		}
	}
	
	public int getNextSymbol(int pos)
	{
		if (pos >= text.length()) return text.length();
		else
		{
			for (int i = pos; i < text.length(); i++) if (!Character.isLetterOrDigit(text.charAt(i))) return i;
			return text.length();
		}
	}

	@Override
	public void onDrag(float mx, float my)
	{
		int pos = font.getSelectedPos(text, (int) (mx - (x1 + 2)));
		if (pos == clickPos) selStart = selEnd = pos;
		else if (pos < clickPos)
		{
			selStart = pos;
			selEnd = clickPos;
			clickTime = 0;
		}
		else if (pos > clickPos)
		{
			selStart = clickPos;
			selEnd = pos;
			clickTime = 0;
		}
		updatePos();
	}

	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		if (clickTime > 0 && (clickTime -= deltaTime) < 0) clickTime = 0;
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Shader s = Main.instance.shader;
		getTheme().bindRoundedBox(rectangle);
		mesh.render();
		font.drawTextLine(text, x1 + 2, y2 - (5 + font.height), getTheme().getTextColor(), false, false, false);
		Main.instance.textureManager.unbindTexture();
		if (selStart != selEnd)
		{
			s.setColor(0, 0, 1, .5f);
			Main.MODMESH.setMesh(selX1, y1 + 2, selX2, y2 - 2, 0, 0, 0, 1, 1);
			Main.MODMESH.render();
		}
		s.setColor(1, 1, 1, 1);
	}

	@Override
	public void onCharTyped(char chr) {}

	@Override
	public void onKeyPressed(int key, int scancode, int mods)
	{
		onKey(key, scancode, mods);
	}

	@Override
	public void onKeyReleased(int key, int scancode, int mods) {}

	@Override
	public void onKeyRepeat(int key, int scancode, int mods) {}
	
	public void onKey(int key, int scancode, int mods)
	{
		switch (key)
		{
		case GLFW.GLFW_KEY_C:
			if ((mods & GLFW.GLFW_MOD_CONTROL) > 0) onCopy();
			break;
		case GLFW.GLFW_KEY_X:
			if ((mods & GLFW.GLFW_MOD_CONTROL) > 0) onCopy();
			break;
		}
	}
	
	public void onCopy()
	{
		if (selStart != selEnd) ClipboardUtil.setString(text.substring(selStart, selEnd));
	}
	
	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		selStart = selEnd = 0;
	}

	@Override
	public long getCursor(float mx, float my)
	{
		return Cursors.text;
	}
}