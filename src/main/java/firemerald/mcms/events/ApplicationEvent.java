package firemerald.mcms.events;

public class ApplicationEvent extends Event
{
	public static class PreInitialization extends ApplicationEvent {}
	public static class Initialization extends ApplicationEvent {}
	public static class PostInitialization extends ApplicationEvent {}
}