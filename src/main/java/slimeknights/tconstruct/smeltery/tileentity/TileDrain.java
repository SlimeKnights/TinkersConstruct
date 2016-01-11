package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Drains allow access to the bottommost liquid in the smeltery.
 * They can insert and drain liquids from the smeltery.
 */
public class TileDrain extends TileSmelteryComponent implements IFluidHandler, IFluidTank {

  @Override
  public FluidStack getFluid() {
    TileSmeltery smeltery = getSmeltery();
    if(smeltery != null) {
      if(smeltery.getTank().getFluids().size() > 0) {
        return smeltery.getTank().getFluids().get(0);
      }
    }
    return null;
  }

  @Override
  public int getFluidAmount() {
    FluidStack fs = getFluid();
    return fs != null ? fs.amount : 0;
  }

  @Override
  public int getCapacity() {
    TileSmeltery smeltery = getSmeltery();
    // return the capacity with respect to the current fluid
    if(smeltery != null) {
      return smeltery.getTank().getMaxCapacity() - smeltery.getTank().getUsedCapacity() + getFluidAmount();
    }
    return 0;
  }

  @Override
  public FluidTankInfo getInfo() {
    return new FluidTankInfo(getFluid(), getCapacity());
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    TileSmeltery smeltery = getSmeltery();
    if(smeltery != null) {
      return smeltery.getTank().fill(resource, doFill);
    }
    return 0;
  }

  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    TileSmeltery smeltery = getSmeltery();
    if(smeltery != null) {
      return smeltery.getTank().drain(maxDrain, doDrain);
    }
    return null;
  }

  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    return fill(resource, doFill);
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    if(resource == null || getFluid() == null) {
      return null;
    }

    if(getFluid().isFluidEqual(resource)) {
      return drain(resource.amount, doDrain);
    }
    return null;
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    return drain(maxDrain, doDrain);
  }

  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    return getCapacity() - getFluidAmount() > 0;
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    return getFluid() != null && getFluid().getFluid() == fluid;
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    return new FluidTankInfo[] {getInfo()};
  }
}
