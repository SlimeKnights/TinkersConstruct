package slimeknights.mantle.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

// Updated version of InventoryLogic in Mantle. Also contains a few bugfixes
public class TileInventory extends TileEntity implements IInventory {

  private ItemStack[] inventory;
  protected String inventoryTitle;
  protected boolean hasCustomName;
  protected int stackSizeLimit;

  /**
   * @param name Localization String for the inventory title. Can be overridden through setCustomName
   */
  public TileInventory(String name, int inventorySize) {
    this(name, inventorySize, 64);
  }

  /**
   * @param name Localization String for the inventory title. Can be overridden through setCustomName
   */
  public TileInventory(String name, int inventorySize, int maxStackSize) {
    this.inventory = new ItemStack[inventorySize];
    this.stackSizeLimit = maxStackSize;
    this.inventoryTitle = name;
  }

    /* Inventory management */

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot < 0 || slot >= inventory.length) {
      return null;
    }

    return inventory[slot];
  }

  public boolean isStackInSlot(int slot) {
    return getStackInSlot(slot) != null;
  }

  @Override
  public int getSizeInventory() {
    return inventory.length;
  }

  @Override
  public int getInventoryStackLimit() {
    return stackSizeLimit;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    if(slot < 0 || slot >= inventory.length) {
      return;
    }

    inventory[slot] = itemstack;
    if(itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
      itemstack.stackSize = getInventoryStackLimit();
    }
  }

  @Override
  public ItemStack decrStackSize(int slot, int quantity) {
    ItemStack itemStack = getStackInSlot(slot);

    if(itemStack == null) {
      return null;
    }

    // whole itemstack taken out
    if(itemStack.stackSize <= quantity) {
      setInventorySlotContents(slot, null);
      this.markDirty();
      return itemStack;
    }

    // split itemstack
    itemStack = itemStack.splitStack(quantity);
    // slot is empty, set to null
    if(getStackInSlot(slot).stackSize == 0) {
      setInventorySlotContents(slot, null);
    }

    this.markDirty();
    // return remainder
    return itemStack;
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    if(slot < getSizeInventory()) {
      if(inventory[slot] == null || itemstack.stackSize + inventory[slot].stackSize <= getInventoryStackLimit()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void clear() {
    for(int i = 0; i < inventory.length; i++) {
      inventory[i] = null;
    }
  }

  @Override
  public String getCommandSenderName() {
    return this.inventoryTitle;
  }

  @Override
  public boolean hasCustomName() {
    return this.hasCustomName;
  }

  public void setCustomName(String customName) {
    this.hasCustomName = true;
    this.inventoryTitle = customName;
  }

  @Override
  public IChatComponent getDisplayName() {
    if(hasCustomName()) {
      return new ChatComponentText(getCommandSenderName());
    }

    return new ChatComponentTranslation(getCommandSenderName());
  }


  /* Supporting methods */
  @Override
  public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    // block changed/got broken?
    if(worldObj.getTileEntity(pos) != this || worldObj.getBlockState(pos).getBlock() == Blocks.air) {
      return false;
    }

    return
        entityplayer.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D)
        <= 64D;
  }

  @Override
  public void openInventory(EntityPlayer player) {

  }

  @Override
  public void closeInventory(EntityPlayer player) {

  }

  /* NBT */
  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    readInventoryFromNBT(tags);
  }

  public void readInventoryFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    readInventoryFromNBT(this, tags);

    if(tags.hasKey("CustomName", 8)) {
      this.inventoryTitle = tags.getString("CustomName");
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tags) {
    super.writeToNBT(tags);
    writeInventoryToNBT(this, tags);

    if(this.hasCustomName()) {
      tags.setString("CustomName", this.inventoryTitle);
    }
  }

  /** Writes the contents of the inventory to the tag */
  public static void writeInventoryToNBT(IInventory inventory, NBTTagCompound tag) {
    NBTTagList nbttaglist = new NBTTagList();

    for(int i = 0; i < inventory.getSizeInventory(); i++) {
      if(inventory.getStackInSlot(i) != null) {
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

    for(int i = 0; i < nbttaglist.tagCount(); ++i) {
      NBTTagCompound itemTag = nbttaglist.getCompoundTagAt(i);
      int slot = itemTag.getByte("Slot") & 255;

      if(slot >= 0 && slot < inventory.getSizeInventory()) {
        inventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
      }
    }
  }

  /* Default implementations of hardly used methods */
  public ItemStack getStackInSlotOnClosing(int slot) {
    return null;
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {

  }

  @Override
  public int getFieldCount() {
    return 0;
  }
}
