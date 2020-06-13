package firemerald.mcms.gui.decoration;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.util.ClipboardUtil;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.font.FontRenderer;
import firemerald.mcms.util.font.FormattedText;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;
import firemerald.mcms.window.api.MouseButtons;

public class DecoText extends Component
{
	protected FormattedText originalText = new FormattedText("", Main.instance.fontMsg);
	protected FormattedText splitText;
	protected String[] text = new String[0];
	public int clickPos = 0, clickLine = 0;
	public int selStart = 0, selStartLine = 0, selEnd = 0, selEndLine = 0;
	protected float selX1, selX2;
	public float clickTime = 0;
	public int clickNum = 0;
	public boolean clickFlag = false;
	public FontRenderer font;
	/*
	 * sel1line, sel2line
	 */
	
	public DecoText(int x1, int y1, int x2, int y2, String text)
	{
		this(x1, y1, x2, y2, FormattedText.parse(text, Main.instance.fontMsg, Main.instance.getTheme().getTextColor(), false, false));
	}
	
	public DecoText(int x1, int y1, int x2, int y2, FormattedText text)
	{
		this(x1, y1, x2, y2);
		setText(text);
	}
	
	public DecoText(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
		this.font = Main.instance.fontMsg;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		setText(this.originalText);
		updatePos();
	}
	
	protected void updatePos()
	{
		selX1 = selStartLine < text.length ? x1 + font.getStringWidth(text[selStartLine].substring(0, selStart)) : 0;
		selX2 = selEndLine < text.length ? x1 + font.getStringWidth(text[selEndLine].substring(0, selEnd)) : 0;
		clickNum = 0;
		clickTime = 0;
	}
	
	protected void updatePos2()
	{
		selX1 = selStartLine < text.length ? x1 + font.getStringWidth(text[selStartLine].substring(0, selStart)) : 0;
		selX2 = selEndLine < text.length ? x1 + font.getStringWidth(text[selEndLine].substring(0, selEnd)) : 0;
	}
	
	public void setText(FormattedText text)
	{
		this.font = text.font;
		this.originalText = text;
		this.splitText = text.copy().splitForWidth(x2 - x1); //TODO fix
		this.text = splitText.getPlainText().split("\n");
		selStart = selStartLine = selEnd = selEndLine = 0;
		clickPos = clickLine = 0;
		clickFlag = true;
	}
	
	public FormattedText getText()
	{
		return originalText;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		//OffY = 3
		if (button == MouseButtons.LEFT && text.length > 0)
		{
			clickFlag = false;
			int[] pos = getTextPosition(mx, my);
			int clicked = pos[0];
			int clickedLine = pos[1];
			if (clicked == clickPos && clickedLine == clickLine && clickTime > 0)
			{
				if (clickNum == 0)
				{
					selStart = getBeforePreviousSymbol(clicked, clickedLine);
					selEnd = getNextSymbol(clicked, clickedLine);
					clickLine = selStartLine = selEndLine = clickedLine;
					updatePos2();
					clickNum = 1;
				}
				else if (clickNum == 1)
				{
					selStart = 0;
					selStartLine = 0;
					selEnd = text[selEndLine = (text.length - 1)].length();
					updatePos2();
					clickNum = 2;
				}
				else
				{
					clickPos = selStart = selEnd = clicked;
					clickLine = selStartLine = selEndLine = clickedLine;
					updatePos();
				}
			}
			else
			{
				clickPos = selStart = selEnd = clicked;
				clickLine = selStartLine = selEndLine = clickedLine;
				updatePos();
			}
			clickTime = .5f;
		}
	}
	
	public int getBeforePreviousSymbol(int pos, int line)
	{
		String text = this.text[line];
		if (pos <= 0) return 0;
		else if (pos >= text.length()) return text.length();
		else
		{
			for (int i = pos; i >= 0; i--) if (!Character.isLetterOrDigit(text.charAt(i))) return i + 1;
			return 0;
		}
	}
	
	public int getNextSymbol(int pos, int line)
	{
		String text = this.text[line];
		if (pos >= text.length()) return text.length();
		else
		{
			for (int i = pos; i < text.length(); i++) if (!Character.isLetterOrDigit(text.charAt(i))) return i;
			return text.length();
		}
	}
	
	public int[] getTextPosition(float mx, float my)
	{
		int clickedLine = (int) Math.floor((my - (y1 + 3)) / font.height);
		int clicked;
		if (clickedLine < 0) //above text
		{
			clickedLine = 0;
			clicked = 0;
		}
		else if (clickedLine < text.length)
		{
			clicked = font.getSelectedPos(text[clickedLine], (int) (mx - x1));
		}
		else //below text
		{
			clickedLine = text.length - 1;
			clicked = text[clickedLine].length();
		}
		return new int[] {clicked, clickedLine};
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT)
		{
			int[] pos = getTextPosition(mx, my);
			if (clickFlag)
			{
				clickPos = selStart = selEnd = pos[0];
				clickLine = selStartLine = selEndLine = pos[1];
			}
			else if (pos[1] < clickLine || (pos[1] == clickLine && pos[0] < clickPos))
			{
				selStart = pos[0];
				selStartLine = pos[1];
				selEnd = clickPos;
				selEndLine = clickLine;
				clickTime = 0;
			}
			else if (pos[1] > clickLine || (pos[1] == clickLine && pos[0] > clickPos))
			{
				selStart = clickPos;
				selStartLine = clickLine;
				selEnd = pos[0];
				selEndLine = pos[1];
				clickTime = 0;
			}
			else
			{
				selStart = selEnd = pos[0];
				selStartLine = selEndLine = pos[1];
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
		Shader s = Main.instance.shader;
		TextureManager texs = Main.instance.textureManager;
		float y = y1 + 3;
		FontRenderer.drawTextFormatted(splitText, x1, y1, false, false);
		texs.unbindTexture();
		if (selStartLine != selEndLine)
		{
			s.setColor(0, 0, 1, .5f);
			y = y1 + 3 + font.height * selStartLine;
			Main.MODMESH.setMesh(selX1, y, x2, y + font.height, 0, 0, 0, 1, 1);
			Main.MODMESH.render();
			for (int i = selStartLine + 1; i < selEndLine; i++)
			{
				y += font.height;
				Main.MODMESH.setMesh(x1, y, x2, y + font.height, 0, 0, 0, 1, 1);
				Main.MODMESH.render();
			}
			y += font.height;
			Main.MODMESH.setMesh(x1, y, selX2, y + font.height, 0, 0, 0, 1, 1);
			Main.MODMESH.render();
		}
		else if (selStart != selEnd)
		{
			s.setColor(0, 0, 1, .5f);
			y = y1 + 3 + font.height * selStartLine;
			Main.MODMESH.setMesh(selX1, y, selX2, y + font.height, 0, 0, 0, 1, 1);
			Main.MODMESH.render();
		}
		s.setColor(1, 1, 1, 1);
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
		if (selStart != selEnd) ClipboardUtil.setString(getSelected());
	}
	
	public String getSelected()
	{
		if (text.length == 0) return "";
		else if (selStartLine != selEndLine)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(text[selStartLine].substring(selStart));
			if (selStartLine < selEndLine)
			{
				 builder.append("\n");
				 for (int i = selStartLine + 1; i < selEndLine; i++)
				 {
					 builder.append(text[i]);
					 if (selEnd > 0 || i < selEndLine - 1) builder.append("\n");
				 }
				 builder.append(text[selEndLine].substring(0, selEnd));
			}
			return builder.toString();
		}
		else return text[selStartLine].substring(selStart, selEnd);
	}
	
	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		selStartLine = selEndLine = selStart = selEnd = 0;
	}

	@Override
	public Cursor getCursor(float mx, float my)
	{
		return Cursor.TEXT;
	}
}