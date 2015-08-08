package tconstruct.common.inventory;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import tconstruct.TinkerNetwork;
import tconstruct.library.TinkerAPIException;
import tconstruct.tools.inventory.ContainerToolStation;

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

  public void syncOnOpen(EntityPlayerMP playerOpened) {
    // find another player that already has the gui for this tile open
    WorldServer server = playerOpened.getServerForPlayer();
    for(EntityPlayer player : (List<EntityPlayer>)server.playerEntities) {
      if(player == playerOpened)
        continue;
      if(player.openContainer instanceof BaseContainer) {
        if(this.sameGui((ContainerToolStation) player.openContainer)) {
          syncWithOtherContainer((BaseContainer<T>) player.openContainer, playerOpened);
          return;
        }
      }
    }

    // no player has a container open for the tile
    syncNewContainer(playerOpened);
  }

  protected void syncWithOtherContainer(BaseContainer<T> otherContainer, EntityPlayerMP player) {}
  protected void syncNewContainer(EntityPlayerMP player) {}

  public boolean sameGui(ContainerToolStation otherContainer) {
    return this.tile == otherContainer.tile;
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

  protected int playerInventoryStart = -1;

  /**
   * Draws the player inventory starting at the given position
   *
   * @param playerInventory The players inventory
   * @param xCorner         Default Value: 8
   * @param yCorner         Default Value: (rows - 4) * 18 + 103
   */
  protected void addPlayerInventory(InventoryPlayer playerInventory, int xCorner, int yCorner) {
    int index = 9;

    int start = this.inventorySlots.size();

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

    playerInventoryStart = start;
  }

  @Override
  protected Slot addSlotToContainer(Slot slotIn) {
    if(playerInventoryStart >= 0) {
      throw new TinkerAPIException("BaseContainer: Player inventory has to be last slots. Add all slots before adding the player inventory.");
    }
    return super.addSlotToContainer(slotIn);
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
      int end = this.inventorySlots.size();

      // Is it a slot in the main inventory? (aka not player inventory)
      if(index < playerInventoryStart) {
        // try to put it into the player inventory (if we have a player inventory)
        if(!this.mergeItemStack(itemstack1, playerInventoryStart, end, true)) {
          return null;
        }
      }
      // Slot is in the player inventory (if it exists), transfer to main inventory
      else if(!this.mergeItemStack(itemstack1, 0, playerInventoryStart, false)) {
        return null;
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
  protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
    return mergeItemStackRefill(stack, startIndex, endIndex, useEndIndex) || mergeItemStackMove(stack, startIndex, endIndex, useEndIndex);
  }

  // only refills items that are already present
  protected boolean mergeItemStackRefill(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
    if(stack.stackSize <= 0) {
      return false;
    }

    boolean flag1 = false;
    int k = startIndex;

    if(useEndIndex) {
      k = endIndex - 1;
    }

    Slot slot;
    ItemStack itemstack1;

    if(stack.isStackable()) {
      while(stack.stackSize > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)) {
        slot = (Slot) this.inventorySlots.get(k);
        itemstack1 = slot.getStack();

        if(itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes()
                                                                             || stack.getMetadata() == itemstack1
            .getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
          int l = itemstack1.stackSize + stack.stackSize;
          int limit = Math.min(stack.getMaxStackSize(), slot.getItemStackLimit(stack));

          if(l <= limit) {
            stack.stackSize = 0;
            itemstack1.stackSize = l;
            slot.onSlotChanged();
            flag1 = true;
          }
          else if(itemstack1.stackSize < limit) {
            stack.stackSize -= limit - itemstack1.stackSize;
            itemstack1.stackSize = limit;
            slot.onSlotChanged();
            flag1 = true;
          }
        }

        if(useEndIndex) {
          --k;
        }
        else {
          ++k;
        }
      }
    }

    return flag1;
  }

  // only moves items into empty slots
  protected boolean mergeItemStackMove(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
    if(stack.stackSize <= 0) {
      return false;
    }

    boolean flag1 = false;
    int k;

    if(useEndIndex) {
      k = endIndex - 1;
    }
    else {
      k = startIndex;
    }

    while(!useEndIndex && k < endIndex || useEndIndex && k >= startIndex) {
      Slot slot = (Slot) this.inventorySlots.get(k);
      ItemStack itemstack1 = slot.getStack();

      if(itemstack1 == null && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
      {
        int limit = slot.getItemStackLimit(stack);
        ItemStack stack2 = stack.copy();
        if(stack2.stackSize > limit) {
          stack2.stackSize = limit;
          stack.stackSize -= limit;
        }
        else {
          stack.stackSize = 0;
        }
        slot.putStack(stack2);
        slot.onSlotChanged();
        flag1 = true;

        if(stack.stackSize == 0) {
          break;
        }
      }

      if(useEndIndex) {
        --k;
      }
      else {
        ++k;
      }
    }


    return flag1;
  }
}
