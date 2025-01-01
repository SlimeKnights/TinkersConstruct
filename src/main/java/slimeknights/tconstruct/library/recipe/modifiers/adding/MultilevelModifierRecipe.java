package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.Streams;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Modifier recipe that changes max level and slot behavior each level. Used for a single input recipe that has multiple slot requirements
 */
public class MultilevelModifierRecipe extends ModifierRecipe implements IMultiRecipe<IDisplayModifierRecipe> {
  public static final RecordLoadable<MultilevelModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    SizedIngredient.LOADABLE.list(0).defaultField("inputs", List.of(), r -> r.inputs),
    TOOLS_FIELD, MAX_TOOL_SIZE_FIELD, RESULT_FIELD, ALLOW_CRYSTAL_FIELD,
    LevelEntry.LOADABLE.list(1).requiredField("levels", r -> r.levels),
    CHECK_TRAIT_LEVEL_FIELD,
    MultilevelModifierRecipe::new).validate((recipe, error) -> {
    if (recipe.inputs.isEmpty() && !recipe.allowCrystal) {
      throw error.create("Must either have inputs or allow crystal");
    }
    return recipe;
  });

  private final List<LevelEntry> levels;
  protected MultilevelModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierId result, boolean allowCrystal, List<LevelEntry> levels, boolean checkTraitLevel) {
    super(id, inputs, toolRequirement, maxToolSize, result, levels.get(0).level, levels.get(0).slots(), allowCrystal, checkTraitLevel);
    this.levels = levels;
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable();

    // next few checks depend on the current level to decide
    int newLevel = (checkTraitLevel ? tool.getModifiers() : tool.getUpgrades()).getLevel(result.getId()) + 1;
    LevelEntry levelEntry = null;
    for (LevelEntry check : levels) {
      if (check.matches(newLevel)) {
        levelEntry = check;
        break;
      }
    }
    // no entry means our level is above the max, so done now
    if (levelEntry == null) {
      // if the level is below the minimum, then display a different error
      int min = levels.get(0).level.min();
      if (newLevel < min) {
        return RecipeResult.failure(KEY_MIN_LEVEL, result.get().getDisplayName(min - 1));
      }
      return RecipeResult.failure(KEY_MAX_LEVEL, result.get().getDisplayName(), levels.get(levels.size() - 1).level.max());
    }

    // found our level entry, time to validate slots
    SlotCount slots = levelEntry.slots();
    Component requirements = checkSlots(tool, slots);
    if (requirements != null) {
      return RecipeResult.failure(requirements);
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    if (slots != null) {
      persistentData.addSlots(slots.type(), -slots.count());
    }

    // add modifier
    tool.addModifier(result.getId(), 1);

    // ensure no modifier problems
    Component toolValidation = tool.tryValidate();
    if (toolValidation != null) {
      return RecipeResult.failure(toolValidation);
    }

    return RecipeResult.success(LazyToolStack.from(tool, Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
  }


  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.multilevelModifierSerializer.get();
  }


  /* JEI display */

  private List<IDisplayModifierRecipe> displayRecipes = null;

  @Override
  public List<IDisplayModifierRecipe> getRecipes(RegistryAccess access) {
    // no inputs means this recipe is to handle internal crystal stuff
    if (inputs.isEmpty()) {
      return Collections.emptyList();
    }
    if (displayRecipes == null) {
      // this instance is a proper display recipe for the first level entry, for the rest build display instances with unique requirements keys
      List<ItemStack> toolWithoutModifier = getToolWithoutModifier();
      List<ItemStack> toolWithModifier = getToolWithModifier();
      ModifierEntry result = getDisplayResult();
      displayRecipes = Streams.concat(
        Stream.of(this),
        levels.stream().skip(1).map(levelEntry -> new DisplayModifierRecipe(inputs, toolWithoutModifier, toolWithModifier, result, levelEntry.level, levelEntry.slots))
      ).toList();
    }

    return displayRecipes;
  }

  /** Entry in the levels list */
  record LevelEntry(@Nullable SlotCount slots, IntRange level) {
    public static final RecordLoadable<LevelEntry> LOADABLE = RecordLoadable.create(
      SlotCount.LOADABLE.nullableField("slots", LevelEntry::slots),
      ModifierEntry.VALID_LEVEL.requiredField("level", LevelEntry::level),
      LevelEntry::new);

    /** Checks if this entry matches the given level */
    public boolean matches(int level) {
      return this.level.test(level);
    }
  }
}
