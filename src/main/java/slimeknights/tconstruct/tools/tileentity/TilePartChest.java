package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.block.ITinkerStationBlock;
import slimeknights.tconstruct.tools.client.GuiPartChest;
import slimeknights.tconstruct.tools.inventory.ContainerPartChest;

public class TilePartChest extends TileTable implements IInventoryGui {

  public static final int MAX_INVENTORY = 256;

  // how big the 'perceived' inventory is
  public int actualSize;

  public TilePartChest() {
    super("gui.partchest.name", MAX_INVENTORY);
    actualSize = 1;
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerPartChest(inventoryplayer, this);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiPartChest(inventoryplayer, world, pos, this);
  }

  @Override
  public int getSizeInventory() {
    return actualSize;
  }

  @Override
  public void writeToNBT(NBTTagCompound tags) {
    super.writeToNBT(tags);

    tags.setInteger("ActualInventorySize", actualSize);
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);

    actualSize = tags.getInteger("ActualInventorySize");
  }

  public void doResize(int slotChanged, int newSize) {
    this.resize(newSize);
    // when resizing we have to refresh all guicontainers of all players
    if(worldObj instanceof WorldServer) {
      for(EntityPlayer player : worldObj.playerEntities) {
        if(player.openContainer instanceof ContainerPartChest) {
          if(((ContainerPartChest) player.openContainer).getTile() == this) {
            // found a container that has this tile open, reopen it to update, yay
            Block block = worldObj.getBlockState(getPos()).getBlock();
            if(block instanceof ITinkerStationBlock) {
              ((ITinkerStationBlock) block).openGui(player, player.worldObj, getPos());
            }
            else {
              player.openGui(TConstruct.instance, 0, player.worldObj, pos.getX(), pos.getY(), pos.getZ());
            }
          }
        }
      }
    }
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    // adjustment from possible external stuff (looking at you there, hoppers >:()
    if(slot > actualSize && itemstack != null) {
      actualSize = slot+1;
    }

    // non-null and gets put into the last slot?
    if(slot == actualSize-1 && itemstack != null && itemstack.stackSize > 0) {
      // expand slots until the last visible slot is empty (could be something was in there through faulty state)
      do {
        actualSize++;
      } while(getStackInSlot(actualSize-1) != null);
    }
    // null, gets taken from the slot before the last visible slot?
    else if(slot >= actualSize-2 && (itemstack == null || itemstack.stackSize == 0)) {
      // decrease inventory size so that 1 free slot after the last non-empty slot is left
      while(actualSize-2 > 0 && getStackInSlot(actualSize-2) == null) {
        actualSize--;
      }
    }

    // actually put the thing in/out
    super.setInventorySlotContents(slot, itemstack);
//      doResize(slot, getSizeInventory()+1);
  }

  // toolparts only
  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    // check if there is no other slot containing that item
    for(int i = 0; i < getSizeInventory(); i++) {
      // don't compare count
      if(ItemStack.areItemsEqual(itemstack, getStackInSlot(i))
         && ItemStack.areItemStackTagsEqual(itemstack, getStackInSlot(i))) {
        return i == slot; // only allowed in the same slot
      }
    }

    return itemstack != null && itemstack.getItem() instanceof IToolPart;
  }

}
