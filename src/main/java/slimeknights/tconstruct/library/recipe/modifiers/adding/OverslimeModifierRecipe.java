package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to add overslime to a tool
 */
public class OverslimeModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  private static final ValidatedResult AT_CAPACITY = ValidatedResult.failure(TConstruct.makeTranslationKey("recipe", "overslime.at_capacity"));

  @Getter
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private final int restoreAmount;

  public OverslimeModifierRecipe(ResourceLocation id, Ingredient ingredient, int restoreAmount) {
    this.id = id;
    this.ingredient = ingredient;
    this.restoreAmount = restoreAmount;
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    if (!TinkerTags.Items.DURABILITY.contains(inv.getTinkerableStack().getItem())) {
      return false;
    }
    // must find at least one slime, but multiple is fine, as is empty slots
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationContainer inv) {
    ItemStack tinkerable = inv.getTinkerableStack();
    ToolStack tool = ToolStack.from(tinkerable);
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    // if the tool lacks true overslime, add overslime
    if (tool.getUpgrades().getLevel(TinkerModifiers.overslime.get()) == 0) {
      // however, if we have overslime though a trait and reached our cap, also do nothing
      if (tool.getModifierLevel(TinkerModifiers.overslime.get()) > 0) {
        if (overslime.getOverslime(tool) >= overslime.getCapacity(tool)) {
          return AT_CAPACITY;
        }
      }
      // truely add overslime, this will cost a slime crystal if full durability
      tool = tool.copy();
      tool.addModifier(TinkerModifiers.overslime.get(), 1);
    } else {
      // ensure we are not at the cap already
      if (overslime.getOverslime(tool) >= overslime.getCapacity(tool)) {
        return AT_CAPACITY;
      }
      // copy the tool as we will change it later
      tool = tool.copy();
    }

    // see how much value is available, update overslime to the max possible
    int available = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, restoreAmount);
    overslime.addOverslime(tool, available);
    return ValidatedResult.success(tool.createStack(Math.min(tinkerable.getCount(), shrinkToolSlotBy())));
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #assemble(ITinkerStationContainer)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());
    // if the original tool did not have overslime, its treated as having no slime
    int current = 0;
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    if (tool.getModifierLevel(TinkerModifiers.overslime.get()) != 0) {
      current = overslime.getOverslime(tool);
    }

    // how much did we actually consume?
    int maxNeeded = overslime.getOverslime(ToolStack.from(result)) - current;
    IncrementalModifierRecipe.updateInputs(inv, ingredient, maxNeeded, restoreAmount, ItemStack.EMPTY);
  }

  /** @deprecated use {@link #assemble(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.overslimeSerializer.get();
  }

  /* JEI display */
  /** Cache of modifier result, same for all overslime */
  private static final Lazy<ModifierEntry> RESULT = Lazy.of(() -> new ModifierEntry(TinkerModifiers.overslime.get(), 1));
  /** Cache of tools for input, same for all overslime */
  private static final Lazy<List<ItemStack>> DISPLAY_TOOLS = Lazy.of(() -> TinkerTags.Items.DURABILITY.getValues().stream().map(MAP_TOOL_FOR_RENDERING).collect(Collectors.toList()));

  private List<ItemStack> toolWithModifier = null;
  /** Cache of display outputs, value depends on recipe */
  private List<List<ItemStack>> displayItems = null;

  @Override
  public List<List<ItemStack>> getDisplayItems() {
    if (displayItems == null) {
      // set cap and amount based on the restore amount for output
      displayItems = Arrays.asList(
        DISPLAY_TOOLS.get(),
        Arrays.asList(ingredient.getItems()));
    }
    return displayItems;
  }

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      OverslimeModifier overslime = TinkerModifiers.overslime.get();
      toolWithModifier = TinkerTags.Items.DURABILITY.getValues().stream()
                                                    .map(MAP_TOOL_FOR_RENDERING)
                                                    .map(stack -> IDisplayModifierRecipe.withModifiers(stack, null, RESULT.get(), data -> overslime.setShield(data, restoreAmount)))
                                                    .toList();
    }
    return toolWithModifier;
  }

  @Override
  public ModifierEntry getDisplayResult() {
    return RESULT.get();
  }

  public static class Serializer extends LoggingRecipeSerializer<OverslimeModifierRecipe> {
    @Override
    public OverslimeModifierRecipe fromJson(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(json, "ingredient"));
      int restoreAmount = GsonHelper.getAsInt(json, "restore_amount");
      return new OverslimeModifierRecipe(id, ingredient, restoreAmount);
    }

    @Nullable
    @Override
    protected OverslimeModifierRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient ingredient = Ingredient.fromNetwork(buffer);
      int restoreAmount = buffer.readVarInt();
      return new OverslimeModifierRecipe(id, ingredient, restoreAmount);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, OverslimeModifierRecipe recipe) {
      recipe.ingredient.toNetwork(buffer);
      buffer.writeVarInt(recipe.restoreAmount);
    }
  }
}
