package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.tables.inventory.TinkerChestContainer;

import javax.annotation.Nullable;

public abstract class ChestTileEntity extends InventoryTileEntity {
  /** Default maximum size */
  protected static final int DEFAULT_MAX = 256;
  /** Current visual size of the inventory */
  private int actualSize = 1;

  public ChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name) {
    this(tileEntityTypeIn, name, DEFAULT_MAX, 64);
  }

  public ChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, new TranslationTextComponent(name), inventorySize, maxStackSize);
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
    if (this.getStackInSlot(this.actualSize - 1).isEmpty()) {
      while (this.actualSize > 1 && this.getStackInSlot(this.actualSize - 2).isEmpty()) {
        this.actualSize--;
      }
    }
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    int max = getSizeInventory();
    // if the slot is too large, don't insert
    if (slot >= max) {
      return;
    }
    // catch for slots far past the current one
    if (slot >= this.actualSize && !itemstack.isEmpty()) {
      this.actualSize = slot + 1;
    }

    // space to expand and the index too large? expand the visual size
    if (this.actualSize < max && slot >= this.actualSize - 1 && !itemstack.isEmpty()) {
      // expand slots until the last visible slot is empty (could be something was in there through faulty state)
      do {
        this.actualSize++;
      } while (this.actualSize < max && !this.getStackInSlot(this.actualSize - 1).isEmpty());
    }

    // actually put the thing in/out
    super.setInventorySlotContents(slot, itemstack);

    // empty, and gets taken from one of the last two slots
    if (this.actualSize > 1 && slot >= this.actualSize - 2 && itemstack.isEmpty() && this.getStackInSlot(this.actualSize - 1).isEmpty()) {
      // decrease inventory size so that 1 free slot after the last non-empty slot is left
      while (this.actualSize > 1 && this.getStackInSlot(this.actualSize - 2).isEmpty()) {
        this.actualSize--;
      }
    }

    // actually put the thing in/out
    super.setInventorySlotContents(slot, itemstack);
  }
}
