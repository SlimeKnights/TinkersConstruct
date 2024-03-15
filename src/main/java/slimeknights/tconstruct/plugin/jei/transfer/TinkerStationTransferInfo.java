package slimeknights.tconstruct.plugin.jei.transfer;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TinkerStationTransferInfo implements IRecipeTransferInfo<TinkerStationContainerMenu,IDisplayModifierRecipe> {
  @Override
  public Class<TinkerStationContainerMenu> getContainerClass() {
    return TinkerStationContainerMenu.class;
  }

  @Override
  public Optional<MenuType<TinkerStationContainerMenu>> getMenuType() {
    return Optional.of(TinkerTables.tinkerStationContainer.get());
  }

  @Override
  public RecipeType<IDisplayModifierRecipe> getRecipeType() {
    return TConstructJEIConstants.MODIFIERS;
  }

  @Override
  public boolean canHandle(TinkerStationContainerMenu container, IDisplayModifierRecipe recipe) {
    return true;
  }

  @Override
  public List<Slot> getRecipeSlots(TinkerStationContainerMenu container, IDisplayModifierRecipe recipe) {
    return container.getInputSlots();
  }

  @Override
  public List<Slot> getInventorySlots(TinkerStationContainerMenu container, IDisplayModifierRecipe recipe) {
    List<Slot> slots = new ArrayList<>();
    // skip over inputs, output slot, tool slot, armor, and offhand
    int start = container.getInputSlots().size() + 3 + ArmorSlotType.values().length;
    for(int i = start; i < start + 36; i++) {
      Slot slot = container.getSlot(i);
      slots.add(slot);
    }

    return slots;
  }
}
