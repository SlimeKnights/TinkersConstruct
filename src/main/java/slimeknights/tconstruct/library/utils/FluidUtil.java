package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class FluidUtil {

  private FluidUtil() {}

  public static boolean interactWithTank(ItemStack stack, EntityPlayer player, IFluidHandler tank, EnumFacing side) {
    if(stack == null) {
      return false;
    }

    ItemStack result;

    // regular bucket?
    if((result = FluidUtil.tryFillBucket(stack, tank, side)) != null ||
       (result = FluidUtil.tryEmptyBucket(stack, tank, side)) != null) {
      // "use up" the input item if the player is not in creative
      if(!player.capabilities.isCreativeMode) {
        player.inventory.decrStackSize(player.inventory.currentItem, 1);
        giveItemToPlayer(result, player);
      }
      return true;
    }
    // IFluidContainerItems
    else {
      // copy of the original item for creative mode
      ItemStack copy = null;
      boolean changedBucket = false;
      if(player.capabilities.isCreativeMode && stack != null) {
        copy = stack.copy();
      }
      // convert to fluidcontainer-bucket if it's a regular empty bucket
      if(ItemStack.areItemsEqual(stack, FluidContainerRegistry.EMPTY_BUCKET)) {
        stack = new ItemStack(TinkerSmeltery.bucket);
        changedBucket = true;
      }

      // try filling an empty fluidcontainer or emptying a filled fluidcontainer
      if(FluidUtil.tryFillFluidContainerItem(stack, tank, side, player) ||
         FluidUtil.tryEmptyFluidContainerItem(stack, tank, side)) {
        if(player.capabilities.isCreativeMode) {
          // reset the stack that got modified
          player.inventory.setInventorySlotContents(player.inventory.currentItem, copy);
        }
        else if(changedBucket) {
          // replace the original bucket with the new one
          player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
        }
        return true;
      }
    }

    return false;
  }

  /**
   * Fill an empty bucket from the given tank.
   * @param bucket  The empty bucket
   * @param tank    The tank to fill the bucket from
   * @param side    Side to access the tank from
   * @return The filled bucket or null if the liquid couldn't be taken from the tank.
   */
  public static ItemStack tryFillBucket(ItemStack bucket, IFluidHandler tank, EnumFacing side) {
    FluidTankInfo[] info = tank.getTankInfo(side);
    // check for fluid in the tank
    if(info == null || info.length == 0) {
      return null;
    }
    // check if we actually have an empty bucket
    if(!FluidContainerRegistry.isEmptyContainer(bucket)) {
      return null;
    }
    // fluid in the tank
    FluidStack inTank = info[0].fluid;
    // drain one bucket if possible
    FluidStack liquid = tank.drain(side, FluidContainerRegistry.getContainerCapacity(inTank, bucket), false);
    if(liquid != null && liquid.amount > 0) {
      // success, return filled bucket
      tank.drain(side, FluidContainerRegistry.getContainerCapacity(liquid, bucket), true);
      return FluidContainerRegistry.fillFluidContainer(liquid, bucket);
    }

    return null;
  }

  /**
   * Takes a filled bucket and tries to empty it into the given tank.
   * @param bucket  The filled bucket
   * @param tank    The tank to fill with the bucket
   * @param side    Side to access the tank from
   * @return The empty bucket if successful, null if the tank couldn't be filled.
   */
  public static ItemStack tryEmptyBucket(ItemStack bucket, IFluidHandler tank, EnumFacing side) {
    // not a filled bucket
    if(!FluidContainerRegistry.isFilledContainer(bucket)) {
      return null;
    }

    // try filling the fluid from the bucket into the tank
    FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(bucket);
    if(tank.canFill(side, liquid.getFluid())) {
      // how much can we put into the tank?
      int amount = tank.fill(side, liquid, false);
      // not everything?
      if(amount == liquid.amount) {
        // success, fully filled it into the tank, return empty bucket
        tank.fill(side, liquid, true);
        return FluidContainerRegistry.drainFluidContainer(bucket);
      }
    }

    return null;
  }

  /**
   * Takes an IFluidContainerItem and tries to fill it from the given tank.
   * @param container  The IFluidContainerItem Itemstack to fill. WILL BE MODIFIED!
   * @param tank       The tank to fill from
   * @param side       Side to access the tank from
   * @param player     The player that tries to fill the bucket. Needed if the input itemstack has a stacksize > 1 to determine where the filled container goes.
   * @return True if the IFluidContainerItem was filled successfully, false otherwise. The passed container will have been modified to accomodate for anything done in this method. New Itemstacks might have been added to the players inventory.
   */
  public static boolean tryFillFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, EntityPlayer player) {
    return tryFillFluidContainerItem(container, tank, side, player, -1);
  }

  /**
   * Takes an IFluidContainerItem and tries to fill it from the given tank.
   * To support buckets that are not in the FluidContainerRegistry but implement IFluidContainerItem be sure to convert
   * the empty bucket to your empty bucket variant before passing it to this function.
   * @param container  The IFluidContainerItem Itemstack to fill. WILL BE MODIFIED!
   * @param tank       The tank to fill from
   * @param side       Side to access the tank from
   * @param player     The player that tries to fill the bucket. Needed if the input itemstack has a stacksize > 1 to determine where the filled container goes.
   * @param max        Maximum amount to take from the tank. Uses IFluidContainerItem capacity if <= 0
   * @return True if the IFluidContainerItem was filled successfully, false otherwise. The passed container will have been modified to accomodate for anything done in this method. New Itemstacks might have been added to the players inventory.
   */
  public static boolean tryFillFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, EntityPlayer player, int max) {
    if(!(container.getItem() instanceof IFluidContainerItem)) {
      // not a fluid container
      return false;
    }

    IFluidContainerItem fluidContainer = (IFluidContainerItem) container.getItem();
    if(fluidContainer.getFluid(container) != null) {
      // not empty
      return false;
    }

    // if no maximum is given, fill fully
    if(max <= 0) {
      max = fluidContainer.getCapacity(container);
    }
    // check how much liquid we can drain from the tank
    FluidStack liquid = tank.drain(side, max, false);
    if(liquid != null && liquid.amount > 0) {
      // check which itemstack shall be altered by the fill call
      ItemStack toFill = container;
      if(container.stackSize > 1) {
        toFill = container.copy();
        toFill.stackSize = 1;
      }

      // This manipulates the toFill Itemstack!
      int filled = fluidContainer.fill(toFill, liquid, true);
      tank.drain(side, filled, true);

      // if the filled item is not the container, we have to adjust
      if(toFill != container) {
        // decrease its stacksize to accommodate the filled one (it was >1 from the check above)
        container.stackSize--;

        giveItemToPlayer(toFill, player);
      }

      return true;
    }

    return false;
  }

  /**
   * Takes an IFluidContainerItem and tries to empty it into the given tank.
   * @param container  The IFluidContainerItem Itemstack to empty. WILL BE MODIFIED!
   * @param tank       The tank to fill
   * @param side       Side to access the tank from
   * @return True if the container successfully emptied at least 1 mb into the tank, false otherwise. The passed container itemstack will be modified to accommodate for the liquid transaction.
   */
  public static boolean tryEmptyFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side) {
    if(!(container.getItem() instanceof IFluidContainerItem)) {
      // not a fluid container
      return false;
    }

    IFluidContainerItem fluidContainer = (IFluidContainerItem) container.getItem();
    if(fluidContainer.getFluid(container) != null) {
      // drain everything out of the fluidcontainer
      FluidStack drained = fluidContainer.drain(container, fluidContainer.getCapacity(container), false);
      if(drained != null) {
        // check how much we can fill into the tank
        int filled = tank.fill(side, drained, false);
        if(filled > 0) {
          // verify that the new amount can also be drained (buckets can only extract full amounts for example)
          drained = fluidContainer.drain(container, filled, false);
          if(drained != null && drained.amount == filled) {
            // actually transfer the liquid
            drained = fluidContainer.drain(container, filled, true); // modifies the container!
            tank.fill(side, drained, true);
            return true;
          }
        }
      }
    }

    return false;
  }

  private static void giveItemToPlayer(ItemStack stack, EntityPlayer player) {
    if(stack == null) {
      return;
    }
    // add it to the players inventory
    if(player.inventory.addItemStackToInventory(stack)) {
      player.worldObj.playSoundAtEntity(player, "random.pop", 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
    }
    else {
      // couldn't be added, drop in world
      player.dropPlayerItemWithRandomChoice(stack, false);
    }
  }
}
