package slimeknights.tconstruct.smeltery.tileentity.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.common.inventory.SingleItemHandler;

/**
 * Item handler holding the heater inventory
 */
public class HeaterItemHandler extends SingleItemHandler<MantleTileEntity> {
  public HeaterItemHandler(MantleTileEntity parent) {
    super(parent, 64);
  }

  @Override
  protected boolean isItemValid(ItemStack stack) {
    // fuel module divides by 4, so anything 3 or less is treated as 0
    return ForgeHooks.getBurnTime(stack) > 3;
  }
}
