package tconstruct.library.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event;

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
