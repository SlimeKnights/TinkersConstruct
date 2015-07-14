package tconstruct.tools.tileentity;

import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.common.tileentity.TileSimpleChest;

public abstract class TileTableInventory extends TileTable implements IInvBasic {
  protected InventoryBasic inventoryBasic;

  public TileTableInventory(String name, int slotCount) {
    this.inventoryBasic = new InventoryBasic(name, true, slotCount);
    inventoryBasic.func_110134_a(this);
  }

  public IInventory getInventory() {
    return inventoryBasic;
  }

  @Override
  public void onInventoryChanged(InventoryBasic inv) {
    // when the inventory changed, we mark ourselves as dirty
    this.markDirty();
  }

  @Override
  public void writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    TileSimpleChest.writeInventoryToNBT(inventoryBasic, compound);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    TileSimpleChest.readInventoryFromNBT(inventoryBasic, compound);
  }
}
