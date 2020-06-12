package firemerald.mcms.util.action;

import java.util.function.Supplier;

@FunctionalInterface
public interface IAction extends Supplier<IAction> {}