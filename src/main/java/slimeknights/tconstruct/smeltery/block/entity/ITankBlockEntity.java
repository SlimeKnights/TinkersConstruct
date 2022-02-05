package slimeknights.tconstruct.smeltery.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.block.TankModel.Baked;
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
    return 15 * tank.getFluidAmount() / tank.getCapacity();
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

  @SuppressWarnings("ConstantConditions")
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
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
      if (isFluidInModel()) {
        // if the amount change is bigger than a single increment, or we changed whether we have a fluid, update the world renderer
        BlockEntity te = getTE();
        Baked<?> model = ModelHelper.getBakedModel(te.getBlockState(), Baked.class);
        if (model != null && (Math.abs(newAmount - oldAmount) >= (tank.getCapacity() / model.getFluid().getIncrements()) || (oldAmount == 0) != (newAmount == 0))) {
          //this.requestModelDataUpdate();
          Minecraft.getInstance().levelRenderer.blockChanged(null, te.getBlockPos(), null, null, 3);
        }
      }
    });
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
}
