package mods.tinker.tconstruct.util.player;

import java.lang.ref.WeakReference;

import mods.tinker.tconstruct.util.PHConstruct;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TFoodStats extends FoodStats
{
    /** The player's food level. */
    public int foodLevel = 20;

    /** The player's food saturation. */
    public float foodSaturationLevel = 5.0F;

    /** The player's food exhaustion. */
    public float foodExhaustionLevel;

    /** The player's food timer value. */
    public int foodTimer = 0;
    public int prevFoodLevel = 20;

    public WeakReference<EntityPlayer> entityplayer;

    public void initPlayer (EntityPlayer entityplayer)
    {
        this.entityplayer = new WeakReference<EntityPlayer>(entityplayer);
    }

    /**
     * Args: int foodLevel, float foodSaturationModifier
     */
    public void addStats (int healAmount, float saturation)
    {
        if (PHConstruct.alphaRegen)
        {
            EntityPlayer player = this.entityplayer.get();
            if (player != null)
            {
                player.heal(healAmount);

                if (PHConstruct.alphaHunger)
                {
                    if (player.getHealth() >= player.maxHealth)
                    {
                        this.foodLevel = 20;
                    }
                    else
                    {
                        this.foodLevel = 10;
                    }
                }
            }
        }
        else if (!PHConstruct.alphaHunger)
        {
            this.foodLevel = Math.min(healAmount + this.foodLevel, 20);
            this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float) healAmount * saturation * 2.0F, (float) this.foodLevel);
        }
        else
        {
            this.foodLevel = 20;
        }
    }

    /**
     * Eat some food.
     */
    public void addStats (ItemFood par1ItemFood)
    {
        this.addStats(par1ItemFood.getHealAmount(), par1ItemFood.getSaturationModifier());
    }

    /**
     * Handles the food game logic.
     */
    public void onUpdate (EntityPlayer player)
    {
        int difficulty = player.worldObj.difficultySetting;
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > 6.0F)
        {
            this.foodExhaustionLevel -= 6.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (difficulty > 0)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (!PHConstruct.alphaHunger)
        {
            if (this.foodLevel >= 12 + 2 * difficulty && player.shouldHeal() && PHConstruct.enableHealthRegen)
            {
                ++this.foodTimer;

                if (this.foodTimer >= 80)
                {
                    player.heal(1);
                    this.foodTimer = 0;
                }
            }
            else if (this.foodLevel <= 0)
            {
                ++this.foodTimer;

                if (this.foodTimer >= 80)
                {
                    if (player.getHealth() > 10 || difficulty >= 3 || player.getHealth() > 1 && difficulty >= 2)
                    {
                        player.attackEntityFrom(DamageSource.starve, 1);
                    }

                    this.foodTimer = 0;
                }
            }
            else
            {
                this.foodTimer = 0;
            }
        }

    }

    public void readStats (FoodStats stats)
    {
        this.foodLevel = stats.foodLevel;

        /** The player's food saturation. */
        this.foodSaturationLevel = stats.foodSaturationLevel;

        /** The player's food exhaustion. */
        this.foodExhaustionLevel = stats.foodExhaustionLevel;

        /** The player's food timer value. */
        this.foodTimer = stats.foodTimer;
        this.prevFoodLevel = stats.prevFoodLevel;
    }

    /**
     * Reads food stats from an NBT object.
     */
    public void readNBT (NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.hasKey("foodLevel"))
        {
            this.foodLevel = par1NBTTagCompound.getInteger("foodLevel");
            this.foodTimer = par1NBTTagCompound.getInteger("foodTickTimer");
            this.foodSaturationLevel = par1NBTTagCompound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = par1NBTTagCompound.getFloat("foodExhaustionLevel");
        }
    }

    /**
     * Writes food stats to an NBT object.
     */
    public void writeNBT (NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("foodLevel", this.foodLevel);
        par1NBTTagCompound.setInteger("foodTickTimer", this.foodTimer);
        par1NBTTagCompound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        par1NBTTagCompound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    /**
     * Get the player's food level.
     */
    public int getFoodLevel ()
    {
        return this.foodLevel;
    }

    @SideOnly(Side.CLIENT)
    public int getPrevFoodLevel ()
    {
        return this.prevFoodLevel;
    }

    /**
     * If foodLevel is not max.
     */
    public boolean needFood ()
    {
        return this.foodLevel < 20;
    }

    /**
     * adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion (float par1)
    {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + par1, 40.0F);
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel ()
    {
        return this.foodSaturationLevel;
    }

    //@SideOnly(Side.CLIENT)
    public void setFoodLevel (int par1)
    {
        this.foodLevel = par1;
    }

    //@SideOnly(Side.CLIENT)
    public void setFoodSaturationLevel (float par1)
    {
        this.foodSaturationLevel = par1;
    }
}
