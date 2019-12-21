package firemerald.mcms.window.api;

public abstract class Window
{
	public abstract boolean isClosed();
	
	public abstract void close();
	
	public abstract void showWindow();
	
	public abstract void hideWindow();
	
	public abstract int getDisplayW();
	
	public abstract int getDisplayH();
	
	public abstract int getX();
	
	public abstract int getY();
	
	public abstract void setPosition(int x, int y);
	
	public abstract int getW();
	
	public abstract int getH();
	
	public abstract void setSize(int w, int h);
	
	public abstract boolean isMaximized();
	
	public abstract void setMaximized(boolean maximized);
	
	public abstract void setTitle(String title);
	
	public abstract void setCursor(Cursor cursor);
	
	public abstract void tick(long thisTick);
	
	public abstract void render();
	
	public abstract boolean isKeyDown(Key key);
	
	public abstract boolean isMouseDown(int button);
}