package tconstruct.armor.player;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import tconstruct.library.accessory.IHealthAccessory;

public class ArmorExtended implements IInventory
{
    public ItemStack[] inventory = new ItemStack[7];
    public WeakReference<EntityPlayer> parent;
    public UUID globalID = UUID.fromString("B243BE32-DC1B-4C53-8D13-8752D5C69D5B");

    public void init (EntityPlayer player)
    {
        parent = new WeakReference<EntityPlayer>(player);
    }

    @Override
    public int getSizeInventory ()
    {
        return inventory.length;
    }

    public boolean isStackInSlot (int slot)
    {
        return inventory[slot] != null;
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            // TConstruct.logger.info("Took something from slot " + slot);
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return stack;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }
            EntityPlayer player = parent.get();
            TPlayerStats stats = TPlayerStats.get(player);
            recalculateHealth(player, stats);
            return split;
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        // TConstruct.logger.info("Changed slot " + slot + " on side " +
        // FMLCommonHandler.instance().getEffectiveSide());
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }

        EntityPlayer player = parent.get();
        TPlayerStats stats = TPlayerStats.get(player);
        recalculateHealth(player, stats);
    }

    @Override
    public String getInventoryName ()
    {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    @Override
    public void markDirty ()
    {
        EntityPlayer player = parent.get();
        TPlayerStats stats = TPlayerStats.get(player);
        // recalculateSkills(player, stats);
        recalculateHealth(player, stats);

        /*
         * if (inventory[2] == null && stats.knapsack != null) {
         * stats.knapsack.unequipItems(); }
         */
    }

    public void recalculateHealth (EntityPlayer player, TPlayerStats stats)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (inventory[4] != null || inventory[5] != null || inventory[6] != null)
        {
            int bonusHP = 0;
            for (int i = 4; i < 7; i++)
            {
                ItemStack stack = inventory[i];
                if (stack != null && stack.getItem() instanceof IHealthAccessory)
                {
                    bonusHP += ((IHealthAccessory) stack.getItem()).getHealthBoost(stack);
                }
            }
            int prevHealth = stats.bonusHealth;
            stats.bonusHealth = bonusHP;

            int healthChange = bonusHP - prevHealth;
            if (healthChange != 0)
            {
                IAttributeInstance attributeinstance = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth);
                try
                {
                    attributeinstance.removeModifier(attributeinstance.getModifier(globalID));
                }
                catch (Exception e)
                {
                }
                attributeinstance.applyModifier(new AttributeModifier(globalID, "tconstruct.heartCanister", bonusHP, 0));

            }
        }
        else if (parent != null && parent.get() != null)
        {
            int prevHealth = stats.bonusHealth;
            int bonusHP = 0;
            stats.bonusHealth = bonusHP;
            int healthChange = bonusHP - prevHealth;
            if (healthChange != 0)
            {
                IAttributeInstance attributeinstance = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth);
                try
                {
                    attributeinstance.removeModifier(attributeinstance.getModifier(globalID));
                }
                catch (Exception e)
                {
                }
            }
        }
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return true;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemstack)
    {
        return false;
    }

    /* Save/Load */
    public void saveToNBT (NBTTagCompound tagCompound)
    {
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound invSlot;

        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                invSlot = new NBTTagCompound();
                invSlot.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(invSlot);
                tagList.appendTag(invSlot);
            }
        }

        tagCompound.setTag("Inventory", tagList);
    }

    public void readFromNBT (NBTTagCompound tagCompound)
    {
        if (tagCompound != null)
        {
            NBTTagList tagList = tagCompound.getTagList("Inventory", 10);
            for (int i = 0; i < tagList.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = (NBTTagCompound) tagList.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

                if (itemstack != null)
                {
                    this.inventory[j] = itemstack;
                }
            }
        }
    }

    public void dropItems (ArrayList<EntityItem> drops)
    {
        EntityPlayer player = parent.get();
        for (int i = 0; i < 4; ++i)
        {
            if (this.inventory[i] != null)
            {
                EntityItem entityItem = player.func_146097_a(this.inventory[i], true, false);
                drops.add(entityItem);
                this.inventory[i] = null;
            }
        }
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public void closeInventory ()
    {
    }

    public void writeInventoryToStream (ByteBuf os) throws IOException
    {
        for (int i = 0; i < inventory.length; i++)
            ByteBufUtils.writeItemStack(os, inventory[i]);
    }

    public void readInventoryFromStream (ByteBuf is) throws IOException
    {
        for (int i = 0; i < inventory.length; i++)
            inventory[i] = ByteBufUtils.readItemStack(is);
    }
}
