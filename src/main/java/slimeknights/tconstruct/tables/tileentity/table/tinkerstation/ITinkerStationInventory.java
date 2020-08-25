package slimeknights.tconstruct.tables.tileentity.table.tinkerstation;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.recipe.inventory.IReadOnlyInventory;

public interface ITinkerStationInventory extends IReadOnlyInventory {

  NonNullList<ItemStack> getAllInputStacks();

  void clearInputs();

  ItemStack getTinkerableStack();
}
