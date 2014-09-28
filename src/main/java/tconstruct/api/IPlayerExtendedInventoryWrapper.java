package tconstruct.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public interface IPlayerExtendedInventoryWrapper
{
    public IInventory getKnapsackInventory (EntityPlayer player);

    public IInventory getAccessoryInventory (EntityPlayer player);
}
