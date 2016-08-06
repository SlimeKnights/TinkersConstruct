package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockSearedFurnaceController;
import slimeknights.tconstruct.smeltery.client.GuiSearedFurnace;
import slimeknights.tconstruct.smeltery.inventory.ContainerSearedFurnace;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSearedFurnace;

public class TileSearedFurnace extends TileHeatingStructureFuelTank implements IMasterLogic, ITickable, IInventoryGui {

  public static final Logger log = Util.getLogger("Furnace");

  protected static final int MAX_SIZE = 9; // because the smeltery max is 9x9, duh

  // Info about the furnace structure/multiblock
  public MultiblockDetection.MultiblockStructure info;

  protected MultiblockSearedFurnace multiblock;
  protected int tick;

  public TileSearedFurnace() {
    super("gui.searedfurnace.name", 0, 16);

    multiblock = new MultiblockSearedFurnace(this);
  }

  @Override
  public void update() {
    if(worldObj.isRemote) {
      return;
    }

    // are we fully formed?
    if(!isActive()) {
      // check for furnace once per second
      if(tick == 0) {
        checkFurnaceStructure();
      }
    }
    else {
      // we have a lot less to do than a smeltery since the inside is unreachable and we have no liquids
      // basically just heating and fuel consumption

      // we heat items every tick, as otherwise we are quite slow compared to a vanilla furnace
      if(tick % 4 == 0) {
        heatItems();
      }

      if(needsFuel) {
        consumeFuel();
      }

      // we don't check the inside for obstructions since it should not be possible unless the outside was modified
    }

    tick = (tick + 1) % 20;
  }

  /* Grabs the heat for a furnace */
  @Override
  protected void updateHeatRequired(int index) {
    ItemStack stack = getStackInSlot(index);
    if(stack != null) {
      ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
      if(result != null) {
        int newSize = stack.stackSize * result.stackSize;
        if(newSize <= stack.getMaxStackSize() && newSize <= getInventoryStackLimit()) {
          // we like our steaks medium rare :)
          setHeatRequiredForSlot(index, getHeatForStack(stack, result));
        }
        else {
          // if its too big, set the error state
          itemTemperatures[index] = -1;
        }

        // instantly consume fuel if required
        if(!hasFuel()) {
          consumeFuel();
        }

        return;
      }
    }

    setHeatRequiredForSlot(index, 0);
  }

  /**
   * Returns the heat required to smelt the itemstack
   *
   * @param input  Input stack
   * @param result Output stack, here just for conveince so we don't have to index it twice
   * @return A number representing the time
   */
  private int getHeatForStack(@Nonnull ItemStack input, @Nonnull ItemStack result) {
    // base stack temp, here in case Forge adds a hook
    int base = 200;
    float temp = base * input.stackSize / 4f;

    // adjust the speed based on if its a food or not
    // after adjustment with the base of 200, we get a temp of 160 for foods, though its slightly faster than that due to TileHeatingStructure logic
    if(result.getItem() instanceof ItemFood) {
      temp *= 0.8;
    }

    return (int) temp;
  }

  // melt stuff
  @Override
  protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
    ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);

    if(result == null) {
      // recipe changed mid smelting...
      return false;
    }

    // remember, we can smelt a whole stack at once
    result = result.copy();
    result.stackSize *= stack.stackSize;

    setInventorySlotContents(slot, result);
    // we set to 1 so we get positive infinity instead of NaN for progress, since NaN is already defined as no recipe
    itemTemperatures[slot] = 1;
    itemTempRequired[slot] = 0;

    // we return false since the itemstack does not leave the slot, otherwise any data we set here is lost (which really would just be the temp)
    return false;
  }


  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos) {
    checkFurnaceStructure();
  }

  /**
   * Check if the structure is fully formed
   */
  public void checkFurnaceStructure() {
    boolean wasActive = isActive();

    IBlockState state = this.worldObj.getBlockState(getPos());
    if(!(state.getBlock() instanceof BlockSearedFurnaceController)) {
      active = false;
    }
    else {
      EnumFacing in = state.getValue(BlockSearedFurnaceController.FACING).getOpposite();

      MultiblockDetection.MultiblockStructure structure = multiblock.detectMultiblock(this.worldObj, this.getPos().offset(in), MAX_SIZE);
      if(structure == null) {
        active = false;
        updateFurnaceInfo(null);
      }
      else {
        // we found a valid furnace. hurrah!
        active = true;
        MultiblockDetection.assignMultiBlock(this.worldObj, this.getPos(), structure.blocks);
        updateFurnaceInfo(structure);
        // we still have to update since something caused us to rebuild our stats
        // might be the smeltery size changed
        if(wasActive) {
          worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
      }
    }

    // mark the block for updating so the smeltery controller block updates its graphics
    if(wasActive != isActive()) {
      worldObj.notifyBlockUpdate(getPos(), state, state, 3);
      this.markDirty();
    }
  }

  protected void updateFurnaceInfo(MultiblockDetection.MultiblockStructure structure) {
    info = structure;

    if(structure == null) {
      structure = new MultiblockDetection.MultiblockStructure(0, 0, 0, ImmutableList.<BlockPos>of(this.pos));
    }

    // find all tanks for input
    tanks.clear();
    for(BlockPos pos : structure.blocks) {
      if(worldObj.getBlockState(pos).getBlock() == TinkerSmeltery.searedTank) {
        tanks.add(pos);
      }
    }

    // inventory size is 2 per internal space plus an additional 6 for the controller
    // this gives us 12 in a 3x3x3, 90 in a 5x5x5, 
    int inventorySize = 9 + (3 * structure.xd * structure.yd * structure.zd);

    // if the new smeltery is smaller we pop out all items that don't fit in anymore
    if(this.getSizeInventory() > inventorySize) {
      for(int i = inventorySize; i < getSizeInventory(); i++) {
        if(getStackInSlot(i) != null) {
          dropItem(getStackInSlot(i));
        }
      }
    }

    // adjust inventory sizes
    this.resize(inventorySize);
  }

  private void dropItem(ItemStack stack) {
    EnumFacing direction = worldObj.getBlockState(pos).getValue(BlockSearedFurnaceController.FACING);
    BlockPos pos = this.getPos().offset(direction);

    EntityItem entityitem = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
    worldObj.spawnEntityInWorld(entityitem);
  }

  /* GUI */
  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerSearedFurnace(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiSearedFurnace((ContainerSearedFurnace) createContainer(inventoryplayer, world, pos), this);
  }

  /* Networking and saving */
  @Override
  public void validate() {
    super.validate();
    // on validation we set active to false so the furnace checks anew if it's formed
    active = false;
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    boolean wasActive = active;

    readFromNBT(pkt.getNbtCompound());

    // update chunk (rendering) if the active state changed
    if(isActive() != wasActive) {
      IBlockState state = worldObj.getBlockState(getPos());
      worldObj.notifyBlockUpdate(getPos(), state, state, 3);
    }
  }

  @Nonnull
  @Override
  public NBTTagCompound getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
    readFromNBT(tag);
  }
}
