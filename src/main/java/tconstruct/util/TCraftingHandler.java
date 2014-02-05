package tconstruct.util;

import tconstruct.achievements.TAchievements;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.util.player.TPlayerStats;
import mantle.common.ComparisonHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class TCraftingHandler //implements ICraftingHandler
{

    @SubscribeEvent
    public void onCrafting (ItemCraftedEvent event)//EntityPlayer player, ItemStack itemstack, IInventory craftMatrix)
    {
        Item item = event.crafting.getItem();
        if (!event.player.worldObj.isRemote)
        {
            if (ComparisonHelper.areEquivalent(item, TRepo.toolStationWood))
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(event.player.getDisplayName());
                NBTTagCompound tags = event.player.getEntityData().getCompoundTag("TConstruct");
                if (!tags.getBoolean("materialManual") || !stats.materialManual)
                {
                    stats.materialManual = true;
                    tags.setBoolean("materialManual", true);
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TRepo.manualBook, 1, 1));
                }
            }
            if (ComparisonHelper.areEquivalent(item, TRepo.smeltery) || ComparisonHelper.areEquivalent(item, TRepo.lavaTank))
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(event.player.getDisplayName());
                NBTTagCompound tags = event.player.getEntityData().getCompoundTag("TConstruct");
                if (!tags.getBoolean("smelteryManual") || !stats.smelteryManual)
                {
                    stats.smelteryManual = true;
                    tags.setBoolean("smelteryManual", true);
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TRepo.manualBook, 1, 2));
                }
                event.player.addStat(TAchievements.achievements.get("tconstruct.smelteryMaker"), 1);
            }

            if (ComparisonHelper.areEquivalent(item, TRepo.craftingStationWood))
            {
                event.player.addStat(TAchievements.achievements.get("tconstruct.betterCrafting"), 1);
            }
        }
    }

}
