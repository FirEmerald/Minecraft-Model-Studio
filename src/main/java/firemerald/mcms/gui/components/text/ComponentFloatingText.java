package firemerald.mcms.gui.components.text;

import java.util.Stack;
import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.ClipboardUtil;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.font.FontRenderer;
import firemerald.mcms.util.history.HistoryAction;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;
import firemerald.mcms.window.api.Modifier;
import firemerald.mcms.window.api.MouseButtons;

public class ComponentFloatingText extends Component
{
	protected String text = "";
	public int pos = 0, clickPos = 0;
	public int selStart = 0, selEnd = 0;
	public FontRenderer font;
	protected float cursorPos = 0;
	protected float selX1, selX2;
	public boolean hasChanged;
	public float cursorTime = 0;
	public float clickTime = 0;
	public int clickNum = 0;
	private final Consumer<String> onTextChange;
	public final Stack<String> undoText = new Stack<>();
	public final Stack<String> redoText = new Stack<>();
	public static final long SETTING_TIME = 500000000; //.5 seconds
	public long lastTextChangeTime = 0;
	private String initialText = "";
	
	public ComponentFloatingText(int x1, int y1, int x2, int y2, FontRenderer font, String text, Consumer<String> onTextChange)
	{
		this(x1, y1, x2, y2, font, onTextChange);
		setTextNoUpdate(text);
	}
	
	public ComponentFloatingText(int x1, int y1, int x2, int y2, FontRenderer font, Consumer<String> onTextChange)
	{
		super(x1, y1, x2, y2);
		this.font = font;
		this.onTextChange = onTextChange;
		setSize(x1, y1, x2, y2);
	}
	
	public boolean shouldUndo()
	{
		return this.onTextChange != null;
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		updatePos2();
	}
	
	public void setTextNoUpdate(String text)
	{
		this.initialText = this.text = text;
		this.undoText.clear();
		this.redoText.clear();
		selStart = selEnd = pos = text.length();
	}
	
	public void setText(String text)
	{
		onTextPreUpdate();
		setTextNoUpdate(text);
		onTextUpdate();
	}
	
	public String getText()
	{
		return text;
	}
	
	protected void updatePos()
	{
		if (pos < 0) pos = 0;
		else if (pos > text.length()) pos = text.length();
		if (selStart < 0) selStart = 0;
		else if (selStart > text.length()) selStart = text.length();
		if (selEnd < 0) selEnd = 0;
		else if (selEnd > text.length()) selEnd = text.length();
		cursorPos = x1 + 2 + font.getStringWidth(text.substring(0, pos));
		selX1 = x1 + 2 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + 2 + font.getStringWidth(text.substring(0, selEnd));
		cursorTime = 0;
		clickNum = 0;
		clickTime = 0;
	}
	
	protected void updatePos2()
	{
		cursorPos = x1 + 2 + font.getStringWidth(text.substring(0, pos));
		selX1 = x1 + 2 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + 2 + font.getStringWidth(text.substring(0, selEnd));
		cursorTime = 0;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (button == MouseButtons.LEFT)
		{
			int clicked = font.getSelectedPos(text, (int) (mx - (x1 + 2)));
			if (clicked == clickPos && clickTime > 0)
			{
				if (clickNum == 0)
				{
					selStart = getBeforePreviousSymbol(clicked);
					selEnd = pos = getNextSymbol(clicked);
					updatePos2();
					clickNum = 1;
				}
				else if (clickNum == 1)
				{
					selStart = 0;
					selEnd = pos = text.length();
					updatePos2();
					clickNum = 2;
				}
				else
				{
					clickPos = selStart = selEnd = pos = clicked;
					updatePos();
				}
			}
			else
			{
				clickPos = selStart = selEnd = pos = clicked;
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
			for (int i = pos - 1; i >= 0; i--)
			{
				char c = text.charAt(i);
				if (!(Character.isLetterOrDigit(c) || c == '_')) return i + 1;
			}
			return 0;
		}
	}
	
	public int getNextSymbol(int pos)
	{
		if (pos >= text.length()) return text.length();
		else
		{
			for (int i = pos; i < text.length(); i++)
			{
				char c = text.charAt(i);
				if (!(Character.isLetterOrDigit(c) || c == '_')) return i;
			}
			return text.length();
		}
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT && isTextFocused())
		{
			pos = font.getSelectedPos(text, (int) (mx - (x1 + 2)));
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

	public void onTextUpdate()
	{
		long time = Main.instance.lastNanos;
		if (time - this.lastTextChangeTime > SETTING_TIME) //add to undo stack
		{
			this.redoText.clear();
			this.undoText.add(text);
		}
		this.lastTextChangeTime = time;
		onTextUpdateAction();
	}
	
	public void onTextUpdateAction()
	{
		if (onTextChange != null) onTextChange.accept(text);
		hasChanged = true;
	}

	public void onTextPreUpdate()
	{
		long time = Main.instance.lastNanos;
		if (time - this.lastTextChangeTime > SETTING_TIME) //add to undo stack
		{
			this.redoText.clear();
			this.undoText.add(text);
		}
		this.lastTextChangeTime = time;
	}

	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		cursorTime = (cursorTime + deltaTime) % 1;
		if (clickTime > 0 && (clickTime -= deltaTime) < 0) clickTime = 0;
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		GuiShader s = Main.instance.guiShader;
		TextureManager texs = Main.instance.textureManager;
		this.setScissor(0, 0, x2 - x1, y2 - y1);
		font.drawTextLine(getDisplayString(), x1 + 2, y2 - (5 + font.height), getTextColor(), false, false, false);
		texs.unbindTexture();
		if (selStart != selEnd)
		{
			s.setColor(0, 0, 1, .5f);
			Main.guiTempMesh.setMesh(selX1, y1 + 2, selX2, y2 - 2, 0, 0, 1, 1);
			Main.guiTempMesh.render();
		}
		if (this.isTextFocused() && cursorTime < .5f)
		{
			s.setColor(getTheme().getTextColor());
			Main.guiTempMesh.setMesh(cursorPos, y1 + 2, cursorPos + 1, y2 - 2, 0, 0, 1, 1);
			Main.guiTempMesh.render();
		}
		s.setColor(1, 1, 1, 1);
		RenderUtil.popScissor();
	}
	
	public String getDisplayString()
	{
		return text;
	}
	
	public Color getTextColor()
	{
		return getTheme().getTextColor();
	}

	@Override
	public void onCharTyped(char chr)
	{
		if (isTextFocused()) if (chr != '\n')
		{
			onTextPreUpdate();
			int pos1, pos2;
			if (selStart != selEnd)
			{
				pos1 = selStart;
				pos2 = selEnd;
				pos = 1 + (selEnd = selStart);
			}
			else
			{
				pos1 = pos2 = pos;
				pos++;
			}
			text = text.substring(0, pos1) + chr + text.substring(pos2);
			selStart = selEnd = pos;
			updatePos();
			onTextUpdate();
		}
	}

	@Override
	public boolean onKeyPressed(Key key, int scancode, int mods)
	{
		if (isTextFocused()) return onKey(key, scancode, mods);
		else return false;
	}

	@Override
	public boolean onKeyRepeat(Key key, int scancode, int mods)
	{
		if (isTextFocused()) return onKey(key, scancode, mods);
		else return false;
	}
	
	public boolean onKey(Key key, int scancode, int mods)
	{
		switch (key)
		{
		case BACKSPACE:
			onBackspace(mods);
			break;
		case DELETE:
			onDelete(mods);
			break;
		case LEFT:
			onLeft(mods);
			break;
		case RIGHT:
			onRight(mods);
			break;
		case HOME:
			onHome(mods);
			break;
		case END:
			onEnd(mods);
			break;
		case C:
			if ((mods & Modifier.CONTROL.flag) > 0) onCopy();
			break;
		case X:
			if ((mods & Modifier.CONTROL.flag) > 0) onCut();
			break;
		case V:
			if ((mods & Modifier.CONTROL.flag) > 0) onPaste();
			break;
		case A:
			if ((mods & Modifier.CONTROL.flag) > 0)
			{
				selStart = 0;
				selEnd = pos = text.length();
				updatePos2();
			}
			break;
		default:
			return false;
		}
		return true;
	}
	
	public void onBackspace(int mods)
	{
		if (selEnd > 0)
		{
			onTextPreUpdate();
			int pos2;
			if (selStart != selEnd)
			{
				pos2 = selEnd;
				pos = selEnd = selStart;
			}
			else
			{
				pos2 = pos;
				pos--;
			}
			text = text.substring(0, pos) + text.substring(pos2);
			selStart = selEnd = pos;
			updatePos();
			onTextUpdate();
		}
	}
	
	public void onDelete(int mods)
	{
		if (selStart < text.length())
		{
			onTextPreUpdate();
			int pos2;
			if (selStart != selEnd)
			{
				pos2 = selEnd;
				pos = selEnd = selStart;
			}
			else
			{
				pos2 = pos + 1;
			}
			text = text.substring(0, pos) + text.substring(pos2);
			selStart = selEnd = pos;
			updatePos();
			onTextUpdate();
		}
	}
	
	/* breaks:
	 * whitespace|non-whitespace
	 * alphanumeric|non-whitespace and non-alphanumeric and not '_'
	 * non-uppercase|uppercase
	 * non-whitespace and non-alphanumeric and not '_'|alphanumeric
	 * '_'|uppercase
	 */
	
	public boolean isBreak(int pos)
	{
		char c1 = text.charAt(pos - 1);
		char c2 = text.charAt(pos);
		return 
				(Character.isWhitespace(c1) && !Character.isWhitespace(c2)) || 
				(!Character.isUpperCase(c1) && Character.isUpperCase(c2)) || 
				(Character.isLetterOrDigit(c1) && !Character.isWhitespace(c2) && !Character.isLetterOrDigit(c2) && c2 != '_') ||
				(!Character.isWhitespace(c1) && !Character.isLetterOrDigit(c1) && c1 != '_' && Character.isLetterOrDigit(c2)) ||
				(c1 == '_' && Character.isUpperCase(c2));
	}
	
	public int getPrevPos(int pos)
	{
		if (pos <= 1) return 0;
		else
		{
			for (int i = pos - 1; i > 0; i--) if (isBreak(i)) return i;
			return 0;
		}
	}
	
	public int getNextPos(int pos)
	{
		if (pos >= text.length() - 1) return text.length();
		else
		{
			for (int i = pos + 1; i < text.length(); i++) if (isBreak(i)) return i;
			return text.length();
		}
	}
	
	public void onLeft(int mods)
	{
		boolean isShift = (mods & Modifier.SHIFT.flag) > 0;
		if (pos > 0)
		{
			int newPos = (mods & Modifier.CONTROL.flag) > 0 ? getPrevPos(pos) : pos - 1;
			if (isShift)
			{
				if (pos <= selStart) selStart = pos = newPos; //<-|   |
				else if (newPos > selStart) selEnd = pos = newPos; //|   <-|
				else //<|-|
				{
					selEnd = selStart;
					selStart = pos = newPos;
				}
			}
			else selStart = selEnd = pos = newPos;
			updatePos();
		}
		else if (!isShift && selStart != selEnd)
		{
			selStart = selEnd = 0;
			updatePos();
		}
	}
	
	public void onRight(int mods)
	{
		boolean isShift = (mods & Modifier.SHIFT.flag) > 0;
		if (pos < text.length())
		{
			int newPos = (mods & Modifier.CONTROL.flag) > 0 ? getNextPos(pos) : pos + 1;
			if (isShift)
			{
				if (pos >= selEnd) selEnd = pos = newPos; //|   |->
				else if (newPos < selEnd) selStart = pos = newPos; //|->   |
				else //|-|>
				{
					selStart = selEnd;
					selEnd = pos = newPos;
				}
			}
			else selStart = selEnd = pos = newPos;
			updatePos();
		}
		else if (!isShift && selStart != selEnd)
		{
			selStart = selEnd = text.length();
			updatePos();
		}
	}
	
	public void onHome(int mods)
	{
		boolean isShift = (mods & Modifier.SHIFT.flag) > 0;
		if (pos > 0)
		{
			if (isShift)
			{
				if (pos == selEnd) selEnd = selStart;
				selStart = pos = 0;
			}
			else selStart = selEnd = pos = 0;
			updatePos();
		}
		else if (!isShift && selStart != selEnd)
		{
			selStart = selEnd = 0;
			updatePos();
		}
	}
	
	public void onEnd(int mods)
	{
		boolean isShift = (mods & Modifier.SHIFT.flag) > 0;
		if (pos < text.length())
		{
			if (isShift)
			{
				if (pos == selStart) selStart = selEnd;
				selEnd = pos = text.length();
			}
			else selStart = selEnd = pos = text.length();
			updatePos();
		}
		else if (!isShift && selStart != selEnd)
		{
			selStart = selEnd = text.length();
			updatePos();
		}
	}
	
	public void onCopy()
	{
		if (selStart != selEnd) ClipboardUtil.setString(text.substring(selStart, selEnd));
	}
	
	public void onCut()
	{
		if (selStart != selEnd)
		{
			onTextPreUpdate();
			ClipboardUtil.setString(text.substring(selStart, selEnd));
			text = text.substring(0, selStart) + text.substring(selEnd);
			pos = selEnd = selStart;
			updatePos();
			onTextUpdate();
		}
	}
	
	public void onPaste()
	{
		String str = ClipboardUtil.getString();
		if (str != null)
		{
			int ind = str.indexOf('\n');
			if (ind >= 0) str = str.substring(0, ind);
			if (str.length() != 0)
			{
				onTextPreUpdate();
				int pos1, pos2;
				if (selStart != selEnd)
				{
					pos1 = selStart;
					pos2 = selEnd;
				}
				else pos1 = pos2 = pos;
				text = text.substring(0, pos1) + str + text.substring(pos2);
				selStart = selEnd = pos = pos1 + str.length();
				updatePos();
				onTextUpdate();
			}
		}
	}
	
	@Override
	public void onFocus()
	{
		super.onFocus();
		this.undoText.clear();
		this.redoText.clear();
	}
	
	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		selStart = selEnd = pos = 0;
		final String endText = this.text;
		if (shouldUndo() && !endText.equals(initialText)) Main.instance.project.onAction(new HistoryAction(getUndo(), getRedo()));
		this.undoText.clear();
		this.redoText.clear();
	}
	
	@Override
	public Cursor getCursor(float mx, float my)
	{
		return Cursor.TEXT;
	}
	
	public boolean isTextFocused()
	{
		return focused;
	}
	

	@Override
	public boolean onHotkey(Action action)
	{
		if (action == Action.UNDO)
		{
			if (!undoText.isEmpty())
			{
				redoText.push(this.text);
				this.lastTextChangeTime = Main.instance.lastNanos;
				String newText = undoText.pop();
				this.text = newText;
				onTextUpdateAction();
				selStart = selEnd = pos = text.length();
				this.updatePos2();
				return true;
			}
			else return false;
		}
		else if (action == Action.REDO)
		{
			if (!redoText.isEmpty())
			{
				undoText.push(this.text);
				this.lastTextChangeTime = Main.instance.lastNanos;
				String newText = redoText.pop();
				this.text = newText;
				onTextUpdateAction();
				selStart = selEnd = pos = text.length();
				this.updatePos2();
				return true;
			}
			else return false;
		}
		else return false;
	}

	public Runnable getUndo()
	{
		final Consumer<String> onTextChange = this.onTextChange;
		final String initialText = this.initialText;
		return () -> {
			if (onTextChange != null) onTextChange.accept(initialText);
		};
	}

	public Runnable getRedo()
	{
		final Consumer<String> onTextChange = this.onTextChange;
		final String text = this.text;
		return () -> {
			if (onTextChange != null) onTextChange.accept(text);
		};
	}
}