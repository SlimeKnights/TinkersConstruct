package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.Streams;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipe.LevelEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.stream.Stream;

/** Combination of {@link MultilevelModifierRecipe} into {@link IncrementalModifierRecipe}. */
public class MultilevelIncrementalModifierRecipe extends IncrementalModifierRecipe implements IMultiRecipe<IDisplayModifierRecipe> {
  public static final RecordLoadable<MultilevelIncrementalModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), INPUT_FIELD, AMOUNT_FIELD, NEEDED_FIELD,
    TOOLS_FIELD, MAX_TOOL_SIZE_FIELD, RESULT_FIELD, LEFTOVER_FIELD, ALLOW_CRYSTAL_FIELD,
    LevelEntry.LOADABLE.list(1).requiredField("levels", r -> r.levels),
    CHECK_TRAIT_LEVEL_FIELD,
    MultilevelIncrementalModifierRecipe::new);

  private final List<LevelEntry> levels;
  protected MultilevelIncrementalModifierRecipe(ResourceLocation id, Ingredient input, int amountPerInput, int neededPerLevel, Ingredient toolRequirement, int maxToolSize, ModifierId result, ItemOutput leftover, boolean allowCrystal, List<LevelEntry> levels, boolean checkTraitLevel) {
    super(id, input, amountPerInput, neededPerLevel, toolRequirement, maxToolSize, result, levels.get(0).level(), levels.get(0).slots(), leftover, allowCrystal, checkTraitLevel);
    this.levels = levels;
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable();

    // fetch the amount from the modifier, will be 0 if we have a full level
    ModifierId modifier = result.getId();
    boolean crystal = matchesCrystal(inv);
    boolean isNewLevel = tool.getUpgrades().getEntry(modifier).getAmount(0) <= 0;

    // can skip validations if we are not adding a new level
    SlotCount slots = null;
    if (isNewLevel) {
      // next few checks depend on the current level to decide
      int newLevel = getNewLevel(tool);
      LevelEntry levelEntry = LevelEntry.find(levels, newLevel);

      // no entry means our level is above the max, so done now
      if (levelEntry == null) {
        return MultilevelModifierRecipe.missingLevelError(levels, newLevel, result, checkTraitLevel);
      }

      // found our level entry, time to validate slots
      slots = levelEntry.slots();
      Component requirements = checkSlots(tool, slots);
      if (requirements != null) {
        return RecipeResult.failure(requirements);
      }
    }

    // validations pass, time to modify the tool
    tool = tool.copy();

    // if a new level, consume slots now that we copied
    if (slots != null) {
      tool.getPersistentData().addSlots(slots.type(), -slots.count());
    }

    // crystal adds 1 level, does not care about amount
    if (crystal) {
      tool.addModifier(modifier, 1);
    } else {
      // for adding amount, we just use the convenient helper method, which will automatically stop at max
      tool.addModifierAmount(modifier, getAvailableAmount(inv, input, amountPerInput), neededPerLevel);
    }

    // ensure no modifier problems
    Component toolValidation = tool.tryValidate();
    if (toolValidation != null) {
      return RecipeResult.failure(toolValidation);
    }

    // successfully added the modifier
    return success(tool, inv);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.multilevelIncrementalModifierSerializer.get();
  }


  /* JEI display */

  private List<IDisplayModifierRecipe> displayRecipes = null;

  @Override
  public List<IDisplayModifierRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      // this instance is a proper display recipe for the first level entry, for the rest build display instances with unique requirements keys
      DisplayModifierRecipe.Builder builder = DisplayModifierRecipe.builder()
        .id(getId()).inputs(getInputs()).resultSlots(getResultSlots()).incremental()
        .toolWithoutModifier(getToolWithoutModifier()).toolWithModifier(getToolWithModifier());
      displayRecipes = Streams.concat(
        Stream.of(this),
        levels.stream().skip(1).map(levelEntry -> builder.copy()
          .result(new ModifierEntry(result, levelEntry.level().min()))
          .level(levelEntry.level()).slots(levelEntry.slots()).build())
      ).toList();
    }
    return displayRecipes;
  }
}
