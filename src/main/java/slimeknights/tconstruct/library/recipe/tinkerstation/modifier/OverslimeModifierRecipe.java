package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to add overslime to a tool
 */
public class OverslimeModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  private static final ValidatedResult AT_CAPACITY = ValidatedResult.failure(Util.makeTranslationKey("recipe", "overslime.at_capacity"));

  @Getter
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private final int restoreAmount;

  public OverslimeModifierRecipe(ResourceLocation id, Ingredient ingredient, int restoreAmount) {
    this.id = id;
    this.ingredient = ingredient;
    this.restoreAmount = restoreAmount;
    ModifierRecipeLookup.addIngredient(ingredient);
    ModifierRecipeLookup.setUpgradeSlots(TinkerModifiers.overslime.get(), 0);
  }

  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    if (!TinkerTags.Items.MODIFIABLE.contains(inv.getTinkerableStack().getItem())) {
      return false;
    }
    // must find at least one slime, but multiple is fine, as is empty slots
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());
    int current = 0;
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    int cap = overslime.getCapacity(tool);
    // if the tool lacks true overslime, add overslime
    if (tool.getUpgrades().getLevel(TinkerModifiers.overslime.get()) == 0) {
      // however, if we have overslime though a trait and reached our cap, also do nothing
      if (tool.getModifierLevel(TinkerModifiers.overslime.get()) > 0) {
        current = overslime.getOverslime(tool);
        if (current >= cap) {
          return AT_CAPACITY;
        }
      }

      // truely add overslime, this will cost a slime crystal if full durability
      tool = tool.copy();
      tool.addModifier(TinkerModifiers.overslime.get(), 1);
      cap = overslime.getCapacity(tool);
    } else {
      // ensure we are not at the cap already
      current = overslime.getOverslime(tool);
      if (current >= cap) {
        return AT_CAPACITY;
      }
      // copy the tool as we will change it later
      tool = tool.copy();
    }

    // see how much value is available, update overslime to the max possible
    int available = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, restoreAmount);
    overslime.setOverslime(tool, Math.min(current + available, cap));
    return ValidatedResult.success(tool.createStack());
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #getCraftingResult(ITinkerStationInventory)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv) {
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

  /** @deprecated use {@link #getCraftingResult(ITinkerStationInventory)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.overslimeSerializer.get();
  }

  /* JEI display */
  /** Cache of modifier result, same for all overslime */
  private static final Lazy<ModifierEntry> RESULT = Lazy.of(() -> new ModifierEntry(TinkerModifiers.overslime.get(), 1));
  /** Cache of tools for input, same for all overslime */
  private static final Lazy<List<ItemStack>> DISPLAY_TOOLS = Lazy.of(() -> IDisplayModifierRecipe.getAllModifiable().map(MAP_TOOL_FOR_RENDERING).collect(Collectors.toList()));

  private List<ItemStack> toolWithModifier = null;
  /** Cache of display outputs, value depends on recipe */
  private List<List<ItemStack>> displayItems = null;

  @Override
  public List<List<ItemStack>> getDisplayItems() {
    if (displayItems == null) {
      // set cap and amount based on the restore amount for output
      displayItems = Arrays.asList(
        DISPLAY_TOOLS.get(),
        Arrays.asList(ingredient.getMatchingStacks()));
    }
    return displayItems;
  }

  @SuppressWarnings("deprecation")
  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      OverslimeModifier overslime = TinkerModifiers.overslime.get();
      toolWithModifier = IDisplayModifierRecipe.getAllModifiable()
                                               .map(MAP_TOOL_FOR_RENDERING)
                                               .map(stack -> IDisplayModifierRecipe.withModifiers(stack, null, RESULT.get(), data -> overslime.setOverslime(data, restoreAmount)))
                                               .collect(Collectors.toList());
    }
    return toolWithModifier;
  }

  @Override
  public ModifierEntry getDisplayResult() {
    return RESULT.get();
  }

  public static class Serializer extends LoggingRecipeSerializer<OverslimeModifierRecipe> {
    @Override
    public OverslimeModifierRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
      int restoreAmount = JSONUtils.getInt(json, "restore_amount");
      return new OverslimeModifierRecipe(id, ingredient, restoreAmount);
    }

    @Nullable
    @Override
    protected OverslimeModifierRecipe readSafe(ResourceLocation id, PacketBuffer buffer) {
      Ingredient ingredient = Ingredient.read(buffer);
      int restoreAmount = buffer.readVarInt();
      return new OverslimeModifierRecipe(id, ingredient, restoreAmount);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, OverslimeModifierRecipe recipe) {
      recipe.ingredient.write(buffer);
      buffer.writeVarInt(recipe.restoreAmount);
    }
  }
}
