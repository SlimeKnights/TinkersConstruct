package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.common.PlayerHelper;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.smeltery.events.TinkerCastingEvent;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public abstract class TileCasting extends TileTable implements ITickable, ISidedInventory, IFluidHandler {

  // the internal fluidtank of the casting block
  public FluidTank tank;
  public float renderOffset;
  protected int timer; // timer for recipe cooldown
  protected CastingRecipe recipe; // current recipe

  public TileCasting() {
    super("casting", 2, 1); // 2 slots. 0 == input, 1 == output
    // initialize with empty tank
    tank = new FluidTank(0);
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
      PlayerHelper.spawnItemAtPlayer(player, stack);
      setInventorySlotContents(slot, null);

    }
  }

  @Override
  public int[] getSlotsForFace(EnumFacing side) {
    return new int[] {0, 1};
  }

  @Override
  public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
    return index == 0;
  }

  @Override
  public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
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
      if(timer >= recipe.getTime()) {
        TinkerCastingEvent.OnCasted event = TinkerCastingEvent.OnCasted.fire(recipe, this);
        // done, finish!
        if(event.consumeCast) {
          setInventorySlotContents(0, null);
        }

        // put result into output
        if(event.switchOutputs) {
          setInventorySlotContents(1, getStackInSlot(0));
          setInventorySlotContents(0, event.output);
        }
        else {
          setInventorySlotContents(1, event.output);
        }

        // reset state
        reset();
      }
    }
  }

  public float getCooldownProgress() {
    if(recipe == null) {
      return 1f;
    }
    return (float)timer/(float)recipe.getTime();
  }

  public ItemStack getCurrentResult() {
    if(recipe == null) {
      return null;
    }
    return recipe.getResult();
  }

  /** Return the recipe for the current state, if one exists. Don't forget to fire the OnCasting event! */
  protected abstract CastingRecipe findRecipe(ItemStack cast, Fluid fluid);

  protected CastingRecipe findRecipe(Fluid fluid) {
    CastingRecipe recipe = findRecipe(getStackInSlot(0), fluid);
    if(TinkerCastingEvent.OnCasting.fire(recipe, this)) {
      return recipe;
    }
    // event was cancelled
    return null;
  }

  protected void reset() {
    timer = 0;
    recipe = null;
    tank.setCapacity(0);
    tank.setFluid(null);

    if(!worldObj.isRemote && worldObj instanceof WorldServer) {
      TinkerNetwork.sendToClients((WorldServer) worldObj, pos, new FluidUpdatePacket(pos, null));
    }
  }

  // called only clientside to sync with the server
  @SideOnly(Side.CLIENT)
  public void updateFluidTo(FluidStack fluid) {
    int oldAmount = tank.getFluidAmount();
    tank.setFluid(fluid);

    if(fluid == null) {
      tank.setCapacity(0);
      recipe = null;
    }
    else if(recipe == null) {
      recipe = findRecipe(fluid.getFluid());
      if(recipe != null) {
        tank.setCapacity(recipe.fluid.amount);
      }
    }

    renderOffset += tank.getFluidAmount() - oldAmount;
  }


  /* Fluid Management */
  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    // can only fill if no output in the inventory
    if(isStackInSlot(1)) {
      return false;
    }
    // can only fill if same fluid or empty and recipe present
    if(tank.getFluidAmount() > 0) {
      // if we have a fluid we also have a recipe
      return tank.getFluid().getFluid() == fluid && tank.getFluidAmount() < tank.getCapacity();
    }

    return findRecipe(fluid) != null;
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    // can only drain if cooldown hasn't started and fluid is there and it's the same
    return timer == 0 && tank.getFluid() != null && tank.getFluid().getFluid() == fluid;
  }

  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    // this is where all the action happens
    if(!canFill(from, resource.getFluid())) {
      return 0;
    }

    // if empty, find a new recipe
    if(this.tank.getFluidAmount() == 0) {
      CastingRecipe recipe = findRecipe(resource.getFluid());
      if(recipe == null) {
        // no recipe found -> can't fill
        return 0;
      }

      int capacity = recipe.fluid.amount;
      IFluidTank calcTank = new FluidTank(capacity);

      // no extra checks needed for the tank since it's empty and we have to set the capacity anyway
      if(doFill) {
        this.recipe = recipe;
        tank.setCapacity(capacity);
        calcTank = tank;
      }

      int filled = calcTank.fill(resource, doFill);
      if(filled > 0 && doFill) {
        renderOffset = filled;
        if(!worldObj.isRemote && worldObj instanceof WorldServer) {
          TinkerNetwork.sendToClients((WorldServer) worldObj, pos, new FluidUpdatePacket(pos, tank.getFluid()));
        }
      }
      return filled;
    }

    // non-empty tank. just try to fill
    int filled = tank.fill(resource, doFill);
    if(filled > 0 && doFill) {
      renderOffset += filled;
      if(!worldObj.isRemote && worldObj instanceof WorldServer) {
        TinkerNetwork.sendToClients((WorldServer) worldObj, pos, new FluidUpdatePacket(pos, tank.getFluid()));
      }
    }

    return filled;
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    if(tank.getFluidAmount() == 0) {
      return null;
    }
    if(tank.getFluid().getFluid() != resource.getFluid()) {
      return null;
    }

    // same fluid, k
    return this.drain(from, resource.amount, doDrain);
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    FluidStack amount = tank.drain(maxDrain, doDrain);
    if(amount != null && doDrain) {
      renderOffset = -maxDrain;
      // if we're empty after the drain we reset the recipe
      if(!worldObj.isRemote && worldObj instanceof WorldServer) {
        TinkerNetwork.sendToClients((WorldServer) worldObj, pos, new FluidUpdatePacket(pos, tank.getFluid()));
      }
      reset();
    }

    return amount;
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    return new FluidTankInfo[]{new FluidTankInfo(tank)};
  }
}
