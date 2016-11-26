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

  @Nonnull
  @Override
  public List<Slot> getRecipeSlots(ContainerCraftingStation container) {
    List<Slot> slots = new ArrayList<Slot>();
    for(int i = 1; i < 10; i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }

  @Nonnull
  @Override
  public List<Slot> getInventorySlots(ContainerCraftingStation container) {
    List<Slot> slots = new ArrayList<Slot>();

    // we skip all slots from within the side inventory and crafting grid for transfer
    // side inventory can cause too many issues since transfer is not validated the same way as clicking does
    for(int i = container.getPlayerInventoryStart(); i < container.inventorySlots.size(); i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }
}
