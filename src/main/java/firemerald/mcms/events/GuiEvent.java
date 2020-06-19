package firemerald.mcms.events;

import firemerald.mcms.gui.GuiScreen;

public abstract class GuiEvent extends Event
{
	protected GuiScreen gui;
	
	public GuiEvent(GuiScreen gui)
	{
		this.gui = gui;
	}
	
	public GuiScreen getGui()
	{
		return gui;
	}
	
	public static class Open extends GuiEvent
	{
		public Open(GuiScreen gui)
		{
			super(gui);
		}
		
		public void setScreen(GuiScreen gui)
		{
			this.gui = gui;
		}
	}
	
	public static class Init extends GuiEvent
	{
		final int width, height;
		
		public Init(GuiScreen gui, int w, int h)
		{
			super(gui);
			this.width = w;
			this.height = h;
		}
	}
	
	public static class Resize extends GuiEvent
	{
		final int width, height;
		
		public Resize(GuiScreen gui, int w, int h)
		{
			super(gui);
			this.width = w;
			this.height = h;
		}
	}
	
	public static class Close extends GuiEvent
	{
		public Close(GuiScreen gui)
		{
			super(gui);
		}
	}
}