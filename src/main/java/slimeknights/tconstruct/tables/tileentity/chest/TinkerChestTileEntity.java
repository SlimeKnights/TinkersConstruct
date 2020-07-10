package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;

public abstract class TinkerChestTileEntity extends TableTileEntity {

  public static final int MAX_INVENTORY = 256;
  // how big the 'perceived' inventory is
  public int actualSize;

  public TinkerChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name) {
    this(tileEntityTypeIn, name, MAX_INVENTORY);
  }

  public TinkerChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize) {
    super(tileEntityTypeIn, name, inventorySize);
    this.actualSize = 1;
  }

  public TinkerChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, name, inventorySize, maxStackSize);
    this.actualSize = 1;
  }

  @Override
  public int getSizeInventory() {
    return actualSize;
  }

  @Override
  public void readInventoryFromNBT(CompoundNBT tag) {
    // we need to set it to max because the loading code uses getSizeInventory and we want to load all stacks
    this.actualSize = MAX_INVENTORY;
    super.readInventoryFromNBT(tag);

    // recalculate actual size from inventory:
    // decrease until it matches
    while (this.actualSize > 0 && this.getStackInSlot(this.actualSize - 1).isEmpty()) {
      this.actualSize--;
    }

    this.actualSize++; // add empty slot
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    // adjustment from possible external stuff (looking at you there, hoppers >:()
    if (slot > this.actualSize && !itemstack.isEmpty()) {
      this.actualSize = slot + 1;
    }

    // non-null and gets put into the last slot?
    if (slot == this.actualSize - 1 && !itemstack.isEmpty() && itemstack.getCount() > 0) {
      // expand slots until the last visible slot is empty (could be something was in there through faulty state)
      do {
        this.actualSize++;
      } while (!this.getStackInSlot(this.actualSize - 1).isEmpty());
    }
    // null, gets taken from the slot before the last visible slot?
    else if (slot >= this.actualSize - 2 && itemstack.isEmpty()) {
      // decrease inventory size so that 1 free slot after the last non-empty slot is left
      while (this.actualSize - 2 >= 0 && this.getStackInSlot(this.actualSize - 2).isEmpty()) {
        this.actualSize--;
      }
    }

    // actually put the thing in/out
    super.setInventorySlotContents(slot, itemstack);
  }
}
