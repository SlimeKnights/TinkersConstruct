package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;
import slimeknights.tconstruct.tables.inventory.TinkerChestContainer;

import javax.annotation.Nullable;

public abstract class TinkerChestTileEntity extends TableTileEntity {
  /** Default maximum size */
  protected static final int DEFAULT_MAX = 256;
  /** Current visual size of the inventory */
  private int actualSize = 1;

  public TinkerChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name) {
    this(tileEntityTypeIn, name, DEFAULT_MAX, 64);
  }

  public TinkerChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, name, inventorySize, maxStackSize);
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new TinkerChestContainer(menuId, playerInventory, this);
  }

  @Override
  public abstract boolean isItemValidForSlot(int slot, ItemStack itemstack);

  @Override
  public int getSizeInventory() {
    return actualSize;
  }

  /** Gets the maximum size for this inventory */
  public int getMaxInventory() {
    return super.getSizeInventory();
  }

  @Override
  public void readInventoryFromNBT(CompoundNBT tag) {
    // we need to set it to max because the loading code uses getSizeInventory and we want to load all stacks
    this.actualSize = getMaxInventory();
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

    // non-empty and gets put into the last slot?
    if (slot == this.actualSize - 1 && !itemstack.isEmpty()) {
      // expand slots until the last visible slot is empty (could be something was in there through faulty state)
      do {
        this.actualSize++;
      } while (!this.getStackInSlot(this.actualSize - 1).isEmpty());
    }
    // empty, gets taken from the slot before the last visible slot?
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
