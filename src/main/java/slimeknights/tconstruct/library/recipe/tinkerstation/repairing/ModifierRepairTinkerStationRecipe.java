package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeSerializer.IModifierRepairRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

@RequiredArgsConstructor
public class ModifierRepairTinkerStationRecipe implements ITinkerStationRecipe, IModifierRepairRecipe {
  @Getter
  private final ResourceLocation id;
  @Getter
  private final Modifier modifier;
  @Getter
  private final Ingredient ingredient;
  @Getter
  private final int repairAmount;

  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (!TinkerTags.Items.DURABILITY.contains(tinkerable.getItem())) {
      return false;
    }
    ToolStack tool = ToolStack.from(tinkerable);
    if (tool.getModifierLevel(modifier) == 0) {
      return false;
    }
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());
    int amountPerItem = tool.getModifierLevel(modifier) * repairAmount;
    if (amountPerItem <= 0) {
      return ValidatedResult.PASS;
    }

    // apply modifiers to possibly boost it
    float repairFactor = 1;
    for (ModifierEntry entry : tool.getModifierList()) {
      repairFactor = entry.getModifier().getRepairFactor(tool, entry.getLevel(), repairFactor);
      if (repairFactor <= 0) {
        return ValidatedResult.PASS;
      }
    }
    amountPerItem *= repairFactor;

    // get the max amount we can repair
    int available = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, amountPerItem);
    if (available <= 0) {
      return ValidatedResult.PASS;
    }
    // we will just repair the max possible here, no reason to try less
    tool = tool.copy();
    ToolDamageUtil.repair(tool, available);
    return ValidatedResult.success(tool.createStack());
  }

  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv, boolean isServer) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());

    // rescale the amount based on modifiers
    float repairFactor = 1.0f;
    for (ModifierEntry entry : tool.getModifierList()) {
      repairFactor = entry.getModifier().getRepairFactor(tool, entry.getLevel(), repairFactor);
      if (repairFactor <= 0) {
        return;
      }
    }
    // also scale by relevant modifier level
    int amountPerItem = (int)(tool.getModifierLevel(modifier) * repairAmount * repairFactor);
    if (amountPerItem < 0) {
      return;
    }
    // how much do we need to subtract from our inputs still
    int repairRemaining = tool.getDamage() - ToolStack.from(result).getDamage();
    IncrementalModifierRecipe.updateInputs(inv, ingredient, repairRemaining, amountPerItem, ItemStack.EMPTY);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierRepair.get();
  }

  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }
}
