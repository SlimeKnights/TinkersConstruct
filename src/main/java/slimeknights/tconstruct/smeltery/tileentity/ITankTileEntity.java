package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.block.TankModel;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

/**
 * Common logic between the tank and the melter
 */
public interface ITankTileEntity extends IFluidTankUpdater, FluidUpdatePacket.IFluidPacketReceiver {
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
    TileEntity te = getTE();
    World world = te.getWorld();
    if (newStrength != getLastStrength() && world != null) {
      world.notifyNeighborsOfStateChange(te.getPos(), te.getBlockState().getBlock());
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
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
      if (isFluidInModel()) {
        // if the amount change is bigger than a single increment, or we changed whether we have a fluid, update the world renderer
        TileEntity te = getTE();
        TankModel.BakedModel<?> model = ModelHelper.getBakedModel(te.getBlockState(), TankModel.BakedModel.class);
        if (model != null && (Math.abs(newAmount - oldAmount) >= (tank.getCapacity() / model.getFluid().getIncrements()) || (oldAmount == 0) != (newAmount == 0))) {
          //this.requestModelDataUpdate();
          Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, te.getPos(), null, null, 3);
        }
      }
    });
  }

  /*
   * Tile entity methods
   */

  /** @return tile entity world */
  default TileEntity getTE() {
    return (TileEntity) this;
  }

  /*
   * Helpers
   */

  /**
   * Implements logic for {@link net.minecraft.block.Block#getComparatorInputOverride(BlockState, World, BlockPos)}
   * @param world  World instance
   * @param pos    Block position
   * @return  Comparator power
   */
  static int getComparatorInputOverride(IWorld world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof ITankTileEntity)) {
      return 0;
    }
    return ((ITankTileEntity) te).comparatorStrength();
  }
}
