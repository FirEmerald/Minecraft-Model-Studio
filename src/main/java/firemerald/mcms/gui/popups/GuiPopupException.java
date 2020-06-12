package firemerald.mcms.gui.popups;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.gui.decoration.DecoText;
import firemerald.mcms.texture.Color;
import firemerald.mcms.util.font.FormattedText;

public class GuiPopupException extends GuiPopup
{
	public final DecoPane pane;
	public final DecoText message;
	public final StandardButton ok;
	public final int ch;
	
	private GuiPopupException(String message)
	{
		this(message, (Throwable) null);
	}
	
	private GuiPopupException(FormattedText message)
	{
		this(message, (Throwable) null);
	}
	
	private GuiPopupException(Throwable e)
	{
		this(e, "ok");
	}
	
	private GuiPopupException(String message, Throwable e)
	{
		this(message, e, "ok");
	}
	
	private GuiPopupException(FormattedText message, Throwable e)
	{
		this(message, e, "ok");
	}
	
	private GuiPopupException(String message, String button)
	{
		this(message, (Throwable) null, button);
	}
	
	private GuiPopupException(FormattedText message, String button)
	{
		this(message, (Throwable) null, button);
	}
	
	private GuiPopupException(Throwable e, String button)
	{
		this((FormattedText) null, e, "ok");
	}
	
	private GuiPopupException(String message, Throwable e, String button)
	{
		this(FormattedText.parse(message, Main.instance.fontMsg, Main.instance.getTheme().getTextColor(), false, false), e, button);
	}
	
	private GuiPopupException(FormattedText message, Throwable e, String button)
	{
		if (e != null) message = parse(e, message, new StackTraceElement[0], true);
		final int cw = 640;
		message = message.splitForWidth(cw - 40);
		ch = 80 + message.getNumLines() * Main.instance.fontMsg.height;
		this.addElement(pane = new DecoPane(0, 0, 320, 160, 2, 16));
		this.addElement(this.message = new DecoText(20, 20, 300, 100, message));
		this.addElement(ok = new StandardButton(0, 0, 80, 20, 1, 4, button, this::deactivate));
	}
	
	private FormattedText parse(Throwable e, FormattedText message, StackTraceElement[] prevTrace, boolean first)
	{
		if (message != null)
		{
			if (first) message = message.append("\n");
		}
		else message = new FormattedText("", getTheme().getTextColor(), Main.instance.fontMsg);
		//message = message.append(e.getClass().getName() + ": ").append(e.getLocalizedMessage(), Color.RED);
		message = message.append(e.toString(), Color.RED);
		StackTraceElement[] traces = e.getStackTrace();
		int j = traces.length - 1;
		for (StackTraceElement trace : traces)
		{
			if (j < prevTrace.length && trace.equals(prevTrace[prevTrace.length - 1 - j]))
			{
				message = message.append("\n\t... " + (j + 1) + " more", getTheme().getTextColor());
				break;
			}
			else message = message.append("\n\tat " + trace.toString(), getTheme().getTextColor());
			j--;
		}
		Throwable[] suppressed = e.getSuppressed();
		if (suppressed.length > 0)
		{
			message = message.append("\nSuppressed {", getTheme().getTextColor());
			for (Throwable err : suppressed) parse(err, message, traces, true);
			message = message.append("\n}", getTheme().getTextColor());
		}
		Throwable err = e.getCause();
		if (err != null)
		{
			message = message.append("\nCaused by: ", getTheme().getTextColor());
			message = parse(err, message, traces, false);
		}
		return message;
	}
	
	public static void onException(String message)
	{
		onException(message, (Throwable) null);
	}
	
	public static void onException(FormattedText message)
	{
		onException(message, (Throwable) null);
	}
	
	public static void onException(Throwable e)
	{
		onException(e, "ok");
	}
	
	public static void onException(String message, Throwable e)
	{
		onException(message, e, "ok");
	}
	
	public static void onException(FormattedText message, Throwable e)
	{
		onException(message, e, "ok");
	}
	
	public static void onException(String message, String button)
	{
		onException(message, (Throwable) null, button);
	}
	
	public static void onException(FormattedText message, String button)
	{
		onException(message, (Throwable) null, button);
	}
	
	public static void onException(Throwable e, String button)
	{
		onException((FormattedText) null, e, "ok");
	}
	
	public static void onException(String message, Throwable e, String button)
	{
		onException(message, e, button, Level.WARN);
	}
	
	public static void onException(FormattedText message, Throwable e, String button)
	{
		onException(message, e, button, Level.WARN);
	}
	
	public static void onException(String message, Level level)
	{
		onException(message, (Throwable) null, level);
	}
	
	public static void onException(FormattedText message, Level level)
	{
		onException(message, (Throwable) null, level);
	}
	
	public static void onException(Throwable e, Level level)
	{
		onException(e, "ok", level);
	}
	
	public static void onException(String message, Throwable e, Level level)
	{
		onException(message, e, "ok", level);
	}
	
	public static void onException(FormattedText message, Throwable e, Level level)
	{
		onException(message, e, "ok", level);
	}
	
	public static void onException(String message, String button, Level level)
	{
		onException(message, (Throwable) null, button, level);
	}
	
	public static void onException(FormattedText message, String button, Level level)
	{
		onException(message, (Throwable) null, button, level);
	}
	
	public static void onException(Throwable e, String button, Level level)
	{
		onException((FormattedText) null, e, "ok", level);
	}
	
	public static void onException(String message, Throwable e, String button, Level level)
	{
		onException(FormattedText.parse(message, Main.instance.fontMsg, Main.instance.getTheme().getTextColor(), false, false), e, button, level);
	}
	
	public static void onException(FormattedText message, Throwable e, String button, Level level)
	{
		if (message == null) Main.LOGGER.log(level, e);
		else if (e == null) Main.LOGGER.log(level, message.getPlainText());
		else Main.LOGGER.log(level, message.getPlainText(), e);
		new GuiPopupException(message, e, button).activate();
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 640;
		final int cx = w / 2;
		final int sx = (w - cw) / 2;
		final int sy = (h - ch) / 2;
		pane.setSize(sx, sy, sx + cw, sy + ch);
		message.setSize(sx + 20, sy + 20, sx + cw - 20, sy + ch - 60);
		ok.setSize(cx - 40, sy + ch - 40, cx + 40, sy + ch - 20);
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.shader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.shader.setColor(1, 1, 1, 1);
	}
}