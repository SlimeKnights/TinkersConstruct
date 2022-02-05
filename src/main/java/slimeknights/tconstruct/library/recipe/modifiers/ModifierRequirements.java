package slimeknights.tconstruct.library.recipe.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;

import java.util.List;

/** Essentially {@link ModifierMatch} but sensitive to the item and the level as well. Acts as an extraction of requirements from modifier recipes */
@RequiredArgsConstructor
public class ModifierRequirements {
  /** Ingredient that must match the tool for this to be attempted. {@link Ingredient#EMPTY} acts as wildcard */
  private final Ingredient ingredient;
  /** Modifier to check, level must be equal or greater for this to be attempted */
  @Getter
  private final Modifier modifier;
  /** The minimum level for which this requirement applies */
  private final int minLevel;
  /** Actual requirements to attempt */
  private final ModifierMatch requirements;
  /** Error to display if the requirements fail */
  private final ValidatedResult errorMessage;

  /**
   * Checks if the tool matches the requirements. Does not validate that the tool actually has the relevant modifier
   * @param stack           Stack containing the tool, if wrong ingredient this requirements does not matter
   * @param modifierLevel   Level of the modifier to check, if too low this requirements does not matter
   * @param modifiers       List of modifiers on the tool, used for the actual validation
   * @return  PASS if this result is successful or does not apply, failed result if there is an error
   */
  public ValidatedResult check(ItemStack stack, int modifierLevel, List<ModifierEntry> modifiers) {
    // if the level is too small, don't care
    // example: luck in higher levels requires lower levels of modifiers, this allows it to also require another modifier to reach level 2
    if (modifierLevel < minLevel) {
      return ValidatedResult.PASS;
    }
    // if the item does not match, don't care
    // example: dual wielding has different requirements for one handed and two handed tools
    if (ingredient != Ingredient.EMPTY && !ingredient.test(stack)) {
      return ValidatedResult.PASS;
    }
    // actual requirements validation
    if (requirements.test(modifiers)) {
      return ValidatedResult.PASS;
    }
    return errorMessage;
  }
}
