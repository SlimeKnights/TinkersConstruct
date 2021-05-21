package slimeknights.tconstruct.plugin.jei.transfer;

import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;

import java.util.ArrayList;
import java.util.List;

public class TinkerStationTransferInfo implements IRecipeTransferInfo<TinkerStationContainer> {
  @Override
  public Class<TinkerStationContainer> getContainerClass() {
    return TinkerStationContainer.class;
  }

  @Override
  public ResourceLocation getRecipeCategoryUid() {
    return TConstructRecipeCategoryUid.modifiers;
  }

  @Override
  public boolean canHandle(TinkerStationContainer container) {
    return true;
  }

  @Override
  public List<Slot> getRecipeSlots(TinkerStationContainer container) {
    return container.getInputSlots();
  }

  @Override
  public List<Slot> getInventorySlots(TinkerStationContainer container) {
    List<Slot> slots = new ArrayList<>();
    // skip over inputs and the output slot
    int start = container.getInputSlots().size() + 1;
    for(int i = start; i < start + 36; i++) {
      Slot slot = container.getSlot(i);
      slots.add(slot);
    }

    return slots;
  }
}
