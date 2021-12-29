package slimeknights.tconstruct.library.recipe;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;

/** Simple class for an inventory containing just one item */
public class SingleItemInventory implements ISingleItemInventory {
  @Getter @Setter
  private ItemStack stack = ItemStack.EMPTY;
}
