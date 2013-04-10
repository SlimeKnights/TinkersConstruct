package mods.tinker.tconstruct;

import mods.tinker.tconstruct.library.AbilityHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.ICraftingHandler;

public class TCraftingHandler implements ICraftingHandler
{

    @Override
    public void onCrafting (EntityPlayer player, ItemStack item, IInventory craftMatrix)
    {
        if (item.getItem().itemID == TContent.toolStationWood.blockID)
        {
            NBTTagCompound tags = player.getEntityData().getCompoundTag("TConstruct");
            if (!tags.getBoolean("materialManual"))
            {
                tags.setBoolean("materialManual", true);
                AbilityHelper.spawnItemAtPlayer(player, new ItemStack(TContent.manualBook, 1, 1));
            }
        }
        if (item.getItem().itemID == TContent.smeltery.blockID)
        {
            NBTTagCompound tags = player.getEntityData().getCompoundTag("TConstruct");
            if (!tags.getBoolean("smelteryManual"))
            {
                tags.setBoolean("smelteryManual", true);
                AbilityHelper.spawnItemAtPlayer(player, new ItemStack(TContent.manualBook, 1, 2));
            }
        }
    }

    @Override
    public void onSmelting (EntityPlayer player, ItemStack item)
    {
    }

}
