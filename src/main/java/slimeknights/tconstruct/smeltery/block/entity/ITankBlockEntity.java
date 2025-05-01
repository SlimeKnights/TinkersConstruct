package slimeknights.tconstruct.smeltery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.SafeClient;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

/**
 * Common logic between the tank and the melter
 */
public interface ITankBlockEntity extends IFluidTankUpdater, FluidUpdatePacket.IFluidPacketReceiver {
  /**
   * Gets the tank in this tile entity
   * @return  Tank
   */
  FluidTankAnimated getTank();

  /*
   * Comparator
   */

  /**
   * Gets the comparator strength for the tank
   * @return  Tank comparator strength
   */
  default int comparatorStrength() {
    FluidTankAnimated tank = getTank();
    if (tank.isEmpty()) {
      return 0;
    }
    return 1 + 14 * tank.getFluidAmount() / tank.getCapacity();
  }

  /**
   * Gets the last comparator strength for this tank
   * @return  Last comparator strength
   */
  int getLastStrength();

  /**
   * Updates the last comparator strength for this tank
   * @param strength  Last comparator strength
   */
  void setLastStrength(int strength);

  @Override
  default void onTankContentsChanged() {
    int newStrength = this.comparatorStrength();
    BlockEntity te = getTE();
    Level world = te.getLevel();
    if (newStrength != getLastStrength() && world != null) {
      world.updateNeighborsAt(te.getBlockPos(), te.getBlockState().getBlock());
      setLastStrength(newStrength);
    }
  }

  /*
   * Fluid tank updater
   */

  /** If true, the fluid is rendered as part of the model */
  default boolean isFluidInModel() {
    return Config.CLIENT.tankFluidModel.get();
  }

  @Override
  default void updateFluidTo(FluidStack fluid) {
    // update tank fluid
    FluidTankAnimated tank = getTank();
    int oldAmount = tank.getFluidAmount();
    int newAmount = fluid.getAmount();
    tank.setFluid(fluid);

    // update the tank render offset from the change
    tank.setRenderOffset(tank.getRenderOffset() + newAmount - oldAmount);

    // update the block model
    if (isFluidInModel()) {
      SafeClient.updateFluidModel(getTE(), tank, oldAmount, newAmount);
    }
  }


  /*
   * Tile entity methods
   */

  /** @return tile entity world */
  default BlockEntity getTE() {
    return (BlockEntity) this;
  }

  /*
   * Helpers
   */

  /**
   * Implements logic for {@link net.minecraft.world.level.block.Block#getAnalogOutputSignal(BlockState, Level, BlockPos)}
   * @param world  World instance
   * @param pos    Block position
   * @return  Comparator power
   */
  static int getComparatorInputOverride(LevelAccessor world, BlockPos pos) {
    BlockEntity te = world.getBlockEntity(pos);
    if (!(te instanceof ITankBlockEntity)) {
      return 0;
    }
    return ((ITankBlockEntity) te).comparatorStrength();
  }

  /** Represents a  tank block entity with an inventory */
  interface ITankInventoryBlockEntity extends ITankBlockEntity {
    /** Gets the associated item handler for this tank with an inventory */
    IItemHandler getItemHandler();
  }
}
