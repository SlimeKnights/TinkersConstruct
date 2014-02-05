package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Event;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SmelteryEvent extends Event
{

    public InventoryLogic component;
    public int x, y, z;

    public SmelteryEvent(InventoryLogic component, int x, int y, int z)
    {
        this.component = component;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static class ItemInsertedIntoCasting extends SmelteryEvent
    {
        public ItemStack item;
        public EntityPlayer player;

        public ItemInsertedIntoCasting(InventoryLogic component, int x, int y, int z, ItemStack item, EntityPlayer player)
        {
            super(component, x, y, z);
            this.item = item;
            this.player = player;
        }
    }

    public static class ItemRemovedFromCasting extends SmelteryEvent
    {
        public ItemStack item;
        public EntityPlayer player;

        public ItemRemovedFromCasting(InventoryLogic component, int x, int y, int z, ItemStack item, EntityPlayer player)
        {
            super(component, x, y, z);
            this.item = item;
            this.player = player;
        }
    }

}
