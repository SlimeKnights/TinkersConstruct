package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to dynamically provide the right slot count to JEI
 */
public class CraftingStationTransferInfo implements IRecipeTransferInfo<CraftingStationContainer, CraftingRecipe> {

  @Override
  public Class<CraftingStationContainer> getContainerClass() {
    return CraftingStationContainer.class;
  }

  @Override
  public Class<CraftingRecipe> getRecipeClass() {
    return CraftingRecipe.class;
  }

  @Override
  public ResourceLocation getRecipeCategoryUid() {
    return VanillaRecipeCategoryUid.CRAFTING;
  }

  @Override
  public List<Slot> getInventorySlots(CraftingStationContainer container, CraftingRecipe recipe) {
    List<Slot> slots = new ArrayList<>();

    // 36 for player inventory
    int totalSize = container.slots.size();
    int sideInventoryEnd = totalSize - 36;

    // first, add all inventory slots, ensures they are first for emptying the table
    for (int i = sideInventoryEnd; i < totalSize; i++) {
      slots.add(container.getSlot(i));
    }

    // next, add side inventory. shouldn't be a problem due to the blacklist
    // 10 slots for the crafting table
    for (int i = 10; i < sideInventoryEnd; i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }

  @Override
  public List<Slot> getRecipeSlots(CraftingStationContainer container, CraftingRecipe recipe) {
    List<Slot> slots = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }

  @Override
  public boolean canHandle(CraftingStationContainer container, CraftingRecipe recipe) {
    return true;
  }
}
