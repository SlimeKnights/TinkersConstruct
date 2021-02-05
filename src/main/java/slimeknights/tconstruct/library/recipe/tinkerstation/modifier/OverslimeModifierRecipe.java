package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

import javax.annotation.Nullable;

/**
 * Recipe to add overslime to a tool
 */
@RequiredArgsConstructor
public class OverslimeModifierRecipe implements ITinkerStationRecipe {
  private static final ValidatedResult AT_CAPACITY = ValidatedResult.failure(Util.makeTranslationKey("recipe", "overslime.at_capacity"));

  @Getter
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private final int restoreAmount;

  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    if (!(inv.getTinkerableStack().getItem() instanceof IModifiable)) {
      return false;
    }
    // must find at least one slime, but multiple is fine, as is empty slots
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        if (ingredient.test(stack)) {
          found = true;
        } else {
          return false;
        }
      }
    }
    return found;
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());
    // if the tool lacks overslime, add overslime
    int current = 0;
    int cap = OverslimeModifier.getCap(tool);
    if (tool.getUpgrades().getLevel(TinkerModifiers.overslime.get()) == 0) {
      tool = tool.copy();
      tool.addModifier(TinkerModifiers.overslime.get(), 1);
    } else {
      // ensure we are not at the cap already
      current = OverslimeModifier.getOverslime(tool);
      if (current >= cap) {
        return AT_CAPACITY;
      }
      // copy the tool as we will change it later
      tool = tool.copy();
    }

    // at most, how many slime will we consume?
    int maxNeeded = cap - current;
    int itemsNeeded = maxNeeded / restoreAmount;
    if (maxNeeded % restoreAmount != 0) {
      itemsNeeded++;
    }
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        int count = stack.getCount();
        // if this stack fully covers the remaining needs, done
        if (count > itemsNeeded) {
          current = cap;
          break;
        }
        // otherwise, reduce the items needed and try the next stack
        itemsNeeded -= count;
        current += restoreAmount * count;
      }
    }

    // update overslime
    OverslimeModifier.setOverslime(tool, current);
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
    if (tool.getUpgrades().getLevel(TinkerModifiers.overslime.get()) != 0) {
      current = OverslimeModifier.getOverslime(tool);
    }

    // how much did we actually consume?
    int maxNeeded = OverslimeModifier.getOverslime(ToolStack.from(result)) - current;
    int itemsNeeded = maxNeeded / restoreAmount;
    if (maxNeeded % restoreAmount != 0) {
      itemsNeeded++;
    }
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        int count = stack.getCount();
        // if this stack fully covers the remaining needs, done
        if (count > itemsNeeded) {
          inv.shrinkInput(i, itemsNeeded);
          break;
        }
        // otherwise, clear stack and try the next stack
        inv.shrinkInput(i, count);
        itemsNeeded -= count;
      }
    }
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

  public static class Serializer extends RecipeSerializer<OverslimeModifierRecipe> {
    @Override
    public OverslimeModifierRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
      int restoreAmount = JSONUtils.getInt(json, "restore_amount");
      return new OverslimeModifierRecipe(id, ingredient, restoreAmount);
    }

    @Nullable
    @Override
    public OverslimeModifierRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient ingredient = Ingredient.read(buffer);
      int restoreAmount = buffer.readVarInt();
      return new OverslimeModifierRecipe(id, ingredient, restoreAmount);
    }

    @Override
    public void write(PacketBuffer buffer, OverslimeModifierRecipe recipe) {
      recipe.ingredient.write(buffer);
      buffer.writeVarInt(recipe.restoreAmount);
    }
  }
}
