package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public class TileTank extends TileSmelteryComponent {

  public static final int CAPACITY = Fluid.BUCKET_VOLUME * 4;

  protected FluidTankAnimated tank;

  public TileTank() {
    this.tank = new FluidTankAnimated(CAPACITY, this);
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T)tank;
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
      return tank.getFluid().getFluid().getLuminosity();
    }
    return 0;
  }

  // called only clientside to sync with the server
  @SideOnly(Side.CLIENT)
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


  public int comparatorStrength() {
    return 15 * tank.getFluidAmount() / tank.getCapacity();
  }
}
