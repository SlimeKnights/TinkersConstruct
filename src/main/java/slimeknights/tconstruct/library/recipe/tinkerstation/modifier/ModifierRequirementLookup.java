package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.mojang.datafixers.util.Pair;
import slimeknights.tconstruct.common.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.RecipeCacheInvalidator.DuelSidedListener;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to keep track of requirements for modifiers
 */
public class ModifierRequirementLookup {
  /** Key of default error message, in case an error message is missing */
  public static final String DEFAULT_ERROR_KEY = Util.makeTranslationKey("recipe", "modifier.requirements_error");

  /** Map of requirements for each modifier */
  private static final Map<Modifier,Pair<ModifierMatch,String>> REQUIREMENTS = new HashMap<>();

  /** Listener for clearing the recipe cache on recipe reload */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(REQUIREMENTS::clear);

  /**
   * Adds a modifier requirement. Typically called by the recipe
   * @param modifier  Modifier
   * @param match     Requirement
   * @param error     Translation key for error message
   */
  public static void addRequirement(Modifier modifier, ModifierMatch match, String error) {
    LISTENER.checkClear();
    if (error.isEmpty()) {
      error = DEFAULT_ERROR_KEY;
    }
    REQUIREMENTS.put(modifier, Pair.of(match, error));
  }

  /**
   * Validates the given list of upgrades
   * @param upgrades   List to validate
   * @param modifiers  total list of modifiers
   * @return  Validated result. Pass if no error, failure if error
   */
  public static ValidatedResult checkRequirements(List<ModifierEntry> upgrades, List<ModifierEntry> modifiers) {
    for (ModifierEntry entry : upgrades) {
      Pair<ModifierMatch,String> pair = REQUIREMENTS.get(entry.getModifier());
      if (pair != null && !pair.getFirst().test(modifiers)) {
        return ValidatedResult.failure(pair.getSecond());
      }
    }
    return ValidatedResult.PASS;
  }
}
