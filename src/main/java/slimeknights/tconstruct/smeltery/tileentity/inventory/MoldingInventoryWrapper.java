package slimeknights.tconstruct.smeltery.tileentity.inventory;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.recipe.molding.IMoldingInventory;
import slimeknights.tconstruct.misc.IItemHandler;

/** Wrapper around an item handler for the sake of use as a molding inventory */
public class MoldingInventoryWrapper implements IMoldingInventory {
  private final IItemHandler handler;
  private final int slot;

  private ItemStack pattern = ItemStack.EMPTY;

  public MoldingInventoryWrapper(IItemHandler handler, int slot) {
    this.handler = handler;
    this.slot = slot;
  }

  @Override
  public ItemStack getMaterial() {
    return handler.getStackInSlot(slot);
  }

  @Override
  public ItemStack getPattern() {
    return pattern;
  }

  public void setPattern(ItemStack pattern) {
    this.pattern = pattern;
  }
}
