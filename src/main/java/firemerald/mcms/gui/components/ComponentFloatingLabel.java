package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.util.ClipboardUtil;
import firemerald.mcms.util.EnumTextAlignment;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.font.FontRenderer;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;
import firemerald.mcms.window.api.MouseButtons;

public class ComponentFloatingLabel extends Component
{
	protected String text = "";
	public int clickPos = 0;
	public int selStart = 0, selEnd = 0;
	public FontRenderer font;
	protected float selX1, selX2;
	public float clickTime = 0;
	public int clickNum = 0;
	private EnumTextAlignment alignment = EnumTextAlignment.LEFT;
	private int offset = 0;
	
	public ComponentFloatingLabel(int x1, int y1, int x2, int y2, FontRenderer font, String text, EnumTextAlignment alignment)
	{
		this(x1, y1, x2, y2, font);
		this.alignment = alignment;
		setText(text);
	}
	
	public ComponentFloatingLabel(int x1, int y1, int x2, int y2, FontRenderer font, String text)
	{
		this(x1, y1, x2, y2, font, text, EnumTextAlignment.LEFT);
	}
	
	public ComponentFloatingLabel(int x1, int y1, int x2, int y2, FontRenderer font)
	{
		super(x1, y1, x2, y2);
		this.font = font;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		updateOffset();
		updatePos();
	}
	
	protected void updateOffset()
	{
		switch (alignment)
		{
		case CENTER:
			offset = ((x2 - x1) - font.getStringWidth(text)) / 2;
			break;
		case RIGHT:
			offset = (x2 - x1) - font.getStringWidth(text) - 2;
			break;
		case LEFT:
		default:
			offset = 2;
			break;
		}
	}
	
	protected void updatePos()
	{
		selX1 = x1 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + font.getStringWidth(text.substring(0, selEnd));
		clickNum = 0;
		clickTime = 0;
	}
	
	protected void updatePos2()
	{
		selX1 = x1 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + font.getStringWidth(text.substring(0, selEnd));
	}
	
	public void setText(String text)
	{
		this.text = text;
		selStart = selEnd = 0;
		updateOffset();
	}
	
	public String getText()
	{
		return text;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		mx -= offset;
		if (button == MouseButtons.LEFT)
		{
			int clicked = font.getSelectedPos(text, (int) (mx - x1));
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
	public void onDrag(float mx, float my, int button)
	{
		mx -= offset;
		if (button == MouseButtons.LEFT)
		{
			int pos = font.getSelectedPos(text, (int) (mx - x1));
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
	}

	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		if (clickTime > 0 && (clickTime -= deltaTime) < 0) clickTime = 0;
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		this.setScissor(0, 0, x2 - x1, y2 - y1);
		GuiShader s = Main.instance.guiShader;
		font.drawTextLine(text, x1 + offset, y2 - (5 + font.height), getTheme().getTextColor(), false, false, false);
		Main.instance.textureManager.unbindTexture();
		if (selStart != selEnd)
		{
			s.setColor(0, 0, 1, .5f);
			Main.guiTempMesh.setMesh(selX1 + offset, y1 + 2, selX2 + offset, y2 - 2, 0, 0, 1, 1);
			Main.guiTempMesh.render();
		}
		s.setColor(1, 1, 1, 1);
		RenderUtil.popScissor();
	}

	@Override
	public void onCharTyped(char chr) {}

	@Override
	public boolean onKeyPressed(Key key, int scancode, int mods)
	{
		return onKey(key, scancode, mods);
	}
	
	public boolean onKey(Key key, int scancode, int mods)
	{
		switch (key)
		{
		case C:
		case X:
			if ((mods & Modifier.CONTROL.flag) > 0)
			{
				onCopy();
				return true;
			}
		default:
			return false;
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
	public Cursor getCursor(float mx, float my)
	{
		return Cursor.TEXT;
	}
}