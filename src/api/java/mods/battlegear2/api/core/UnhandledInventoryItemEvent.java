package mods.battlegear2.api.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created by GotoLink on 01/05/2014.
 * Event posted on {@link MinecraftForge.EVENT_BUS} from the player inventory {@link InventoryPlayerBattle#readFromNBT(NBTTagList)}
 * if the slot number is outside the expected boundaries
 */
public class UnhandledInventoryItemEvent extends PlayerEvent {
    /**
     * The slot number read from NBT, that is outside the arrays limits
     */
    public final int inventorySlot;
    /**
     * The item read from the NBT, not null
     */
    public final ItemStack item;
    public UnhandledInventoryItemEvent(EntityPlayer player, int slot, ItemStack itemStack) {
        super(player);
        inventorySlot = slot;
        item = itemStack;
    }
}
