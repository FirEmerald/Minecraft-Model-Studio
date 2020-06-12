package firemerald.mcms.util;

import java.util.Objects;

@FunctionalInterface
public interface FloatConsumer
{
	public void accept(float value);
	
    default public FloatConsumer andThen(FloatConsumer after)
    {
        Objects.requireNonNull(after);
        return value -> {
            this.accept(value);
            after.accept(value);
        };
    }
}