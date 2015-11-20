package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileTank extends TileSmelteryComponent implements IFluidHandler {

  public static final int CAPACITY = FluidContainerRegistry.BUCKET_VOLUME * 4;

  public FluidTank tank;
  public float renderOffset;

  public TileTank() {
    this.tank = new FluidTank(CAPACITY);
  }

  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    int amount = tank.fill(resource, doFill);
    if(amount > 0 && doFill) {
      renderOffset = resource.amount;
    }

    return amount;
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    if(tank.getFluidAmount() == 0) {
      return null;
    }
    if(tank.getFluid().getFluid() != resource.getFluid()) {
      return null;
    }

    // same fluid, k
    return this.drain(from, resource.amount, doDrain);
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    FluidStack amount = tank.drain(maxDrain, doDrain);
    if(amount != null && doDrain) {
      renderOffset = -maxDrain;
    }

    return amount;
  }

  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    return tank.getFluidAmount() == 0 || (tank.getFluid().getFluid() == fluid && tank.getFluidAmount() < tank
        .getCapacity());
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    return tank.getFluidAmount() > 0;
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    return new FluidTankInfo[]{new FluidTankInfo(tank)};
  }

  public float getFluidAmountScaled() {
    return (float) (tank.getFluid().amount - renderOffset) / (float) (tank.getCapacity() * 1.01F);
  }

  public boolean containsFluid() {
    return tank.getFluid() != null;
  }

  public int getBrightness() {
    if(containsFluid()) {
      return tank.getFluid().getFluid().getLuminosity();
    }
    return 0;
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    tank.readFromNBT(tags);
  }

  @Override
  public void writeToNBT(NBTTagCompound tags) {
    super.writeToNBT(tags);
    tank.writeToNBT(tags);
  }

  public int comparatorStrength() {
    return 15 * tank.getFluidAmount() / tank.getCapacity();
  }
}
