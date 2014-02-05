package tconstruct.util.player;

import java.util.UUID;
import java.lang.ref.WeakReference;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

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
            //TConstruct.logger.info("Took something from slot " + slot);
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
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
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
        //TConstruct.logger.info("Changed slot " + slot + " on side " + FMLCommonHandler.instance().getEffectiveSide());
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }

        EntityPlayer player = parent.get();
        TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
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
    public void onInventoryChanged ()
    {
        EntityPlayer player = parent.get();
        TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
        //recalculateSkills(player, stats);
        recalculateHealth(player, stats);

        /*if (inventory[2] == null && stats.knapsack != null)
        {
            stats.knapsack.unequipItems();
        }*/
    }

    /*public void recalculateSkills(EntityPlayer player, TPlayerStats stats)
    {
    	if (inventory[1] != null && inventory[1].getItem() == TRepo.glove)
    	{
    		if (stats.skillList.size() < 1)
    		{
    			try
    			{
    				stats.skillList.add(SkillRegistry.skills.get("Wall Building").copy());
    			}
    			catch (Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	else
    	{
    		if (stats.skillList.size() > 0)
    		{
    			stats.skillList.remove(0);
    		}
    	}
    }*/

    public void recalculateHealth (EntityPlayer player, TPlayerStats stats)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (inventory[6] != null && inventory[6].getItem() == TRepo.heartCanister)
        {
            ItemStack stack = inventory[6];
            int meta = stack.getItemDamage();
            //TConstruct.logger.info("Calculating HP on side " + FMLCommonHandler.instance().getEffectiveSide());
            if (meta == 2)
            {
                int prevHealth = stats.bonusHealth;
                if (side == Side.CLIENT)
                    prevHealth = stats.bonusHealthClient;

                int bonusHP = stack.stackSize * 2;
                if (side == Side.CLIENT)
                    stats.bonusHealthClient = bonusHP;
                else
                    stats.bonusHealth = bonusHP;

                int healthChange = bonusHP - prevHealth;
                //TConstruct.logger.info("healthChange: "+healthChange+" on side "+FMLCommonHandler.instance().getEffectiveSide());
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
        }
        else if (parent != null && parent.get() != null)
        {
            int prevHealth = stats.bonusHealth;
            if (side == Side.CLIENT)
                prevHealth = stats.bonusHealthClient;
            int bonusHP = 0;
            if (side == Side.CLIENT)
                stats.bonusHealthClient = bonusHP;
            else
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
    public void saveToNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
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

        tags.setTag("TConstruct.Inventory", tagList);
    }

    public void readFromNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
        NBTTagList tagList = tags.getTagList("TConstruct.Inventory", 9);
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

    public void dropItems ()
    {
        EntityPlayer player = parent.get();
        for (int i = 0; i < 4; ++i)
        {
            if (this.inventory[i] != null)
            {
                player.dropPlayerItemWithRandomChoice(this.inventory[i], true);
                this.inventory[i] = null;
            }
        }
    }
}
