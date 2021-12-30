package slimeknights.tconstruct.plugin.jei.transfer;

import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;

import java.util.ArrayList;
import java.util.List;

public class TinkerStationTransferInfo implements IRecipeTransferInfo<TinkerStationContainerMenu,ITinkerStationRecipe> {
  @Override
  public Class<TinkerStationContainerMenu> getContainerClass() {
    return TinkerStationContainerMenu.class;
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
  public boolean canHandle(TinkerStationContainerMenu container, ITinkerStationRecipe recipe) {
    return true;
  }

  @Override
  public List<Slot> getRecipeSlots(TinkerStationContainerMenu container, ITinkerStationRecipe recipe) {
    return container.getInputSlots();
  }

  @Override
  public List<Slot> getInventorySlots(TinkerStationContainerMenu container, ITinkerStationRecipe recipe) {
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
