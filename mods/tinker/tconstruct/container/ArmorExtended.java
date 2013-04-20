package mods.tinker.tconstruct.container;

import mods.tinker.common.IPattern;
import mods.tinker.tconstruct.player.TPlayerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ArmorExtended extends ActiveContainer
{
    public InventoryPlayer invPlayer;
    public TPlayerStats armor;
    
    public ArmorExtended(InventoryPlayer inventoryplayer, TPlayerStats partLogic)
    {
        invPlayer = inventoryplayer;
        armor = partLogic;
        
        /*inventory = new Slot[] { new SlotPattern(partLogic, 0, 40, 27), new SlotPattern(partLogic, 1, 40, 45), new Slot(partLogic, 2, 58, 27), new Slot(partLogic, 3, 58, 45),
                new SlotOnlyTake(partLogic, 4, 102, 27), new SlotOnlyTake(partLogic, 5, 120, 27), new SlotOnlyTake(partLogic, 6, 102, 45), new SlotOnlyTake(partLogic, 7, 120, 45) };       
        for (int iter = 0; iter < inventory.length; iter ++)
            this.addSlotToContainer(inventory[iter]);*/
            
        /* Player inventory */
        /*for (int armor = 0; armor < 4; ++armor)
        {
            this.addSlotToContainer(new SlotArmor(this, par1InventoryPlayer, par1InventoryPlayer.getSizeInventory() - 1 - armor, 8, 8 + armor * 18, armor));
        }*/
        
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
    
    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return true;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotID < armor.getSizeInventory())
            {
                if (!this.mergeItemStack(slotStack, armor.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else
            {
                if (slotStack.getItem() instanceof IPattern)
                {
                    if (!this.mergeItemStack(slotStack, 0, 2, false))
                        return null;
                }
                else if (!this.mergeItemStack(slotStack, 2, 4, false))
                    return null;
            }

            /*if (slotStack.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
                armor.tryBuildPart(slotID);
            }*/
            slot.onSlotChanged();
        }
        return stack;
    }
}
