package slimeknights.tconstruct.library.recipe.modifiers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.salvage.AbstractModifierSalvage;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Logic to check various modifier recipe based properties */
public class ModifierRecipeLookup {
  /** Key of default error message, in case an error message for a modifier requirement is missing */
  public static final String DEFAULT_ERROR_KEY = TConstruct.makeTranslationKey("recipe", "modifier.requirements_error");
  /** Default requirements error, for if a proper error is missing */
  public static final ValidatedResult DEFAULT_ERROR = ValidatedResult.failure(ModifierRecipeLookup.DEFAULT_ERROR_KEY);

  /** Set of all modifier input items for the chest */
  private static final Set<Item> MODIFIERS = new HashSet<>();

  /** Map of requirements for each modifier */
  private static final Multimap<Modifier,ModifierRequirements> REQUIREMENTS = HashMultimap.create();
  /** Map of the number needed for each incremental modifier */
  private static final Object2IntMap<Modifier> INCREMENTAL_PER_LEVEL = new Object2IntOpenHashMap<>();
  /** Map of salvage recipes for each modifier */
  private static final Multimap<Modifier, AbstractModifierSalvage> SALVAGE = HashMultimap.create();

  /** Listener for clearing the caches on recipe reload */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    MODIFIERS.clear();
    REQUIREMENTS.clear();
    INCREMENTAL_PER_LEVEL.clear();
    SALVAGE.clear();
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
   * @deprecated  No longer needed in base tinkers. If you have a use of this method, justify it and this will be kept
   */
  @Deprecated
  public static boolean isModifier(Item item) {
    return MODIFIERS.contains(item);
  }


  /* Requirements */

  /**
   * Adds a modifier requirement, typically called by the recipe
   * @param requirements  Requirements object
   */
  public static void addRequirements(ModifierRequirements requirements) {
    LISTENER.checkClear();
    REQUIREMENTS.put(requirements.getModifier(), requirements);
  }

  /**
   * Adds a modifier requirement, typically called by the recipe
   * @param ingredient    Ingredient that must match the tool for this to be attempted
   * @param entry         Modifier to check, level determines amount added per level
   * @param requirements  Actual requirements to attempt
   * @param errorMessage  Error to display if the requirements fail
   */
  public static void addRequirements(Ingredient ingredient, ModifierEntry entry, ModifierMatch requirements, String errorMessage) {
    if (requirements != ModifierMatch.ALWAYS) {
      // if the key is empty, use the default
      ValidatedResult error;
      if (errorMessage.isEmpty()) {
        error = DEFAULT_ERROR;
      } else {
        error = ValidatedResult.failure(errorMessage);
      }
      Modifier modifier = entry.getModifier();
      addRequirements(new ModifierRequirements(ingredient, modifier, requirements.getMinLevel(modifier) + entry.getLevel(), requirements, error));
    }
  }

  /**
   * Validates that the tool meets all requirements. Typically called when modifiers are removed, but should be able to be called at any time after modifiers change.
   * @param stack  ItemStack containing the tool. Most of the time its just a tag check, so the correct item with any NBT is valid.
   *               However, if addons do really hacky things the actual stack corresponding to {@code tool} might matter.
   * @param tool   Tool instance to check
   */
  public static ValidatedResult checkRequirements(ItemStack stack, IModifierToolStack tool) {
    List<ModifierEntry> modifiers = tool.getModifierList();
    for (ModifierEntry entry : tool.getUpgrades().getModifiers()) {
      for (ModifierRequirements requirements : REQUIREMENTS.get(entry.getModifier())) {
        ValidatedResult result = requirements.check(stack, entry.getLevel(), modifiers);
        if (result.hasError()) {
          return result;
        }
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
        TConstruct.LOG.warn("Inconsistent amount needed per level for {}, originally {}, newest {}, keeping largest", modifier, original, neededPerLevel);
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


  /* Salvage */

  /**
   * Stores a salvage recipe
   * @param salvage  Salvage recipe
   */
  public static void addSalvage(AbstractModifierSalvage salvage) {
    LISTENER.checkClear();
    SALVAGE.put(salvage.getModifier(), salvage);
  }

  /**
   * Gets a salvage recipe
   * @param tool            Tool stack, primarily used for tag checks, but may do weird things
   * @param modifier        Modifier instance
   * @param modifierLevel   Modifier level
   * @return  Salvage recipe, or null if no salvage is found
   */
  @Nullable
  public static AbstractModifierSalvage getSalvage(ItemStack stack, IModifierToolStack tool, Modifier modifier, int modifierLevel) {
    for (AbstractModifierSalvage salvage : SALVAGE.get(modifier)) {
      if (salvage.matches(stack, tool, modifierLevel)) {
        return salvage;
      }
    }
    return null;
  }
}
