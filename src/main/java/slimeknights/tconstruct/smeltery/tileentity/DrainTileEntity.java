package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

import javax.annotation.Nullable;
import java.util.Objects;

public class DrainTileEntity extends SmelteryComponentTileEntity {
  private final NonNullConsumer<LazyOptional<IFluidHandler>> listener = new WeakConsumerWrapper<>(this, (te, cap) -> te.clearHandler());
  @Nullable
  private LazyOptional<IFluidHandler> fluidHandler = null;

  public DrainTileEntity() {
    this(TinkerSmeltery.drain.get());
  }

  protected DrainTileEntity(TileEntityType<?> type) {
    super(type);
  }

  /** Clears all cached capabilities */
  private void clearHandler() {
    if (fluidHandler != null) {
      fluidHandler.invalidate();
      fluidHandler = null;
    }
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    clearHandler();
  }

  @Override
  protected void setMaster(@Nullable BlockPos master, @Nullable Block block) {
    // invalidate if the master changed
    if (!Objects.equals(getMasterPos(), master)) {
      clearHandler();
      assert world != null;
      world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
    }
    super.setMaster(master, block);
  }

  /**
   * Gets a tile entity at the position of the master that contains a ISmelteryTankHandler
   *
   * @return null if the TE is not an ISmelteryTankHandler or if the master is missing
   */
  @Nullable
  private ISmelteryTankHandler getSmelteryTankHandler() {
    BlockPos master = this.getMasterPos();
    if (master != null && this.world != null) {
      TileEntity te = this.world.getTileEntity(master);
      if (te instanceof ISmelteryTankHandler) {
        return (ISmelteryTankHandler) te;
      }
    }
    return null;
  }

  /**
   * Fetches the capability handlers if missing
   */
  private LazyOptional<IFluidHandler> fetchHandler() {
    if (fluidHandler == null) {
      ISmelteryTankHandler smeltery = getSmelteryTankHandler();
      if (smeltery != null) {
        LazyOptional<IFluidHandler> capability = smeltery.getFluidCapability();
        if (capability.isPresent()) {
          // add listener for when the smeltery invalidates
          capability.addListener(listener);
          // create capabilities
          fluidHandler = LazyOptional.of(() -> capability.orElse(EmptyFluidHandler.INSTANCE));
          return fluidHandler;
        }
      }

      fluidHandler = LazyOptional.empty();
    }
    return fluidHandler;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return fetchHandler().cast();
    }
    return super.getCapability(capability, facing);
  }
}
