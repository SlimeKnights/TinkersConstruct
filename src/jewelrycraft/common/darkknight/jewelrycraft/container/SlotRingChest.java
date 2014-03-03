package common.darkknight.jewelrycraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRingChest extends Slot
{
    public boolean locked = false;
    
    public SlotRingChest(IInventory tile, int slotID, int x, int y, boolean locked)
    {
        super(tile, slotID, x, y);
        this.locked = locked;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return !locked;
    }
    
    @Override
    public ItemStack decrStackSize(int amount)
    {
        if (!locked)
        {
            return super.decrStackSize(amount);
        }
        return null;
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        return !locked;
    }
}
