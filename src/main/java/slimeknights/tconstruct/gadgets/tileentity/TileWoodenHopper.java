package slimeknights.tconstruct.gadgets.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.NonNullList;

public class TileWoodenHopper extends TileEntityHopper {

  public TileWoodenHopper() {
    this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
  }

  @Override
  public void setTransferCooldown(int ticks) {
    super.setTransferCooldown(ticks*2);
  }
}
