package slimeknights.tconstruct.library.recipe.toolstation;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.recipe.inventory.IReadOnlyInventory;

public interface IToolStationInventory extends IReadOnlyInventory {

  Iterable<ItemStack> getInputStacks();

  ItemStack getToolStack();
}
