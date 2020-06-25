package firemerald.mcms.util;

import java.util.Objects;

@FunctionalInterface
public interface BooleanConsumer
{
	public void accept(boolean value);
	
    default public BooleanConsumer andThen(BooleanConsumer after)
    {
        Objects.requireNonNull(after);
        return value -> {
            this.accept(value);
            after.accept(value);
        };
    }
}