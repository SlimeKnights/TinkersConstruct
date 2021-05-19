package slimeknights.tconstruct.smeltery.recipe;

import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

/**
 * Provides read only access to the input of a casting table. Prevents extra data from leaking
 */
@RequiredArgsConstructor
public class TileCastingWrapper implements ICastingInventory {
  private final CastingTileEntity tile;
  private Fluid fluid;
  private boolean switchSlots = false;

  @Override
  public ItemStack getStack() {
    return tile.getStackInSlot(switchSlots ? CastingTileEntity.OUTPUT : CastingTileEntity.INPUT);
  }

  @Override
  public Fluid getFluid() {
    return fluid;
  }

  /**
   * Sets the contained fluid in this inventory
   */
  public void setFluid(Fluid fluid) {
    this.fluid = fluid;
  }

  /** Uses the input for input (default) */
  public void useInput() {
    switchSlots = false;
  }

  /** Uses the output for input (for multistep casting) */
  public void useOutput() {
    switchSlots = true;
  }
}
