package mods.tinker.tconstruct.inventory;

import mods.tinker.tconstruct.blocks.logic.DrawbridgeLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class DrawbridgeSlot extends Slot
{
    DrawbridgeLogic logic;
    public DrawbridgeSlot(IInventory iinventory, int par2, int par3, int par4, DrawbridgeLogic logic)
    {
        super(iinventory, par2, par3, par4);
        this.logic = logic;
    }
    
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return !logic.hasExtended();
    }
    

    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return !logic.hasExtended();
    }
}
