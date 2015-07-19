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
import net.minecraft.world.World;

import java.util.List;

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

  public <T extends Container> T getSubContainer(Class<T> clazz) {
    return getSubContainer(clazz, 0);
  }

  public <T extends Container> T getSubContainer(Class<T> clazz, int index) {
    for(Container sub : subContainers) {
      if(clazz.isAssignableFrom(sub.getClass())) {
        index--;
      }
      if(index < 0) {
        return (T)sub;
      }
    }

    return null;
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {
    Block block = world.getBlockState(pos).getBlock();
    // does the block we interacted with still exist?
    if(block == Blocks.air || block != originalBlock) {
      return false;
    }

    // check if subcontainers are valid
    for(Container sub : subContainers) {
      if(!sub.canInteractWith(playerIn))
        return false;
    }

    // too far away from block?
    return playerIn.getDistanceSq((double) pos.getX() + 0.5d,
                                  (double) pos.getY() + 0.5d,
                                  (double) pos.getZ() + 0.5d) <= maxDist;
  }

  public void addSubContainer(Container subcontainer) {
    subContainers.add(subcontainer);

    for(Object slot : subcontainer.inventorySlots) {
      addSlotToContainer((Slot)slot);
    }
  }

  @Override
  public void onContainerClosed(EntityPlayer playerIn) {
    for(Container sub : subContainers) {
      sub.onContainerClosed(playerIn);
    }
  }

  @SuppressWarnings("unchecked")
  public List<ItemStack> getInventory() {
    return (List<ItemStack>) super.getInventory();
  }

  // standard yOffset calculation for chestlike inventories:
  // yOffset = (numRows - 4) * 18; (the -4 because of the 3 rows of inventory + 1 row of hotbar)

  /**
   * Draws the player inventory starting at the given position
   *
   * @param playerInventory The players inventory
   * @param xCorner         Default Value: 8
   * @param yCorner         Default Value: (rows - 4) * 18 + 103
   */
  protected void addPlayerInventory(InventoryPlayer playerInventory, int xCorner, int yCorner) {
    int index = 9;

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
  }
}
