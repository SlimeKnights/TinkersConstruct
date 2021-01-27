package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
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
   * Attempts to interact with a flilled bucket on a fluid tank. This is unique as it handles fish buckets, which don't expose fluid capabilities
   * @param world    World instance
   * @param pos      Block position
   * @param player   Player
   * @param hand     Hand
   * @param hit      Hit side
   * @param offset   Direction to place fish
   * @return True if using a bucket
   */
  static boolean interactWithBucket(World world, BlockPos pos, PlayerEntity player, Hand hand, Direction hit, Direction offset) {
    ItemStack held = player.getHeldItem(hand);
    if (held.getItem() instanceof BucketItem) {
      BucketItem bucket = (BucketItem) held.getItem();
      Fluid fluid = bucket.getFluid();
      if (fluid != Fluids.EMPTY) {
        if (!world.isRemote) {
          TileEntity te = world.getTileEntity(pos);
          if (te != null) {
            te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit)
              .ifPresent(handler -> {
                FluidStack fluidStack = new FluidStack(bucket.getFluid(), FluidAttributes.BUCKET_VOLUME);
                // must empty the whole bucket
                if (handler.fill(fluidStack, FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME) {
                  handler.fill(fluidStack, FluidAction.EXECUTE);
                  bucket.onLiquidPlaced(world, held, pos.offset(offset));
                  world.playSound(null, pos, fluid.getAttributes().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                  if (!player.isCreative()) {
                    player.setHeldItem(hand, held.getContainerItem());
                  }
                }
              });
          }
        }
        return true;
      }
    }
    return false;
  }

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
    // success if the item is a fluid handler, regardless of if fluid moved
    ItemStack stack = player.getHeldItem(hand);
    Direction face = hit.getFace();
    if (FluidUtil.getFluidHandler(stack).isPresent()) {
      if (!world.isRemote()) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
          te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face)
            .ifPresent(handler -> FluidUtil.interactWithFluidHandler(player, hand, handler));
        }
      }
      return true;
    }
    // fall back to buckets for fish buckets
    return interactWithBucket(world, pos, player, hand, face, face);
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
