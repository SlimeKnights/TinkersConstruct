package slimeknights.tconstruct.library.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;

public class ChannelTank extends FluidTankBase<TileChannel> {

  private static final String TAG_LOCKED = "locked";
  /**
   * Amount of fluid that may not be extracted this tick
   * Essentially, since we cannot guarantee tick order, this prevents us from having a net 0 fluid for the renderer
   * if draining and filling at the same time
   */
  private int locked;
  public ChannelTank(int capacity, TileChannel parent) {
    super(capacity, parent);
    this.setCanDrain(false);
  }

  /**
   * Called on channel update to clear the lock, allowing this fluid to be drained
   */
  public void freeFluid() {
    this.locked = 0;
  }

  public FluidStack getUsableFluid() {
    if(fluid == null) {
      return null;
    }
    FluidStack stack = this.fluid.copy();
    stack.amount -= locked;

    return stack;
  }

  /**
   * Returns the maximum fluid that can be extracted from this tank
   * @return  Max fluid that can be pulled
   */
  public int usableFluid() {
    if(fluid == null) {
      return 0;
    }

    return fluid.amount - locked;
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    int amount = super.fill(resource, doFill);
    if(doFill) {
      locked += amount;
    }
    return amount;
  }

  @Override
  protected void sendUpdate(int amount) {
    if(amount != 0) {
      // if the fluid is null, we just removed fluid
      // if the amounts matched, that means we had 0 before
      FluidStack fluid = this.getFluid();
      if(fluid == null || fluid.amount == amount) {
        super.sendUpdate(amount);
      }
    }
  }

  @Override
  public FluidTank readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    this.locked = nbt.getInteger(TAG_LOCKED);

    return this;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    nbt = super.writeToNBT(nbt);
    nbt.setInteger(TAG_LOCKED, locked);

    return nbt;
  }
}
