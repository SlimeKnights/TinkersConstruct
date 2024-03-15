package slimeknights.tconstruct.library.recipe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * This class represents the result of a recipe, which is one of:
 * <ul>
 *   <li>Success: returns the generic result</li>
 *   <li>Failure: error state displaying an error message</li>
 *   <li>Pass: acts like a non-match recipe</li>
 * </ul>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeResult<T> {
  /** Single pass instance */
  @SuppressWarnings("rawtypes")
  private static final RecipeResult<?> PASS = new RecipeResult(false);

  /**
   * Result for when this recipe is not craftable, but has no message to display
   */
  @SuppressWarnings("unchecked")
  public static <T> RecipeResult<T> pass() {
    return (RecipeResult<T>)PASS;
  }

  /**
   * Result containing the item result
   * @param result  Result of this recipe
   * @return  Validation result with the given item, or pass if null
   */
  public static <T> RecipeResult<T> success(@Nullable T result) {
    if (result == null) {
      return pass();
    }
    return new Success<>(result);
  }

  /**
   * Result for when this recipe is not craftable and has an error message
   * @param component  Error message
   * @return  Validation result with the given message
   */
  public static <T> RecipeResult<T> failure(Component component) {
    return new Failure<>(component);
  }

  /**
   * Result for when this recipe is not craftable and has an error message
   * @param translationKey  Error message translation key
   * @param params          Arguments to format into the translation key
   * @return  Validation result with the given message
   */
  public static <T> RecipeResult<T> failure(String translationKey, Object... params) {
    return failure(Component.translatable(translationKey, params));
  }

  /** If true, this recipe passed and can be crafted for the given input */
  @Getter
  private final boolean success;

  /**
   * Gets the result, throws if unavailable
   * @return  Result
   */
  public T getResult() {
    throw new UnsupportedOperationException("Cannot get result on failure");
  }

  /**
   * If true, this recipe failed with an error message. This message should be displayed on screen
   * @return  true if the recipe failed with an error message
   */
  public boolean hasError() {
    return false;
  }

  /**
   * Returns the message for this result
   * @return  result message
   * @throws UnsupportedOperationException  if this result is success or pass
   */
  public Component getMessage() {
    throw new UnsupportedOperationException("Cannot show error message on success");
  }

  /** Class for success, which has an item stack */
  private static class Success<T> extends RecipeResult<T> {
    @Getter
    private final T result;

    private Success(T result) {
      super(true);
      this.result = result;
    }
  }

  /** Class for failure, which has a message */
  private static class Failure<T> extends RecipeResult<T> {
    @Getter
    private final Component message;

    private Failure(Component message) {
      super(false);
      this.message = message;
    }

    @Override
    public boolean hasError() {
      return true;
    }
  }
}
