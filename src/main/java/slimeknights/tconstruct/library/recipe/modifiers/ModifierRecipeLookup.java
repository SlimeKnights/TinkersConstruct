package slimeknights.tconstruct.library.recipe.modifiers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Logic to check various modifier recipe based properties */
public class ModifierRecipeLookup {
  /** Key of default error message, in case an error message for a modifier requirement is missing */
  public static final String DEFAULT_ERROR_KEY = TConstruct.makeTranslationKey("recipe", "modifier.requirements_error");
  /** Default requirements error, for if a proper error is missing */
  public static final ValidatedResult DEFAULT_ERROR = ValidatedResult.failure(ModifierRecipeLookup.DEFAULT_ERROR_KEY);

  /** Map of requirements for each modifier */
  private static final Multimap<ModifierId,ModifierRequirements> REQUIREMENTS = HashMultimap.create();
  /** Map of the number needed for each incremental modifier */
  private static final Object2IntMap<ModifierId> INCREMENTAL_PER_LEVEL = new Object2IntOpenHashMap<>();
  /** Map of salvage recipes for each modifier */
  private static final Multimap<ModifierId,ModifierSalvage> SALVAGE = HashMultimap.create();

  /** Map of slot type to modifiers added via that slot, better for fetching lists */
  private static final Multimap<SlotType,LazyModifier> RECIPE_MODIFIERS = HashMultimap.create();
  /** Map of slot type to modifier IDs added via that slot, better for lookup */
  private static final Multimap<SlotType,ModifierId> RECIPE_MODIFIER_IDS = HashMultimap.create();
  /** List of modifiers to show in JEI */
  private static List<ModifierEntry> RECIPE_MODIFIER_LIST = null;

  /** Listener for clearing the caches on recipe reload */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    REQUIREMENTS.clear();
    INCREMENTAL_PER_LEVEL.clear();
    SALVAGE.clear();
    RECIPE_MODIFIERS.clear();
    RECIPE_MODIFIER_IDS.clear();
    RECIPE_MODIFIER_LIST = null;
  });


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
      ModifierId modifier = entry.getId();
      addRequirements(new ModifierRequirements(ingredient, modifier, requirements.getMinLevel(modifier) + entry.getLevel(), requirements, error));
    }
  }

  /** Gets the requirements for the given modifier */
  public static Collection<ModifierRequirements> getRequirements(ModifierId modifier) {
    return REQUIREMENTS.get(modifier);
  }

  /**
   * Validates that the tool meets all requirements. Typically called when modifiers are removed, but should be able to be called at any time after modifiers change.
   * @param stack  ItemStack containing the tool. Most of the time its just a tag check, so the correct item with any NBT is valid.
   *               However, if addons do really hacky things the actual stack corresponding to {@code tool} might matter.
   * @param tool   Tool instance to check
   */
  public static ValidatedResult checkRequirements(ItemStack stack, IToolStackView tool) {
    List<ModifierEntry> modifiers = tool.getModifierList();
    for (ModifierEntry entry : tool.getUpgrades().getModifiers()) {
      for (ModifierRequirements requirements : getRequirements(entry.getId())) {
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
  public static void setNeededPerLevel(ModifierId modifier, int neededPerLevel) {
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
  public static int getNeededPerLevel(ModifierId modifier) {
    return INCREMENTAL_PER_LEVEL.getOrDefault(modifier, 0);
  }


  /* Salvage */

  /**
   * Stores a salvage recipe
   * @param salvage  Salvage recipe
   */
  public static void addSalvage(ModifierSalvage salvage) {
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
  public static ModifierSalvage getSalvage(ItemStack stack, IToolStackView tool, ModifierId modifier, int modifierLevel) {
    for (ModifierSalvage salvage : SALVAGE.get(modifier)) {
      if (salvage.matches(stack, tool, modifierLevel)) {
        return salvage;
      }
    }
    return null;
  }


  /* Recipe modifiers */
  
  /**
   * Adds a modifier requirement, typically called by the recipe
   * @param slotType  Slot type for the modifier, use null for slotless
   * @param modifier  Modifier in that slot
   */
  public static void addRecipeModifier(@Nullable SlotType slotType, LazyModifier modifier) {
    LISTENER.checkClear();
    RECIPE_MODIFIERS.put(slotType, modifier);
    RECIPE_MODIFIER_IDS.put(slotType, modifier.getId());
  }

  /** Gets a stream of all modifiers obtainable via recipes */
  public static Stream<Modifier> getAllRecipeModifiers() {
    return RECIPE_MODIFIERS.values().stream().map(LazyModifier::get).distinct();
  }

  /** Gets a list of modifier entries for display in JEI, basically the same as creating your own, but the result is cached */
  public static List<ModifierEntry> getRecipeModifierList() {
    if (RECIPE_MODIFIER_LIST == null) {
      RECIPE_MODIFIER_LIST = RECIPE_MODIFIERS.values().stream().distinct().sorted(Comparator.comparing(LazyModifier::getId)).map(mod -> new ModifierEntry(mod, 1)).toList();
    }
    return RECIPE_MODIFIER_LIST;
  }

  /** Gets a stream of all modifiers obtainable via the given slot type */
  public static Stream<Modifier> getRecipeModifiers(@Nullable SlotType slotType) {
    return RECIPE_MODIFIERS.get(slotType).stream().map(LazyModifier::get);
  }

  /** Checks if the given modifier ID is a recipe modifier */
  public static boolean isRecipeModifier(ModifierId modifier) {
    return RECIPE_MODIFIER_IDS.containsValue(modifier);
  }

  /** Checks if the given modifier ID is a recipe modifier */
  public static boolean isRecipeModifier(@Nullable SlotType slotType, ModifierId modifier) {
    return RECIPE_MODIFIER_IDS.containsEntry(slotType, modifier);
  }
}
