package firemerald.mcms.window.glfw;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

import firemerald.mcms.window.api.Key;
import static firemerald.mcms.window.api.Key.*;

public class KeyConverter
{
	static final Map<Integer, Key> GLFW_TO_KEY = new HashMap<>();
	static final Map<Key, Integer> KEY_TO_GLFW = new HashMap<>();
	
	static void put(Integer code, Key key)
	{
		GLFW_TO_KEY.put(code, key);
		KEY_TO_GLFW.put(key, code);
	}
	
	static
	{
		put(GLFW_KEY_SPACE, SPACE);
		put(GLFW_KEY_APOSTROPHE, APOSTROPHE);
		put(GLFW_KEY_COMMA, COMMA);
		put(GLFW_KEY_MINUS, MINUS);
		put(GLFW_KEY_PERIOD, PERIOD);
		put(GLFW_KEY_SLASH, SLASH);
		put(GLFW_KEY_0, _0);
		put(GLFW_KEY_1, _1);
		put(GLFW_KEY_2, _2);
		put(GLFW_KEY_3, _3);
		put(GLFW_KEY_4, _4);
		put(GLFW_KEY_5, _5);
		put(GLFW_KEY_6, _6);
		put(GLFW_KEY_7, _7);
		put(GLFW_KEY_8, _8);
		put(GLFW_KEY_9, _9);
		put(GLFW_KEY_SEMICOLON, SEMICOLON);
		put(GLFW_KEY_EQUAL, EQUAL);
		put(GLFW_KEY_A, A);
		put(GLFW_KEY_B, B);
		put(GLFW_KEY_C, C);
		put(GLFW_KEY_D, D);
		put(GLFW_KEY_E, E);
		put(GLFW_KEY_F, F);
		put(GLFW_KEY_G, G);
		put(GLFW_KEY_H, H);
		put(GLFW_KEY_I, I);
		put(GLFW_KEY_J, J);
		put(GLFW_KEY_K, K);
		put(GLFW_KEY_L, L);
		put(GLFW_KEY_M, M);
		put(GLFW_KEY_N, N);
		put(GLFW_KEY_O, O);
		put(GLFW_KEY_P, P);
		put(GLFW_KEY_Q, Q);
		put(GLFW_KEY_R, R);
		put(GLFW_KEY_S, S);
		put(GLFW_KEY_T, T);
		put(GLFW_KEY_U, U);
		put(GLFW_KEY_V, V);
		put(GLFW_KEY_W, W);
		put(GLFW_KEY_X, X);
		put(GLFW_KEY_Y, Y);
		put(GLFW_KEY_Z, Z);
		put(GLFW_KEY_LEFT_BRACKET, LEFT_BRACKET);
		put(GLFW_KEY_BACKSLASH, BACKSLASH);
		put(GLFW_KEY_RIGHT_BRACKET, RIGHT_BRACKET);
		put(GLFW_KEY_GRAVE_ACCENT, GRAVE_ACCENT);
		put(GLFW_KEY_WORLD_1, WORLD_1);
		put(GLFW_KEY_WORLD_2, WORLD_2);

		put(GLFW_KEY_ESCAPE, ESCAPE);
		put(GLFW_KEY_ENTER, ENTER);
		put(GLFW_KEY_TAB, TAB);
		put(GLFW_KEY_BACKSPACE, BACKSPACE);
		put(GLFW_KEY_INSERT, INSERT);
		put(GLFW_KEY_DELETE, DELETE);
		put(GLFW_KEY_RIGHT, RIGHT);
		put(GLFW_KEY_LEFT, LEFT);
		put(GLFW_KEY_DOWN, DOWN);
		put(GLFW_KEY_UP, UP);
		put(GLFW_KEY_PAGE_UP, PAGE_UP);
		put(GLFW_KEY_PAGE_DOWN, PAGE_DOWN);
		put(GLFW_KEY_HOME, HOME);
		put(GLFW_KEY_END, END);
		put(GLFW_KEY_CAPS_LOCK, CAPS_LOCK);
		put(GLFW_KEY_SCROLL_LOCK, SCROLL_LOCK);
		put(GLFW_KEY_NUM_LOCK, NUM_LOCK);
		put(GLFW_KEY_PRINT_SCREEN, PRINT_SCREEN);
		put(GLFW_KEY_PAUSE, PAUSE);
		put(GLFW_KEY_F1, F1);
		put(GLFW_KEY_F2, F2);
		put(GLFW_KEY_F3, F3);
		put(GLFW_KEY_F4, F4);
		put(GLFW_KEY_F5, F5);
		put(GLFW_KEY_F6, F6);
		put(GLFW_KEY_F7, F7);
		put(GLFW_KEY_F8, F8);
		put(GLFW_KEY_F9, F9);
		put(GLFW_KEY_F10, F10);
		put(GLFW_KEY_F11, F11);
		put(GLFW_KEY_F12, F12);
		put(GLFW_KEY_F13, F13);
		put(GLFW_KEY_F14, F14);
		put(GLFW_KEY_F15, F15);
		put(GLFW_KEY_F16, F16);
		put(GLFW_KEY_F17, F17);
		put(GLFW_KEY_F18, F18);
		put(GLFW_KEY_F19, F19);
		put(GLFW_KEY_F20, F20);
		put(GLFW_KEY_F21, F21);
		put(GLFW_KEY_F22, F22);
		put(GLFW_KEY_F23, F23);
		put(GLFW_KEY_F24, F24);
		put(GLFW_KEY_F25, F25);
		put(GLFW_KEY_KP_0, KP_0);
		put(GLFW_KEY_KP_1, KP_1);
		put(GLFW_KEY_KP_2, KP_2);
		put(GLFW_KEY_KP_3, KP_3);
		put(GLFW_KEY_KP_4, KP_4);
		put(GLFW_KEY_KP_5, KP_5);
		put(GLFW_KEY_KP_6, KP_6);
		put(GLFW_KEY_KP_7, KP_7);
		put(GLFW_KEY_KP_8, KP_8);
		put(GLFW_KEY_KP_9, KP_9);
		put(GLFW_KEY_KP_DECIMAL, KP_DECIMAL);
		put(GLFW_KEY_KP_DIVIDE, KP_DIVIDE);
		put(GLFW_KEY_KP_MULTIPLY, KP_MULTIPLY);
		put(GLFW_KEY_KP_SUBTRACT, KP_SUBTRACT);
		put(GLFW_KEY_KP_ADD, KP_ADD);
		put(GLFW_KEY_KP_ENTER, KP_ENTER);
		put(GLFW_KEY_KP_EQUAL, KP_EQUAL);
		put(GLFW_KEY_LEFT_SHIFT, LEFT_SHIFT);
		put(GLFW_KEY_LEFT_CONTROL, LEFT_CONTROL);
		put(GLFW_KEY_LEFT_ALT, LEFT_ALT);
		put(GLFW_KEY_LEFT_SUPER, LEFT_SUPER);
		put(GLFW_KEY_RIGHT_SHIFT, RIGHT_SHIFT);
		put(GLFW_KEY_RIGHT_CONTROL, RIGHT_CONTROL);
		put(GLFW_KEY_RIGHT_ALT, RIGHT_ALT);
		put(GLFW_KEY_RIGHT_SUPER, RIGHT_SUPER);
		put(GLFW_KEY_MENU, MENU);
	}
	
	public static Key getKey(int code)
	{
		Key key;
		if ((key = GLFW_TO_KEY.get(code)) != null) return key;
		else return UNKNOWN;
	}
	
	public static Integer getGLFW(Key key)
	{
		return KEY_TO_GLFW.get(key);
	}
}