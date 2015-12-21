package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.shared.tileentity.TileTable;

// The pattern and part chests
// Technically not tables, but we reuse its code minus the "its legs are block X"
public class TileTinkerChest extends TileTable {

  public static final int MAX_INVENTORY = 256;
  // how big the 'perceived' inventory is
  public int actualSize;

  TileTinkerChest() {
    super("", 0,0);
    actualSize = 1;
  }

  public TileTinkerChest(String name) {
    this(name, MAX_INVENTORY);
  }

  public TileTinkerChest(String name, int inventorySize) {
    super(name, inventorySize);
    actualSize = 1;
  }

  public TileTinkerChest(String name, int inventorySize, int maxStackSize) {
    super(name, inventorySize, maxStackSize);
    actualSize = 1;
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

    if(tags.hasKey("ActualInventorySize")) {
      actualSize = tags.getInteger("ActualInventorySize");
    }
    else {
      // calculate actual size
      actualSize = super.getSizeInventory() - 1;
      // decrease until it matches
      while(actualSize > 0 && getStackInSlot(actualSize-1) == null) {
        actualSize--;
      }
      actualSize++; // add empty slot
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
      while(actualSize-2 >= 0 && getStackInSlot(actualSize-2) == null) {
        actualSize--;
      }
    }

    // actually put the thing in/out
    super.setInventorySlotContents(slot, itemstack);
//      doResize(slot, getSizeInventory()+1);
  }
}
