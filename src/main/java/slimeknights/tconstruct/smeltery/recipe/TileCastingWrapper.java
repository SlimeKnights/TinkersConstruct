package slimeknights.tconstruct.smeltery.recipe;

import lombok.AllArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.smeltery.tileentity.AbstractCastingTileEntity;

/**
 * Provides read only access to the input of a casting table. Prevents extra data from leaking
 */
@AllArgsConstructor
public class TileCastingWrapper implements ICastingInventory {
  private final AbstractCastingTileEntity tile;
  private Fluid fluid;

  @Override
  public ItemStack getStack() {
    return tile.getStackInSlot(AbstractCastingTileEntity.INPUT);
  }

  public Fluid getFluid() {
    return fluid;
  }

  /**
   * Sets the contained fluid in this inventory
   */
  public void setFluid(Fluid fluid) {
    this.fluid = fluid;
  }
}
