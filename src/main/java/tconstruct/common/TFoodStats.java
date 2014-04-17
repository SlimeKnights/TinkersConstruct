package tconstruct.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import tconstruct.util.player.TPlayerHandler;

public class TFoodStats extends FoodStats
{
    protected double maxFoodLevel = 40;
    protected int regenThreshold = 32;
    protected double foodExhaustionThreshold = 4.0F;
    
    public TFoodStats(FoodStats fs)
    {
        this.foodLevel = fs.foodLevel;
        this.foodSaturationLevel = fs.foodSaturationLevel;
        this.foodExhaustionLevel = fs.foodExhaustionLevel;
        this.foodTimer = fs.foodTimer;
        this.prevFoodLevel = fs.prevFoodLevel;
    }
    
    @Override
    public void addStats (int par1, float par2)
    {
        this.foodLevel = par1 + this.foodLevel;//Math.min(par1 + this.foodLevel, maxFoodLevel);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float) par1 * par2 * 2.0F, (float) this.foodLevel);
    }
    
    @Override
    public void onUpdate (EntityPlayer par1EntityPlayer)
    {
        int i = par1EntityPlayer.worldObj.difficultySetting;
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > foodExhaustionThreshold)
        {
            this.foodExhaustionLevel -= foodExhaustionThreshold;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (i > 0)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (par1EntityPlayer.shouldHeal() && par1EntityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.foodLevel >= 18)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                par1EntityPlayer.heal(1.0F);
                this.addExhaustion(3.0F);
                this.foodTimer = 0;
            }
        }
        else if (this.foodLevel <= 0)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                if (par1EntityPlayer.getHealth() > 10.0F || i >= 3 || par1EntityPlayer.getHealth() > 1.0F && i >= 2)
                {
                    par1EntityPlayer.attackEntityFrom(DamageSource.starve, 1.0F);
                }

                this.foodTimer = 0;
            }
        }
        else
        {
            this.foodTimer = 0;
        }
    }
    
    @Override
    public boolean needFood ()
    {
        return this.foodLevel < maxFoodLevel;
    }
    
    public void setMaxFoodLevel(int food)
    {
        this.maxFoodLevel = food;
    }
}
