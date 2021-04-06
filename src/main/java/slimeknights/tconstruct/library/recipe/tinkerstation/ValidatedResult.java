package slimeknights.tconstruct.library.recipe.tinkerstation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * This class represents the result of a tinker station recipe, which is one of:
 * <ul>
 *   <li>Success: returns an item stack result</li>
 *   <li>Failure: error state displaying an error message</li>
 *   <li>Pass: acts like a non-recipe match</li>
 * </ul>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatedResult {
  /**
   * Result for when this recipe is not craftable, but has no message to display
   */
  public static final ValidatedResult PASS = new ValidatedResult(false);

  /**
   * Result containing the item result
   * @param result  Item result of this recipe
   * @return  Validation result with the given message
   */
  public static ValidatedResult success(ItemStack result) {
    return new ValidatedResult.Success(result);
  }

  /**
   * Result for when this recipe is not craftable and has an error message
   * @param translationKey  Error message translation key
   * @param params          Arguments to format into the translation key
   * @return  Validation result with the given message
   */
  public static ValidatedResult failure(String translationKey, Object... params) {
    return new ValidatedResult.Failure(translationKey, params);
  }

  /** If true, this recipe passed and can be crafted for the given input */
  @Getter
  private final boolean success;

  /**
   * Gets the item result
   * @return  Item result
   */
  public ItemStack getResult() {
    return ItemStack.EMPTY;
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
  public ITextComponent getMessage() {
    throw new UnsupportedOperationException("Cannot show error message on success");
  }

  /** Class for success, which has an item stack */
  private static class Success extends ValidatedResult {
    @Getter
    private final ItemStack result;

    private Success(ItemStack result) {
      super(true);
      this.result = result;
    }
  }

  /** Class for failure, which has a message */
  private static class Failure extends ValidatedResult {
    @Getter
    private final ITextComponent message;

    private Failure(String translationKey, Object[] params) {
      super(false);
      this.message = new TranslationTextComponent(translationKey, params);
    }

    @Override
    public boolean hasError() {
      return true;
    }
  }
}
