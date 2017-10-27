package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket.IFluidPacketReceiver;

public class TileTank extends TileSmelteryComponent implements IFluidTankUpdater, IFluidPacketReceiver {

  public static final int CAPACITY = Fluid.BUCKET_VOLUME * 4;

  protected FluidTankAnimated tank;

  // used to only run block updates if the value actually changes
  private int lastStrength;

  public TileTank() {
    this.tank = new FluidTankAnimated(CAPACITY, this);
    this.lastStrength = -1;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) tank;
    }
    return super.getCapability(capability, facing);
  }

  public FluidTankAnimated getInternalTank() {
    return tank;
  }

  public boolean containsFluid() {
    return tank.getFluid() != null;
  }

  public int getBrightness() {
    if(containsFluid()) {
      assert tank.getFluid() != null;
      return tank.getFluid().getFluid().getLuminosity();
    }
    return 0;
  }

  // called only clientside to sync with the server
  @Override
  public void updateFluidTo(FluidStack fluid) {
    int oldAmount = tank.getFluidAmount();
    tank.setFluid(fluid);

    tank.renderOffset += tank.getFluidAmount() - oldAmount;
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    readTankFromNBT(tags);
  }

  public void readTankFromNBT(NBTTagCompound tags) {
    tank.readFromNBT(tags);
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {
    tags = super.writeToNBT(tags);
    writeTankToNBT(tags);

    return tags;
  }

  public void writeTankToNBT(NBTTagCompound tags) {
    tank.writeToNBT(tags);
  }

  /**
   * @return The current comparator strength based on the tank's capicity
   */
  public int comparatorStrength() {
    return 15 * tank.getFluidAmount() / tank.getCapacity();
  }

  @Override
  public void onTankContentsChanged() {
    int newStrength = this.comparatorStrength();
    if(newStrength != lastStrength) {
      this.getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
      this.lastStrength = newStrength;
    }
  }

}
