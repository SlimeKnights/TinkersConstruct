package tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import tconstruct.library.mantle.InventoryCraftingPersistent;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.tileentity.TileCraftingStation;

// nearly the same as ContainerWorkbench but uses the TileEntities inventory
public class ContainerCraftingStation extends Container {

  protected final TileCraftingStation tile;

  public InventoryCraftingPersistent craftMatrix;
  public IInventory craftResult;
  private World world;
  private BlockPos pos;

  public ContainerCraftingStation(InventoryPlayer playerInventory, World worldIn, BlockPos pos, TileCraftingStation tile) {
    this.world = worldIn;
    this.pos = pos;
    this.tile = tile;

    craftResult = new InventoryCraftResult();
    craftMatrix = new InventoryCraftingPersistent(this, tile, 3, 3);

    this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
    int i;
    int j;

    for (i = 0; i < 3; ++i)
    {
      for (j = 0; j < 3; ++j)
      {
        this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
      }
    }

    for (i = 0; i < 3; ++i)
    {
      for (j = 0; j < 9; ++j)
      {
        this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (i = 0; i < 9; ++i)
    {
      this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
    }

    this.onCraftMatrixChanged(this.craftMatrix);
  }

  public void onCraftMatrixChanged(IInventory inventoryIn)
  {
    this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.world));
  }

  public void onContainerClosed(EntityPlayer playerIn)
  {
    super.onContainerClosed(playerIn);

    if (!this.world.isRemote)
    {
      for (int i = 0; i < 9; ++i)
      {
        ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

        if (itemstack != null)
        {
          playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
        }
      }
    }
  }

  public boolean canInteractWith(EntityPlayer playerIn)
  {
    return this.world.getBlockState(this.pos).getBlock() != TinkerTools.toolTables ? false : playerIn.getDistanceSq(
        (double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
        (double) this.pos.getZ() + 0.5D) <= 64.0D;
  }

  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
  {
    ItemStack itemstack = null;
    Slot slot = (Slot)this.inventorySlots.get(index);

    if (slot != null && slot.getHasStack())
    {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();

      if (index == 0)
      {
        if (!this.mergeItemStack(itemstack1, 10, 46, true))
        {
          return null;
        }

        slot.onSlotChange(itemstack1, itemstack);
      }
      else if (index >= 10 && index < 37)
      {
        if (!this.mergeItemStack(itemstack1, 37, 46, false))
        {
          return null;
        }
      }
      else if (index >= 37 && index < 46)
      {
        if (!this.mergeItemStack(itemstack1, 10, 37, false))
        {
          return null;
        }
      }
      else if (!this.mergeItemStack(itemstack1, 10, 46, false))
      {
        return null;
      }

      if (itemstack1.stackSize == 0)
      {
        slot.putStack((ItemStack)null);
      }
      else
      {
        slot.onSlotChanged();
      }

      if (itemstack1.stackSize == itemstack.stackSize)
      {
        return null;
      }

      slot.onPickupFromSlot(playerIn, itemstack1);
    }

    return itemstack;
  }

  public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_)
  {
    return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
  }

}
