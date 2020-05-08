package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.tileentity.SmelteryTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankTileEntity extends SmelteryComponentTileEntity implements IFluidTankUpdater {

  public static final int CAPACITY = FluidAttributes.BUCKET_VOLUME * 4;

  protected FluidTankAnimated tank;

  private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
  private int lastStrength;
  public TankTileEntity() {
    this(SmelteryTileEntities.TANK);
    this.tank = new FluidTankAnimated(CAPACITY, this);
    this.lastStrength = -1;
  }

  public TankTileEntity(TileEntityType<?> tileEntityTypein) {
    super(tileEntityTypein);
  }

  @Override
  @Nonnull
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
      return holder.cast();
    return super.getCapability(capability, facing);
  }

  @Override
  public void read(CompoundNBT nbt) {
    super.read(nbt);
    readTank(nbt);
  }

  public void readTank(CompoundNBT nbt) {
    tank.readFromNBT(nbt);
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    nbt = super.write(nbt);
    writeTank(nbt);

    return nbt;
  }

  public void writeTank(CompoundNBT nbt) {
    tank.writeToNBT(nbt);
  }
  @Override
  public void onTankContentsChanged() {

  }
}
