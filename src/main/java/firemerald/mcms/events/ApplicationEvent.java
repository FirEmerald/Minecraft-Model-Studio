package firemerald.mcms.events;

public class ApplicationEvent extends Event
{
	public static class PreInitilization extends ApplicationEvent {}
	public static class Initilization extends ApplicationEvent {}
	public static class PostInitilization extends ApplicationEvent {}
}