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
        this.setFoodLevel(foodTimer);
        this.setFoodSaturationLevel(fs.getSaturationLevel());
        this.foodExhaustionLevel = fs.foodExhaustionLevel;
        this.foodTimer = fs.foodTimer;
        this.prevFoodLevel = fs.getPrevFoodLevel();
    }
    
    @Override
    public void addStats (int par1, float par2)
    {
        this.setFoodLevel(par1 + this.getFoodLevel());//Math.min(par1 + this.foodLevel, maxFoodLevel);
        this.setFoodSaturationLevel(Math.min(this.getSaturationLevel() + (float) par1 * par2 * 2.0F, (float) this.getFoodLevel()));
    }
    
    @Override
    public void onUpdate (EntityPlayer par1EntityPlayer)
    {
        int i = par1EntityPlayer.worldObj.difficultySetting;
        this.prevFoodLevel = this.getFoodLevel();

        if (this.foodExhaustionLevel > foodExhaustionThreshold)
        {
            this.foodExhaustionLevel -= foodExhaustionThreshold;

            if (this.getSaturationLevel() > 0.0F)
            {
                this.setFoodSaturationLevel(Math.max(this.getSaturationLevel() - 1.0F, 0.0F));
            }
            else if (i > 0)
            {
                this.setFoodLevel(Math.max(this.getFoodLevel() - 1, 0));
            }
        }

        if (par1EntityPlayer.shouldHeal() && par1EntityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.getFoodLevel() >= 18)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                par1EntityPlayer.heal(1.0F);
                this.addExhaustion(3.0F);
                this.foodTimer = 0;
            }
        }
        else if (this.getFoodLevel() <= 0)
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
        return this.getFoodLevel() < maxFoodLevel;
    }
    
    public void setMaxFoodLevel(int food)
    {
        this.maxFoodLevel = food;
    }
}
