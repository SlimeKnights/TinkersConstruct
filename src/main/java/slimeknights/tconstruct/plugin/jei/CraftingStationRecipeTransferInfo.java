package slimeknights.tconstruct.plugin.jei;

import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import slimeknights.tconstruct.tools.common.inventory.ContainerCraftingStation;

/**
 * @author shadowfacts
 */
public class CraftingStationRecipeTransferInfo implements IRecipeTransferInfo<ContainerCraftingStation> {

  @Nonnull
  @Override
  public Class<ContainerCraftingStation> getContainerClass() {
    return ContainerCraftingStation.class;
  }

  @Nonnull
  @Override
  public String getRecipeCategoryUid() {
    return VanillaRecipeCategoryUid.CRAFTING;
  }

  @Override
  public boolean canHandle(ContainerCraftingStation container) {
    return true;
  }

  @Nonnull
  @Override
  public List<Slot> getRecipeSlots(ContainerCraftingStation container) {
    List<Slot> slots = new ArrayList<>();
    for(int i = 1; i < 10; i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }

  @Nonnull
  @Override
  public List<Slot> getInventorySlots(ContainerCraftingStation container) {
    List<Slot> slots = new ArrayList<>();

    // skip the actual slots of the crafting table
    for(int i = 10; i < container.inventorySlots.size(); i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }
}
