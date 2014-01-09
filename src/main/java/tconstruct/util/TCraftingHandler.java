package tconstruct.util;

import tconstruct.achievements.TAchievements;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.util.player.TPlayerStats;
import mantle.common.ComparisonHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.ICraftingHandler;

public class TCraftingHandler implements ICraftingHandler
{

    @Override
    public void onCrafting (EntityPlayer player, ItemStack itemstack, IInventory craftMatrix)
    {
    	Item item = itemstack.getItem();
        if (!player.worldObj.isRemote)
        {
            if (ComparisonHelper.areEquivalent(item,TRepo.toolStationWood))
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
                NBTTagCompound tags = player.getEntityData().getCompoundTag("TConstruct");
                if (!tags.getBoolean("materialManual") || !stats.materialManual)
                {
                    stats.materialManual = true;
                    tags.setBoolean("materialManual", true);
                    AbilityHelper.spawnItemAtPlayer(player, new ItemStack(TRepo.manualBook, 1, 1));
                }
            }
            if (ComparisonHelper.areEquivalent(item,TRepo.smeltery) || ComparisonHelper.areEquivalent(item,TRepo.lavaTank))
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
                NBTTagCompound tags = player.getEntityData().getCompoundTag("TConstruct");
                if (!tags.getBoolean("smelteryManual") || !stats.smelteryManual)
                {
                    stats.smelteryManual = true;
                    tags.setBoolean("smelteryManual", true);
                    AbilityHelper.spawnItemAtPlayer(player, new ItemStack(TRepo.manualBook, 1, 2));
                }
                player.addStat(TAchievements.achievements.get("tconstruct.smelteryMaker"), 1);
            }
            
            if (ComparisonHelper.areEquivalent(item,TRepo.craftingStationWood)){
            	player.addStat(TAchievements.achievements.get("tconstruct.betterCrafting"), 1);
            }
        }
    }

    @Override
    public void onSmelting (EntityPlayer player, ItemStack item)
    {
    }

}
