package slimeknights.tconstruct.library.json.variable;

/** Functional inferface mapping an object to a float. Used in simple variables. */
@FunctionalInterface
public interface ToFloatFunction<T> {
  /** Gets a float from the given value */
  float apply(T value);
}
