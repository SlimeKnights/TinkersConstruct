package slimeknights.tconstruct.library.recipe.modifiers.adding;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe.withModifiers;

/**
 * Recipe to add overslime to a tool
 */
public class OverslimeModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  private static final RecipeResult<LazyToolStack> AT_CAPACITY = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "overslime.at_capacity"));
  public static final RecordLoadable<OverslimeModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.defaultField("tools", Ingredient.of(TinkerTags.Items.DURABILITY), true, r -> r.tools),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", r -> r.ingredient),
    IntLoadable.FROM_ONE.requiredField("restore_amount", r -> r.restoreAmount),
    OverslimeModifierRecipe::new);

  @Getter
  private final ResourceLocation id;
  private final Ingredient tools;
  private final Ingredient ingredient;
  private final int restoreAmount;

  @Internal
  protected OverslimeModifierRecipe(ResourceLocation id, Ingredient tools, Ingredient ingredient, int restoreAmount) {
    this.id = id;
    this.tools = tools;
    this.ingredient = ingredient;
    this.restoreAmount = restoreAmount;
    ModifierRecipeLookup.addRecipeModifier(null, TinkerModifiers.overslime);
  }

  /** @deprecated use {@link #OverslimeModifierRecipe(ResourceLocation, Ingredient, Ingredient, int)} */
  @Deprecated(forRemoval = true)
  public OverslimeModifierRecipe(ResourceLocation id, Ingredient ingredient, int restoreAmount) {
    this(id, Ingredient.of(TinkerTags.Items.DURABILITY), ingredient, restoreAmount);
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    if (!tools.test(inv.getTinkerableStack())) {
      return false;
    }
    // must find at least one slime, but multiple is fine, as is empty slots
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable();
    ModifierId overslime = TinkerModifiers.overslime.getId();
    // if the tool lacks true overslime, add overslime
    if (tool.getUpgrades().getLevel(overslime) == 0) {
      // however, if we have overslime though a trait and reached our cap, also do nothing
      if (tool.getModifierLevel(overslime) > 0 && OverslimeModule.INSTANCE.getAmount(tool) >= OverslimeModule.getCapacity(tool)) {
        return AT_CAPACITY;
      }
      // truely add overslime, this will cost a slime crystal if full durability
      tool = tool.copy();
      tool.addModifier(overslime, 1);
    } else {
      // ensure we are not at the cap already
      if (OverslimeModule.INSTANCE.getAmount(tool) >= OverslimeModule.getCapacity(tool)) {
        return AT_CAPACITY;
      }
      // copy the tool as we will change it later
      tool = tool.copy();
    }

    // see how much value is available, update overslime to the max possible
    int available = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, restoreAmount);
    OverslimeModule.INSTANCE.addAmount(tool, available);
    return ITinkerStationRecipe.success(tool, inv);
  }

  @Override
  public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    ToolStack tool = inv.getTinkerable();
    // how much did we actually consume?
    // if the original tool did not have overslime, its treated as having no slime
    int maxNeeded = OverslimeModule.INSTANCE.getAmount(result.getTool()) - OverslimeModule.INSTANCE.getAmount(tool);
    IncrementalModifierRecipe.updateInputs(inv, ingredient, maxNeeded, restoreAmount * OverslimeModule.getOverworkedBonus(tool), ItemStack.EMPTY);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.overslimeSerializer.get();
  }

  /* JEI display */
  /** Cache of modifier result, same for all overslime */
  private static final ModifierEntry RESULT = new ModifierEntry(TinkerModifiers.overslime, 1);
  /** Cache of input and output tools for display */
  private List<ItemStack> toolWithoutModifier, toolWithModifier = null;

  @Nullable
  @Override
  public ResourceLocation getRecipeId() {
    return getId();
  }

  @Override
  public int getInputCount() {
    return 1;
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    if (slot == 0) {
      return Arrays.asList(ingredient.getItems());
    }
    return Collections.emptyList();
  }
  @Override
  public List<ItemStack> getToolWithoutModifier() {
    if (toolWithoutModifier == null) {
      toolWithoutModifier = Arrays.stream(this.tools.getItems()).map(MAP_TOOL_STACK_FOR_RENDERING).toList();
    }
    return toolWithoutModifier;
  }

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      List<ModifierEntry> result = List.of(RESULT);
      int maxSize = shrinkToolSlotBy();
      toolWithModifier = Arrays.stream(this.tools.getItems())
        .map(MAP_TOOL_STACK_FOR_RENDERING)
        .map(stack -> withModifiers(stack, maxSize, result, data -> OverslimeModule.INSTANCE.setAmountRaw(data, restoreAmount)))
        .toList();
    }
    return toolWithModifier;
  }

  @Override
  public ModifierEntry getDisplayResult() {
    return RESULT;
  }
}
