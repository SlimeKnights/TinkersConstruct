package slimeknights.tconstruct.library.recipe.modifiers.adding;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe.modifiersForResult;
import static slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe.withModifiers;

/** Shared logic between modifier and incremental modifier recipes */
public abstract class AbstractModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  /** Error for when the tool has does not have enough existing levels of this modifier, has a single parameter, modifier with level */
  protected static final String KEY_MIN_LEVEL = TConstruct.makeTranslationKey("recipe", "modifier.min_level");
  protected static final String KEY_MIN_LEVEL_TRAITS = KEY_MIN_LEVEL + ".traits";
  /** Error for when the tool is at the max modifier level */
  protected static final String KEY_MAX_LEVEL = TConstruct.makeTranslationKey("recipe", "modifier.max_level");
  protected static final String KEY_MAX_LEVEL_TRAITS = KEY_MAX_LEVEL + ".traits";
  /** Error for when the tool has too few upgrade slots */
  protected static final String KEY_NOT_ENOUGH_SLOTS = TConstruct.makeTranslationKey("recipe", "modifier.not_enough_slots");
  /** Error for when the tool has too few upgrade slots from a single slot */
  protected static final String KEY_NOT_ENOUGH_SLOT = TConstruct.makeTranslationKey("recipe", "modifier.not_enough_slot");

  /* Fields */
  protected static final LoadableField<Ingredient,AbstractModifierRecipe> TOOLS_FIELD = IngredientLoadable.DISALLOW_EMPTY.requiredField("tools", r -> r.toolRequirement);
  protected static final LoadableField<Integer,AbstractModifierRecipe> MAX_TOOL_SIZE_FIELD = IntLoadable.FROM_ONE.defaultField("max_tool_size", ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE, r -> r.maxToolSize);
  protected static final LoadableField<ModifierId,AbstractModifierRecipe> RESULT_FIELD = ModifierId.PARSER.requiredField("result", r -> r.result.getId());
  protected static final LoadableField<IntRange,AbstractModifierRecipe> LEVEL_FIELD = ModifierEntry.VALID_LEVEL.defaultField("level", r -> r.level);
  protected static final LoadableField<SlotCount,AbstractModifierRecipe> SLOTS_FIELD = SlotCount.LOADABLE.nullableField("slots", r -> r.slots);
  protected static final LoadableField<Boolean,AbstractModifierRecipe> ALLOW_CRYSTAL_FIELD = BooleanLoadable.INSTANCE.defaultField("allow_crystal", true, r -> r.allowCrystal);
  protected static final LoadableField<Boolean,AbstractModifierRecipe> CHECK_TRAIT_LEVEL_FIELD = BooleanLoadable.INSTANCE.defaultField("check_trait_level", false, false, r -> r.checkTraitLevel);


  @Getter
  private final ResourceLocation id;
  /** Ingredient representing the required tool, typically a tag */
  protected final Ingredient toolRequirement;
  /** Max size of the tool for this modifier. If the tool size is smaller, the stack will reduce by less */
  protected final int maxToolSize;
  /** Modifier this recipe is adding */
  protected final LazyModifier result;
  /** Range of result levels that is valid on the tool */
  @Getter
  private final IntRange level;
  /** Gets the slots required by this recipe. If null, no slots required */
  @Getter
  @Nullable
  private final SlotCount slots;
  /** If true, this recipe can be applied using modifier crystals */
  protected final boolean allowCrystal;
  /** If true, validates the level against the trait level. False validates against recipe modifiers only. */
  protected final boolean checkTraitLevel;

  protected AbstractModifierRecipe(ResourceLocation id, Ingredient toolRequirement, int maxToolSize,
                                   ModifierId result, IntRange level, @Nullable SlotCount slots, boolean allowCrystal, boolean checkTraitLevel) {
    this.id = id;
    this.toolRequirement = toolRequirement;
    this.maxToolSize = maxToolSize;
    this.result = new LazyModifier(result);
    this.level = level;
    this.slots = slots;
    this.allowCrystal = allowCrystal;
    this.checkTraitLevel = checkTraitLevel;
    ModifierRecipeLookup.addRecipeModifier(SlotCount.type(slots), this.result);
  }

  @Override
  public abstract RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access);

  @Override
  public int shrinkToolSlotBy() {
    return maxToolSize;
  }

  /* JEI display */
  /** Cache of input items shared between result and input */
  @Nullable
  private List<ItemStack> toolInputs = null;

  /** Gets or builds the list of tool inputs */
  protected List<ItemStack> getToolInputs() {
    if (toolInputs == null) {
      toolInputs = Arrays.stream(this.toolRequirement.getItems()).map(stack -> {
        if (stack.getItem() instanceof IModifiableDisplay) {
          return ((IModifiableDisplay)stack.getItem()).getRenderTool();
        }
        return stack;
      }).collect(Collectors.toList());
    }
    return toolInputs;
  }

  /** Cache of display tool inputs */
  private List<ItemStack> displayInputs = null;

  /** Cache of display output */
  List<ItemStack> toolWithModifier = null;

  /** Display result, may be a higher level than real result */
  private ModifierEntry displayResult;

  @Override
  public ModifierEntry getDisplayResult() {
    if (displayResult == null) {
      // display result is just the min level result, means when a recipe is for Luck II, it displays as Luck II
      displayResult = new ModifierEntry(result, this.level.min());
    }
    return displayResult;
  }

  @Override
  public List<ItemStack> getToolWithoutModifier() {
    if (displayInputs == null) {
      int min = level.min() - 1;
      ModifierEntry existing = min > 0 ? new ModifierEntry(result, min) : null;
      ModifierEntry displayResult = getDisplayResult();
      displayInputs = getToolInputs().stream().map(stack -> withModifiers(stack, modifiersForResult(displayResult, existing))).collect(Collectors.toList());
    }
    return displayInputs;
  }

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      ModifierEntry result = getDisplayResult();
      toolWithModifier = getToolInputs().stream().map(stack -> withModifiers(stack, modifiersForResult(result, result))).collect(Collectors.toList());
    }
    return toolWithModifier;
  }


  /* Helpers */

  /** Checks if the inventory contains a crystal */
  public static boolean matchesCrystal(ITinkerStationContainer container, ModifierId match) {
    boolean found = false;
    for (int i = 0; i < container.getInputCount(); i++) {
      ItemStack stack = container.getInput(i);
      if (!stack.isEmpty()) {
        // cannot have two stacks
        // must be a crystal
        if (found || !stack.is(TinkerModifiers.modifierCrystal.asItem())) {
          return false;
        }
        // found a crystal, make sure we have enough and the ID matches
        ModifierId modifier = ModifierCrystalItem.getModifier(stack);
        if (!match.equals(modifier)) {
          return false;
        }
        found = true;
      }
    }
    return found;
  }

  /** Checks if the inventory contains a crystal */
  protected boolean matchesCrystal(ITinkerStationContainer container) {
    return allowCrystal && matchesCrystal(container, result.getId());
  }

  /** Validates that the given level is a valid result */
  @Nullable
  protected Component validateLevel(int resultLevel) {
    if (resultLevel < this.level.min()) {
      return Component.translatable(checkTraitLevel ? KEY_MIN_LEVEL_TRAITS : KEY_MIN_LEVEL, result.get().getDisplayName(this.level.min() - 1));
    }
    // max level of modifier
    if (resultLevel > this.level.max()) {
      return Component.translatable(checkTraitLevel ? KEY_MAX_LEVEL_TRAITS : KEY_MAX_LEVEL, result.get().getDisplayName(), this.level.max());
    }
    return null;
  }

  /**
   * Validate tool has the right number of slots, called internally by {@link #validatePrerequisites(IToolStackView, int)}
   * @param tool   Tool instance
   * @param slots  Required slots
   * @return  Error message, or null if no error
   */
  @Nullable
  protected static Component checkSlots(IToolStackView tool, @Nullable SlotCount slots) {
    if (slots != null) {
      int count = slots.count();
      if (tool.getFreeSlots(slots.type()) < count) {
        if (count == 1) {
          return Component.translatable(KEY_NOT_ENOUGH_SLOT, slots.type().getDisplayName());
        } else {
          return Component.translatable(KEY_NOT_ENOUGH_SLOTS, count, slots.type().getDisplayName());
        }
      }
    }
    return null;
  }

  /**
   * Validates that this tool has a resulting level in the range and has enough modifier slots
   * @param tool    Tool stack instance
   * @param resultLevel  Level after adding this modifier
   * @return  Error message, or null if no error
   */
  @Nullable
  protected Component validatePrerequisites(IToolStackView tool, int resultLevel) {
    Component error = validateLevel(resultLevel);
    if (error != null) {
      return error;
    }
    return checkSlots(tool, slots);
  }

  /**
   * Validates that this tool has a resulting level in the range and has enough modifier slots
   * @param tool    Tool stack instance
   * @return  Error message, or null if no error
   */
  @Nullable
  protected Component validatePrerequisites(IToolStackView tool) {
    return validatePrerequisites(tool, (checkTraitLevel ? tool.getModifiers() : tool.getUpgrades()).getLevel(result.getId()) + 1);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '{' + id + '}';
  }
}
