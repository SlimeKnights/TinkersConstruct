package tconstruct.common.inventory;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.List;

import codechicken.lib.math.MathHelper;

/** Same as Container but provides some extra functionality to simplify things */
public abstract class BaseContainer<T extends TileEntity> extends Container {

  protected double maxDist = 8 * 8; // 8 blocks
  protected T tile;
  protected final Block originalBlock; // used to check if the block we interacted with got broken
  protected final BlockPos pos;
  protected final World world;

  public List<Container> subContainers = Lists.newArrayList();

  public BaseContainer(T tile) {
    this.tile = tile;

    this.world = tile.getWorld();
    this.pos = tile.getPos();
    this.originalBlock = world.getBlockState(pos).getBlock();
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {
    Block block = world.getBlockState(pos).getBlock();
    // does the block we interacted with still exist?
    if(block == Blocks.air || block != originalBlock) {
      return false;
    }

    // too far away from block?
    return playerIn.getDistanceSq((double) pos.getX() + 0.5d,
                                  (double) pos.getY() + 0.5d,
                                  (double) pos.getZ() + 0.5d) <= maxDist;
  }

  @SuppressWarnings("unchecked")
  public List<ItemStack> getInventory() {
    return (List<ItemStack>) super.getInventory();
  }

  public IChatComponent getInventoryDisplayName() {
    if(tile instanceof IInventory) {
      return ((IInventory) tile).getDisplayName();
    }
    return null;
  }

  // standard yOffset calculation for chestlike inventories:
  // yOffset = (numRows - 4) * 18; (the -4 because of the 3 rows of inventory + 1 row of hotbar)

  private int playerInventoryStart = -1;
  private int playerInventoryEnd = -1;

  /**
   * Draws the player inventory starting at the given position
   *
   * @param playerInventory The players inventory
   * @param xCorner         Default Value: 8
   * @param yCorner         Default Value: (rows - 4) * 18 + 103
   */
  protected void addPlayerInventory(InventoryPlayer playerInventory, int xCorner, int yCorner) {
    int index = 9;

    playerInventoryStart = this.inventorySlots.size();

    for(int row = 0; row < 3; row++) {
      for(int col = 0; col < 9; col++) {
        this.addSlotToContainer(new Slot(playerInventory, index, xCorner + col * 18, yCorner + row * 18));
        index++;
      }
    }

    index = 0;
    for(int col = 0; col < 9; col++) {
      this.addSlotToContainer(new Slot(playerInventory, index, xCorner + col * 18, yCorner + 58));
      index++;
    }

    playerInventoryEnd = this.inventorySlots.size() - 1;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
    // we can only support inventory <-> playerInventory
    if(playerInventoryStart < 0) {
      // so we don't do anything if no player inventory is present because we don't know what to do
      return null;
    }

    ItemStack itemstack = null;
    Slot slot = (Slot) this.inventorySlots.get(index);

    // slot that was clicked on not empty?
    if(slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();

      // Is it a slot in the main inventory? (aka not player inventory)
      if(index < playerInventoryStart || index > playerInventoryEnd) {
        // try to put it into the player inventory
        if(!this.mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd, true)) {
          return null;
        }
      }
      // Slot is in the player inventory, transfer to main inventory
      else {
        if(playerInventoryEnd > 0 && !this.mergeItemStack(itemstack1, 0, playerInventoryStart, false)) {
          return null;
        }
        else if(playerInventoryEnd < this.inventorySlots.size()-1 && !this.mergeItemStack(itemstack1, playerInventoryEnd, this.inventorySlots.size(), false)) {
          return null;
        }
      }

      if(itemstack1.stackSize == 0) {
        slot.putStack(null);
      }
      else {
        slot.onSlotChanged();
      }
    }

    return itemstack;
  }

  // Fix for a vanilla bug: doesn't take Slot.getMaxStackSize into account
  protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
  {
    boolean flag1 = false;
    int k = startIndex;

    if (useEndIndex)
    {
      k = endIndex - 1;
    }

    Slot slot;
    ItemStack itemstack1;

    if (stack.isStackable())
    {
      while (stack.stackSize > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex))
      {
        slot = (Slot)this.inventorySlots.get(k);
        itemstack1 = slot.getStack();

        if (itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack1.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack1))
        {
          int l = itemstack1.stackSize + stack.stackSize;
          int limit = Math.min(stack.getMaxStackSize(), slot.getItemStackLimit(stack));

          if (l <= limit)
          {
            stack.stackSize = 0;
            itemstack1.stackSize = l;
            slot.onSlotChanged();
            flag1 = true;
          }
          else if (itemstack1.stackSize < limit)
          {
            stack.stackSize -= limit - itemstack1.stackSize;
            itemstack1.stackSize = limit;
            slot.onSlotChanged();
            flag1 = true;
          }
        }

        if (useEndIndex)
        {
          --k;
        }
        else
        {
          ++k;
        }
      }
    }

    if (stack.stackSize > 0)
    {
      if (useEndIndex)
      {
        k = endIndex - 1;
      }
      else
      {
        k = startIndex;
      }

      while (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)
      {
        slot = (Slot)this.inventorySlots.get(k);
        itemstack1 = slot.getStack();

        if (itemstack1 == null && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
        {
          int limit = slot.getItemStackLimit(stack);
          ItemStack stack2 = stack.copy();
          if(stack2.stackSize > limit) {
            stack2.stackSize = limit;
            stack.stackSize -= limit;
            flag1 = false;
          }
          else {
            stack.stackSize = 0;
            flag1 = true;
          }
          slot.putStack(stack2);
          slot.onSlotChanged();
          break;
        }

        if (useEndIndex)
        {
          --k;
        }
        else
        {
          ++k;
        }
      }
    }

    return flag1;
  }

}
