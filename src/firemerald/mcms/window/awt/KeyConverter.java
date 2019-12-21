package firemerald.mcms.window.awt;

import static java.awt.event.KeyEvent.*;

import java.util.HashMap;
import java.util.Map;

import firemerald.mcms.window.api.Key;

import static firemerald.mcms.window.api.Key.*;

public class KeyConverter
{
	@SuppressWarnings("unchecked")
	public static final Map<Integer, Key>[] AWT_TO_KEY = new Map[5];
	public static final Map<Key, int[]> KEY_TO_AWT = new HashMap<>();
	
	static void put(int code, int location, Key key)
	{
		AWT_TO_KEY[location].put(code, key);
		KEY_TO_AWT.put(key, new int[] {code, location});
	}
	
	static void putStandard(int code, Key key)
	{
		put(code, KEY_LOCATION_STANDARD, key);
	}
	
	static void putLeft(int code, Key key)
	{
		put(code, KEY_LOCATION_LEFT, key);
	}
	
	static void putRight(int code, Key key)
	{
		put(code, KEY_LOCATION_RIGHT, key);
	}
	
	static void putNumpad(int code, Key key)
	{
		put(code, KEY_LOCATION_NUMPAD, key);
	}
	
	static
	{
		for (int i = 0; i < AWT_TO_KEY.length; i++) AWT_TO_KEY[i] = new HashMap<>();
		putStandard(VK_SPACE, SPACE);
		putStandard(VK_QUOTE, APOSTROPHE);
		putStandard(VK_COMMA, COMMA);
		putStandard(VK_MINUS, MINUS);
		putStandard(VK_PERIOD, PERIOD);
		putStandard(VK_SLASH, SLASH);
		putStandard(VK_0, _0);
		putStandard(VK_1, _1);
		putStandard(VK_2, _2);
		putStandard(VK_3, _3);
		putStandard(VK_4, _4);
		putStandard(VK_5, _5);
		putStandard(VK_6, _6);
		putStandard(VK_7, _7);
		putStandard(VK_8, _8);
		putStandard(VK_9, _9);
		putStandard(VK_SEMICOLON, SEMICOLON);
		putStandard(VK_EQUALS, EQUAL);
		putStandard(VK_A, A);
		putStandard(VK_B, B);
		putStandard(VK_C, C);
		putStandard(VK_D, D);
		putStandard(VK_E, E);
		putStandard(VK_F, F);
		putStandard(VK_G, G);
		putStandard(VK_H, H);
		putStandard(VK_I, I);
		putStandard(VK_J, J);
		putStandard(VK_K, K);
		putStandard(VK_L, L);
		putStandard(VK_M, M);
		putStandard(VK_N, N);
		putStandard(VK_O, O);
		putStandard(VK_P, P);
		putStandard(VK_Q, Q);
		putStandard(VK_R, R);
		putStandard(VK_S, S);
		putStandard(VK_T, T);
		putStandard(VK_U, U);
		putStandard(VK_V, V);
		putStandard(VK_W, W);
		putStandard(VK_X, X);
		putStandard(VK_Y, Y);
		putStandard(VK_Z, Z);
		putStandard(VK_OPEN_BRACKET, LEFT_BRACKET);
		putStandard(VK_BACK_SLASH, BACKSLASH);
		putStandard(VK_CLOSE_BRACKET, RIGHT_BRACKET);
		putStandard(VK_BACK_QUOTE, GRAVE_ACCENT);

		putStandard(VK_ESCAPE, ESCAPE);
		putStandard(VK_ENTER, ENTER);
		putStandard(VK_TAB, TAB);
		putStandard(VK_BACK_SPACE, BACKSPACE);
		putStandard(VK_INSERT, INSERT);
		putStandard(VK_DELETE, DELETE);
		putStandard(VK_RIGHT, RIGHT);
		putStandard(VK_LEFT, LEFT);
		putStandard(VK_DOWN, DOWN);
		putStandard(VK_UP, UP);
		putStandard(VK_PAGE_DOWN, PAGE_UP);
		putStandard(VK_PAGE_UP, PAGE_DOWN);
		putStandard(VK_HOME, HOME);
		putStandard(VK_END, END);
		putStandard(VK_CAPS_LOCK, CAPS_LOCK);
		putStandard(VK_SCROLL_LOCK, SCROLL_LOCK);
		putStandard(VK_NUM_LOCK, NUM_LOCK);
		putStandard(VK_PRINTSCREEN, PRINT_SCREEN);
		putStandard(VK_PAUSE, PAUSE);
		putStandard(VK_F1, F1);
		putStandard(VK_F2, F2);
		putStandard(VK_F3, F3);
		putStandard(VK_F4, F4);
		putStandard(VK_F5, F5);
		putStandard(VK_F6, F6);
		putStandard(VK_F7, F7);
		putStandard(VK_F8, F8);
		putStandard(VK_F9, F9);
		putStandard(VK_F10, F10);
		putStandard(VK_F11, F11);
		putStandard(VK_F12, F12);
		putStandard(VK_F13, F13);
		putStandard(VK_F14, F14);
		putStandard(VK_F15, F15);
		putStandard(VK_F16, F16);
		putStandard(VK_F17, F17);
		putStandard(VK_F18, F18);
		putStandard(VK_F19, F19);
		putStandard(VK_F20, F20);
		putStandard(VK_F21, F21);
		putStandard(VK_F22, F22);
		putStandard(VK_F23, F23);
		putStandard(VK_F24, F24);
		putNumpad(VK_NUMPAD0, KP_0);
		putNumpad(VK_NUMPAD1, KP_1);
		putNumpad(VK_NUMPAD2, KP_2);
		putNumpad(VK_NUMPAD3, KP_3);
		putNumpad(VK_NUMPAD4, KP_4);
		putNumpad(VK_NUMPAD5, KP_5);
		putNumpad(VK_NUMPAD6, KP_6);
		putNumpad(VK_NUMPAD7, KP_7);
		putNumpad(VK_NUMPAD8, KP_8);
		putNumpad(VK_NUMPAD9, KP_9);
		putNumpad(VK_DECIMAL, KP_DECIMAL);
		putNumpad(VK_DIVIDE, KP_DIVIDE);
		putNumpad(VK_MULTIPLY, KP_MULTIPLY);
		putNumpad(VK_SUBTRACT, KP_SUBTRACT);
		putNumpad(VK_ADD, KP_ADD);
		putNumpad(VK_ENTER, KP_ENTER);
		putNumpad(VK_EQUALS, KP_EQUAL);
		putLeft(VK_SHIFT, LEFT_SHIFT);
		putLeft(VK_CONTROL, LEFT_CONTROL);
		putLeft(VK_ALT, LEFT_ALT);
		putLeft(VK_WINDOWS, LEFT_SUPER);
		putRight(VK_SHIFT, RIGHT_SHIFT);
		putRight(VK_CONTROL, RIGHT_CONTROL);
		putRight(VK_ALT, RIGHT_ALT);
		putRight(VK_WINDOWS, RIGHT_SUPER);
		putStandard(VK_CONTEXT_MENU, MENU);
	}
	
	public static Key getKey(int code, int location)
	{
		if (location < 0 || location >= AWT_TO_KEY.length) return UNKNOWN;
		else
		{
			Key val;
			if ((val = AWT_TO_KEY[location].get(code)) != null) return val;
			else return UNKNOWN;
		}
	}
	
	public static int[] getAWT(Key key)
	{
		return KEY_TO_AWT.get(key);
	}
}