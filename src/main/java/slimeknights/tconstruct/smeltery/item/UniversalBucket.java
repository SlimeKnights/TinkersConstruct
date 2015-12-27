package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.TagUtil;

public class UniversalBucket extends Item implements IFluidContainerItem {

  public final int capacity; // how much the bucket holds
  public final ItemStack empty; // empty item to return and recognize when filling
  public final boolean nbtSensitive;

  public UniversalBucket() {
    this(FluidContainerRegistry.BUCKET_VOLUME, new ItemStack(Items.bucket), false);
  }

  public UniversalBucket(int capacity, ItemStack empty, boolean nbtSensitive) {
    this.capacity = capacity;
    this.empty = empty;
    this.nbtSensitive = nbtSensitive;

    this.setMaxStackSize(1);
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    FluidStack fluidStack = getFluid(stack);
    if(fluidStack == null) {
      return (StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".empty")).trim();
    }



    return Util.translateFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", fluidStack.getLocalizedName());
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
    FluidStack fluidStack = getFluid(itemstack);
    // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
    if(fluidStack == null) {
      return itemstack;
    }

    // clicked on a block?
    MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
    if(mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
      BlockPos clickPos = mop.getBlockPos();
      // can we place liquid there?
      if(world.isBlockModifiable(player, clickPos)) {
        // the block adjacent to the side we clicked on
        BlockPos targetPos = clickPos.offset(mop.sideHit);

        // can the player place there?
        if(player.canPlayerEdit(targetPos, mop.sideHit, itemstack)) {
          // try placing liquid
          if(this.tryPlaceFluid(fluidStack.getFluid().getBlock(), world, targetPos)
             && !player.capabilities.isCreativeMode) {
            // success!
            player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

            // todo: take stacking filled containers into account
            return empty.copy();
          }
        }
      }
    }

    // couldn't place liquid there2
    return itemstack;
  }


  public boolean tryPlaceFluid(Block block, World worldIn, BlockPos pos) {
    if(block == null) {
      return false;
    }

    Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
    boolean isSolid = !material.isSolid();

    // can only place in air or non-solid blocks
    if(!worldIn.isAirBlock(pos) && isSolid) {
      return false;
    }

    // water goes poof?
    if(worldIn.provider.doesWaterVaporize() && (block == Blocks.flowing_water || block == Blocks.water)) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      worldIn.playSoundEffect((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k
                                                                                                  + 0.5F), "random.fizz", 0.5F,
                              2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

      for(int l = 0; l < 8; ++l) {
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                              (double) i + Math.random(),
                              (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
      }
    }
    else {
      if(!worldIn.isRemote && !isSolid && !material.isLiquid()) {
        worldIn.destroyBlock(pos, true);
      }

      worldIn.setBlockState(pos, block.getDefaultState(), 3);
    }
    return true;
  }

  @SubscribeEvent(priority = EventPriority.LOW) // low priority so other mods can handle their stuff first
  public void onFillBucket(FillBucketEvent event) {
    if(event.getResult() != Event.Result.DEFAULT) {
      // event was already handled
      return;
    }

    // not for us to handle
    if(event.current == null ||
       !event.current.isItemEqual(empty) ||
       (nbtSensitive && ItemStack.areItemStackTagsEqual(event.current, empty))) {
      return;
    }

    // needs to target a block
    if(event.target == null || event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
      return;
    }

    World world = event.world;
    BlockPos pos = event.target.getBlockPos();
    IBlockState state = world.getBlockState(pos);
    if(state.getBlock() instanceof IFluidBlock) {
      IFluidBlock fluidBlock = (IFluidBlock) state.getBlock();
      if(fluidBlock.canDrain(world, pos)) {
        FluidStack drained = fluidBlock.drain(world, pos, false);
        // check if it fits exactly
        if(drained != null && drained.amount == capacity) {
          drained = fluidBlock.drain(world, pos, true);
          // fill the fluid into the container
          ItemStack filledBucket = new ItemStack(this);
          this.fill(filledBucket, drained, true);

          // set it as the result
          event.setResult(Event.Result.ALLOW);
          event.result = filledBucket;
        }
      }
    }
  }

  /* FluidContainer Management */

  @Override
  public FluidStack getFluid(ItemStack container) {
    NBTTagCompound tag = TagUtil.getTagSafe(container);
    return FluidStack.loadFluidStackFromNBT(tag);
  }

  @Override
  public int getCapacity(ItemStack container) {
    return capacity;
  }

  @Override
  public int fill(ItemStack container, FluidStack resource, boolean doFill) {
    // has to be exactly 1, must be handled from the caller
    if(container.stackSize != 1) {
      return 0;
    }

    // can only fill exact capacity
    if(resource == null || resource.amount != capacity) {
      return 0;
    }
    // fill the container
    if(doFill) {
      NBTTagCompound tag = TagUtil.getTagSafe(container);
      resource.writeToNBT(tag);
      container.setTagCompound(tag);
    }
    return capacity;
  }

  @Override
  public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
    // can only drain everything at once
    if(maxDrain < capacity) {
      return null;
    }

    FluidStack fluidStack = getFluid(container);
    if(doDrain && fluidStack != null) {
      NBTTagCompound tag = TagUtil.getTagSafe(container);
      tag.removeTag("FluidName");
      tag.removeTag("Amount");
      tag.removeTag("Tag");
      container.setTagCompound(tag);
    }

    return fluidStack;
  }
}
