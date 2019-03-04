package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import slimeknights.tconstruct.shared.inventory.InventoryCraftingPersistent;

/**
 *  SlotCraftingSucks from FastWorkbench adapted for the Crafting Station container (no change in functionality)
 *  See: https://github.com/Shadows-of-Fire/FastWorkbench/blob/master/src/main/java/shadows/fastbench/gui/SlotCraftingSucks.java
 *
 *  Basically it ju
 */
public class SlotCraftingFastWorkbench extends SlotCrafting {

  private final ContainerCraftingStation containerCraftingStation;
  private final InventoryCraftingPersistent craftMatrixPersistent;

  public SlotCraftingFastWorkbench(ContainerCraftingStation containerCraftingStation, EntityPlayer player, InventoryCraftingPersistent craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
    this.containerCraftingStation = containerCraftingStation;
    this.craftMatrixPersistent = craftingInventory;
  }

  @Override
  public ItemStack decrStackSize(int amount) {
    if (this.getHasStack())
    {
      this.amountCrafted += Math.min(amount, this.getStack().getCount());
    }

    return super.decrStackSize(amount);
  }

  @Override
  protected void onCrafting(ItemStack stack) {
    if (this.amountCrafted > 0) {
      stack.onCrafting(this.player.world, this.player, this.amountCrafted);
      FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, craftMatrix);
    }

    this.amountCrafted = 0;
  }

  @Override
  public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
    this.onCrafting(stack);
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
    /* CHANGE BEGINS HERE */
    NonNullList<ItemStack> nonnulllist = containerCraftingStation.getRemainingItems();
    /* END OF CHANGE */
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

    // note: craftMatrixPersistent and this.craftMatrix are the same object!
    craftMatrixPersistent.setDoNotCallUpdates(true);

    for (int i = 0; i < nonnulllist.size(); ++i)
    {
      ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
      ItemStack itemstack1 = nonnulllist.get(i);

      if (!itemstack.isEmpty())
      {
        this.craftMatrix.decrStackSize(i, 1);
        itemstack = this.craftMatrix.getStackInSlot(i);
      }

      if (!itemstack1.isEmpty())
      {
        if (itemstack.isEmpty())
        {
          this.craftMatrix.setInventorySlotContents(i, itemstack1);
        }
        else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
        {
          itemstack1.grow(itemstack.getCount());
          this.craftMatrix.setInventorySlotContents(i, itemstack1);
        }
        else if (!this.player.inventory.addItemStackToInventory(itemstack1))
        {
          this.player.dropItem(itemstack1, false);
        }
      }
    }

    craftMatrixPersistent.setDoNotCallUpdates(false);
    containerCraftingStation.onCraftMatrixChanged(craftMatrixPersistent);

    return stack;
  }

}
