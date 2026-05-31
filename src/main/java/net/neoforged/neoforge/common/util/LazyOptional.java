package slimeknights.tconstruct.compat.neoforged.neoforge.common.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Compatibility shim for older Forge capability code while the runtime paths are ported to NeoForge capabilities.
 */
public class LazyOptional<T> {
  private static final LazyOptional<?> EMPTY = new LazyOptional<>(null);
  private final Supplier<T> supplier;
  private T value;
  private boolean resolved;

  private LazyOptional(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public static <T> LazyOptional<T> empty() {
    @SuppressWarnings("unchecked")
    LazyOptional<T> empty = (LazyOptional<T>) EMPTY;
    return empty;
  }

  public static <T> LazyOptional<T> of(Supplier<T> supplier) {
    return new LazyOptional<>(supplier);
  }

  public static <T> LazyOptional<T> ofNullable(T value) {
    return value == null ? empty() : of(() -> value);
  }

  public Optional<T> resolve() {
    return Optional.ofNullable(orElse(null));
  }

  public boolean isPresent() {
    return resolve().isPresent();
  }

  public void ifPresent(Consumer<? super T> consumer) {
    resolve().ifPresent(consumer);
  }

  public T orElse(T other) {
    if (!resolved) {
      value = supplier == null ? null : supplier.get();
      resolved = true;
    }
    return value == null ? other : value;
  }

  public T orElseGet(Supplier<? extends T> other) {
    T value = orElse(null);
    return value == null ? other.get() : value;
  }

  public <U> LazyOptional<U> cast() {
    @SuppressWarnings("unchecked")
    LazyOptional<U> cast = (LazyOptional<U>) this;
    return cast;
  }

  public LazyOptional<T> filter(Predicate<? super T> predicate) {
    T value = orElse(null);
    return value != null && predicate.test(value) ? this : empty();
  }

  public <U> LazyOptional<U> map(Function<? super T, ? extends U> mapper) {
    T value = orElse(null);
    return value == null ? empty() : ofNullable(mapper.apply(value));
  }

  public <U> LazyOptional<U> flatMap(Function<? super T, LazyOptional<U>> mapper) {
    T value = orElse(null);
    return value == null ? empty() : mapper.apply(value);
  }

  public void addListener(Consumer<LazyOptional<T>> listener) {}

  public void removeListener(Consumer<LazyOptional<T>> listener) {}

  public void invalidate() {
    value = null;
    resolved = true;
  }
}
