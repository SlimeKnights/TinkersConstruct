package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.armor.inventory.SlotOnlyTake;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.StencilTableLogic;

public class PatternShaperContainer extends Container
{
    public StencilTableLogic logic;

    public PatternShaperContainer(InventoryPlayer inventoryplayer, StencilTableLogic shaper)
    {
        logic = shaper;
        this.addSlotToContainer(new Slot(shaper, 0, 48, 35));
        this.addSlotToContainer(new SlotOnlyTake(shaper, 1, 106, 35));
        /*for (int i = 0; i < 3; i++)
        {
            for (int l = 0; l < 3; l++)
            {
            	this.addSlotToContainer(new Slot(craftMatrix, l + i * 3, 30 + l * 18, 17 + i * 18));
            }
        }*/

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 8 + column * 18, 142));
        }
    }

    /*public void onCraftMatrixChanged(IInventory iinventory)
    {
        craftResult.setInventorySloTRepos(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj));
    }*/

    /*@Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);
        if (logic.worldObj.isRemote)
        {
            return;
        }
        ItemStack itemstack = logic.getStackInSlot(0);
        if (itemstack != null)
        {
            entityplayer.dropPlayerItem(itemstack);
        }
    }*/

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        Block block = logic.getWorldObj().getBlock(logic.xCoord, logic.yCoord, logic.zCoord);
        if (block != TinkerTools.toolStationWood && block != TinkerTools.craftingSlabWood)
            return false;
        return logic.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer player, int slotID)
    {
        return null;
        /*ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotID < logic.getSizeInventory())
            {
                if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, logic.getSizeInventory()-1, false))
            {
                return null;
            }

            if (slotStack.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return stack;*/
    }

}