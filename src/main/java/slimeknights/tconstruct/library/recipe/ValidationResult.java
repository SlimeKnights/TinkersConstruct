package slimeknights.tconstruct.library.recipe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {
  /**
   * Result for when this recipe succeeds and is craftable
   */
  public static final ValidationResult SUCCESS = new ValidationResult(true);
  /**
   * Result for when this recipe is not craftable, but has no message to display
   */
  public static final ValidationResult PASS = new ValidationResult(false);

  /**
   * Result for when this recipe is not craftable and has an error message
   * @param translationKey  Error message translation key
   * @param params          Arguments to format into the translation key
   * @return  Validation result with the given message
   */
  public static ValidationResult failure(String translationKey, Object... params) {
    return new ValidationResult.Failure(translationKey, params);
  }

  /** If true, this recipe passed and can be crafted for the given input */
  @Getter
  private final boolean success;

  /**
   * If true, this recipe failed with an error message. This message should be displayed on screen
   * @return  true if the recipe failed with an error message
   */
  public boolean hasMessage() {
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

  /** Class for failure, which has a message */
  private static class Failure extends ValidationResult {
    @Getter
    private final ITextComponent message;

    private Failure(String translationKey, Object[] params) {
      super(false);
      this.message = new TranslationTextComponent(translationKey, params);
    }

    @Override
    public boolean hasMessage() {
      return true;
    }
  }
}
