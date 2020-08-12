package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
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
      if (Config.CLIENT.tankFluidModel.get()) {
        // if the amount change is bigger than a single increment, or we changed whether we have a fluid, update the world renderer
        TileEntity te = getTE();
        TankModel.BakedModel model = ModelHelper.getBakedModel(te.getBlockState(), TankModel.BakedModel.class);
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
   * Base logic to interact with a tank
   * @param world   World instance
   * @param pos     Tank position
   * @param player  Player instance
   * @param hand    Hand used
   * @param hit     Hit position
   * @return  True if further interactions should be blocked, false otherwise
   */
  static boolean interactWithTank(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (!world.isRemote()) {
      // simply update the fluid handler capability
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
        te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace()).ifPresent((handler) -> {
          if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
            // FIXME: this is wrong, should have the fluid play the sound
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1, 1);
          }
        });
      }
    }

    // if its a fluid handler item, block further interactions
    return FluidUtil.getFluidHandler(player.getHeldItem(hand)).isPresent();
  }

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
