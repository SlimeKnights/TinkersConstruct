package slimeknights.tconstruct.shared.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.network.InventoryCraftingSyncPacket;

// variant of InventoryCrafting that saves its itemstacks into the given inventory
public class InventoryCraftingPersistent extends InventoryCrafting {

  private final int length;
  private final Container eventHandler;
  private final IInventory parent;

  public InventoryCraftingPersistent(Container eventHandler, IInventory parent, int width, int height) {
    super(eventHandler, width, height);
    int k = width * height;

    assert (k == parent.getSizeInventory());

    this.parent = parent;
    this.length = k;
    this.eventHandler = eventHandler;
  }

  @Override
  public int getSizeInventory() {
    return this.length;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return index >= this.getSizeInventory() ? null : this.parent.getStackInSlot(index);
  }

  public String getCommandSenderName() {
    return "container.crafting";
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  public ItemStack getStackInSlotOnClosing(int index) {
    return null;
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    if(this.getStackInSlot(index) != null) {
      ItemStack itemstack;

      if(this.getStackInSlot(index).stackSize <= count) {
        itemstack = this.getStackInSlot(index);
        this.setInventorySlotContents(index, null);
        this.eventHandler.onCraftMatrixChanged(this);
        return itemstack;
      }
      else {
        itemstack = this.getStackInSlot(index).splitStack(count);

        if(this.getStackInSlot(index).stackSize == 0) {
          this.setInventorySlotContents(index, null);
        }

        this.eventHandler.onCraftMatrixChanged(this);
        return itemstack;
      }
    }
    else {
      return null;
    }
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    this.parent.setInventorySlotContents(index, stack);
    this.eventHandler.onCraftMatrixChanged(this);
  }

  @Override
  public void markDirty() {
    this.parent.markDirty();
    this.eventHandler.onCraftMatrixChanged(this);

    TinkerTools.proxy.sendPacketToServerOnly(new InventoryCraftingSyncPacket());
  }

  @Override
  public void clear() {
    // inventory can't clear the tile container
  }
}
