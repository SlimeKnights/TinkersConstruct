package slimeknights.tconstruct.plugin.jei.transfer;

import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;

import java.util.ArrayList;
import java.util.List;

public class TinkerStationTransferInfo implements IRecipeTransferInfo<TinkerStationContainer,ITinkerStationRecipe> {
  @Override
  public Class<TinkerStationContainer> getContainerClass() {
    return TinkerStationContainer.class;
  }

  @Override
  public Class<ITinkerStationRecipe> getRecipeClass() {
    return ITinkerStationRecipe.class;
  }

  @Override
  public ResourceLocation getRecipeCategoryUid() {
    return TConstructRecipeCategoryUid.modifiers;
  }

  @Override
  public boolean canHandle(TinkerStationContainer container, ITinkerStationRecipe recipe) {
    return true;
  }

  @Override
  public List<Slot> getRecipeSlots(TinkerStationContainer container, ITinkerStationRecipe recipe) {
    return container.getInputSlots();
  }

  @Override
  public List<Slot> getInventorySlots(TinkerStationContainer container, ITinkerStationRecipe recipe) {
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
