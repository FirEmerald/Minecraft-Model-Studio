package firemerald.mcms.api.util;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    R apply(final T p0, final U p1, final V p2);
    
    default <W> TriFunction<T, U, V, W> andThen(final Function<? super R, ? extends W> function)
    {
        Objects.requireNonNull(function);
        return (o, o2, o3) -> function.apply(this.apply(o, o2, o3));
    }
}