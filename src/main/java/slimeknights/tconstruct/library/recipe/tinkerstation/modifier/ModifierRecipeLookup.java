package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Logic to check various modifier recipe based properties */
public class ModifierRecipeLookup {
  /** Key of default error message, in case an error message for a modifier requirement is missing */
  public static final String DEFAULT_ERROR_KEY = Util.makeTranslationKey("recipe", "modifier.requirements_error");

  /** Set of all modifier input items for the chest */
  private static final Set<Item> MODIFIERS = new HashSet<>();

  /** Map of requirements for each modifier */
  private static final Map<Modifier,Pair<ModifierMatch,String>> REQUIREMENTS = new HashMap<>();

  private static final Object2IntMap<Modifier> INCREMENTAL_PER_LEVEL = new Object2IntOpenHashMap<>();

  /** Listener for clearing the caches on recipe reload */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    MODIFIERS.clear();
    REQUIREMENTS.clear();
    INCREMENTAL_PER_LEVEL.clear();
  });


  /* Modifier item */

  /**
   * Adds an item as a modifier
   * @param item  Item
   */
  public static void addItem(Item item) {
    LISTENER.checkClear();
    MODIFIERS.add(item);
  }

  /**
   * Adds an ingredient as a modifier
   */
  public static void addIngredient(Ingredient ingredient) {
    LISTENER.checkClear();
    // this should work on both client and server
    // server just pulls from the tag, client does not use tags directly at this stage
    for (ItemStack stack : ingredient.getMatchingStacks()) {
      MODIFIERS.add(stack.getItem());
    }
  }

  /**
   * Adds a sized ingredient as a modifier
   */
  public static void addIngredient(SizedIngredient ingredient) {
    LISTENER.checkClear();
    // this should work on both client and server
    // server just pulls from the tag, client does not use tags directly at this stage
    for (ItemStack stack : ingredient.getMatchingStacks()) {
      MODIFIERS.add(stack.getItem());
    }
  }

  /**
   * Checks if an item is a modifier
   * @param item  Item to check
   * @return  True if its a modifier
   */
  public static boolean isModifier(Item item) {
    return MODIFIERS.contains(item);
  }


  /* Requirements */

  /**
   * Adds a modifier requirement. Typically called by the recipe
   * @param modifier  Modifier
   * @param match     Requirement
   * @param error     Translation key for error message
   */
  public static void addRequirement(Modifier modifier, ModifierMatch match, String error) {
    LISTENER.checkClear();

    // if this recipe depends on the same modifier as the output, may cause inconsistencies in the requirements check
    // main reason this is true is when each level of a modifier requires a different item, in that case the requirement is just for internal calculations
    if (match != ModifierMatch.ALWAYS && match.getMinLevel(modifier) == 0) {
      if (error.isEmpty()) {
        error = DEFAULT_ERROR_KEY;
      }
      REQUIREMENTS.put(modifier, Pair.of(match, error));
    }
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


  /* Incremental modifiers */

  /**
   * Sets the amount needed per level for an incremental modifier
   * @param modifier        Modifier
   * @param neededPerLevel  Amount needed per level
   */
  public static void setNeededPerLevel(Modifier modifier, int neededPerLevel) {
    if (INCREMENTAL_PER_LEVEL.containsKey(modifier)) {
      int original = INCREMENTAL_PER_LEVEL.getInt(modifier);
      if (original != neededPerLevel) {
        TConstruct.log.warn("Inconsistent amount needed per level for {}, originally {}, newest {}, keeping largest", modifier, original, neededPerLevel);
      }
      // keep largest as that will make it most accurate towards the larger recipe
      if (neededPerLevel > original) {
        INCREMENTAL_PER_LEVEL.put(modifier, neededPerLevel);
      }
    } else {
      INCREMENTAL_PER_LEVEL.put(modifier, neededPerLevel);
    }
  }

  /**
   * Gets the amount needed per level for an incremental modifier
   * @param modifier  Modifier
   * @return  Amount needed per level
   */
  public static int getNeededPerLevel(Modifier modifier) {
    return INCREMENTAL_PER_LEVEL.getOrDefault(modifier, 0);
  }
}
