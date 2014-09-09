package tconstruct.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.*;
import tconstruct.armor.player.*;

public class PlayerAbilityHelper
{

    public static void toggleGoggles (EntityPlayer player, boolean active)
    {
        TPlayerStats stats = TPlayerStats.get(player);
        stats.activeGoggles = active;
        if (!stats.activeGoggles)
        {
            player.removePotionEffect(Potion.nightVision.id);
        }
        else
        {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
        }
    }

    public static void swapBelt (EntityPlayer player, ArmorExtended armor)
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

        ItemStack belt = armor.inventory[3];
        NBTTagList replaceSlots = belt.getTagCompound().getTagList("Inventory", 10);
        for (int i = 0; i < replaceSlots.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = replaceSlots.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                if (j >= 0 && j < hotbar.mainInventory.length)
                {
                    hotbar.mainInventory[j] = itemstack;
                }
            }
        }
        belt.getTagCompound().setTag("Inventory", slots);
    }

    public static void setEntitySize (Entity entity, float width, float height)
    {
        float f2;

        if (width != entity.width || height != entity.height)
        {
            f2 = entity.width;
            entity.width = width;
            entity.height = height;
            entity.boundingBox.maxX = entity.boundingBox.minX + (double) entity.width;
            entity.boundingBox.maxZ = entity.boundingBox.minZ + (double) entity.width;
            entity.boundingBox.maxY = entity.boundingBox.minY + (double) entity.height;

            if (entity.width > f2 && !entity.worldObj.isRemote)
            {
                entity.moveEntity((double) (f2 - entity.width), 0.0D, (double) (f2 - entity.width));
            }
        }

        f2 = width % 2.0F;

        if ((double) f2 < 0.375D)
        {
            entity.myEntitySize = Entity.EnumEntitySize.SIZE_1;
        }
        else if ((double) f2 < 0.75D)
        {
            entity.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        }
        else if ((double) f2 < 1.0D)
        {
            entity.myEntitySize = Entity.EnumEntitySize.SIZE_3;
        }
        else if ((double) f2 < 1.375D)
        {
            entity.myEntitySize = Entity.EnumEntitySize.SIZE_4;
        }
        else if ((double) f2 < 1.75D)
        {
            entity.myEntitySize = Entity.EnumEntitySize.SIZE_5;
        }
        else
        {
            entity.myEntitySize = Entity.EnumEntitySize.SIZE_6;
        }
    }
}
