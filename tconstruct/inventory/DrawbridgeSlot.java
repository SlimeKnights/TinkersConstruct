package tconstruct.inventory;

import tconstruct.blocks.logic.DrawbridgeLogic;
import tconstruct.landmine.inventory.SlotBlocksOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class DrawbridgeSlot extends SlotBlocksOnly
{
    DrawbridgeLogic logic;
    public DrawbridgeSlot(IInventory iinventory, int par2, int par3, int par4, DrawbridgeLogic logic)
    {
        super(iinventory, par2, par3, par4);
        this.logic = logic;
    }
    
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return !logic.hasExtended() && super.isItemValid(par1ItemStack);
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return !logic.hasExtended();
    }
    
	@Override
	public int getSlotStackLimit(){
		return inventory.getInventoryStackLimit();
	}
}
