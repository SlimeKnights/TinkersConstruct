package slimeknights.tconstruct.library.fluid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

/**
 * Alternative to {@link net.minecraftforge.fluids.FluidUtil} since no one has time to make the forge util not a buggy mess
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FluidTransferUtil {
  private static final String KEY_FILLED = Util.makeTranslationKey("block", "tank.filled");
  private static final String KEY_DRAINED = Util.makeTranslationKey("block", "tank.drained");

  /**
   * Attempts to transfer fluid
   * @param input    Fluid source
   * @param output   Fluid destination
   * @param maxFill  Maximum to transfer
   * @return  True if transfer succeeded
   */
  public static FluidStack tryTransfer(IFluidHandler input, IFluidHandler output, int maxFill) {
    // first, figure out how much we can drain
    FluidStack simulated = input.drain(maxFill, FluidAction.SIMULATE);
    if (!simulated.isEmpty()) {
      // next, find out how much we can fill
      int simulatedFill = output.fill(simulated, FluidAction.SIMULATE);
      if (simulatedFill > 0) {
        // actually drain
        FluidStack drainedFluid = input.drain(simulatedFill, FluidAction.EXECUTE);
        if (!drainedFluid.isEmpty()) {
          // acutally fill
          int actualFill = output.fill(drainedFluid.copy(), FluidAction.EXECUTE);
          if (actualFill != drainedFluid.getAmount()) {
            TConstruct.log.error("Lost {} fluid during transfer", drainedFluid.getAmount() - actualFill);
          }
        }
        return drainedFluid;
      }
    }
    return FluidStack.EMPTY;
  }

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
  public static boolean interactWithBucket(World world, BlockPos pos, PlayerEntity player, Hand hand, Direction hit, Direction offset) {
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
                  player.sendStatusMessage(new TranslationTextComponent(KEY_FILLED, FluidAttributes.BUCKET_VOLUME, fluidStack.getDisplayName()), true);
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
  public static boolean interactWithFluidItem(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    // success if the item is a fluid handler, regardless of if fluid moved
    ItemStack stack = player.getHeldItem(hand);
    Direction face = hit.getFace();
    // fetch capability before copying, bit more work when its a fluid handler, but saves copying time when its not
    if (!stack.isEmpty() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
      // only server needs to transfer stuff
      if (!world.isRemote) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
          LazyOptional<IFluidHandler> teCapability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face);
          if (teCapability.isPresent()) {
            IFluidHandler teHandler = teCapability.orElse(EmptyFluidHandler.INSTANCE);
            ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
            copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(itemHandler -> {
              // first, try filling the TE from the item
              FluidStack transferred = tryTransfer(itemHandler, teHandler, Integer.MAX_VALUE);
              if (!transferred.isEmpty()) {
                world.playSound(null, pos, transferred.getFluid().getAttributes().getEmptySound(transferred), SoundCategory.BLOCKS, 1.0F, 1.0F);
                player.sendStatusMessage(new TranslationTextComponent(KEY_FILLED, transferred.getAmount(), transferred.getDisplayName()), true);
              } else {
                // if that failed, try filling the item handler from the TE
                transferred = tryTransfer(teHandler, itemHandler, Integer.MAX_VALUE);
                if (!transferred.isEmpty()) {
                  world.playSound(null, pos, transferred.getFluid().getAttributes().getFillSound(transferred), SoundCategory.BLOCKS, 1.0F, 1.0F);
                  player.sendStatusMessage(new TranslationTextComponent(KEY_DRAINED, transferred.getAmount(), transferred.getDisplayName()), true);
                }
              }
              // if either worked, update the player's inventory
              if (!transferred.isEmpty()) {
                player.setHeldItem(hand, DrinkHelper.fill(stack, player, itemHandler.getContainer()));
              }
            });
          }
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Utility to try fluid item then bucket
   * @param world   World instance
   * @param pos     Tank position
   * @param player  Player instance
   * @param hand    Hand used
   * @param hit     Hit position
   * @return  True if interacted
   */
  public static boolean interactWithTank(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    return interactWithFluidItem(world, pos, player, hand, hit)
           || interactWithBucket(world, pos, player, hand, hit.getFace(), hit.getFace());
  }
}
