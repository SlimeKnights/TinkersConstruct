package tconstruct.common;

import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.*;
import tconstruct.TConstruct;
import tconstruct.util.player.TPlayerStats;

public class PlayerAbilityHelper
{

    public static void toggleGoggles (EntityPlayer player)
    {
        TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getUniqueID());
        stats.activeGoggles = !stats.activeGoggles;
        if (!stats.activeGoggles)
        {
            player.removePotionEffect(Potion.nightVision.id);
        }
        else
        {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
        }
    }

    public static void swapBelt (EntityPlayer player, TPlayerStats stats)
    {
        NBTTagList slots = new NBTTagList();
        InventoryPlayer hotbar = player.inventory;

        NBTTagCompound itemTag;

        for (int i = 0; i < 9; ++i)
        {
            if (hotbar.mainInventory[i] != null)
            {
                itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                hotbar.mainInventory[i].writeToNBT(itemTag);
                slots.appendTag(itemTag);
            }
            hotbar.mainInventory[i] = null;
        }
        
        ItemStack belt = stats.armor.inventory[3];
        NBTTagList replaceSlots = belt.getTagCompound().getTagList("Inventory", 9);
        if (replaceSlots != null)
        {
            for (int i = 0; i < replaceSlots.tagCount(); ++i)
            {
                itemTag = (NBTTagCompound) replaceSlots.getCompoundTagAt(i);
                int j = itemTag.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.loadItemStackFromNBT(itemTag);

                if (itemstack != null)
                {
                    if (j >= 0 && j < hotbar.mainInventory.length)
                    {
                        hotbar.mainInventory[j] = itemstack;
                    }
                }
            }
        }
        belt.getTagCompound().setTag("Inventory", slots);
    }
}
