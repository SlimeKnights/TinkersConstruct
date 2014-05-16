package tconstruct.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.TConstruct;
import tconstruct.achievements.TAchievements;
import tconstruct.common.TRepo;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class TCraftingHandler // implements ICraftingHandler
{

    @SubscribeEvent
    public void onCrafting (ItemCraftedEvent event)// EntityPlayer player,
                                                   // ItemStack itemstack,
                                                   // IInventory craftMatrix)
    {
        Item item = event.crafting.getItem();
        if (!event.player.worldObj.isRemote)
        {
            if (item == Item.getItemFromBlock(TRepo.toolStationWood))
            {
                TPlayerStats stats = TPlayerStats.get(event.player);
                if (!stats.materialManual)
                {
                    stats.materialManual = true;
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TRepo.manualBook, 1, 1));
                }
            }
            if (item == Item.getItemFromBlock(TRepo.smeltery) || item == Item.getItemFromBlock(TRepo.lavaTank))
            {
                TPlayerStats stats = TPlayerStats.get(event.player);
                if (!stats.smelteryManual)
                {
                    stats.smelteryManual = true;
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TRepo.manualBook, 1, 2));
                }
                event.player.addStat(TAchievements.achievements.get("tconstruct:smelteryMaker"), 1);
            }

            if (item == Item.getItemFromBlock(TRepo.craftingStationWood))
            {
                event.player.addStat(TAchievements.achievements.get("tconstruct:betterCrafting"), 1);
            }
            else if (item == TRepo.blankPattern)
            {
                event.player.addStat(TAchievements.achievements.get("tconstruct:pattern"), 1);
            }
        }
    }

}
