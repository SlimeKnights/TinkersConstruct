package tconstruct.common.tileentity;

import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileSimpleChest extends TileEntity implements IInvBasic {
  protected InventoryBasic inventoryBasic;

  public TileSimpleChest(String name, int slotCount) {
    this.inventoryBasic = new InventoryBasic(name, true, slotCount);
    this.inventoryBasic.func_110134_a(this);
  }

  public IInventory getInventory() {
    return inventoryBasic;
  }

  @Override
  public void onInventoryChanged(InventoryBasic p_76316_1_) {
    // mark dirty when one of our subinventories changed
    this.markDirty();
  }

  @Override
  public void writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    writeInventoryToNBT(inventoryBasic, compound);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    readInventoryFromNBT(inventoryBasic, compound);
  }

  /** Writes the contents of the inventory to the tag */
  public static void writeInventoryToNBT(IInventory inventory, NBTTagCompound tag) {
    NBTTagList nbttaglist = new NBTTagList();

    for (int i = 0; i < inventory.getSizeInventory(); i++)
    {
      if (inventory.getStackInSlot(i) != null)
      {
        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setByte("Slot", (byte) i);
        inventory.getStackInSlot(i).writeToNBT(itemTag);
        nbttaglist.appendTag(itemTag);
      }
    }

    tag.setTag("Items", nbttaglist);
  }

  /** Reads a an inventory from the tag. Overwrites current content */
  public static void readInventoryFromNBT(IInventory inventory, NBTTagCompound tag) {
    NBTTagList nbttaglist = tag.getTagList("Items", 10);

    for (int i = 0; i < nbttaglist.tagCount(); ++i)
    {
      NBTTagCompound itemTag = nbttaglist.getCompoundTagAt(i);
      int slot = itemTag.getByte("Slot") & 255;

      if (slot >= 0 && slot < inventory.getSizeInventory())
      {
        inventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
      }
    }
  }
}
