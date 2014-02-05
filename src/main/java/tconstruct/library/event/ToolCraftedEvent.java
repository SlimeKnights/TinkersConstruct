package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ToolCraftedEvent extends Event
{

    public IInventory inventory;
    public EntityPlayer player;
    public ItemStack tool;

    public ToolCraftedEvent(IInventory inventory, EntityPlayer player, ItemStack tool)
    {
        this.inventory = inventory;
        this.player = player;
        this.tool = tool;
    }

}
