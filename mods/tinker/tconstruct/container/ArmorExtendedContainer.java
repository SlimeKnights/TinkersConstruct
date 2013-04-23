package mods.tinker.tconstruct.container;

import mods.tinker.common.IPattern;
import mods.tinker.tconstruct.player.TPlayerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ArmorExtendedContainer extends ActiveContainer
{
    public InventoryPlayer invPlayer;
    public TPlayerStats armor;
    
    public ArmorExtendedContainer(InventoryPlayer inventoryplayer, TPlayerStats stats)
    {
        invPlayer = inventoryplayer;
        armor = stats;
        
        this.addSlotToContainer(new Slot(stats, 0, 80, 17));
        this.addSlotToContainer(new Slot(stats, 1, 80, 35));
        this.addSlotToContainer(new Slot(stats, 2, 116, 17));
        this.addSlotToContainer(new Slot(stats, 3, 116, 35));
        this.addSlotToContainer(new Slot(stats, 4, 152, 17));
        this.addSlotToContainer(new Slot(stats, 5, 152, 35));
        this.addSlotToContainer(new Slot(stats, 6, 152, 53));
            
        /* Player inventory */
        for (int armor = 0; armor < 4; ++armor)
        {
            this.addSlotToContainer(new SlotArmorCopy(this, inventoryplayer, inventoryplayer.getSizeInventory() - 1 - armor, 98, 8 + armor * 18, armor));
        }
        
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
        return null;
    }
}
