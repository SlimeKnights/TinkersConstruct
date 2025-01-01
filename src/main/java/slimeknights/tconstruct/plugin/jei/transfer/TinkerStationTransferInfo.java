package slimeknights.tconstruct.plugin.jei.transfer;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record TinkerStationTransferInfo<T>(RecipeType<T> getRecipeType) implements IRecipeTransferInfo<TinkerStationContainerMenu,T> {
  @Override
  public Class<TinkerStationContainerMenu> getContainerClass() {
    return TinkerStationContainerMenu.class;
  }

  @Override
  public Optional<MenuType<TinkerStationContainerMenu>> getMenuType() {
    return Optional.of(TinkerTables.tinkerStationContainer.get());
  }

  @Override
  public boolean canHandle(TinkerStationContainerMenu container, T recipe) {
    return true;
  }

  @Override
  public List<Slot> getRecipeSlots(TinkerStationContainerMenu container, T recipe) {
    return container.getInputSlots();
  }

  @Override
  public List<Slot> getInventorySlots(TinkerStationContainerMenu container, T recipe) {
    List<Slot> slots = new ArrayList<>();
    // skip over inputs, output slot, tool slot, armor, and offhand
    int start = container.getInputSlots().size() + 3 + ArmorItem.Type.values().length;
    for(int i = start; i < start + 36; i++) {
      Slot slot = container.getSlot(i);
      slots.add(slot);
    }

    return slots;
  }
}
