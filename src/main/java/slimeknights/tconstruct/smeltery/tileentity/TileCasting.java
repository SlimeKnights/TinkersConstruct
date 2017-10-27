package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.fluid.FluidHandlerCasting;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.tileentity.IProgress;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.smeltery.events.TinkerCastingEvent;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket.IFluidPacketReceiver;

public abstract class TileCasting extends TileTable implements ITickable, ISidedInventory, IProgress, IFluidPacketReceiver {

  // the internal fluidtank of the casting block
  public FluidTankAnimated tank;
  public IFluidHandler fluidHandler;
  protected int timer; // timer for recipe cooldown
  protected ICastingRecipe recipe; // current recipe

  public TileCasting() {
    super("casting", 2, 1); // 2 slots. 0 == input, 1 == output
    // initialize with empty tank
    tank = new FluidTankAnimated(0, this);
    fluidHandler = new FluidHandlerCasting(this, tank);

    // use a SidedInventory Wrapper to respect the canInsert/Extract calls
    this.itemHandler = new SidedInvWrapper(this, EnumFacing.DOWN);
  }

  // capability
  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) fluidHandler;
    }
    return super.getCapability(capability, facing);
  }

  /* Inventory Management */

  public void interact(EntityPlayer player) {
    // can't interact if liquid inside
    if(tank.getFluidAmount() > 0) {
      return;
    }

    // completely empty -> insert current item into input
    if(!isStackInSlot(0) && !isStackInSlot(1)) {
      ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
      setInventorySlotContents(0, stack);
    }
    // take item out
    else {
      // take out of stack 1 if something is in there, 0 otherwise
      int slot = isStackInSlot(1) ? 1 : 0;

      // Additional Info: Only 1 item can only be put into the casting block usually, however recipes
      // can have Itemstacks with stacksize > 1 as output
      // we therefore spill the whole contents on extraction
      ItemStack stack = getStackInSlot(slot);
      if(slot == 1) {
        FMLCommonHandler.instance().firePlayerSmeltedEvent(player, stack);
      }
      ItemHandlerHelper.giveItemToPlayer(player, stack);
      setInventorySlotContents(slot, ItemStack.EMPTY);

      // send a block update for the comparator, needs to be done after the stack is removed
      if(slot == 1) {
        this.getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
      }
    }
  }

  @Nonnull
  @Override
  public int[] getSlotsForFace(@Nonnull EnumFacing side) {
    return new int[]{0, 1};
  }

  @Override
  public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
    return index == 0 && !isStackInSlot(1);
  }

  @Override
  public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
    return index == 1;
  }

  /* Logic */

  @Override
  public void update() {
    // no recipeeeh
    if(recipe == null) {
      return;
    }

    // fully filled
    if(tank.getFluidAmount() == tank.getCapacity()) {
      timer++;
      if(!getWorld().isRemote) {
        if(timer >= recipe.getTime()) {
          TinkerCastingEvent.OnCasted event = TinkerCastingEvent.OnCasted.fire(recipe, this);
          // done, finish!
          if(event.consumeCast) {
            // todo: play breaking sound and animation
            setInventorySlotContents(0, ItemStack.EMPTY);

            for(EntityPlayer player : getWorld().playerEntities) {
              if(player.getDistanceSq(pos) < 1024 && player instanceof EntityPlayerMP) {
                TinkerNetwork.sendPacket(player, new SPacketParticles(EnumParticleTypes.FLAME, false,
                                                                      pos.getX() + 0.5f,
                                                                      pos.getY() + 1.1f,
                                                                      pos.getZ() + 0.5f,
                                                                      0.25f, 0.0125f, 0.25f,
                                                                      0.005f, 5));
              }
            }
          }

          // put result into output
          if(event.switchOutputs) {
            setInventorySlotContents(1, getStackInSlot(0));
            setInventorySlotContents(0, event.output);
          }
          else {
            setInventorySlotContents(1, event.output);
          }
          getWorld().playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.AMBIENT, 0.07f, 4f);

          // reset state
          reset();

          // comparator update
          getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
        }
      }
      else if(getWorld().rand.nextFloat() > 0.9f) {
        getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + getWorld().rand.nextDouble(), pos.getY() + 1.1, pos.getZ() + getWorld().rand.nextDouble(), 0.0D, 0.0D, 0.0D);
      }
    }
  }

  @Override
  public float getProgress() {
    if(recipe == null || tank.getFluidAmount() == 0) {
      return 0f;
    }
    return Math.min(1f, (float) timer / (float) recipe.getTime());
  }

  public ItemStack getCurrentResult() {
    if(recipe == null) {
      return null;
    }

    Fluid fluid = null;
    if(tank.getFluid() != null) {
      fluid = tank.getFluid().getFluid();
    }

    return recipe.getResult(getStackInSlot(0), fluid);
  }

  /** Return the recipe for the current state, if one exists. Don't forget to fire the OnCasting event! */
  protected abstract ICastingRecipe findRecipe(ItemStack cast, Fluid fluid);

  protected ICastingRecipe findRecipe(Fluid fluid) {
    ICastingRecipe recipe = findRecipe(getStackInSlot(0), fluid);
    if(TinkerCastingEvent.OnCasting.fire(recipe, this)) {
      return recipe;
    }
    // event was cancelled
    return null;
  }

  /** Sets the state for a new casting recipe, returns the fluid amount needed for casting */
  public int initNewCasting(Fluid fluid, boolean setNewRecipe) {
    ICastingRecipe recipe = findRecipe(fluid);
    if(recipe != null) {
      if(setNewRecipe) {
        this.recipe = recipe;
      }
      return recipe.getFluidAmount();
    }
    return 0;
  }

  /** Resets the current state completely */
  public void reset() {
    timer = 0;
    recipe = null;
    tank.setCapacity(0);
    tank.setFluid(null);
    tank.renderOffset = 0;

    if(getWorld() != null && !getWorld().isRemote && getWorld() instanceof WorldServer) {
      TinkerNetwork.sendToClients((WorldServer) getWorld(), pos, new FluidUpdatePacket(pos, null));
    }
  }

  // called clientside to sync with the server and on load
  @Override
  public void updateFluidTo(FluidStack fluid) {
    int oldAmount = tank.getFluidAmount();
    tank.setFluid(fluid);

    if(fluid == null) {
      reset();
      return;
    }
    else if(recipe == null) {
      recipe = findRecipe(fluid.getFluid());
      if(recipe != null) {
        tank.setCapacity(recipe.getFluidAmount());
      }
    }

    tank.renderOffset += tank.getFluidAmount() - oldAmount;
  }

  /**
   * @return The current comparator strength based on if an output exists
   */
  public int comparatorStrength() {
    return isStackInSlot(1) ? 15 : 0;
  }

  /* Saving and Loading */

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {
    tags = super.writeToNBT(tags);

    NBTTagCompound tankTag = new NBTTagCompound();
    tank.writeToNBT(tankTag);
    tags.setTag("tank", tankTag);

    tags.setInteger("timer", timer);
    return tags;
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);

    tank.readFromNBT(tags.getCompoundTag("tank"));

    updateFluidTo(tank.getFluid());

    timer = tags.getInteger("timer");
  }
}
